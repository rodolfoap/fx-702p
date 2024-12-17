package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class RunModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.RUN_MODE);
  }

  static public final RunModeCommand RUN_MODE_COMMAND = new RunModeCommand();
}
