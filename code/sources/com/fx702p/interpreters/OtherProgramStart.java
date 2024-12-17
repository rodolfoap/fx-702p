package com.fx702p.interpreters;


public class OtherProgramStart extends FirstInstruction
{
  public OtherProgramStart(int aProgramIndex)
  {
    programNumber = aProgramIndex;
  }

  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.changeActiveProgramIndex(programNumber);
    super.callContinuation(anInterpreter);
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return null;
  }

  protected int programNumber;
}
