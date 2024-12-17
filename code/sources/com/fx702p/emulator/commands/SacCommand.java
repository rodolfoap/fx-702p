package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class SacCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.sac();
  }

  static public final SacCommand SAC_COMMAND = new SacCommand();
}
