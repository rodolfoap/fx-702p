package com.fx702p.interpreters;


public class NextInstruction implements Continuation
{
  public NextInstruction(int aNextLineIndex, int aNextInstructionIndex)
  {
    basicInstructionIndex = new BasicInstructionIndex(aNextLineIndex, aNextInstructionIndex);
  }

  protected NextInstruction(BasicInstructionIndex aBasicInstructionIndex)
  {
    basicInstructionIndex = aBasicInstructionIndex;
  }

  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.executeInstruction(basicInstructionIndex);
  }

  public BasicInstructionIndex getBasicIntructionIndex()
  {
    return basicInstructionIndex;
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return basicInstructionIndex;
  }

  protected BasicInstructionIndex basicInstructionIndex;
}
