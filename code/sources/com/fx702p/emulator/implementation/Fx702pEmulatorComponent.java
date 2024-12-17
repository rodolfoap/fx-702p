package com.fx702p.emulator.implementation;

import com.fx702p.emulator.Variable;
import com.fx702p.emulator.commands.Command;
import com.fx702p.interpreters.Fx702pException;

public interface Fx702pEmulatorComponent
{
  public void setRunMode();
  public void setWrtMode();

  public void runProgram();
  public void endProgram();
  public void stopProgram();
  public void contProgram();
  public void suspendProgram();
  public void resumeProgram();
  public void stepInProgram();
  public void debugAndStepActiveProgram();
  public void nextLoop(Variable aVariable);
  public void endLoop(Variable aVariable);

  public void loadProgram(int aProgramIndex);
  public void clearProgram(int aProgramIndex);
  public void setActiveProgramIndex(int aProgramIndex);

  public void stop();
  public void cont();

  public void startScroll();
  public void endScroll();

  public void waitAfterPrint(int aPrintWait, Command aCommand);
  public void endWaitAfterPrint();
  public void cancelWaitAfterPrint();
  public void resultPrinted();
  public void startMultiLinePrint();
  public void endMultiLinePrint();

  public void execute(String aString);

  public void enterString(String aString);
  public void input(String anInputPrompt);

  public void reportFx702pError(Fx702pException anError);

  public void allClear();
  public void home();

  public interface MethodCaller
  {
    public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent);
  }
}
