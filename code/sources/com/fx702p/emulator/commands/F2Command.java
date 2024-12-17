package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class F2Command extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.f2Prefix();
  }

  public void postExecute(Fx702pEmulator anEmulator)
  {
  }

  static public final F2Command F2_COMMAND = new F2Command();
}
