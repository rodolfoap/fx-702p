package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class InsertCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.insertSpace();
  }

  static public final InsertCommand INSERT_COMMAND = new InsertCommand();
}
