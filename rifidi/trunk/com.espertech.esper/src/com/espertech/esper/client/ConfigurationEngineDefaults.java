/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import com.espertech.esper.client.soda.StreamSelector;

import java.io.Serializable;

/**
 * Provides access to engine configuration defaults for modification.
 */
public class ConfigurationEngineDefaults implements Serializable
{
    private Threading threading;
    private ViewResources viewResources;
    private EventMeta eventMeta;
    private Logging logging;
    private Variables variables;
    private StreamSelection streamSelection;
    private TimeSource timeSource;
    private Language language;
    private Expression expression;
    private ConfigurationMetricsReporting metricsReporting;
    private static final long serialVersionUID = -528835191586154300L;

    /**
     * Ctor.
     */
    protected ConfigurationEngineDefaults()
    {
        threading = new Threading();
        viewResources = new ViewResources();
        eventMeta = new EventMeta();
        logging = new Logging();
        variables = new Variables();
        streamSelection = new StreamSelection();
        timeSource = new TimeSource();
        metricsReporting = new ConfigurationMetricsReporting();
        language = new Language();
        expression = new Expression();
    }

    /**
     * Returns threading settings.
     * @return threading settings object
     */
    public Threading getThreading()
    {
        return threading;
    }

    /**
     * Returns view resources defaults.
     * @return view resources defaults
     */
    public ViewResources getViewResources()
    {
        return viewResources;
    }

    /**
     * Returns event representation default settings.
     * @return event representation default settings
     */
    public EventMeta getEventMeta()
    {
        return eventMeta;
    }

    /**
     * Returns logging settings applicable to the engine, other then Log4J settings.
     * @return logging settings
     */
    public Logging getLogging()
    {
        return logging;
    }

    /**
     * Returns engine defaults applicable to variables.
     * @return variable engine defaults
     */
    public Variables getVariables()
    {
        return variables;
    }

    /**
     * Returns engine defaults applicable to streams (insert and remove, insert only or remove only) selected for a statement.
     * @return stream selection defaults
     */
    public StreamSelection getStreamSelection()
    {
        return streamSelection;
    }

    /**
     * Returns the time source configuration.
     * @return time source enum
     */
    public TimeSource getTimeSource()
    {
        return timeSource;
    }

    /**
     * Returns the metrics reporting configuration.
     * @return metrics reporting config
     */
    public ConfigurationMetricsReporting getMetricsReporting()
    {
        return metricsReporting;
    }

    /**
     * Returns the language-related settings for the engine.
     * @return language-related settings
     */
    public Language getLanguage()
    {
        return language;
    }

    /**
     * Returns the expression-related settings for the engine.
     * @return expression-related settings
     */
    public Expression getExpression()
    {
        return expression;
    }

    /**
     * Holds threading settings.
     */
    public static class Threading implements Serializable
    {
        private boolean isListenerDispatchPreserveOrder;
        private long listenerDispatchTimeout;
        private Locking listenerDispatchLocking;

        private boolean isInsertIntoDispatchPreserveOrder;
        private long insertIntoDispatchTimeout;
        private Locking insertIntoDispatchLocking;

        private long internalTimerMsecResolution;
        private boolean internalTimerEnabled;
        private static final long serialVersionUID = 6504606101119059962L;

        private boolean isThreadPoolTimerExec;
        private boolean isThreadPoolInbound;
        private boolean isThreadPoolRouteExec;
        private boolean isThreadPoolOutbound;
        private int threadPoolTimerExecNumThreads;
        private int threadPoolInboundNumThreads;
        private int threadPoolRouteExecNumThreads;
        private int threadPoolOutboundNumThreads;
        private Integer threadPoolTimerExecCapacity;
        private Integer threadPoolInboundCapacity;
        private Integer threadPoolRouteExecCapacity;
        private Integer threadPoolOutboundCapacity;

        /**
         * Ctor - sets up defaults.
         */
        public Threading()
        {
            listenerDispatchTimeout = 1000;
            isListenerDispatchPreserveOrder = true;
            listenerDispatchLocking = Locking.SPIN;

            insertIntoDispatchTimeout = 100;
            isInsertIntoDispatchPreserveOrder = true;
            insertIntoDispatchLocking = Locking.SPIN;

            internalTimerEnabled = true;
            internalTimerMsecResolution = 100;

            isThreadPoolInbound = false;
            isThreadPoolOutbound = false;
            isThreadPoolRouteExec = false;
            isThreadPoolTimerExec = false;

            threadPoolTimerExecNumThreads = 2;
            threadPoolInboundNumThreads = 2;
            threadPoolRouteExecNumThreads = 2;
            threadPoolOutboundNumThreads = 2;
        }

        /**
         * In multithreaded environments, this setting controls whether dispatches to listeners preserve
         * the ordering in which the statement processes events.
         * @param value is true to preserve ordering, or false if not
         */
        public void setListenerDispatchPreserveOrder(boolean value)
        {
            isListenerDispatchPreserveOrder = value;
        }

        /**
         * In multithreaded environments, this setting controls when dispatches to listeners preserve
         * the ordering the timeout to complete any outstanding dispatches.
         * @param value is the timeout in milliseconds that the engine may spend
         * waiting for a listener dispatch to complete before dispatching further
         * results for the same statement to listeners for that statement
         */
        public void setListenerDispatchTimeout(long value)
        {
            listenerDispatchTimeout = value;
        }

        /**
         * In multithreaded environments, this setting controls whether insert-into streams preserve
         * the order of events inserted into them by one or more statements
         * such that statements that consume other statement's events behave deterministic
         * @param value is true to indicate to preserve order, or false to not preserve order
         */
        public void setInsertIntoDispatchPreserveOrder(boolean value)
        {
            isInsertIntoDispatchPreserveOrder = value;
        }

        /**
         * Returns true to indicate preserve order for dispatch to listeners,
         * or false to indicate not to preserve order
         * @return true or false
         */
        public boolean isListenerDispatchPreserveOrder()
        {
            return isListenerDispatchPreserveOrder;
        }

        /**
         * Returns the timeout in millisecond to wait for listener code to complete
         * before dispatching the next result, if dispatch order is preserved
         * @return listener dispatch timeout
         */
        public long getListenerDispatchTimeout()
        {
            return listenerDispatchTimeout;
        }

        /**
         * Returns true to indicate preserve order for inter-statement insert-into,
         * or false to indicate not to preserve order
         * @return true or false
         */
        public boolean isInsertIntoDispatchPreserveOrder()
        {
            return isInsertIntoDispatchPreserveOrder;
        }

        /**
         * Sets the use of internal timer.
         * <p>
         * By setting internal timer to true (the default) the engine starts the internal timer thread
         * and relies on internal timer events to supply the time.
         * <p>
         * By setting internal timer to false the engine does not start the internal timer thread
         * and relies on external application-supplied timer events to supply the time.
         * @param internalTimerEnabled is true for internal timer enabled, or false if the application supplies timer events
         */
        public void setInternalTimerEnabled(boolean internalTimerEnabled)
        {
            this.internalTimerEnabled = internalTimerEnabled;
        }

        /**
         * Returns true if internal timer is enabled (the default), or false for internal timer disabled.
         * @return true for internal timer enabled, false for internal timer disabled
         */
        public boolean isInternalTimerEnabled()
        {
            return internalTimerEnabled;
        }

        /**
         * Returns the millisecond resolutuion of the internal timer thread.
         * @return number of msec between timer processing intervals
         */
        public long getInternalTimerMsecResolution()
        {
            return internalTimerMsecResolution;
        }

        /**
         * Sets the length of the interval (resolution) of the timer thread.
         * @param internalTimerMsecResolution is the millisecond interval length
         */
        public void setInternalTimerMsecResolution(long internalTimerMsecResolution)
        {
            this.internalTimerMsecResolution = internalTimerMsecResolution;
        }

        /**
         * Returns the number of milliseconds that a thread may maximually be blocking
         * to deliver statement results from a producing statement that employs insert-into
         * to a consuming statement.
         * @return millisecond timeout for order-of-delivery blocking between statements
         */
        public long getInsertIntoDispatchTimeout()
        {
            return insertIntoDispatchTimeout;
        }

        /**
         * Sets the blocking strategy to use when multiple threads deliver results for
         * a single statement to listeners, and the guarantee of order of delivery must be maintained.
         * @param listenerDispatchLocking is the blocking technique
         */
        public void setListenerDispatchLocking(Locking listenerDispatchLocking)
        {
            this.listenerDispatchLocking = listenerDispatchLocking;
        }

        /**
         * Sets the number of milliseconds that a thread may maximually be blocking
         * to deliver statement results from a producing statement that employs insert-into
         * to a consuming statement.
         * @param msecTimeout timeout for order-of-delivery blocking between statements
         */
        public void setInsertIntoDispatchTimeout(long msecTimeout)
        {
            this.insertIntoDispatchTimeout = msecTimeout;
        }

        /**
         * Sets the blocking strategy to use when multiple threads deliver results for
         * a single statement to consuming statements of an insert-into, and the guarantee of order of delivery must be maintained.
         * @param insertIntoDispatchLocking is the blocking technique
         */
        public void setInsertIntoDispatchLocking(Locking insertIntoDispatchLocking)
        {
            this.insertIntoDispatchLocking = insertIntoDispatchLocking;
        }

        /**
         * Returns the blocking strategy to use when multiple threads deliver results for
         * a single statement to listeners, and the guarantee of order of delivery must be maintained.
         * @return is the blocking technique
         */
        public Locking getListenerDispatchLocking()
        {
            return listenerDispatchLocking;
        }

        /**
         * Returns the blocking strategy to use when multiple threads deliver results for
         * a single statement to consuming statements of an insert-into, and the guarantee of order of delivery must be maintained.
         * @return is the blocking technique
         */
        public Locking getInsertIntoDispatchLocking()
        {
            return insertIntoDispatchLocking;
        }

        /**
         * Returns true for inbound threading enabled, the default is false for not enabled.
         * @return indicator whether inbound threading is enabled
         */
        public boolean isThreadPoolInbound()
        {
            return isThreadPoolInbound;
        }

        /**
         * Set to true for inbound threading enabled, the default is false for not enabled.
         * @param threadPoolInbound indicator whether inbound threading is enabled
         */
        public void setThreadPoolInbound(boolean threadPoolInbound)
        {
            isThreadPoolInbound = threadPoolInbound;
        }

        /**
         * Returns true for timer execution threading enabled, the default is false for not enabled.
         * @return indicator whether timer execution threading is enabled
         */
        public boolean isThreadPoolTimerExec()
        {
            return isThreadPoolTimerExec;
        }

        /**
         * Set to true for timer execution threading enabled, the default is false for not enabled.
         * @param threadPoolTimerExec indicator whether timer execution threading is enabled
         */
        public void setThreadPoolTimerExec(boolean threadPoolTimerExec)
        {
            isThreadPoolTimerExec = threadPoolTimerExec;
        }

        /**
         * Returns true for route execution threading enabled, the default is false for not enabled.
         * @return indicator whether route execution threading is enabled
         */
        public boolean isThreadPoolRouteExec()
        {
            return isThreadPoolRouteExec;
        }

        /**
         * Set to true for route execution threading enabled, the default is false for not enabled.
         * @param threadPoolRouteExec indicator whether route execution threading is enabled
         */
        public void setThreadPoolRouteExec(boolean threadPoolRouteExec)
        {
            isThreadPoolRouteExec = threadPoolRouteExec;
        }

        /**
         * Returns true for outbound threading enabled, the default is false for not enabled.
         * @return indicator whether outbound threading is enabled
         */
        public boolean isThreadPoolOutbound()
        {
            return isThreadPoolOutbound;
        }

        /**
         * Set to true for outbound threading enabled, the default is false for not enabled.
         * @param threadPoolOutbound indicator whether outbound threading is enabled
         */
        public void setThreadPoolOutbound(boolean threadPoolOutbound)
        {
            isThreadPoolOutbound = threadPoolOutbound;
        }

        /**
         * Returns the number of thread in the inbound threading pool. 
         * @return number of threads
         */
        public int getThreadPoolInboundNumThreads()
        {
            return threadPoolInboundNumThreads;
        }

        /**
         * Sets the number of threads in the thread pool for inbound threading.  
         * @param num number of threads
         */
        public void setThreadPoolInboundNumThreads(int num)
        {
            this.threadPoolInboundNumThreads = num;
        }

        /**
         * Returns the number of thread in the outbound threading pool.
         * @return number of threads
         */
        public int getThreadPoolOutboundNumThreads()
        {
            return threadPoolOutboundNumThreads;
        }

        /**
         * Sets the number of threads in the thread pool for outbound threading.
         * @param num number of threads
         */
        public void setThreadPoolOutboundNumThreads(int num)
        {
            this.threadPoolOutboundNumThreads = num;
        }

        /**
         * Returns the number of thread in the route execution thread pool.
         * @return number of threads
         */
        public int getThreadPoolRouteExecNumThreads()
        {
            return threadPoolRouteExecNumThreads;
        }

        /**
         * Sets the number of threads in the thread pool for route exec threading.
         * @param num number of threads
         */
        public void setThreadPoolRouteExecNumThreads(int num)
        {
            this.threadPoolRouteExecNumThreads = num;
        }

        /**
         * Returns the number of thread in the timer execution threading pool.
         * @return number of threads
         */
        public int getThreadPoolTimerExecNumThreads()
        {
            return threadPoolTimerExecNumThreads;
        }

        /**
         * Sets the number of threads in the thread pool for timer exec threading.  
         * @param num number of threads
         */
        public void setThreadPoolTimerExecNumThreads(int num)
        {
            this.threadPoolTimerExecNumThreads = num;
        }

        /**
         * Returns the capacity of the timer execution queue, or null if none defined (the unbounded case, default).
         * @return capacity or null if none defined
         */
        public Integer getThreadPoolTimerExecCapacity()
        {
            return threadPoolTimerExecCapacity;
        }

        /**
         * Sets the capacity of the timer execution queue, or null if none defined (the unbounded case, default).
         * @param capacity capacity or null if none defined
         */
        public void setThreadPoolTimerExecCapacity(Integer capacity)
        {
            this.threadPoolTimerExecCapacity = capacity;
        }

        /**
         * Returns the capacity of the inbound execution queue, or null if none defined (the unbounded case, default).
         * @return capacity or null if none defined
         */
        public Integer getThreadPoolInboundCapacity()
        {
            return threadPoolInboundCapacity;
        }

        /**
         * Sets the capacity of the inbound queue, or null if none defined (the unbounded case, default).
         * @param capacity capacity or null if none defined
         */
        public void setThreadPoolInboundCapacity(Integer capacity)
        {
            this.threadPoolInboundCapacity = capacity;
        }

        /**
         * Returns the capacity of the route execution queue, or null if none defined (the unbounded case, default).
         * @return capacity or null if none defined
         */
        public Integer getThreadPoolRouteExecCapacity()
        {
            return threadPoolRouteExecCapacity;
        }

        /**
         * Sets the capacity of the route execution queue, or null if none defined (the unbounded case, default).
         * @param capacity capacity or null if none defined
         */
        public void setThreadPoolRouteExecCapacity(Integer capacity)
        {
            this.threadPoolRouteExecCapacity = capacity;
        }

        /**
         * Returns the capacity of the outbound queue, or null if none defined (the unbounded case, default).
         * @return capacity or null if none defined
         */
        public Integer getThreadPoolOutboundCapacity()
        {
            return threadPoolOutboundCapacity;
        }

        /**
         * Sets the capacity of the outbound queue, or null if none defined (the unbounded case, default).
         * @param capacity capacity or null if none defined
         */
        public void setThreadPoolOutboundCapacity(Integer capacity)
        {
            this.threadPoolOutboundCapacity = capacity;
        }

        /**
         * Enumeration of blocking techniques.
         */
        public enum Locking
        {
            /**
             * Spin lock blocking is good for locks held very shortly or generally uncontended locks and
             * is therefore the default.
             */
            SPIN,

            /**
             * Blocking that suspends a thread and notifies a thread to wake up can be
             * more expensive then spin locks.
             */
            SUSPEND
        }
    }

    /**
     * Holds view resources settings.
     */
    public static class ViewResources implements Serializable
    {
        private boolean shareViews;
        private boolean allowMultipleExpiryPolicies;
        private static final long serialVersionUID = 2527853225433208362L;

        /**
         * Ctor - sets up defaults.
         */
        protected ViewResources()
        {
            shareViews = true;
            allowMultipleExpiryPolicies = false;
        }

        /**
         * Returns true to indicate the engine shares view resources between statements, or false
         * to indicate the engine does not share view resources between statements.
         * @return indicator whether view resources are shared between statements if
         * statements share same-views and the engine sees opportunity to reuse an existing view.
         */
        public boolean isShareViews()
        {
            return shareViews;
        }

        /**
         * Set the flag to instruct the engine whether to share view resources between
         * statements for not
         * @param shareViews is true to share view resources between statements, or false to not share view
         * resources between statements declaring same-views
         */
        public void setShareViews(boolean shareViews)
        {
            this.shareViews = shareViews;
        }

        /**
         * By default this setting is false and thereby multiple expiry policies
         * provided by views can only be combined if any of the retain-keywords is also specified for the stream.
         * <p>
         * If set to true then multiple expiry policies are allowed and the following statement compiles without exception:
         * "select * from MyEvent.win:time(10).win:time(10)".
         * @return allowMultipleExpiryPolicies indicator whether to allow combining expiry policies provided by views
         */
        public boolean isAllowMultipleExpiryPolicies()
        {
            return allowMultipleExpiryPolicies;
        }

        /**
         * Set to false (the default) and thereby disallow multiple expiry policies
         * provided by views and only allow if any of the retain-keywords are also specified for the stream.
         * <p>
         * If set to true then multiple expiry policies are allowed and the following statement compiles without exception:
         * "select * from MyEvent.win:time(10).win:time(10)".
         * @param allowMultipleExpiryPolicies indicator whether to allow combining expiry policies provided by views
         */
        public void setAllowMultipleExpiryPolicies(boolean allowMultipleExpiryPolicies)
        {
            this.allowMultipleExpiryPolicies = allowMultipleExpiryPolicies;
        }
    }

    /**
     * Event representation metadata.
     */
    public static class EventMeta implements Serializable
    {
        private Configuration.PropertyResolutionStyle classPropertyResolutionStyle;
        private static final long serialVersionUID = -6091772368103140370L;

        /**
         * Ctor.
         */
        public EventMeta()
        {
            this.classPropertyResolutionStyle = Configuration.PropertyResolutionStyle.getDefault();
        }

        /**
         * Returns the property resolution style to use for resolving property names
         * of Java classes.
         * @return style of property resolution
         */
        public Configuration.PropertyResolutionStyle getClassPropertyResolutionStyle()
        {
            return classPropertyResolutionStyle;
        }

        /**
         * Sets the property resolution style to use for resolving property names
         * of Java classes.
         * @param classPropertyResolutionStyle style of property resolution
         */
        public void setClassPropertyResolutionStyle(Configuration.PropertyResolutionStyle classPropertyResolutionStyle)
        {
            this.classPropertyResolutionStyle = classPropertyResolutionStyle;
        }
    }

    /**
     * Holds view logging settings other then the Apache commons or Log4J settings.
     */
    public static class Logging implements Serializable
    {
        private boolean enableExecutionDebug;
        private boolean enableTimerDebug;
        private static final long serialVersionUID = -8129836306582810327L;

        /**
         * Ctor - sets up defaults.
         */
        protected Logging()
        {
            enableExecutionDebug = false;
            enableTimerDebug = true;
        }

        /**
         * Returns true if execution path debug logging is enabled.
         * <p>
         * Only if this flag is set to true, in addition to LOG4J settings set to DEBUG, does an engine instance,
         * produce debug out.
         * @return true if debug logging through Log4j is enabled for any event processing execution paths
         */
        public boolean isEnableExecutionDebug()
        {
            return enableExecutionDebug;
        }

        /**
         * Set the debug flag for debugging the execution path, in which case the engine logs
         * to Log4j in debug-level during execution.
         * @param enableExecutionDebug false to disable debug logging in the execution path, true to enable
         */
        public void setEnableExecutionDebug(boolean enableExecutionDebug)
        {
            this.enableExecutionDebug = enableExecutionDebug;
        }

        /**
         * Returns true if timer debug level logging is enabled (true by default).
         * <p>
         * Set this value to false to reduce the debug-level logging output for the timer thread(s).
         * For use only when debug-level logging is enabled.
         * @return indicator whether timer execution is noisy in debug or not
         */
        public boolean isEnableTimerDebug()
        {
            return enableTimerDebug;
        }

        /**
         * Set this value to false to reduce the debug-level logging output for the timer thread(s).
         * For use only when debug-level logging is enabled.
         * @param enableTimerDebug indicator whether timer execution is noisy in debug or not (true is noisy)
         */
        public void setEnableTimerDebug(boolean enableTimerDebug)
        {
            this.enableTimerDebug = enableTimerDebug;
        }
    }

    /**
     * Holds variables settings.
     */
    public static class Variables implements Serializable
    {
        private long msecVersionRelease;
        private static final long serialVersionUID = 8276015152830052323L;

        /**
         * Ctor - sets up defaults.
         */
        protected Variables()
        {
            msecVersionRelease = 15000;
        }

        /**
         * Returns the number of milliseconds that a version of a variables is held stable for
         * use by very long-running atomic statement execution.
         * <p>
         * A slow-executing statement such as an SQL join may use variables that, at the time
         * the statement starts to execute, have certain values. The engine guarantees that during
         * statement execution the value of the variables stays the same as long as the statement
         * does not take longer then the given number of milliseconds to execute. If the statement does take longer
         * to execute then the variables release time, the current variables value applies instead.
         * @return millisecond time interval that a variables version is guaranteed to be stable
         * in the context of an atomic statement execution
         */
        public long getMsecVersionRelease()
        {
            return msecVersionRelease;
        }

        /**
         * Sets the number of milliseconds that a version of a variables is held stable for
         * use by very long-running atomic statement execution.
         * @param msecVersionRelease millisecond time interval that a variables version is guaranteed to be stable
         * in the context of an atomic statement execution
         */
        public void setMsecVersionRelease(long msecVersionRelease)
        {
            this.msecVersionRelease = msecVersionRelease;
        }
    }

    /**
     * Holds default settings for stream selection in the select-clause.
     */
    public static class StreamSelection implements Serializable
    {
        private StreamSelector defaultStreamSelector;
        private static final long serialVersionUID = -7943748323859161674L;

        /**
         * Ctor - sets up defaults.
         */
        protected StreamSelection()
        {
            defaultStreamSelector = StreamSelector.ISTREAM_ONLY;
        }

        /**
         * Returns the default stream selector.
         * <p>
         * Statements that select data from streams and that do not use one of the explicit stream
         * selection keywords (istream/rstream/irstream), by default,
         * generate selection results for the insert stream only, and not for the remove stream.
         * <p>
         * This setting can be used to change the default behavior: Use the RSTREAM_ISTREAM_BOTH
         * value to have your statements generate both insert and remove stream results
         * without the use of the "irstream" keyword in the select clause. 
         * @return default stream selector, which is ISTREAM_ONLY unless changed
         */
        public StreamSelector getDefaultStreamSelector()
        {
            return defaultStreamSelector;
        }

        /**
         * Sets the default stream selector.
         * <p>
         * Statements that select data from streams and that do not use one of the explicit stream
         * selection keywords (istream/rstream/irstream), by default,
         * generate selection results for the insert stream only, and not for the remove stream.
         * <p>
         * This setting can be used to change the default behavior: Use the RSTREAM_ISTREAM_BOTH
         * value to have your statements generate both insert and remove stream results
         * without the use of the "irstream" keyword in the select clause.
         * @param defaultStreamSelector default stream selector
         */
        public void setDefaultStreamSelector(StreamSelector defaultStreamSelector)
        {
            this.defaultStreamSelector = defaultStreamSelector;
        }
    }

    /**
     * Time source configuration, the default in MILLI (millisecond resolution from System.currentTimeMillis).
     */
    public static class TimeSource implements Serializable
    {
        private TimeSourceType timeSourceType;
        private static final long serialVersionUID = 2075039404763313824L;

        /**
         * Ctor.
         */
        public TimeSource()
        {
            timeSourceType = TimeSourceType.MILLI;
        }

        /**
         * Returns the time source type.
         * @return time source type enum
         */
        public TimeSourceType getTimeSourceType()
        {
            return timeSourceType;
        }

        /**
         * Sets the time source type.
         * @param timeSourceType time source type enum
         */
        public void setTimeSourceType(TimeSourceType timeSourceType)
        {
            this.timeSourceType = timeSourceType;
        }
    }

    /**
     * Language settings in the engine are for string comparisons.
     */
    public static class Language implements Serializable
    {
        private boolean sortUsingCollator;
        private static final long serialVersionUID = -6237674558477894392L;

        /**
         * Ctor.
         */
        public Language()
        {
            sortUsingCollator = false;
        }

        /**
         * Returns true to indicate to perform locale-independent string comparisons using Collator.
         * <p>
         * By default this setting is false, i.e. string comparisons use the compare method.
         * @return indicator whether to use Collator for string comparisons
         */
        public boolean isSortUsingCollator()
        {
            return sortUsingCollator;
        }

        /**
         * Set to true to indicate to perform locale-independent string comparisons using Collator.
         * <p>
         * Set to false to perform string comparisons via the compare method (the default).
         * @param sortUsingCollator indicator whether to use Collator for string comparisons
         */
        public void setSortUsingCollator(boolean sortUsingCollator)
        {
            this.sortUsingCollator = sortUsingCollator;
        }
    }

    /**
     * Expression evaluation settings in the engine are for results of expressions.
     */
    public static class Expression implements Serializable
    {
        private static final long serialVersionUID = 3192205923560011213L;

        private boolean integerDivision;
        private boolean divisionByZeroReturnsNull;
        private boolean udfCache;
        private boolean selfSubselectPreeval;

        /**
         * Ctor.
         */
        public Expression()
        {
            integerDivision = false;
            divisionByZeroReturnsNull = false;
            udfCache = true;
            selfSubselectPreeval = true;
        }

        /**
         * Returns false (the default) for integer division returning double values.
         * <p>
         * Returns true to signal that Java-convention integer division semantics
         * are used for divisions, whereas the division between two non-FP numbers
         * returns only the whole number part of the result and any fractional part is dropped.
         * @return indicator
         */
        public boolean isIntegerDivision()
        {
            return integerDivision;
        }

        /**
         * Set to false (default) for integer division returning double values.
         * Set to true to signal the Java-convention integer division semantics
         * are used for divisions, whereas the division between two non-FP numbers
         * returns only the whole number part of the result and any fractional part is dropped.
         * @param integerDivision true for integer division returning integer, false (default) for
         */
        public void setIntegerDivision(boolean integerDivision)
        {
            this.integerDivision = integerDivision;
        }

        /**
         * Returns false (default) when division by zero returns Double.Infinity.
         * Returns true when division by zero return null.
         * <p>
         * If integer devision is set, then division by zero for non-FP operands also returns null. 
         * @return indicator for division-by-zero results
         */
        public boolean isDivisionByZeroReturnsNull()
        {
            return divisionByZeroReturnsNull;
        }

        /**
         * Set to false (default) to have division by zero return Double.Infinity.
         * Set to true to have division by zero return null.
         * <p>
         * If integer devision is set, then division by zero for non-FP operands also returns null.
         * @param divisionByZeroReturnsNull indicator for division-by-zero results
         */
        public void setDivisionByZeroReturnsNull(boolean divisionByZeroReturnsNull)
        {
            this.divisionByZeroReturnsNull = divisionByZeroReturnsNull;
        }

        /**
         * By default true, indicates that user-defined functions cache return results
         * if the parameter set is empty or has constant-only return values.
         * @return cache flag
         */
        public boolean isUdfCache()
        {
            return udfCache;
        }

        /**
         * Set to true (the default) to indicate that user-defined functions cache return results
         * if the parameter set is empty or has constant-only return values.
         * @param udfCache cache flag
         */
        public void setUdfCache(boolean udfCache)
        {
            this.udfCache = udfCache;
        }

        /**
         * Set to true (the default) to indicate that sub-selects within a statement are updated first when a new
         * event arrives. This is only relevant for statements in which both subselects
         * and the from-clause may react to the same exact event.
         * @return indicator whether to evaluate sub-selects first or last on new event arrival
         */
        public boolean isSelfSubselectPreeval()
        {
            return selfSubselectPreeval;
        }

        /**
         * Set to true (the default) to indicate that sub-selects within a statement are updated first when a new
         * event arrives. This is only relevant for statements in which both subselects
         * and the from-clause may react to the same exact event.
         * @param selfSubselectPreeval indicator whether to evaluate sub-selects first or last on new event arrival
         */
        public void setSelfSubselectPreeval(boolean selfSubselectPreeval)
        {
            this.selfSubselectPreeval = selfSubselectPreeval;
        }
    }

    /**
     * Time source type.
     */
    public enum TimeSourceType
    {
        /**
         * Millisecond time source type with time originating from System.currentTimeMillis
         */
        MILLI,

        /**
         * Nanosecond time source from a wallclock-adjusted System.nanoTime
         */
        NANO
    }
}
