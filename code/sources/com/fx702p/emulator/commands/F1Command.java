package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class F1Command extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.f1Prefix();
  }

  public void postExecute(Fx702pEmulator anEmulator)
  {
  }

  static public final F1Command F1_COMMAND = new F1Command();
}
