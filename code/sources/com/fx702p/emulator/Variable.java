package com.fx702p.emulator;


public interface Variable extends Comparable<Variable>
{
  public boolean isStringVariable();
  public boolean isLoopVariable();
  public void clear();
  public Object getValue();
  public void setValue(Object aValue);
  public int getComparisonIndex();
}
