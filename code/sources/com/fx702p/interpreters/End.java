package com.fx702p.interpreters;

public class End implements Continuation
{
  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.end();
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return null;
  }
}
