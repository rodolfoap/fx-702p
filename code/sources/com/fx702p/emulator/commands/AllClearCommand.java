package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class AllClearCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.allClear();
  }

  static public final AllClearCommand ALL_CLEAR_COMMAND = new AllClearCommand();
}
