/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.event.vaevent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.espertech.esper.event.*;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.collection.ArrayDequeJDK6Backport;
import com.espertech.esper.view.StatementStopService;
import com.espertech.esper.view.StatementStopCallback;
import com.espertech.esper.view.Viewable;
import com.espertech.esper.epl.named.NamedWindowRootView;
import com.espertech.esper.epl.named.NamedWindowIndexRepository;
import com.espertech.esper.epl.join.table.EventTable;
import com.espertech.esper.core.EPStatementHandle;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.EventPropertyGetter;
import com.espertech.esper.client.PropertyAccessException;

import java.util.*;

/**
 * Provides overlay strategy for property group-based versioning.
 */
public class VAERevisionProcessorDeclared extends VAERevisionProcessorBase implements ValueAddEventProcessor
{
    private static final Log log = LogFactory.getLog(VAERevisionProcessorDeclared.class);

    private final PropertyGroupDesc groups[];
    private final EventType baseEventType;
    private final EventPropertyGetter[] fullKeyGetters;
    private Map<MultiKeyUntyped, RevisionStateDeclared> statePerKey;

    /**
     * Ctor.
     * @param revisionEventTypeName name
     * @param spec specification
     * @param statementStopService for stop handling
     * @param eventAdapterService for nested property handling
     */
    public VAERevisionProcessorDeclared(String revisionEventTypeName, RevisionSpec spec, StatementStopService statementStopService, EventAdapterService eventAdapterService)
    {
        super(spec, revisionEventTypeName, eventAdapterService);

        // on statement stop, remove versions
        statementStopService.addSubscriber(new StatementStopCallback() {
            public void statementStopped()
            {
                statePerKey.clear();
            }
        });

        this.statePerKey = new HashMap<MultiKeyUntyped, RevisionStateDeclared>();
        this.baseEventType = spec.getBaseEventType();
        this.fullKeyGetters = PropertyUtility.getGetters(baseEventType, spec.getKeyPropertyNames());

        // sort non-key properties, removing keys
        groups = PropertyUtility.analyzeGroups(spec.getChangesetPropertyNames(), spec.getDeltaTypes(), spec.getDeltaNames());
        Map<String, RevisionPropertyTypeDesc> propertyDesc = createPropertyDescriptors(spec, groups);

        typeDescriptors = PropertyUtility.getPerType(groups, spec.getChangesetPropertyNames(), spec.getKeyPropertyNames());
        EventTypeMetadata metadata = EventTypeMetadata.createValueAdd(revisionEventTypeName, EventTypeMetadata.TypeClass.REVISION);
        revisionEventType = new RevisionEventType(metadata, propertyDesc, eventAdapterService);
    }

    public EventBean getValueAddEventBean(EventBean event)
    {
        return new RevisionEventBeanDeclared(revisionEventType, event);
    }

    public void onUpdate(EventBean[] newData, EventBean[] oldData, NamedWindowRootView namedWindowRootView, NamedWindowIndexRepository indexRepository)
    {
        // If new data is filled, it is not a delete
        if ((newData == null) || (newData.length == 0))
        {
            // we are removing an event
            RevisionEventBeanDeclared revisionEvent = (RevisionEventBeanDeclared) oldData[0];
            MultiKeyUntyped key = revisionEvent.getKey();
            statePerKey.remove(key);

            // Insert into indexes for fast deletion, if there are any
            for (EventTable table : indexRepository.getTables())
            {
                table.remove(oldData);
            }

            // make as not the latest event since its due for removal
            revisionEvent.setLatest(false);

            namedWindowRootView.updateChildren(null, oldData);
            return;
        }

        RevisionEventBeanDeclared revisionEvent = (RevisionEventBeanDeclared) newData[0];
        EventBean underlyingEvent = revisionEvent.getUnderlyingFullOrDelta();
        EventType underyingEventType = underlyingEvent.getEventType();

        // obtain key values
        MultiKeyUntyped key = null;
        RevisionTypeDesc typesDesc = null;
        boolean isBaseEventType = false;
        if (underyingEventType == baseEventType)
        {
            key = PropertyUtility.getKeys(underlyingEvent, fullKeyGetters);
            isBaseEventType = true;
        }
        else
        {
            typesDesc = typeDescriptors.get(underyingEventType);

            // if this type cannot be found, check all supertypes, if any
            if (typesDesc == null)
            {
                Iterator<EventType> superTypes = underyingEventType.getDeepSuperTypes();
                if (superTypes != null)
                {
                    EventType superType;
                    for (;superTypes.hasNext();)
                    {
                        superType = superTypes.next();
                        if (superType == baseEventType)
                        {
                            key = PropertyUtility.getKeys(underlyingEvent, fullKeyGetters);
                            isBaseEventType = true;
                            break;
                        }
                        typesDesc = typeDescriptors.get(superType);
                        if (typesDesc != null)
                        {
                            typeDescriptors.put(underyingEventType, typesDesc);
                            key = PropertyUtility.getKeys(underlyingEvent, typesDesc.getKeyPropertyGetters());
                            break;
                        }
                    }
                }
            }
            else
            {
                key = PropertyUtility.getKeys(underlyingEvent, typesDesc.getKeyPropertyGetters());
            }

            if (key == null)
            {
                log.warn("Ignoring event of event type '" + underyingEventType + "' for revision processing type '" + revisionEventTypeName);
                return;
            }
        }

        // get the state for this key value
        RevisionStateDeclared revisionState = statePerKey.get(key);

        // Delta event and no full
        if ((!isBaseEventType) && (revisionState == null))
        {
            return; // Ignore the event, its a delta and we don't currently have a full event for it
        }

        // New full event
        if (revisionState == null)
        {
            revisionState = new RevisionStateDeclared(underlyingEvent, null, null);
            statePerKey.put(key, revisionState);

            // prepare revison event
            revisionEvent.setLastBaseEvent(underlyingEvent);
            revisionEvent.setKey(key);
            revisionEvent.setHolders(null);
            revisionEvent.setLatest(true);

            // Insert into indexes for fast deletion, if there are any
            for (EventTable table : indexRepository.getTables())
            {
                table.add(newData);
            }

            // post to data window
            revisionState.setLastEvent(revisionEvent);
            namedWindowRootView.updateChildren(new EventBean[] {revisionEvent}, null);
            return;
        }

        // new version
        long versionNumber = revisionState.incRevisionNumber();

        // Previously-seen full event
        if (isBaseEventType)
        {
            revisionState.setHolders(null);
            revisionState.setBaseEventUnderlying(underlyingEvent);
        }
        // Delta event to existing full event
        else
        {
            int groupNum = typesDesc.getGroup().getGroupNum();
            RevisionBeanHolder[] holders = revisionState.getHolders();
            if (holders == null)    // optimization - the full event sets it to null, deltas all get a new one
            {
                holders = new RevisionBeanHolder[groups.length];
            }
            else
            {
                holders = arrayCopy(holders);   // preserve the last revisions
            }

            // add the new revision for a property group on top
            holders[groupNum] = new RevisionBeanHolder(versionNumber, underlyingEvent, typesDesc.getChangesetPropertyGetters());
            revisionState.setHolders(holders);
        }

        // prepare revision event
        revisionEvent.setLastBaseEvent(revisionState.getBaseEventUnderlying());
        revisionEvent.setHolders(revisionState.getHolders());
        revisionEvent.setKey(key);
        revisionEvent.setLatest(true);

        // get prior event
        RevisionEventBeanDeclared lastEvent = revisionState.getLastEvent();
        lastEvent.setLatest(false);

        // data to post
        EventBean[] newDataPost = new EventBean[]{revisionEvent};
        EventBean[] oldDataPost = new EventBean[]{lastEvent};

        // update indexes
        for (EventTable table : indexRepository.getTables())
        {
            table.remove(oldDataPost);
            table.add(newDataPost);
        }

        // keep reference to last event
        revisionState.setLastEvent(revisionEvent);

        namedWindowRootView.updateChildren(newDataPost, oldDataPost);
    }

    public Collection<EventBean> getSnapshot(EPStatementHandle createWindowStmtHandle, Viewable parent)
    {
        createWindowStmtHandle.getStatementLock().acquireLock(null);
        try
        {
            Iterator<EventBean> it = parent.iterator();
            if (!it.hasNext())
            {
                return Collections.EMPTY_LIST;
            }
            ArrayDequeJDK6Backport<EventBean> list = new ArrayDequeJDK6Backport<EventBean>();
            while (it.hasNext())
            {
                RevisionEventBeanDeclared fullRevision = (RevisionEventBeanDeclared) it.next();
                MultiKeyUntyped key = fullRevision.getKey();
                RevisionStateDeclared state = statePerKey.get(key);
                list.add(state.getLastEvent());
            }
            return list;
        }
        finally
        {
            createWindowStmtHandle.getStatementLock().releaseLock(null);
        }
    }

    public void removeOldData(EventBean[] oldData, NamedWindowIndexRepository indexRepository)
    {
        for (int i = 0; i < oldData.length; i++)
        {
            RevisionEventBeanDeclared event = (RevisionEventBeanDeclared) oldData[i];

            // If the remove event is the latest event, remove from all caches
            if (event.isLatest())
            {
                MultiKeyUntyped key = event.getKey();
                statePerKey.remove(key);

                for (EventTable table : indexRepository.getTables())
                {
                    table.remove(oldData);
                }
            }
        }
    }

    private RevisionBeanHolder[] arrayCopy(RevisionBeanHolder[] array)
    {
        if (array == null)
        {
            return null;
        }
        RevisionBeanHolder[] result = new RevisionBeanHolder[array.length];
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    /**
     * Creates property descriptors for revision.
     * @param spec specifies revision
     * @param groups the groups that group properties
     * @return map of property and descriptor
     */
    public static Map<String, RevisionPropertyTypeDesc> createPropertyDescriptors(RevisionSpec spec, PropertyGroupDesc groups[])
    {
        Map<String, int[]> propsPerGroup = PropertyUtility.getGroupsPerProperty(groups);

        Map<String, RevisionPropertyTypeDesc> propertyDesc = new HashMap<String, RevisionPropertyTypeDesc>();
        int count = 0;

        for (String property : spec.getChangesetPropertyNames())
        {
            EventPropertyGetter fullGetter = spec.getBaseEventType().getGetter(property);
            int propertyNumber = count;
            int[] propGroupsProperty = propsPerGroup.get(property);
            final RevisionGetterParameters params = new RevisionGetterParameters(property, propertyNumber, fullGetter, propGroupsProperty);

            // if there are no groups (full event property only), then simply use the full event getter
            EventPropertyGetter revisionGetter = new EventPropertyGetter() {
                    public Object get(EventBean eventBean) throws PropertyAccessException
                    {
                        RevisionEventBeanDeclared riv = (RevisionEventBeanDeclared) eventBean;
                        return riv.getVersionedValue(params);
                    }

                    public boolean isExistsProperty(EventBean eventBean)
                    {
                        return true;
                    }

                    public Object getFragment(EventBean eventBean)
                    {
                        return null; // fragments no provided by revision events
                    }
                };

            Class type = spec.getBaseEventType().getPropertyType(property);
            RevisionPropertyTypeDesc propertyTypeDesc = new RevisionPropertyTypeDesc(revisionGetter, params, type);
            propertyDesc.put(property, propertyTypeDesc);
            count++;
        }

        for (String property : spec.getBaseEventOnlyPropertyNames())
        {
            final EventPropertyGetter fullGetter = spec.getBaseEventType().getGetter(property);

            // if there are no groups (full event property only), then simply use the full event getter
            EventPropertyGetter revisionGetter =  new EventPropertyGetter() {
                public Object get(EventBean eventBean) throws PropertyAccessException
                {
                    RevisionEventBeanDeclared riv = (RevisionEventBeanDeclared) eventBean;
                    return fullGetter.get(riv.getLastBaseEvent());
                }

                public boolean isExistsProperty(EventBean eventBean)
                {
                    return true;
                }

                public Object getFragment(EventBean eventBean)
                {
                    return null; // fragments no provided by revision events
                }
            };

            Class type = spec.getBaseEventType().getPropertyType(property);
            RevisionPropertyTypeDesc propertyTypeDesc = new RevisionPropertyTypeDesc(revisionGetter, null, type);
            propertyDesc.put(property, propertyTypeDesc);
            count++;
        }

        count = 0;
        for (String property : spec.getKeyPropertyNames())
        {
            final int keyPropertyNumber = count;

            EventPropertyGetter revisionGetter = new EventPropertyGetter() {
                public Object get(EventBean eventBean) throws PropertyAccessException
                {
                    RevisionEventBeanDeclared riv = (RevisionEventBeanDeclared) eventBean;
                    return riv.getKey().getKeys()[keyPropertyNumber];
                }

                public boolean isExistsProperty(EventBean eventBean)
                {
                    return true;
                }

                public Object getFragment(EventBean eventBean)
                {
                    return null;
                }
            };

            Class type = spec.getBaseEventType().getPropertyType(property);
            RevisionPropertyTypeDesc propertyTypeDesc = new RevisionPropertyTypeDesc(revisionGetter, null, type);
            propertyDesc.put(property, propertyTypeDesc);
            count++;
        }

        return propertyDesc;
    }
}
