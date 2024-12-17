package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class StartInputCommand extends AbstractCommand
{
  public StartInputCommand(String anInputPrompt)
  {
    inputPrompt = anInputPrompt;
  }

  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.startInput(inputPrompt);
  }

  protected String inputPrompt;
}
