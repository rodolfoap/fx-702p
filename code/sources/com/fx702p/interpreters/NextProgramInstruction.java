package com.fx702p.interpreters;

public class NextProgramInstruction extends NextInstruction
{
  public NextProgramInstruction(int aProgramIndex, BasicInstructionIndex aBasicInstructionIndex)
  {
    super(aBasicInstructionIndex);
    programIndex = aProgramIndex;
  }

  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.changeActiveProgramIndex(programIndex);
    super.callContinuation(anInterpreter);
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return null;
  }

  protected int programIndex;
}
