package com.fx702p.interpreters;

public class NextSubInstruction extends NextInstruction
{

  public NextSubInstruction(int aNextLineIndex, int aNextInstructionIndex, int aNextPrintInstructionIndex)
  {
    super(aNextLineIndex, aNextInstructionIndex);
    nextPrintInstructionIndex = aNextPrintInstructionIndex;
  }

  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.executeSubInstruction(basicInstructionIndex, nextPrintInstructionIndex);
  }

  public int getNextPrintInstructionIndex()
  {
    return nextPrintInstructionIndex;
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return null;
  }

  protected int nextPrintInstructionIndex;
}
