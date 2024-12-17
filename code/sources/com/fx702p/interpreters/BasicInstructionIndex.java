package com.fx702p.interpreters;

public class BasicInstructionIndex implements Comparable<BasicInstructionIndex>
{
  public BasicInstructionIndex(int aLineIndex, int anInstructionIndex)
  {
    lineIndex = aLineIndex;
    instructionIndex = anInstructionIndex;
  }

  public int getLineIndex()
  {
    return lineIndex;
  }

  public int getInstructionIndex()
  {
    return instructionIndex;
  }

  public boolean isValid()
  {
    return lineIndex >= 0;
  }

  public int compareTo(BasicInstructionIndex anotherBasicInstructionIndex)
  {
    if (anotherBasicInstructionIndex != null)
    {
      if (lineIndex == anotherBasicInstructionIndex.lineIndex)
      {
        return instructionIndex - anotherBasicInstructionIndex.instructionIndex;
      }
      else
      {
        return lineIndex - anotherBasicInstructionIndex.lineIndex;
      }
    }
    else
    {
      return 1;
    }
  }

  public int hashCode()
  {
    return (lineIndex << 16) & instructionIndex;
  }

  public boolean equals(Object anotherBasicInstructionIndex)
  {
    return anotherBasicInstructionIndex instanceof BasicInstructionIndex && ((BasicInstructionIndex)anotherBasicInstructionIndex).getLineIndex() == lineIndex && ((BasicInstructionIndex)anotherBasicInstructionIndex).getInstructionIndex() == instructionIndex;
  }

  public String toString()
  {
    return "Line: " + lineIndex + ", Instruction: " + instructionIndex;
  }

  protected int lineIndex;
  protected int instructionIndex;

  static public final BasicInstructionIndex FIRST_INSTRUCTION_INDEX = new BasicInstructionIndex(0, 0);
}
