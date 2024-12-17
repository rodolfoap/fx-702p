package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class PrintOnModeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.setMode(Fx702pConstants.PRT_ON_MODE);
  }

  static public final PrintOnModeCommand PRINT_ON_MODE_COMMAND = new PrintOnModeCommand();
}
