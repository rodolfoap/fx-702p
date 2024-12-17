package com.fx702p.emulator.implementation;

import com.fx702p.emulator.Variable;
import com.fx702p.emulator.commands.Command;
import com.fx702p.interpreters.Fx702pException;

public abstract class Fx702pAbstractEmulatorComponent implements Fx702pEmulatorComponent
{
  public void allClear()
  {
  }

  public void home()
  {
  }

  public void enterString(String aString)
  {
  }

  public void execute(String aString)
  {
  }

  public void input(String anInputPrompt)
  {
  }

  public void resultPrinted()
  {
  }

  public void startMultiLinePrint()
  {
  }

  public void endMultiLinePrint()
  {
  }

  public void reportFx702pError(Fx702pException anError)
  {
  }

  public void cont()
  {
  }

  public void startScroll()
  {
  }

  public void endScroll()
  {
  }

  public void endProgram()
  {
  }

  public void runProgram()
  {
  }

  public void stopProgram()
  {
  }

  public void contProgram()
  {
  }

  public void stop()
  {
  }

  public void setRunMode()
  {
  }

  public void endWaitAfterPrint()
  {
  }

  public void cancelWaitAfterPrint()
  {
  }

  public void setWrtMode()
  {
  }

  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
  }

  public void suspendProgram()
  {
  }

  public void resumeProgram()
  {
  }

  public void stepInProgram()
  {
  }

  public void debugAndStepActiveProgram()
  {
    runProgram();
  }

  public void nextLoop(Variable aVariable)
  {
  }

  public void endLoop(Variable aVariable)
  {
  }

  public void loadProgram(int aProgramIndex)
  {
  }

  public void clearProgram(int aProgramIndex)
  {
  }

  public void setActiveProgramIndex(int aProgramIndex)
  {
  }
}
