package com.fx702p.emulator.commands;

import com.fx702p.emulator.*;

public class ReuseResultCommand extends AbstractCommand
{
  public ReuseResultCommand(String aString)
  {
    string = aString;
  }

  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    if (anEmulator.isReusableResult())
    {
      anEmulator.enterString(Fx702pConstants.NORMAL_FORMATTER.format(anEmulator.getMemory().getLastResult()) + string);
    }
    else
    {
      anEmulator.enterString(string);
    }
  }

  private String string;
}
