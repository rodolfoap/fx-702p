package com.fx702p.interpreters;

import static com.fx702p.emulator.Fx702pConstants.ARRAY_COUNT_MAX;
import static com.fx702p.emulator.Fx702pConstants.ARRAY_FIRST_DIMENSION_MAX;
import static com.fx702p.emulator.Fx702pConstants.ARRAY_SECOND_DIMENSION_MAX;
import static com.fx702p.emulator.Fx702pConstants.BIG_PI;
import static com.fx702p.emulator.Fx702pConstants.DMS_MAX;
import static com.fx702p.emulator.Fx702pConstants.FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE;
import static com.fx702p.emulator.Fx702pConstants.HALF;
import static com.fx702p.emulator.Fx702pConstants.HYPERBOLIC_MAX_ARGUMENT;
import static com.fx702p.emulator.Fx702pConstants.MATH_CONTEXT;
import static com.fx702p.emulator.Fx702pConstants.NORMAL_FORMATTER;
import static com.fx702p.emulator.Fx702pConstants.SIXTY;
import static com.fx702p.emulator.Fx702pConstants.SQUARED_SIXTY;
import static com.fx702p.emulator.Fx702pConstants.X_INDEX;
import static com.fx702p.emulator.Fx702pConstants.Y_INDEX;

import java.io.*;
import java.math.*;
import java.util.*;

import com.fx702p.emulator.*;
import com.fx702p.emulator.exceptions.*;
import com.fx702p.parser.*;

public abstract class Fx702pAbstractCalculator extends Fx702pAbstractInterpreter implements Fx702pParserVisitor
{
  public Fx702pAbstractCalculator(Fx702pEmulator anEmulator)
  {
    super(anEmulator);
  }

  @Override
  public void lastAnswer()
  {
    String formattedAnswer = NORMAL_FORMATTER.format(getMemory().getLastResult());
    startEnteringString(formattedAnswer);
  }

  protected void setResult(BigDecimal aResult)
  {
    getMemory().setLastResult(aResult);
  }

  protected BigDecimal toBigDecimal(Object aValue)
  {
    if (aValue instanceof BigDecimal)
    {
      return (BigDecimal)aValue;
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  protected String formatNumber(BigDecimal aNumber)
  {
    getMemory().getFormatter().setRoundingMode(getMemory().getRoundingMode());
    return getMemory().getFormatter().format(aNumber);
  }

  protected boolean isInteger(BigDecimal aNumber)
  {
    return aNumber.scale() >= 0;
  }

  protected int toInteger(BigDecimal aNumber)
  {
    return aNumber.intValue();
  }

  protected Variable getVariable(Node aNode)
  {
    if (aNode instanceof ASTDollar)
    {
      return getMemory().getDollarVariable();
    }
    else if (aNode instanceof ASTVariable)
    {
      if (aNode.jjtGetNumChildren() == 0)
      {
        return getNormalVariable(aNode);
      }
      else
      {
        return getArrayVariable(aNode);
      }
    }
    else if (aNode instanceof ASTIndexedVariable)
    {
      return getIndexedVariable(aNode);
    }
    else
    {
      throw new Fx702pInternalError("Invalid variable type: " + aNode.getClass().getName());
    }
  }

  protected Variable getNormalVariable(Node aNode)
  {
    String name = ((ASTVariable)aNode).name;
    if (name.length() != 1)
    {
      throw new Fx702pInternalError("Invalid variable name:" + name);
    }
    int index = Character.getNumericValue(name.charAt(0)) - FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE;
    if (((ASTVariable)aNode).isString)
    {
      return getMemory().getVariable(index).getStringVariable();
    }
    else
    {
      return getMemory().getVariable(index).getNumberVariable();
    }
  }

  protected Variable getIndexedVariable(Node aNode)
  {
    String name = ((ASTIndexedVariable)aNode).name;
    if (name.length() != 2)
    {
      throw new Fx702pInternalError("Invalid variable name:" + name);
    }
    int index1 = Character.getNumericValue(name.charAt(0)) - FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE;
    int index2 = Character.digit(name.charAt(1), 10);

    int index = index1 * 10 + index2;

    if (((ASTIndexedVariable)aNode).isString)
    {
      return getMemory().getArrayVariable(index).getStringVariable();
    }
    else
    {
      return getMemory().getArrayVariable(index).getNumberVariable();
    }
  }

  protected int getArrayIndex(Object aValue)
  {
    if (aValue instanceof BigDecimal)
    {
      return ((BigDecimal)aValue).intValue();
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  protected Variable getArrayVariable(Node aNode)
  {
    String name = ((ASTVariable)aNode).name;
    if (!name.equals("A"))
    {
      throw new Fx702pInternalError("Invalid array name:" + name);
    }
    int index;
    if (aNode.jjtGetNumChildren() == 1)
    {
      SimpleNode expression = (SimpleNode)aNode.jjtGetChild(0);
      Object value = expression.jjtAccept(this, null);
      index = getArrayIndex(value);

      if (index < 0 || index >= ARRAY_COUNT_MAX)
      {
        throw new Fx702pErr5Exception(this);
      }
    }
    else if (aNode.jjtGetNumChildren() == 2)
    {
      SimpleNode expression1 = (SimpleNode)aNode.jjtGetChild(0);
      Object value1 = expression1.jjtAccept(this, null);
      int index1 = getArrayIndex(value1);
      if (index1 < 0 || index1 >= ARRAY_FIRST_DIMENSION_MAX)
      {
        throw new Fx702pErr5Exception(this);
      }

      SimpleNode expression2 = (SimpleNode)aNode.jjtGetChild(1);
      Object value2 = expression2.jjtAccept(this, null);
      int index2 = getArrayIndex(value2);
      if (index2 < 0 || index2 >= ARRAY_SECOND_DIMENSION_MAX)
      {
        throw new Fx702pErr5Exception(this);
      }

      index = index1 * 10 + index2;
    }
    else
    {
      throw new Fx702pInternalError("Too many children for an array: " + aNode.jjtGetNumChildren());
    }

    if (((ASTVariable)aNode).isString)
    {
      return getMemory().getArrayVariable(index).getStringVariable();
    }
    else
    {
      return getMemory().getArrayVariable(index).getNumberVariable();
    }
  }


  public Object visit(ASTAssignment aNode, Object aData)
  {
    SimpleNode expression = (SimpleNode)aNode.jjtGetChild(1);
    Object value = expression.jjtAccept(this, aData);
    getVariable(aNode.jjtGetChild(0)).setValue(value);

    return null;
  }

  public Object visit(ASTAddition aNode, Object aData)
  {
    Object left = aNode.jjtGetChild(0).jjtAccept(this, aData);
    Object right = aNode.jjtGetChild(1).jjtAccept(this, aData);
    if (left instanceof BigDecimal && right instanceof BigDecimal)
    {
      return ((BigDecimal)left).add((BigDecimal)right);
    }
    else if (left instanceof String && right instanceof String)
    {
      return (String)left + (String)right;
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  public Object visit(ASTSubstraction aNode, Object aData)
  {
    Object left = aNode.jjtGetChild(0).jjtAccept(this, aData);
    Object right = aNode.jjtGetChild(1).jjtAccept(this, aData);
    if (left instanceof BigDecimal && right instanceof BigDecimal)
    {
      return ((BigDecimal)left).subtract((BigDecimal)right);
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  public Object visit(ASTMultiplication aNode, Object aData)
  {
    Object left = aNode.jjtGetChild(0).jjtAccept(this, aData);
    Object right = aNode.jjtGetChild(1).jjtAccept(this, aData);
    if (left instanceof BigDecimal && right instanceof BigDecimal)
    {
      return ((BigDecimal)left).multiply((BigDecimal)right);
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  public Object visit(ASTDivision aNode, Object aData)
  {
    Object left = aNode.jjtGetChild(0).jjtAccept(this, aData);
    Object right = aNode.jjtGetChild(1).jjtAccept(this, aData);
    if (left instanceof BigDecimal && right instanceof BigDecimal)
    {
      return ((BigDecimal)left).divide((BigDecimal)right, MATH_CONTEXT);
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  public Object visit(ASTUnaryMinus aNode, Object aData)
  {
    Object child = aNode.jjtGetChild(0).jjtAccept(this, aData);
    if (child instanceof BigDecimal)
    {
      return ((BigDecimal)child).negate();
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  public Object visit(ASTPower aNode, Object aData)
  {
    Object left = aNode.jjtGetChild(0).jjtAccept(this, aData);
    Object right = aNode.jjtGetChild(1).jjtAccept(this, aData);
    if (left instanceof BigDecimal && right instanceof BigDecimal)
    {
      return new BigDecimal(Math.pow(((BigDecimal)left).doubleValue(), ((BigDecimal)right).doubleValue()));
    }
    else
    {
      throw new Fx702pErr2Exception(this);
    }
  }

  public Object visit(ASTFactorial aNode, Object aData)
  {
    BigDecimal d = toBigDecimal(aNode.jjtGetChild(0).jjtAccept(this, aData));
    if (isInteger(d))
    {
      int n = toInteger(d);
      if (n < 0 || n > 69)
      {
        throw new Fx702pErr3Exception(this);
      }
      else
      {
        BigDecimal f = BigDecimal.ONE;
        for (int i = 1; i <= n; i++)
        {
          f = f.multiply(new BigDecimal(i));
        }
        return f;
      }
    }
    else
    {
      throw new Fx702pErr3Exception(this);
    }
  }

  public Object visit(ASTFunctionCall aNode, Object aData)
  {
    Node function = aNode.jjtGetChild(0);
    if (aNode.jjtGetNumChildren() == 2 && aNode.jjtGetChild(1).jjtGetNumChildren() > 0)
    {
      Node functionArguments = aNode.jjtGetChild(1);
      Object arguments[] = new Object[functionArguments.jjtGetNumChildren()];
      for (int i = 0, last = functionArguments.jjtGetNumChildren(); i < last; i++)
      {
        arguments[i] = functionArguments.jjtGetChild(i).jjtAccept(this, aData);
      }
      return function.jjtAccept(this, arguments);
    }
    else
    {
      return function.jjtAccept(this, new Object[0]);
    }
  }

  @SuppressWarnings("unchecked")
  protected void checkFunctionArguments(Object[] theArguments, Class... theClasses)
  {
    if (theArguments.length != theClasses.length)
    {
      throw new Fx702pErr2Exception(this);
    }
    for (int i = 0, last = theArguments.length; i < last; i++)
    {
      if (!theClasses[i].isAssignableFrom(theArguments[i].getClass()))
      {
        throw new Fx702pErr2Exception(this);
      }
    }
  }

  public Object visit(ASTRandom aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    return new BigDecimal(Math.random());
  }

  public Object visit(ASTCnt aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    return getMemory().getStatCounter();
  }

  public Object visit(ASTSdx aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sqrt((getMemory().getSumX2().subtract(getMemory().getSumX().multiply(getMemory().getSumX()).divide(getMemory().getStatCounter(), MATH_CONTEXT)).divide(getMemory().getStatCounter().subtract(BigDecimal.ONE), MATH_CONTEXT).doubleValue())));
    }
  }

  public Object visit(ASTSdy aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sqrt((getMemory().getStatCounter().multiply(getMemory().getSumY2()).subtract(getMemory().getSumY().multiply(getMemory().getSumY())).multiply(getMemory().getStatCounter()).divide(getMemory().getStatCounter().subtract(BigDecimal.ONE), MATH_CONTEXT).doubleValue()) / getMemory().getStatCounter().intValue()));
    }
  }

  public Object visit(ASTSdxn aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sqrt(getMemory().getStatCounter().multiply(getMemory().getSumX2()).subtract(getMemory().getSumX().multiply(getMemory().getSumX())).doubleValue()) / getMemory().getStatCounter().intValue());
    }
  }

  public Object visit(ASTSdyn aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sqrt(getMemory().getStatCounter().multiply(getMemory().getSumY2()).subtract(getMemory().getSumY().multiply(getMemory().getSumY())).doubleValue()) / getMemory().getStatCounter().intValue());
    }
  }

  public Object visit(ASTMx aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return getMemory().getSumX().divide(getMemory().getStatCounter(), MATH_CONTEXT);
    }
  }

  public Object visit(ASTMy aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return getMemory().getSumY().divide(getMemory().getStatCounter(), MATH_CONTEXT);
    }
  }

  public Object visit(ASTSx aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      return BigDecimal.ZERO;
    }
    else
    {
      return getMemory().getSumX();
    }
  }

  public Object visit(ASTSy aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      return BigDecimal.ZERO;
    }
    else
    {
      return getMemory().getSumY();
    }
  }

  public Object visit(ASTSx2 aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      return BigDecimal.ZERO;
    }
    else
    {
      return getMemory().getSumX2();
    }
  }

  public Object visit(ASTSy2 aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      return BigDecimal.ZERO;
    }
    else
    {
      return getMemory().getSumY2();
    }
  }

  public Object visit(ASTSxy aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().compareTo(BigDecimal.ZERO) == 0)
    {
      return BigDecimal.ZERO;
    }
    else
    {
      return getMemory().getSumXY();
    }
  }

  public Object visit(ASTLra aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      BigDecimal b = getMemory().getStatCounter().multiply(getMemory().getSumXY()).subtract(getMemory().getSumX().multiply(getMemory().getSumY())).divide(getMemory().getStatCounter().multiply(getMemory().getSumX2()).subtract(getMemory().getSumX().multiply(getMemory().getSumX())), MATH_CONTEXT);
      return getMemory().getSumY().subtract(b.multiply(getMemory().getSumX())).divide(getMemory().getStatCounter(), MATH_CONTEXT);
    }
  }

  public Object visit(ASTLrb aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return getMemory().getStatCounter().multiply(getMemory().getSumXY()).subtract(getMemory().getSumX().multiply(getMemory().getSumY())).divide(getMemory().getStatCounter().multiply(getMemory().getSumX2()).subtract(getMemory().getSumX().multiply(getMemory().getSumX())), MATH_CONTEXT);
    }
  }

  public Object visit(ASTCor aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return getMemory().getStatCounter().multiply(getMemory().getSumXY()).subtract(getMemory().getSumX().multiply(getMemory().getSumY())).divide(new BigDecimal(Math.sqrt(getMemory().getStatCounter().multiply(getMemory().getSumX2()).subtract(getMemory().getSumX().multiply(getMemory().getSumX())).multiply(getMemory().getStatCounter().multiply(getMemory().getSumY2()).subtract(getMemory().getSumY().multiply(getMemory().getSumY())), MATH_CONTEXT).doubleValue())));
    }
  }

  public Object visit(ASTEox aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      BigDecimal value = (BigDecimal)((Object[])aData)[0];
      BigDecimal b = getMemory().getStatCounter().multiply(getMemory().getSumXY()).subtract(getMemory().getSumX().multiply(getMemory().getSumY())).divide(getMemory().getStatCounter().multiply(getMemory().getSumX2()).subtract(getMemory().getSumX().multiply(getMemory().getSumX())), MATH_CONTEXT);
      if (b.compareTo(BigDecimal.ZERO) == 0)
      {
        throw new Fx702pErr3Exception(this);
      }
      BigDecimal a = getMemory().getSumY().subtract(b.multiply(getMemory().getSumX())).divide(getMemory().getStatCounter(), MATH_CONTEXT);
      return value.subtract(a).divide(b, MATH_CONTEXT);
    }
  }

  public Object visit(ASTEoy aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    if (getMemory().getStatCounter().intValue() <= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      BigDecimal value = (BigDecimal)((Object[])aData)[0];
      BigDecimal b = getMemory().getStatCounter().multiply(getMemory().getSumXY()).subtract(getMemory().getSumX().multiply(getMemory().getSumY())).divide(getMemory().getStatCounter().multiply(getMemory().getSumX2()).subtract(getMemory().getSumX().multiply(getMemory().getSumX())), MATH_CONTEXT);
      BigDecimal a = getMemory().getSumY().subtract(b.multiply(getMemory().getSumX())).divide(getMemory().getStatCounter(), MATH_CONTEXT);
      return b.multiply(value).add(a);
    }
  }

  public Object visit(ASTKey aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData);
    Character key = getKey();
    if (key == null)
    {
      return "";
    }
    else
    {
      return "" + key;
    }
  }

  public Object visit(ASTSin aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) >= getMemory().getTrigonometricMaxArgument())
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sin(value.doubleValue() * getMemory().getTrigonometricConversionFactor()));
    }
  }

  public Object visit(ASTCos aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) >= getMemory().getTrigonometricMaxArgument())
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.cos(value.doubleValue() * getMemory().getTrigonometricConversionFactor()));
    }
  }

  public Object visit(ASTTan aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) >= getMemory().getTrigonometricMaxArgument())
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.tan(value.doubleValue() * getMemory().getTrigonometricConversionFactor()));
    }
  }

  public Object visit(ASTArcSin aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) > 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.asin(value.doubleValue()) / getMemory().getTrigonometricConversionFactor());
    }
  }

  public Object visit(ASTArcCos aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) > 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.acos(value.doubleValue()) / getMemory().getTrigonometricConversionFactor());
    }
  }

  public Object visit(ASTArcTan aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    return new BigDecimal(Math.atan(value.doubleValue()) / getMemory().getTrigonometricConversionFactor());
  }

  public Object visit(ASTHyperbolicSin aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) > HYPERBOLIC_MAX_ARGUMENT)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sinh(value.doubleValue()));
    }
  }

  public Object visit(ASTHyperbolicCos aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (Math.abs(value.doubleValue()) > HYPERBOLIC_MAX_ARGUMENT)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.cosh(value.doubleValue()));
    }
  }

  public Object visit(ASTHyperbolicTan aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    return new BigDecimal(Math.tanh(value.doubleValue()));
  }

  public Object visit(ASTArcHyperbolicSin aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    return new BigDecimal(Math.log(d + Math.sqrt(d * d + 1)));
  }

  public Object visit(ASTArcHyperbolicCos aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    if (d < 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.log(d + Math.sqrt(d * d - 1)));
    }
  }

  public Object visit(ASTArcHyperbolicTan aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    if (Math.abs(d) >= 1)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.log((1 + d) / (1 - d)) / 2);
    }
  }

  public Object visit(ASTSqrt aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    if (d < 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.sqrt(d));
    }
  }

  public Object visit(ASTExp aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    if (Math.abs(d) > HYPERBOLIC_MAX_ARGUMENT)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.exp(d));
    }
  }

  public Object visit(ASTLn aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    if (d < 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.log(d));
    }
  }

  public Object visit(ASTLog aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    double d = value.doubleValue();
    if (d < 0)
    {
      throw new Fx702pErr3Exception(this);
    }
    else
    {
      return new BigDecimal(Math.log10(d));
    }
  }

  protected BigDecimal integerPart(BigDecimal aNumber)
  {
    return new BigDecimal(aNumber.toBigInteger());
  }

  protected BigDecimal fractionalPart(BigDecimal aNumber)
  {
    return aNumber.subtract(integerPart(aNumber));
  }

  public Object visit(ASTInt aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    return integerPart(value);
  }

  public Object visit(ASTFrac aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    return fractionalPart(value);
  }

  public Object visit(ASTAbs aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    return value.abs();
  }

  public Object visit(ASTSign aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    if (value.compareTo(BigDecimal.ZERO) == 0)
    {
      return 0;
    }
    else if (value.doubleValue() > 0)
    {
      return 1;
    }
    else
    {
      return -1;
    }
  }

  public Object visit(ASTDMS aNode, Object aData)
  {
    checkCommandArguments(1, aData);
    Object value = ((Node)aData).jjtGetChild(0).jjtAccept(this, null);
    if (value instanceof BigDecimal)
    {
      BigDecimal dms = (BigDecimal)value;
      if (Math.abs(dms.doubleValue()) >= DMS_MAX)
      {
        return new DMSResult(formatNumber(dms), dms);
      }
      else
      {
        BigDecimal fracDms = fractionalPart(dms);
        int degrees = integerPart(dms).intValue();
        int minutes = integerPart(fracDms.multiply(SIXTY)).intValue();
        BigDecimal seconds = fractionalPart(fracDms.multiply(SIXTY)).multiply(SIXTY);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        printStream.printf(Locale.US, " %do%d'%.2f\"", degrees, minutes, seconds);
        return new DMSResult(byteArrayOutputStream.toString(), dms);
      }
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
  }

  public Object visit(ASTLength aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, String.class);
    return new BigDecimal(((String)((Object[])aData)[0]).length());
  }

  public Object visit(ASTMid aNode, Object aData)
  {
    if (aData instanceof Object[])
    {
      Object[] arguments = (Object[])aData;
      if (arguments.length == 1)
      {
        Object value = arguments[0];
        if (value instanceof BigDecimal)
        {
          int start = ((BigDecimal)value).intValue();
          if (start > ((String)getMemory().getDollarVariable().getValue()).length() || start < 1)
          {
            throw new Fx702pErr5Exception(this);
          }
          else
          {
            return ((String)getMemory().getDollarVariable().getValue()).substring((start - 1));
          }
        }
        else
        {
          throw new Fx702pErr2Exception();
        }
      }
      else if (arguments.length == 2)
      {
        Object value1 = arguments[0];
        Object value2 = arguments[1];
        if (value1 instanceof BigDecimal && value2 instanceof BigDecimal)
        {
          int start = ((BigDecimal)value1).intValue();
          int length = ((BigDecimal)value2).intValue();
          if (start + length - 1 > ((String)getMemory().getDollarVariable().getValue()).length() || start < 1 || length <= 0)
          {
            throw new Fx702pErr5Exception(this);
          }
          else
          {
            return ((String)getMemory().getDollarVariable().getValue()).substring((start - 1), (start - 1) + length);
          }
        }
        else
        {
          throw new Fx702pErr2Exception();
        }
      }
      else
      {
        throw new Fx702pErr2Exception();
      }
    }
    else
    {
      throw new Fx702pInternalError("Data should be a Object[]");
    }
  }

  public Object visit(ASTRound aNode, Object aData)
  {
    checkFunctionArguments((Object[])aData, BigDecimal.class, BigDecimal.class);
    BigDecimal value = (BigDecimal)((Object[])aData)[0];
    BigDecimal round = (BigDecimal)((Object[])aData)[1];

    int factorExponent = round.intValue() + 1;
    BigDecimal factor = null;
    if (factorExponent >= 0)
    {
      factor = BigDecimal.TEN.pow(factorExponent);
    }
    else
    {
      factor = BigDecimal.ONE.divide(BigDecimal.TEN.pow(-factorExponent));
    }
    value = value.divide(factor, MATH_CONTEXT);
    if (value.signum() > 0)
    {
      value = value.add(HALF);
    }
    else if (value.signum() < 0)
    {
      value = value.subtract(HALF);
    }
    value = new BigDecimal(value.toBigInteger());
    return value.multiply(factor);
  }

  public Object visit(ASTInteger aNode, Object aData)
  {
    return aNode.value;
  }

  public Object visit(ASTFloat aNode, Object aData)
  {
    return aNode.value;
  }

  public Object visit(ASTString aNode, Object aData)
  {
    return aNode.value;
  }

  public Object visit(ASTPi aNode, Object aData)
  {
    return BIG_PI;
  }

  public Object visit(ASTVariable aNode, Object aData)
  {
    return getVariable(aNode).getValue();
  }

  public Object visit(ASTIndexedVariable aNode, Object aData)
  {
    return getIndexedVariable(aNode).getValue();
  }

  public Object visit(ASTDollar aNode, Object aData)
  {
    return getMemory().getDollarVariable().getValue();
  }

  public Object visit(ASTFunctionArguments aNode, Object aData)
  {
    throw new Fx702pInternalError("Visiting ASTFunctionArguments");
  }

  public Object visit(ASTFunctionWithParenthesisArguments aNode, Object aData)
  {
    throw new Fx702pInternalError("Visiting ASTFunctionWithParenthesisArguments");
  }

  protected void checkCommandArguments(int anArgumentCount, Object aData)
  {
    if (anArgumentCount == 0 && (aData == null || (aData instanceof Node && ((Node)aData).jjtGetNumChildren() == 0)))
    {
      return;
    }
    else if (anArgumentCount != 0 && aData != null && aData instanceof Node && ((Node)aData).jjtGetNumChildren() == anArgumentCount)
    {
      return;
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
  }


  public Object visit(ASTSet aNode, Object aData)
  {
    if (aNode.format.equals("0"))
    {
      getMemory().setRoundingMode(RoundingMode.DOWN);
    }
    else if (aNode.format.equals("5"))
    {
      getMemory().setRoundingMode(RoundingMode.HALF_DOWN);
    }
    else if (aNode.format.length() == 1 && aNode.format.charAt(0) == 'N')
    {
      getMemory().setFormatter(NORMAL_FORMATTER);
    }
    else if (aNode.format.length() == 2 && aNode.format.charAt(0) == 'F' && Character.isDigit(aNode.format.charAt(1)))
    {
      int precision = Character.digit(aNode.format.charAt(1), 10);
      getMemory().setFormatter(new FixedFormatter(precision));
    }
    else if (aNode.format.length() == 2 && aNode.format.charAt(0) == 'E' && Character.isDigit(aNode.format.charAt(1)))
    {
      int precision = Character.digit(aNode.format.charAt(1), 10);
      getMemory().setFormatter(new ScientificFormatter(precision));
    }
    return null;
  }

  public Object visit(ASTCommand aNode, Object aData)
  {
    return aNode.jjtGetChild(0).jjtAccept(this, aNode.jjtGetChild(1));
  }

  public Object visit(ASTStop aNode, Object aData)
  {
    // fx702pIO.waitForContinueKey();
    return null;
  }

  public Object visit(ASTEnd aNode, Object aData)
  {
    checkCommandArguments(0, aData);
    return new End();
  }

  public Object visit(ASTVac aNode, Object aData)
  {
    getMemory().clearAllVariables();
    return null;
  }

  public Object visit(ASTSac aNode, Object aData)
  {
    sac();
    return null;
  }

  protected String stat(BigDecimal x, BigDecimal y)
  {
    getMemory().setStatVariables(getMemory().getStatCounter().add(BigDecimal.ONE), getMemory().getSumX().add(x), getMemory().getSumY().add(y), getMemory().getSumX2().add(x.multiply(x)), getMemory().getSumY2().add(y.multiply(y)), getMemory().getSumXY().add(x.multiply(y)));
    return null;
  }

  protected String del(BigDecimal x, BigDecimal y)
  {
    getMemory().setStatVariables(getMemory().getStatCounter().subtract(BigDecimal.ONE), getMemory().getSumX().subtract(x), getMemory().getSumY().subtract(y), getMemory().getSumX2().subtract(x.multiply(x)), getMemory().getSumY2().subtract(y.multiply(y)), getMemory().getSumXY().subtract(x.multiply(y)));
    return null;
  }

  public Object visit(ASTStat aNode, Object aData)
  {
    if (aData instanceof Node)
    {
      StatisticsArguments arguments = getStatisticsArgument((Node)aData);
      if (arguments != null)
      {
        return stat(arguments.x, arguments.y);
      }
      else
      {
        throw new Fx702pErr2Exception();
      }
    }
    else
    {
      throw new Fx702pInternalError("Data should be a Node");
    }
  }

  public Object visit(ASTDel aNode, Object aData)
  {
    if (aData instanceof Node)
    {
      StatisticsArguments arguments = getStatisticsArgument((Node)aData);
      if (arguments != null)
      {
        return del(arguments.x, arguments.y);
      }
      else
      {
        throw new Fx702pErr2Exception();
      }
    }
    else
    {
      throw new Fx702pInternalError("Data should be a Node");
    }
  }

  protected StatisticsArguments getStatisticsArgument(Node aNode)
  {
    try
    {
      if (aNode.jjtGetNumChildren() == 0)
      {
        return null;
      }
      else if (aNode.jjtGetNumChildren() == 1)
      {
        Object x = aNode.jjtGetChild(0).jjtAccept(this, null);
        if (x != null)
        {
          if (x instanceof BigDecimal)
          {
            BigDecimal y = getMemory().getLastY();
            if (y == null)
            {
              y = BigDecimal.ZERO;
            }

            return new StatisticsArguments((BigDecimal)x, y);
          }
        }
        reportError(new Fx702pErr2Exception(this));
      }
      else if (aNode.jjtGetNumChildren() == 2)
      {
        Object x = aNode.jjtGetChild(0).jjtAccept(this, null);
        if (x != null)
        {
          if (x instanceof BigDecimal)
          {
            Object y = aNode.jjtGetChild(1).jjtAccept(this, null);
            if (y != null)
            {
              if (y instanceof BigDecimal)
              {
                getMemory().setLastY((BigDecimal)y);
                return new StatisticsArguments((BigDecimal)x, (BigDecimal)y);
              }
            }
          }
        }
        reportError(new Fx702pErr2Exception(this));
      }
      else
      {
        reportError(new Fx702pErr2Exception(this));
      }
    }
    catch (ArithmeticException exception)
    {
      reportError(new Fx702pErr3Exception(this));
    }
    catch (Fx702pException exception)
    {
      reportError(exception);
    }

    return null;
  }

  public Object visit(ASTRPC aNode, Object aData)
  {
    checkCommandArguments(2, aData);
    Object value1 = ((Node)aData).jjtGetChild(0).jjtAccept(this, null);
    Object value2 = ((Node)aData).jjtGetChild(1).jjtAccept(this, null);

    if (value1 instanceof BigDecimal && value2 instanceof BigDecimal)
    {
      if (((BigDecimal)value1).compareTo(BigDecimal.ZERO) == 0 && ((BigDecimal)value2).compareTo(BigDecimal.ZERO) == 0)
      {
        throw new Fx702pErr3Exception(this);
      }

      double x = ((BigDecimal)value1).doubleValue();
      double y = ((BigDecimal)value2).doubleValue();
      double r = Math.sqrt(x * x + y * y);
      double t = Math.acos(x / r) / getMemory().getTrigonometricConversionFactor();

      getMemory().getVariable(X_INDEX).getNumberVariable().setValue(new BigDecimal(r));
      getMemory().getVariable(Y_INDEX).getNumberVariable().setValue(new BigDecimal(t));
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
    return null;
  }

  public Object visit(ASTPRC aNode, Object aData)
  {
    checkCommandArguments(2, aData);
    Object value1 = ((Node)aData).jjtGetChild(0).jjtAccept(this, null);
    Object value2 = ((Node)aData).jjtGetChild(1).jjtAccept(this, null);

    if (value1 instanceof BigDecimal && value2 instanceof BigDecimal)
    {
      double r = ((BigDecimal)value1).doubleValue();
      double t = ((BigDecimal)value2).doubleValue();

      if (Math.abs(t) > getMemory().getTrigonometricMaxArgument())
      {
        throw new Fx702pErr3Exception(this);
      }

      double x = r * Math.cos(t * getMemory().getTrigonometricConversionFactor());
      double y = r * Math.sin(t * getMemory().getTrigonometricConversionFactor());

      getMemory().getVariable(X_INDEX).getNumberVariable().setValue(new BigDecimal(x));
      getMemory().getVariable(Y_INDEX).getNumberVariable().setValue(new BigDecimal(y));
    }
    else
    {
      throw new Fx702pErr2Exception();
    }
    return null;
  }

  public Object visit(ASTDeg aNode, Object aData)
  {
    if (aData instanceof Node)
    {
      Node node = (Node)aData;
      if (node.jjtGetNumChildren() <= 3)
      {
        BigDecimal values[] = new BigDecimal[3];
        Arrays.fill(values, BigDecimal.ZERO);
        for (int i = 0; i < node.jjtGetNumChildren(); i++)
        {
          Object value = node.jjtGetChild(i).jjtAccept(this, null);
          if (value instanceof BigDecimal)
          {
            values[i] = (BigDecimal)value;
          }
          else
          {
            throw new Fx702pErr2Exception();
          }
        }
        return values[0].add(values[1].divide(SIXTY, MATH_CONTEXT).add(values[2].divide(SQUARED_SIXTY, MATH_CONTEXT)));
      }
      else
      {
        throw new Fx702pErr2Exception();
      }
    }
    else
    {
      throw new Fx702pInternalError("Data should be a Node");
    }
  }

  public Object visit(ASTClosedSubExpression aNode, Object aData)
  {
    return aNode.jjtGetChild(0).jjtAccept(this, aData);
  }

  public Object visit(ASTOpenSubExpression aNode, Object aData)
  {
    return aNode.jjtGetChild(0).jjtAccept(this, aData);
  }

  public Object visit(ASTVariablesAndProgram aNode, Object aData)
  {
    return null;
  }

  public Object visit(ASTCommandArguments aNode, Object aData)
  {
    throw new Fx702pInternalError("Visiting ASTCommandArguments");
  }

  public Object visit(ASTCommandWithParenthesisArguments aNode, Object aData)
  {
    throw new Fx702pInternalError("Visiting ASTCommandWithParenthesisArguments");
  }

  public Object visit(ASTProgramsFile aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTMultiplePrograms aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTSingleProgram aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTProgramSignature aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTBreakpoint aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTWatch aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTWatchpoint aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTStatWatchpoint aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTClear aNode, Object aData)
  {
    throw new Fx702pErr2Exception();
  }

  public Object visit(ASTSavedDefm aNode, Object aData)
  {
    throw new Fx702pInternalError("Found SavedDefm node in a program");
  }

  static protected class StatisticsArguments
  {
    public StatisticsArguments(BigDecimal anX, BigDecimal anY)
    {
      x = anX;
      y = anY;
    }

    public BigDecimal x;
    public BigDecimal y;
  }

  static protected class DMSResult
  {
    public DMSResult(String aResult, BigDecimal aValue)
    {
      result = aResult;
      value = aValue;
    }

    public String result;
    public BigDecimal value;
  }
}
