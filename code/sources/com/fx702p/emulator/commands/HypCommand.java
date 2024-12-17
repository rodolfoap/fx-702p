package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class HypCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.hypPrefix();
  }

  public void postExecute(Fx702pEmulator anEmulator)
  {
  }

  static public final HypCommand HYP_COMMAND = new HypCommand();
}
