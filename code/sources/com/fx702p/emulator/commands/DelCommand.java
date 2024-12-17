package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class DelCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.del();
  }

  static public final DelCommand DEL_COMMAND = new DelCommand();
}
