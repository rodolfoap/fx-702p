package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class ArcCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.arcPrefix();
  }

  public void postExecute(Fx702pEmulator anEmulator)
  {
  }

  static public final ArcCommand ARC_COMMAND = new ArcCommand();
}
