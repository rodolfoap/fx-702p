package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class EndWaitAfterPrintCommand extends AbstractCommand
{
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.endWaitAfterPrint();
  }
}
