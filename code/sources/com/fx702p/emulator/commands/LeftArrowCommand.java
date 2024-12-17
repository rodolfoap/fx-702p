package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class LeftArrowCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.moveCursorLeft();
  }

  static public final LeftArrowCommand LEFT_ARROW_COMMAND = new LeftArrowCommand();

}
