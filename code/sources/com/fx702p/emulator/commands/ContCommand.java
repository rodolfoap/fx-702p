package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class ContCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.cont();
  }

  static public final ContCommand CONT_COMMAND = new ContCommand();
}
