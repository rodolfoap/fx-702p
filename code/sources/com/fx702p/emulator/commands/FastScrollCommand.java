package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class FastScrollCommand extends AbstractCommand
{
  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.fastScroll();
  }
}
