package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class PrintAndScrollCommand extends AbstractCommand
{
  public PrintAndScrollCommand(String aStringToPrintAndScroll)
  {
    stringToPrintAndScroll = aStringToPrintAndScroll;
  }

  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.printAndScroll(stringToPrintAndScroll);
  }


  public String getStringToPrintAndScroll()
  {
    return stringToPrintAndScroll;
  }

  protected String stringToPrintAndScroll;
}
