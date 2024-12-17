package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class HomeCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.home();
  }

  static public final HomeCommand HOME_COMMAND = new HomeCommand();
}
