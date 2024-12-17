package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class ClearCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.clear();
  }

  static public final ClearCommand CLEAR_COMMAND = new ClearCommand();
}
