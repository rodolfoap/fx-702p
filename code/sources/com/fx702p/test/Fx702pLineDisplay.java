package com.fx702p.test;

import java.io.*;

import com.fx702p.emulator.*;
import com.fx702p.emulator.implementation.*;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pLineDisplay extends Fx702pAbstractEmulatorComponent implements Fx702pDisplay
{
  static public void main(String[] args)
  {
    new Fx702pLineDisplay().run();
  }

  public Fx702pLineDisplay()
  {
    emulator = new Fx702pDefaultEmulator(new Fx702pLineDisplay());
  }

  public void run()
  {
    Thread emulatorThread = new Thread(emulator, "Emulator");
    emulatorThread.start();
    reader = new InputStreamReader(System.in);
    for (;;)
    {
      try
      {
        int c = reader.read();
        if (c == -1)
        {
          return;
        }
        else
        {
          Fx702pKey key = Fx702pKey.convertCharToFx702pKey((char)c);
          if (key != null)
          {
            emulator.keyPressed(key);
          }
        }
      }
      catch (IOException exception)
      {
        exception.printStackTrace();
      }
    }
  }

  public boolean isEmpty()
  {
    return false;
  }

  public void print(char[] aTheCharactersToDisplay)
  {
  }

  public void showPrefixes(boolean aF1Prefix, boolean aF2Prefix, boolean anArcPrefix, boolean anHypPrefix, boolean aModePrefix)
  {
  }

  public void showDeg(boolean aFlag)
  {
  }

  public void showRad(boolean aFlag)
  {
  }

  public void showGrd(boolean aFlag)
  {
  }

  public void showStop(boolean aFlag)
  {
  }

  public void showTrace(boolean aFlag)
  {
  }

  public void showPrt(boolean aFlag)
  {
  }

  public void showRun(boolean aFlag)
  {
  }

  public void setCursorVisible(boolean aVisibleFlag)
  {
  }

  public int getCursorPosition()
  {
    return 0;
  }

  public void setCursorPosition(int aCursorPosition)
  {
  }

  public void moveCursorRight()
  {
  }

  public void moveCursorRight(int aCount)
  {
  }

  public void moveCursorLeft()
  {
  }

  public void moveCursorLeft(int aCount)
  {
  }

  @Override
  public void reportFx702pError(Fx702pException anError)
  {
    System.err.println(anError.getMessage());
  }

  public void reportError(String aTitle, String aMessage, Throwable anError)
  {
    System.err.println(aMessage + ": " + anError.getMessage());
  }

  public void showProgram(Fx702pBasicProgram aProgram)
  {
    for (Fx702pBasicLine basicLine : aProgram.getBasicSourceCode().getLines())
    {
      System.out.println(basicLine);
    }
  }

  public boolean askConfirmation(String aQuestion, String aDetailedQuestion)
  {
    System.out.println(aQuestion);
    System.out.println(aDetailedQuestion);
    System.out.println("y/n?");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try
    {
      String line = reader.readLine();
      return line != null && line.equalsIgnoreCase("y");
    }
    catch (IOException exception)
    {
      return false;
    }
  }

  public void select(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
  }

  public void clearSelection()
  {
  }

  public void showBusy(boolean aFlag)
  {
  }

  protected Fx702pEmulator emulator;
  protected InputStreamReader reader;
}
