package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class GraModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.GRA_MODE);
  }

  static public final GraModeCommand GRA_MODE_COMMAND = new GraModeCommand();

}
