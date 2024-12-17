package com.fx702p.interpreters;


public class FirstInstruction implements Continuation
{
  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.executeInstruction(BasicInstructionIndex.FIRST_INSTRUCTION_INDEX);
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return BasicInstructionIndex.FIRST_INSTRUCTION_INDEX;
  }
}
