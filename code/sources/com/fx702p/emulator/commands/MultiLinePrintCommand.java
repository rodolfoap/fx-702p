package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;
import com.fx702p.emulator.implementation.MultiLinePrinter;

public class MultiLinePrintCommand extends AbstractCommand
{
  public MultiLinePrintCommand(MultiLinePrinter aMultiLinePrinter)
  {
    multiLinePrinter = aMultiLinePrinter;
  }

  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.printNextLine(multiLinePrinter);
  }

  protected MultiLinePrinter multiLinePrinter;
}
