package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class RightArrowCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.moveCursorRight();
  }

  static public final RightArrowCommand RIGHT_ARROW_COMMAND = new RightArrowCommand();
}
