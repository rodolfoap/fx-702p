package com.fx702p.emulator;

import com.fx702p.emulator.exceptions.*;

public class DollarVariable extends AbstractVariable
{
  public DollarVariable(Fx702pMemory aMemory, int aComparisonIndex)
  {
    super(aComparisonIndex);
    memory = aMemory;
  }
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
    value = "";
  }

  public void setValue(Object aValue)
  {
    if (aValue instanceof String)
    {
      if (((String)aValue).length() > MAX_LENGTH)
      {
        throw new Fx702pErr6Exception();
      }
      value = (String)aValue;
      memory.getWatcher().variableModified(this);
      memory.markAsModified();
    }
    else
    {
      throw new Fx702pErr3Exception();
    }
  }

  public Object getValue()
  {
    return value;
  }

  private String value = "";
  private Fx702pMemory memory;

  static public final int MAX_LENGTH = 30;
}
