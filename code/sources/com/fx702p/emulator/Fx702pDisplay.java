package com.fx702p.emulator;

import com.fx702p.emulator.implementation.Fx702pEmulatorComponent;


public interface Fx702pDisplay extends Fx702pEmulatorComponent
{
  public void print(char[] theCharactersToDisplay);
  public void setCursorVisible(boolean aVisibleFlag);
  public int getCursorPosition();
  public void setCursorPosition(int aCursorPosition);
  public void moveCursorRight();
  public void moveCursorRight(int aCount);
  public void moveCursorLeft();;
  public void moveCursorLeft(int aCount);
  public boolean isEmpty();

  public void reportError(String aTitle, String aMessage, Throwable anError);
  public boolean askConfirmation(String aQuestion, String aDetailedQuestion);

  public void showPrefixes(boolean aF1Prefix, boolean aF2Prefix, boolean anArcPrefix, boolean anHypPrefix, boolean aModePrefix);
  public void showStop(boolean aFlag);
  public void showBusy(boolean aFlag);
  public void showRun(boolean aFlag);
  public void showTrace(boolean aFlag);
  public void showPrt(boolean aFlag);
  public void showDeg(boolean aFlag);
  public void showRad(boolean aFlag);
  public void showGrd(boolean aFlag);
}
