package com.fx702p.emulator;

import java.io.File;

import com.fx702p.debug.Fx702pDebugger;
import com.fx702p.emulator.commands.Command;
import com.fx702p.emulator.implementation.MultiLinePrinter;

public interface Fx702pEmulator extends Runnable
{
  public Fx702pDisplay getDisplay();
  public Fx702pMemory getMemory();

  public void setMode(int aMode);
  public void setRunMode();
  public void setWrtMode();

  public void setDefm(int aDefm);

  public void displayRunModeMessage();

  public void keyPressed(Fx702pKey aKey);
  public void keyRepeated(Fx702pKey aKey);
  public void keyReleased(Fx702pKey aKey);

  public void clearPrefixes();
  public void f1Prefix();
  public void f2Prefix();
  public void modePrefix();
  public void arcPrefix();
  public void hypPrefix();

  public void addCommand(Command aCommand);

  public Character getKey();

  public void allClear();

  public void loadProgram(File aFile) throws Exception;
  public void reloadProgram() throws Exception;
  public void reloadAll() throws Exception;
  public void saveProgram();
  public void saveProgramAs(File aProgramFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag);
  public void saveAll();
  public void saveAllAs(File aProgramsFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag);
  public void runActiveProgram();
  public void setDebugger(Fx702pDebugger aDebugger);
  public void debugActiveProgram();
  public void debugAndStepActiveProgram();
  public void endDebug();
  public void stepInProgram();
  public void endProgram();
  public void stopProgram();
  public void contProgram();
  public void suspendProgram();
  public void resumeProgram();
  public ProgramStatus continueExecution();
  public boolean isContinuationDebuggable();
  public void setSpeed(int aSpeed);
  public void nextLoop(Variable aVariable);
  public void endLoop(Variable aVariable);

  public void stop();
  public void cont();

  public void lastAnswer();

  public void setCursorPosition(int aCursorPosition);
  public void printResult(String aString);
  public void print(String aString);
  public void print(MultiLinePrinter aMultiLinePrinter);
  public void printNextLine(MultiLinePrinter aMultiLinePrinter);
  public void printAndScroll(String aString);
  public void fastScroll();
  public void normalScroll();
  public void printWait(int aPrintWait, Command aCommand);
  public void endWaitAfterPrint();
  public void cancelWaitAfterPrint();
  public void clearDisplay();
  public boolean isReusableResult();
  public void setReusableResult(boolean aReusableResultFlag);

  public void input(String anInputPrompt);
  public void startInput(String anInputPrompt);
  public void enterString(String aString);
  public void clear();
  public void insertSpace();
  public void moveCursorRight();
  public void moveCursorLeft();
  public void home();

  public void reportError(Throwable anErrorMessage);
  public boolean askConfirmation(String aQuestion, String aDetailedQuestion);

  public void execute();

  public void stat();
  public void del();
  public void astat();
  public void sac();

  public Fx702pBasicProgram getProgram(int aProgramIndex);
  public Fx702pBasicProgram getActiveProgram();
  public int getActiveProgramIndex();
  public void setActiveProgramIndex(int setActiveProgramIndex);

  static public enum ProgramStatus
  {
    RUNNING,
    SUSPENDED,
    ENDED
  }
}
