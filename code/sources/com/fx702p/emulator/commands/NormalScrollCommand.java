package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class NormalScrollCommand extends AbstractCommand
{
  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.normalScroll();
  }
}
