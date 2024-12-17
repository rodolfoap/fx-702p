package com.fx702p.emulator;

import java.math.BigDecimal;

import com.fx702p.emulator.exceptions.*;

public class FullVariable extends AbstractVariable
{
  public FullVariable(Fx702pMemory aMemory, int aComparisonIndex)
  {
    super(aComparisonIndex);
    memory = aMemory;
  }

  public boolean isStringVariable()
  {
    return isString;
  }

  public Object getValue()
  {
    if (isString)
    {
      return stringVariable.getValue();
    }
    else
    {
      return numberVariable.getValue();
    }
  }

  public void setValue(Object aValue)
  {
    if (isString)
    {
      stringVariable.setValue(aValue);
    }
    else
    {
      numberVariable.setValue(aValue);
    }
  }

  public boolean isLoopVariable()
  {
    return true;
  }

  public void clear()
  {
    isString = false;
    numberVariable.clear();
    stringVariable.clear();
  }

  public Variable getStringVariable()
  {
    return stringVariable;
  }

  public Variable getNumberVariable()
  {
    return numberVariable;
  }

  private class StringVariable implements Variable
  {
    public boolean isStringVariable()
    {
      return true;
    }

    public boolean isLoopVariable()
    {
      return false;
    }

    public void clear()
    {
      stringValue = "";
    }

    public Object getValue()
    {
      if (isString)
      {
        return stringValue;
      }
      else
      {
        return "";
      }
    }

    public void setValue(Object aValue)
    {
      if (aValue instanceof String)
      {
        if (((String)aValue).length() > MAX_LENGTH)
        {
          throw new Fx702pErr6Exception();
        }
        isString = true;
        stringValue = (String)aValue;
        memory.getWatcher().variableModified(FullVariable.this);
        memory.markAsModified();
      }
      else
      {
        throw new Fx702pErr2Exception();
      }
    }

    public int getComparisonIndex()
    {
      return FullVariable.this.getComparisonIndex();
    }

    public int compareTo(Variable aVariable)
    {
      return FullVariable.this.compareTo(aVariable);
    }
  }

  private class NumberVariable implements Variable
  {
    public boolean isStringVariable()
    {
      return false;
    }

    public boolean isLoopVariable()
    {
      return FullVariable.this.isLoopVariable();
    }

    public void clear()
    {
      numberValue = BigDecimal.ZERO;
    }

    public Object getValue()
    {
      if (isString)
      {
        throw new Fx702pErr6Exception();
      }
      else
      {
        return numberValue;
      }
    }

    public void setValue(Object aValue)
    {
      if (aValue instanceof BigDecimal)
      {
        isString = false;
        numberValue = (BigDecimal)aValue;
        memory.getWatcher().variableModified(FullVariable.this);
        memory.markAsModified();
      }
      else
      {
        throw new Fx702pErr2Exception();
      }
    }

    public int getComparisonIndex()
    {
      return FullVariable.this.getComparisonIndex();
    }

    public int compareTo(Variable aVariable)
    {
      return FullVariable.this.compareTo(aVariable);
    }
  }

  private boolean isString = false;
  private StringVariable stringVariable = new StringVariable();
  private NumberVariable numberVariable = new NumberVariable();
  private Fx702pMemory memory;

  private String stringValue = "";
  private BigDecimal numberValue = BigDecimal.ZERO;

  static public final int MAX_LENGTH = 30;
}