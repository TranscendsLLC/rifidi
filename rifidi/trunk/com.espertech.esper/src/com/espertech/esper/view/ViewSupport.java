/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view;

import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.util.ExecutionPathDebugLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * A helper class for View implementations that provides generic implementation for some of the methods.
 * Methods that contain the actual logic of the view are not implemented in this class.
 * A common implementation normally does not need to override any of the methods implemented here, their
 * implementation is generic and should suffice.
 * The class provides a convenience method for updateing it's children data updateChildren(Object[], Object[]).
 * This method should be called from within the View.update(Object[], Object[]) methods in the subclasses.
 */
public abstract class ViewSupport implements View
{
    /**
     * Parent viewable to this view - directly accessible by subclasses.
     */
    protected Viewable parent;

    private final ArrayList<View> children;

    /**
     * Constructor.
     */
    protected ViewSupport()
    {
        children = new ArrayList<View>();
    }

    public final Viewable getParent()
    {
        return parent;
    }

    public void setParent(Viewable parent)
    {
        this.parent = parent;
    }

    public final View addView(View view)
    {
        children.add(view);
        view.setParent(this);
        return view;
    }

    public final boolean removeView(View view)
    {
        boolean isRemoved = children.remove(view);
        view.setParent(null);
        return isRemoved;
    }

    public void removeAllViews()
    {
        children.clear();
    }

    public final List<View> getViews()
    {
        return children;
    }

    public final boolean hasViews()
    {
        return (!children.isEmpty());
    }

    /**
     * Updates all the children with new data.
     * Views may want to use the hasViews method on the Viewable interface to determine
     * if there are any child views attached at all, and save the work of constructing the arrays and
     * making the call to updateChildren() in case there aren't any children attached.
     * @param newData is the array of new event data
     * @param oldData is the array of old event data
     */
    public final void updateChildren(EventBean[] newData, EventBean[] oldData)
    {
        int size = children.size();

        // Provide a shortcut for a single child view since this is a very common case.
        // No iteration required here.
        if (size == 0)
        {
            return;
        }
        if (size == 1)
        {
            children.get(0).update(newData, oldData);
        }
        else
        {
            // since there often is zero or one view underneath, the iteration case is slower
            for (View child : children)
            {
                child.update(newData, oldData);
            }
        }
    }

    /**
     * Updates all the children with new data. Static convenience method that accepts the list of child
     * views as a parameter.
     * @param childViews is the list of child views to send the data to
     * @param newData is the array of new event data
     * @param oldData is the array of old event data
     */
    protected static void updateChildren(List<View> childViews, EventBean[] newData, EventBean[] oldData)
    {
        for (View child : childViews)
        {
            child.update(newData, oldData);
        }
    }

    /**
     * Convenience method for logging the parameters passed to the update method. Only logs if debug is enabled.
     * @param prefix is a prefix text to output for each line
     * @param result is the data in an update call
     */
    public static void dumpUpdateParams(String prefix, UniformPair<EventBean[]> result)
    {
        EventBean[] newEventArr = result != null ? result.getFirst() : null;
        EventBean[] oldEventArr = result != null ? result.getSecond() : null;
        dumpUpdateParams(prefix, newEventArr, oldEventArr);
    }

    /**
     * Convenience method for logging the parameters passed to the update method. Only logs if debug is enabled.
     * @param prefix is a prefix text to output for each line
     * @param newData is the new data in an update call
     * @param oldData is the old data in an update call
     */
    public static void dumpUpdateParams(String prefix, Object[] newData, Object[] oldData)
    {
        if (!log.isDebugEnabled())
        {
            return;
        }

        StringWriter buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);
        if (newData == null)
        {
            writer.println(prefix + " newData=null ");
        }
        else
        {
            writer.println(prefix + " newData.size=" + newData.length + "...");
            printObjectArray(prefix, writer, newData);
        }

        if (oldData == null)
        {
            writer.println(prefix + " oldData=null ");
        }
        else
        {
            writer.println(prefix + " oldData.size=" + oldData.length + "...");
            printObjectArray(prefix, writer, oldData);
        }

        if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
        {
            log.debug(".dumpUpdateParams Dumping update parameters..." + buffer.toString());
        }
    }

    private static void printObjectArray(String prefix, PrintWriter writer, Object[] objects)
    {
        int count = 0;
        for (Object object : objects)
        {
            String objectToString = (object == null) ? "null" : object.toString();
            writer.println(prefix + " #" + count + " = " + objectToString);
        }
    }

    /**
     * Convenience method for logging the child views of a Viewable. Only logs if debug is enabled.
     * This is a recursive method.
     * @param prefix is a text to print for each view printed
     * @param parentViewable is the parent for which the child views are displayed.
     */
    public static void dumpChildViews(String prefix, Viewable parentViewable)
    {
        if (log.isDebugEnabled())
        {
            for (View child : parentViewable.getViews())
            {
                if ((ExecutionPathDebugLog.isDebugEnabled) && (log.isDebugEnabled()))
                {
                    log.debug(".dumpChildViews " + prefix + ' ' + child.toString());
                }
                dumpChildViews(prefix + "  ", child);
            }
        }
    }

    /**
     * Find the descendent view in the view tree under the parent view returning the list of view nodes
     * between the parent view and the descendent view. Returns null if the descendent view is not found.
     * Returns an empty list if the descendent view is a child view of the parent view.
     * @param parentView is the view to start searching under
     * @param descendentView is the view to find
     * @return list of Viewable nodes between parent and descendent view.
     */
    public static List<View> findDescendent(Viewable parentView, Viewable descendentView)
    {
        Stack<View> stack = new Stack<View>();

        boolean found;

        for (View view : parentView.getViews())
        {
            if (view == descendentView)
            {
                return stack;
            }

            found = findDescendentRecusive(view, descendentView, stack);

            if (found)
            {
                return stack;
            }
        }

        return null;
    }

    private static boolean findDescendentRecusive(View parentView, Viewable descendentView, Stack<View> stack)
    {
        stack.push(parentView);

        boolean found = false;
        for (View view : parentView.getViews())
        {
            if (view == descendentView)
            {
                return true;
            }

            found = findDescendentRecusive(view, descendentView, stack);

            if (found)
            {
                break;
            }
        }

        if (!found)
        {
            stack.pop();
            return false;
        }

        return true;
    }

    private static final Log log = LogFactory.getLog(ViewSupport.class);
}
