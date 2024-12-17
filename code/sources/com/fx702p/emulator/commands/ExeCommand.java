package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class ExeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.execute();
  }

  static public final ExeCommand EXE_COMMAND = new ExeCommand();

}
