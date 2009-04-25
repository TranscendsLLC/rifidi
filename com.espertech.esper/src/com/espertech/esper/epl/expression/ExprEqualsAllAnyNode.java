package com.espertech.esper.epl.expression;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.core.MethodResolutionService;
import com.espertech.esper.epl.core.StreamTypeService;
import com.espertech.esper.epl.core.ViewResourceDelegate;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.schedule.TimeProvider;
import com.espertech.esper.util.CoercionException;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.util.SimpleNumberCoercer;
import com.espertech.esper.util.SimpleNumberCoercerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents an equals-for-group (= ANY/ALL/SOME (expression list)) comparator in a expression tree.
 */
public class ExprEqualsAllAnyNode extends ExprNode
{
    private final boolean isNot;
    private final boolean isAll;

    private boolean mustCoerce;
    private SimpleNumberCoercer coercer;
    private boolean hasCollectionOrArray;
    
    /**
     * Ctor.
     * @param isNotEquals - true if this is a (!=) not equals rather then equals, false if its a '=' equals
     * @param isAll - true if all, false for any
     */
    public ExprEqualsAllAnyNode(boolean isNotEquals, boolean isAll)
    {
        this.isNot = isNotEquals;
        this.isAll = isAll;
    }

    /**
     * Returns true if this is a NOT EQUALS node, false if this is a EQUALS node.
     * @return true for !=, false for =
     */
    public boolean isNot()
    {
        return isNot;
    }

    /**
     * True if all.
     * @return all-flag
     */
    public boolean isAll()
    {
        return isAll;
    }

    public void validate(StreamTypeService streamTypeService, MethodResolutionService methodResolutionService, ViewResourceDelegate viewResourceDelegate, TimeProvider timeProvider, VariableService variableService) throws ExprValidationException
    {
        // Must have 2 child nodes
        if (this.getChildNodes().size() < 1)
        {
            throw new IllegalStateException("Equals group node does not have 1 or more child nodes");
        }

        // Must be the same boxed type returned by expressions under this
        Class typeOne = JavaClassHelper.getBoxedType(this.getChildNodes().get(0).getType());

        // collections, array or map not supported
        if ((typeOne.isArray()) || (JavaClassHelper.isImplementsInterface(typeOne, Collection.class)) || (JavaClassHelper.isImplementsInterface(typeOne, Map.class)))
        {
            throw new ExprValidationException("Collection or array comparison is not allowed for the IN, ANY, SOME or ALL keywords");
        }

        List<Class> comparedTypes = new ArrayList<Class>();
        comparedTypes.add(typeOne);
        hasCollectionOrArray = false;
        for (int i = 0; i < this.getChildNodes().size() - 1; i++)
        {
            Class propType = this.getChildNodes().get(i + 1).getType();
            if (propType.isArray())
            {
                hasCollectionOrArray = true;
                if (propType.getComponentType() != Object.class)
                {
                    comparedTypes.add(propType.getComponentType());
                }
            }
            else if (JavaClassHelper.isImplementsInterface(propType, Collection.class))
            {
                hasCollectionOrArray = true;
            }
            else if (JavaClassHelper.isImplementsInterface(propType, Map.class))
            {
                hasCollectionOrArray = true;
            }
            else
            {
                comparedTypes.add(propType);
            }
        }

        // Determine common denominator type
        Class coercionType;
        try {
            coercionType = JavaClassHelper.getCommonCoercionType(comparedTypes.toArray(new Class[comparedTypes.size()]));
        }
        catch (CoercionException ex)
        {
            throw new ExprValidationException("Implicit conversion not allowed: " + ex.getMessage());
        }

        // Check if we need to coerce
        mustCoerce = false;
        if (JavaClassHelper.isNumeric(coercionType))
        {
            for (Class compareType : comparedTypes)
            {
                if (coercionType != JavaClassHelper.getBoxedType(compareType))
                {
                    mustCoerce = true;
                }
            }
            if (mustCoerce)
            {
                coercer = SimpleNumberCoercerFactory.getCoercer(null, JavaClassHelper.getBoxedType(coercionType));
            }
        }
    }

    public boolean isConstantResult()
    {
        return false;
    }

    public Class getType()
    {
        return Boolean.class;
    }

    public Object evaluate(EventBean[] eventsPerStream, boolean isNewData)
    {
        Object leftResult = this.getChildNodes().get(0).evaluate(eventsPerStream, isNewData);

        if (hasCollectionOrArray)
        {
            if (isAll)
            {
                return compareAllColl(leftResult, eventsPerStream, isNewData);
            }
            else
            {
                return compareAnyColl(leftResult, eventsPerStream, isNewData);
            }
        }
        else
        {
            // coerce early if testing without collections
            if ((mustCoerce) && (leftResult != null))
            {
                leftResult = coercer.coerceBoxed((Number) leftResult);
            }

            if (isAll)
            {
                return compareAll(leftResult, eventsPerStream, isNewData);
            }
            else
            {
                return compareAny(leftResult, eventsPerStream, isNewData);
            }
        }
    }

    private Object compareAll(Object leftResult, EventBean[] eventsPerStream, boolean isNewData)
    {
        if (isNot)
        {
            int len = this.getChildNodes().size() - 1;
            if ((len > 0) && (leftResult == null))
            {
                return null;
            }
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult != null)
                {
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (leftResult.equals(rightResult))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (leftResult.equals(right))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    hasNullRow = true;
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return true;
        }
        else
        {
            int len = this.getChildNodes().size() - 1;
            if ((len > 0) && (leftResult == null))
            {
                return null;
            }
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult != null)
                {
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (!leftResult.equals(rightResult))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (!leftResult.equals(right))
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    hasNullRow = true;
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return true;
        }
    }

    private Object compareAllColl(Object leftResult, EventBean[] eventsPerStream, boolean isNewData)
    {
        if (isNot)
        {
            int len = this.getChildNodes().size() - 1;
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult == null)
                {
                    hasNullRow = true;
                    continue;
                }

                if (rightResult instanceof Collection)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Collection coll = (Collection) rightResult;
                    if (coll.contains(leftResult))
                    {
                        return false;
                    }
                    hasNonNullRow = true;
                }
                else if (rightResult instanceof Map)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Map coll = (Map) rightResult;
                    if (coll.containsKey(leftResult))
                    {
                        return false;
                    }
                    hasNonNullRow = true;
                }
                else if (rightResult.getClass().isArray())
                {
                    int arrayLength = Array.getLength(rightResult);
                    for (int index = 0; index < arrayLength; index++)
                    {
                        Object item = Array.get(rightResult, index);
                        if (item == null)
                        {
                            hasNullRow = true;
                            continue;
                        }
                        if (leftResult == null)
                        {
                            return null;
                        }                        
                        hasNonNullRow = true;
                        if (!mustCoerce)
                        {
                            if (leftResult.equals(item))
                            {
                                return false;
                            }
                        }
                        else
                        {
                            if (!(item instanceof Number))
                            {
                                continue;
                            }
                            Number left = coercer.coerceBoxed((Number) leftResult);
                            Number right = coercer.coerceBoxed((Number) item);
                            if (left.equals(right))
                            {
                                return false;
                            }
                        }
                    }
                }
                else
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    if (!mustCoerce)
                    {
                        if (leftResult.equals(rightResult))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (left.equals(right))
                        {
                            return false;
                        }
                    }
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return true;
        }
        else
        {
            int len = this.getChildNodes().size() - 1;
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult == null)
                {
                    hasNullRow = true;
                    continue;
                }

                if (rightResult instanceof Collection)
                {
                    hasNonNullRow = true;
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Collection coll = (Collection) rightResult;
                    if (!coll.contains(leftResult))
                    {
                        return false;
                    }
                }
                else if (rightResult instanceof Map)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Map coll = (Map) rightResult;
                    if (!coll.containsKey(leftResult))
                    {
                        return false;
                    }
                    hasNonNullRow = true;
                }
                else if (rightResult.getClass().isArray())
                {
                    int arrayLength = Array.getLength(rightResult);
                    for (int index = 0; index < arrayLength; index++)
                    {
                        Object item = Array.get(rightResult, index);
                        if (item == null)
                        {
                            hasNullRow = true;
                            continue;
                        }
                        if (leftResult == null)
                        {
                            return null;
                        }
                        hasNonNullRow = true;
                        if (!mustCoerce)
                        {
                            if (!leftResult.equals(item))
                            {
                                return false;
                            }
                        }
                        else
                        {
                            if (!(item instanceof Number))
                            {
                                continue;
                            }
                            Number left = coercer.coerceBoxed((Number) leftResult);
                            Number right = coercer.coerceBoxed((Number) item);
                            if (!left.equals(right))
                            {
                                return false;
                            }
                        }
                    }
                }
                else
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    if (!mustCoerce)
                    {
                        if (!leftResult.equals(rightResult))
                        {
                            return false;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (!left.equals(right))
                        {
                            return false;
                        }
                    }
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return true;
        }
    }

    private Object compareAny(Object leftResult, EventBean[] eventsPerStream, boolean isNewData)
    {
        // Return true on the first not-equal.
        if (isNot)
        {
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            int len = this.getChildNodes().size() - 1;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (leftResult == null)
                {
                    return null;
                }
                if (rightResult == null)
                {
                    hasNullRow = true;
                    continue;
                }

                hasNonNullRow = true;
                if (!mustCoerce)
                {
                    if (!leftResult.equals(rightResult))
                    {
                        return true;
                    }
                }
                else
                {
                    Number right = coercer.coerceBoxed((Number) rightResult);
                    if (!leftResult.equals(right))
                    {
                        return true;
                    }
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return false;
        }
        // Return true on the first equal.
        else
        {
            int len = this.getChildNodes().size() - 1;
            if ((len > 0) && (leftResult == null))
            {
                return null;
            }
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult == null)
                {
                    hasNullRow = true;
                    continue;
                }

                hasNonNullRow = true;
                if (!mustCoerce)
                {
                    if (leftResult.equals(rightResult))
                    {
                        return true;
                    }
                }
                else
                {
                    Number right = coercer.coerceBoxed((Number) rightResult);
                    if (leftResult.equals(right))
                    {
                        return true;
                    }
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return false;
        }
    }

    private Object compareAnyColl(Object leftResult, EventBean[] eventsPerStream, boolean isNewData)
    {
        // Return true on the first not-equal.
        if (isNot)
        {
            int len = this.getChildNodes().size() - 1;
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult == null)
                {
                    hasNullRow = true;
                    continue;
                }

                if (rightResult instanceof Collection)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Collection coll = (Collection) rightResult;
                    if (!coll.contains(leftResult))
                    {
                        return true;
                    }
                    hasNonNullRow = true;
                }
                else if (rightResult instanceof Map)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Map coll = (Map) rightResult;
                    if (!coll.containsKey(leftResult))
                    {
                        return true;
                    }
                    hasNonNullRow = true;
                }
                else if (rightResult.getClass().isArray())
                {
                    int arrayLength = Array.getLength(rightResult);
                    if ((arrayLength > 0) && (leftResult == null))
                    {
                        return null;
                    }

                    for (int index = 0; index < arrayLength; index++)
                    {
                        Object item = Array.get(rightResult, index);
                        if (item == null)
                        {
                            hasNullRow = true;
                            continue;
                        }
                        hasNonNullRow = true;
                        if (!mustCoerce)
                        {
                            if (!leftResult.equals(item))
                            {
                                return true;
                            }
                        }
                        else
                        {
                            if (!(item instanceof Number))
                            {
                                continue;
                            }
                            Number left = coercer.coerceBoxed((Number) leftResult);
                            Number right = coercer.coerceBoxed((Number) item);
                            if (!left.equals(right))
                            {
                                return true;
                            }
                        }
                    }
                }
                else
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (!leftResult.equals(rightResult))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (!left.equals(right))
                        {
                            return true;
                        }
                    }
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return false;
        }
        // Return true on the first equal.
        else
        {
            int len = this.getChildNodes().size() - 1;
            boolean hasNonNullRow = false;
            boolean hasNullRow = false;
            for (int i = 1; i <= len; i++)
            {
                Object rightResult = this.getChildNodes().get(i).evaluate(eventsPerStream, isNewData);

                if (rightResult == null)
                {
                    hasNonNullRow = true;
                    continue;
                }
                if (rightResult instanceof Collection)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    hasNonNullRow = true;
                    Collection coll = (Collection) rightResult;
                    if (coll.contains(leftResult))
                    {
                        return true;
                    }
                }
                else if (rightResult instanceof Map)
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    Map coll = (Map) rightResult;
                    if (coll.containsKey(leftResult))
                    {
                        return true;
                    }
                    hasNonNullRow = true;
                }
                else if (rightResult.getClass().isArray())
                {
                    int arrayLength = Array.getLength(rightResult);
                    if ((arrayLength > 0) && (leftResult == null))
                    {
                        return null;
                    }
                    for (int index = 0; index < arrayLength; index++)
                    {
                        Object item = Array.get(rightResult, index);
                        if (item == null)
                        {
                            hasNullRow = true;
                            continue;
                        }
                        hasNonNullRow = true;
                        if (!mustCoerce)
                        {
                            if (leftResult.equals(item))
                            {
                                return true;
                            }
                        }
                        else
                        {
                            if (!(item instanceof Number))
                            {
                                continue;
                            }
                            Number left = coercer.coerceBoxed((Number) leftResult);
                            Number right = coercer.coerceBoxed((Number) item);
                            if (left.equals(right))
                            {
                                return true;
                            }
                        }
                    }
                }
                else
                {
                    if (leftResult == null)
                    {
                        return null;
                    }
                    hasNonNullRow = true;
                    if (!mustCoerce)
                    {
                        if (leftResult.equals(rightResult))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        Number left = coercer.coerceBoxed((Number) leftResult);
                        Number right = coercer.coerceBoxed((Number) rightResult);
                        if (left.equals(right))
                        {
                            return true;
                        }
                    }
                }
            }

            if ((!hasNonNullRow) || (hasNullRow))
            {
                return null;
            }
            return false;
        }
    }

    public String toExpressionString()
    {
        StringBuilder buffer = new StringBuilder();

        buffer.append(this.getChildNodes().get(0).toExpressionString());
        if (isAll)
        {
            if (isNot)
            {
                buffer.append(" != all");
            }
            else
            {
                buffer.append(" = all");
            }
        }
        else
        {
            if (isNot)
            {
                buffer.append(" != any");
            }
            else
            {
                buffer.append(" = any");
            }
        }
        buffer.append("(");

        String delimiter = ""; 
        for (int i = 0; i < this.getChildNodes().size()-1; i++)
        {
            buffer.append(delimiter);
            buffer.append(this.getChildNodes().get(i + 1).toExpressionString());
            delimiter = ",";
        }
        buffer.append(")");

        return buffer.toString();
    }

    public boolean equalsNode(ExprNode node)
    {
        if (!(node instanceof ExprEqualsAllAnyNode))
        {
            return false;
        }

        ExprEqualsAllAnyNode other = (ExprEqualsAllAnyNode) node;
        return (other.isNot == this.isNot) && (other.isAll == this.isAll);
    }
}
