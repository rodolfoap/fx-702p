package com.fx702p.interpreters;

import static com.fx702p.emulator.Fx702pConstants.WAIT_MAX;

import com.fx702p.emulator.*;
import com.fx702p.emulator.implementation.*;
import com.fx702p.parser.SimpleNode;

public abstract class Fx702pAbstractInterpreter extends Fx702pAbstractEmulatorComponent
{
  public Fx702pAbstractInterpreter(Fx702pEmulator anEmulator)
  {
    emulator = anEmulator;
  }

  public abstract void afterError();
  @Override
  public abstract void execute(String anInputBuffer);

  // Needed to be public by the BasicProgram to load watchpoints
  public Fx702pMemory getMemory()
  {
    return emulator.getMemory();
  }

  public void setDefm(int aDefm)
  {
    emulator.setDefm(aDefm);
  }

  public void lastAnswer()
  {
  }

  public boolean isReusableResult()
  {
    return emulator.isReusableResult();
  }

  public void setReusableResult(boolean aReusableResultFlag)
  {
    emulator.setReusableResult(aReusableResultFlag);
  }

  public void startEnteringString(String aString)
  {
    emulator.enterString(aString);
  }

  protected void clearDisplay()
  {
    emulator.clearDisplay();
  }

  public void showBusy(boolean aFlag)
  {
    emulator.getDisplay().showBusy(aFlag);
  }

  protected void printMultiLines(MultiLinePrinter aMultiLinePrinter)
  {
    emulator.print(aMultiLinePrinter);
  }

  protected void printResult(String aString)
  {
    emulator.printResult(aString);
  }

  protected void setCursorPosition(int aCursorPosition)
  {
    emulator.setCursorPosition(aCursorPosition);
  }

  protected void printWait(int aPrtWait)
  {
    if (aPrtWait >= WAIT_MAX)
    {
      emulator.stop();
    }
    else
    {
      emulator.printWait(aPrtWait, null);
    }
  }

  protected void startInput(String anInputPrompt)
  {
    emulator.input(anInputPrompt);
  }

  protected Character getKey()
  {
    return emulator.getKey();
  }

  protected void reportError(Throwable anError)
  {
    emulator.reportError(anError);
  }

  // But display is managed through commands and buffers
  // so no direct access
  private Fx702pDisplay getDisplay()
  {
    return emulator.getDisplay();
  }

  protected void setMode(int aMode)
  {
    emulator.setMode(aMode);
  }

  @Override
  public void stopProgram()
  {
  }

  @Override
  public void contProgram()
  {
  }

  protected void stopProgramOnInput()
  {
    emulator.stopProgram();
  }

  protected void contProgramOnInput()
  {
    emulator.contProgram();
  }

  protected void suspendProgramOnBreakpoint()
  {
    emulator.suspendProgram();
  }

  protected void invokeNextLoop(Variable aVariable)
  {
    emulator.nextLoop(aVariable);
  }

  protected void invokeEndLoop(Variable aVariable)
  {
    emulator.endLoop(aVariable);
  }

  public Object visit(SimpleNode aNode, Object aData)
  {
    throw new RuntimeException("Invalid node type " + aNode.getClass() + ", " + aNode);
  }

  public void stat(String anInputBuffer)
  {
  }

  public void del(String anInputBuffer)
  {
  }

  public void astat()
  {
  }

  public void sac()
  {
    getMemory().clearStatVariables();
  }

  // Emulator is private. Subclasses must use methods here to have access to
  // display functions
  private Fx702pEmulator emulator;
}
