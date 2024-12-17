package com.fx702p.emulator;


public class ArrayVariable extends FullVariable
{
  public ArrayVariable(Fx702pMemory aMemory, int aComparisonIndex)
  {
    super(aMemory, aComparisonIndex);
  }

  @Override
  public boolean isLoopVariable()
  {
    return false;
  }
}
