package com.fx702p.debug;

import static com.fx702p.emulator.Fx702pConstants.ARRAY_COUNT_MAX;
import static com.fx702p.emulator.Fx702pConstants.FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE;

import com.fx702p.emulator.*;
import com.fx702p.parser.*;

public class WatchedVariableHelper
{
  static public String getAlias(Node aNode)
  {
    if (aNode instanceof ASTDollar)
    {
      return "$";
    }
    else if (aNode instanceof ASTVariable)
    {
      if (aNode.jjtGetNumChildren() == 0)
      {
        return ((ASTVariable)aNode).name;
      }
      else
      {
        String indexes = getArrayIndexes(aNode);
        if (indexes == null)
        {
          return null;
        }
        else
        {
          return ((ASTVariable)aNode).name + indexes;
        }
      }
    }
    else if (aNode instanceof ASTIndexedVariable)
    {
      return ((ASTIndexedVariable)aNode).name;
    }
    else
    {
      return null;
    }
  }

  static protected String getArrayIndexes(Node aNode)
  {
    String name = ((ASTVariable)aNode).name;
    if (!name.equals("A"))
    {
      return null;
    }
    int index;
    if (aNode.jjtGetNumChildren() == 1)
    {
      SimpleNode expression = (SimpleNode)aNode.jjtGetChild(0);
      if (expression instanceof ASTInteger)
      {
        index = ((ASTInteger)expression).value.intValue();
      }
      else
      {
        return null;
      }

      if (index < 0 || index >= ARRAY_COUNT_MAX)
      {
        return null;
      }
      return "(" + index + ")";
    }
    else if (aNode.jjtGetNumChildren() == 2)
    {
      SimpleNode expression1 = (SimpleNode)aNode.jjtGetChild(0);
      int index1;
      if (expression1 instanceof ASTInteger)
      {
        index1 = ((ASTInteger)expression1).value.intValue();
      }
      else
      {
        return null;
      }

      SimpleNode expression2 = (SimpleNode)aNode.jjtGetChild(1);
      int index2;
      if (expression2 instanceof ASTInteger)
      {
        index2 = ((ASTInteger)expression2).value.intValue();
      }
      else
      {
        return null;
      }

      return "(" + index1 + "," + index2 + ")";
    }
    else
    {
      return null;
    }
  }

  static public Variable getVariable(Node aNode, Fx702pMemory aMemory)
  {
    if (aNode instanceof ASTDollar)
    {
      return aMemory.getDollarVariable();
    }
    else if (aNode instanceof ASTVariable)
    {
      if (aNode.jjtGetNumChildren() == 0)
      {
        return getNormalVariable(aNode, aMemory);
      }
      else
      {
        return getArrayVariable(aNode, aMemory);
      }
    }
    else if (aNode instanceof ASTIndexedVariable)
    {
      return getIndexedVariable(aNode, aMemory);
    }
    else
    {
      return null;
    }
  }

  static protected Variable getNormalVariable(Node aNode, Fx702pMemory aMemory)
  {
    String name = ((ASTVariable)aNode).name;
    if (name.length() != 1)
    {
      return null;
    }
    int index = Character.getNumericValue(name.charAt(0)) - FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE;
    return aMemory.getVariable(index);
  }

  static protected Variable getIndexedVariable(Node aNode, Fx702pMemory aMemory)
  {
    String name = ((ASTIndexedVariable)aNode).name;
    if (name.length() != 2)
    {
      return null;
    }
    int index1 = Character.getNumericValue(name.charAt(0)) - FIRST_VARIABLE_CHARACTER_NUMERIC_VALUE;
    int index2 = Character.digit(name.charAt(1), 10);

    int index = index1 * 10 + index2;

    return aMemory.getArrayVariable(index);
  }

  static protected Variable getArrayVariable(Node aNode, Fx702pMemory aMemory)
  {
    String name = ((ASTVariable)aNode).name;
    if (!name.equals("A"))
    {
      return null;
    }
    int index;
    if (aNode.jjtGetNumChildren() == 1)
    {
      SimpleNode expression = (SimpleNode)aNode.jjtGetChild(0);
      if (expression instanceof ASTInteger)
      {
        index = ((ASTInteger)expression).value.intValue();
      }
      else
      {
        return null;
      }

      if (index < 0 || index >= ARRAY_COUNT_MAX)
      {
        return null;
      }
    }
    else if (aNode.jjtGetNumChildren() == 2)
    {
      SimpleNode expression1 = (SimpleNode)aNode.jjtGetChild(0);
      int index1;
      if (expression1 instanceof ASTInteger)
      {
        index1 = ((ASTInteger)expression1).value.intValue();
      }
      else
      {
        return null;
      }

      SimpleNode expression2 = (SimpleNode)aNode.jjtGetChild(1);
      int index2;
      if (expression2 instanceof ASTInteger)
      {
        index2 = ((ASTInteger)expression2).value.intValue();
      }
      else
      {
        return null;
      }

      index = index1 * 10 + index2;
    }
    else
    {
      return null;
    }

    return aMemory.getArrayVariable(index);
  }
}
