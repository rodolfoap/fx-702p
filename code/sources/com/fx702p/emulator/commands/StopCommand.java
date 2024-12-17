package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class StopCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.stop();
  }

  static public final StopCommand STOP_COMMAND = new StopCommand();
}
