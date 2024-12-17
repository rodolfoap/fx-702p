package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class AStatCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.astat();
  }

  static public final AStatCommand ASTAT_COMMAND = new AStatCommand();
}
