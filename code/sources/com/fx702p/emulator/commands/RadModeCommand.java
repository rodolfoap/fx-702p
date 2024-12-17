package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class RadModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.RAD_MODE);
  }

  static public final RadModeCommand RAD_MODE_COMMAND = new RadModeCommand();
}
