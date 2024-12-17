package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;
import com.fx702p.emulator.implementation.MultiLinePrinter;

public class MultiLineWaitCommand extends AbstractCommand
{
  public MultiLineWaitCommand(int aTimeToWait, MultiLinePrinter aMultiLinePrinter)
  {
    timeToWait = aTimeToWait;
    multiLinePrinter = aMultiLinePrinter;
  }

  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    if (timeToWait > 0)
    {
      anEmulator.printWait(timeToWait, new MultiLinePrintCommand(multiLinePrinter));
    }
    else
    {
      anEmulator.printNextLine(multiLinePrinter);
    }
  }

  protected int timeToWait;
  protected MultiLinePrinter multiLinePrinter;
}
