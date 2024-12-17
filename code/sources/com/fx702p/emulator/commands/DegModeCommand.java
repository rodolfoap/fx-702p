package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class DegModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.DEG_MODE);
  }

  static public final DegModeCommand DEG_MODE_COMMAND = new DegModeCommand();
}
