package com.fx702p.interpreters;

public class PrintWaitInstruction implements Continuation
{
  public PrintWaitInstruction(Continuation aContinuation)
  {
    continuation = aContinuation;
  }

  public void callContinuation(Fx702pBasicInterpreter anInterpreter)
  {
    anInterpreter.printWait();
    anInterpreter.setContinuation(continuation);
  }

  public BasicInstructionIndex getBasicInstructionIndex()
  {
    return null;
  }

  protected Continuation continuation;
}
