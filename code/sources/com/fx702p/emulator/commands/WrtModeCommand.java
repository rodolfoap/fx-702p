package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class WrtModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.WRT_MODE);
  }

  static public final WrtModeCommand WRT_MODE_COMMAND = new WrtModeCommand();
}
