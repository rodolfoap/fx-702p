package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class ModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.modePrefix();
  }

  public void preExecute(Fx702pEmulator anEmulator)
  {
    anEmulator.clearPrefixes();
  }

  public void postExecute(Fx702pEmulator anEmulator)
  {
  }

  static public final ModeCommand MODE_COMMAND = new ModeCommand();
}
