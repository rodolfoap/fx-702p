package com.fx702p.emulator;

public abstract class AbstractVariable implements Variable
{
  protected AbstractVariable(int aComparisonIndex)
  {
    comparisonIndex = aComparisonIndex;
  }

  public int getComparisonIndex()
  {
    return comparisonIndex;
  }

  public int compareTo(Variable aVariable)
  {
    return new Integer(comparisonIndex).compareTo(aVariable.getComparisonIndex());
  }

  private int comparisonIndex;
}
