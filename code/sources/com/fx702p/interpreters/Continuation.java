package com.fx702p.interpreters;


public interface Continuation
{
  public void callContinuation(Fx702pBasicInterpreter anInterpreter);
  public BasicInstructionIndex getBasicInstructionIndex();
}
