package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class StatCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.stat();
  }

  static public final StatCommand STAT_COMMAND = new StatCommand();
}
