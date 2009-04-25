/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

import com.espertech.esper.collection.Pair;
import com.espertech.esper.core.StatementContext;
import com.espertech.esper.epl.spec.ViewSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility methods to deal with chains of views, and for merge/group-by views.
 */
public class ViewServiceHelper
{
    /**
     * Add merge views for any views in the chain requiring a merge (group view).
     * Appends to the list of view specifications passed in one ore more
     * new view specifications that represent merge views.
     * Merge views have the same parameter list as the (group) view they merge data for.
     * @param specifications is a list of view definitions defining the chain of views.
     * @throws ViewProcessingException indicating that the view chain configuration is invalid
     */
    protected static void addMergeViews(List<ViewSpec> specifications)
            throws ViewProcessingException
    {
        if (log.isDebugEnabled())
        {
            log.debug(".addMergeViews Incoming specifications=" + Arrays.toString(specifications.toArray()));
        }

        // A grouping view requires a merge view and cannot be last since it would not group sub-views
        if (specifications.size() > 0)
        {
            ViewSpec lastView = specifications.get(specifications.size() - 1);
            ViewEnum viewEnum = ViewEnum.forName(lastView.getObjectNamespace(), lastView.getObjectName());
            if ((viewEnum != null) && (viewEnum.getMergeView() != null))
            {
                throw new ViewProcessingException("Invalid use of the '" + lastView.getObjectNamespace() + ":" +
                            lastView.getObjectName() + "' view, the view requires one or more child views to group, or consider using the group-by clause");
            }
        }

        LinkedList<ViewSpec> mergeViewSpecs = new LinkedList<ViewSpec>();

        for (ViewSpec spec : specifications)
        {
            ViewEnum viewEnum = ViewEnum.forName(spec.getObjectNamespace(), spec.getObjectName());
            if (viewEnum == null)
            {
                continue;
            }

            if (viewEnum.getMergeView() == null)
            {
                continue;
            }

            // The merge view gets the same parameters as the view that requires the merge
            ViewSpec mergeViewSpec = new ViewSpec(viewEnum.getMergeView().getNamespace(), viewEnum.getMergeView().getName(),
                    spec.getObjectParameters());

            // The merge views are added to the beginning of the list.
            // This enables group views to stagger ie. marketdata.group("symbol").group("feed").xxx.merge(...).merge(...)
            mergeViewSpecs.addFirst(mergeViewSpec);
        }

        specifications.addAll(mergeViewSpecs);

        if (log.isDebugEnabled())
        {
            log.debug(".addMergeViews Outgoing specifications=" + Arrays.toString(specifications.toArray()));
        }
    }

    /**
     * Instantiate a chain of views.
     * @param parentViewable - parent view to add the chain to
     * @param viewFactories - is the view factories to use to make each view, or reuse and existing view
     * @param context - dependent services
     * @return chain of views instantiated
     */
    protected static List<View> instantiateChain(Viewable parentViewable,
                                                 List<ViewFactory> viewFactories,
                                                 StatementContext context)
    {
        List<View> newViews = new LinkedList<View>();
        Viewable parent = parentViewable;

        for (ViewFactory viewFactory : viewFactories)
        {
            // Create the new view object
            View currentView = viewFactory.makeView(context);

            newViews.add(currentView);
            parent.addView(currentView);

            // Next parent is the new view
            parent = currentView;
        }

        return newViews;
    }

    /**
     * Removes a view from a parent view returning the orphaned parent views in a list.
     * @param parentViewable - parent to remove view from
     * @param viewToRemove - view to remove
     * @return chain of orphaned views
     */
    protected static List<View> removeChainLeafView(Viewable parentViewable,
                                                    Viewable viewToRemove)
    {
        List<View> removedViews = new LinkedList<View>();

        // The view to remove must be a leaf node - non-leaf views are just not removed
        if (viewToRemove.hasViews())
        {
            return removedViews;
        }

        // Find child viewToRemove among descendent views
        List<View> viewPath = ViewSupport.findDescendent(parentViewable, viewToRemove);

        if (viewPath == null)
        {
            String message = "Viewable not found when removing view " + viewToRemove;
            throw new IllegalArgumentException(message);
        }

        // The viewToRemove is a direct child view of the stream
        if (viewPath.isEmpty())
        {
            boolean isViewRemoved = parentViewable.removeView( (View) viewToRemove);

            if (!isViewRemoved)
            {
                String message = "Failed to remove immediate child view " + viewToRemove;
                log.fatal(".remove " + message);
                throw new IllegalStateException(message);
            }

            removedViews.add((View) viewToRemove);
            return removedViews;
        }

        View[] viewPathArray = viewPath.toArray(new View[viewPath.size()]);
        View currentView = (View) viewToRemove;

        // Remove child from parent views until a parent view has more children,
        // or there are no more parents (index=0).
        for (int index = viewPathArray.length - 1; index >= 0; index--)
        {
            boolean isViewRemoved = viewPathArray[index].removeView(currentView);
            removedViews.add(currentView);

            if (!isViewRemoved)
            {
                String message = "Failed to remove view " + currentView;
                log.fatal(".remove " + message);
                throw new IllegalStateException(message);
            }

            // If the parent views has more child views, we are done
            if (viewPathArray[index].hasViews())
            {
                break;
            }

            // The parent of the top parent is the stream, remove from stream
            if (index == 0)
            {
                parentViewable.removeView(viewPathArray[0]);
                removedViews.add(viewPathArray[0]);
            }
            else
            {
                currentView = viewPathArray[index];
            }
        }

        return removedViews;
    }

    /**
     * Match the views under the stream to the list of view specications passed in.
     * The method changes the view specifications list passed in and removes those
     * specifications for which matcing views have been found.
     * If none of the views under the stream matches the first view specification passed in,
     * the method returns the stream itself and leaves the view specification list unchanged.
     * If one view under the stream matches, the view's specification is removed from the list.
     * The method will then attempt to determine if any child views of that view also match
     * specifications.
     * @param rootViewable is the top rootViewable event stream to which all views are attached as child views
     * This parameter is changed by this method, ie. specifications are removed if they match existing views.
     * @param viewFactories is the view specifications for making views
     * @return a pair of (A) the stream if no views matched, or the last child view that matched (B) the full list
     * of parent views
     */
    protected static Pair<Viewable, List<View>> matchExistingViews(Viewable rootViewable,
                                                                   List<ViewFactory> viewFactories)
    {
        Viewable currentParent = rootViewable;
        List<View> matchedViewList = new LinkedList<View>();

        boolean foundMatch;

        if (viewFactories.isEmpty())
        {
            return new Pair<Viewable, List<View>>(rootViewable, new LinkedList<View>());
        }

        do      // while ((foundMatch) && (specifications.size() > 0));
        {
            foundMatch = false;

            for (View childView : currentParent.getViews())
            {
                ViewFactory currentFactory = viewFactories.get(0);

                if (!(currentFactory.canReuse(childView)))
                {
                     continue;
                }

                // The specifications match, check current data window size
                viewFactories.remove(0);
                currentParent = childView;
                foundMatch = true;
                matchedViewList.add(childView);
                break;
            }
        }
        while ((foundMatch) && (!viewFactories.isEmpty()));

        return new Pair<Viewable, List<View>>(currentParent, matchedViewList);
    }

    /**
     * Given a list of view specifications obtained from by parsing this method instantiates a list of view factories.
     * The view factories are not yet aware of each other after leaving this method (so not yet chained logically).
     * They are simply instantiated and assigned view parameters.
     * @param streamNum is the stream number
     * @param viewSpecList is the view definition
     * @param statementContext is statement service context and statement info
     * @return list of view factories
     * @throws ViewProcessingException if the factory cannot be creates such as for invalid view spec
     */
    public static List<ViewFactory> instantiateFactories(int streamNum,
                                                         List<ViewSpec> viewSpecList,
                                                         StatementContext statementContext)
            throws ViewProcessingException
    {
        List<ViewFactory> factoryChain = new ArrayList<ViewFactory>();

        int viewNum = 0;
        for (ViewSpec spec : viewSpecList)
        {
            // Create the new view factory
            ViewFactory viewFactory = statementContext.getViewResolutionService().create(spec.getObjectNamespace(), spec.getObjectName());
            factoryChain.add(viewFactory);

            // Set view factory parameters
            try
            {
                ViewFactoryContext context = new ViewFactoryContext(statementContext, streamNum, viewNum, spec.getObjectNamespace(), spec.getObjectName());
                viewFactory.setViewParameters(context, spec.getObjectParameters());
            }
            catch (ViewParameterException e)
            {
                throw new ViewProcessingException("Error in view '" + spec.getObjectNamespace() + ':' + spec.getObjectName() +
                        "', " + e.getMessage());
            }
            viewNum++;
        }

        return factoryChain;
    }

    private static final Log log = LogFactory.getLog(ViewServiceHelper.class);
}
