package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class StringCommand extends AbstractCommand
{
  public StringCommand(String aString)
  {
    string = aString;
  }

  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.enterString(string);
  }

  public String getString()
  {
    return string;
  }

  protected String string;
}
