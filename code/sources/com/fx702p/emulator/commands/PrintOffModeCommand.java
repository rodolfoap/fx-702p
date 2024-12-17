package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class PrintOffModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.PRT_OFF_MODE);
    ;
  }

  static public final PrintOffModeCommand PRINT_OFF_MODE_COMMAND = new PrintOffModeCommand();

}
