package com.fx702p.emulator.commands;

import com.fx702p.emulator.Fx702pEmulator;

public class RunNumberedProgramCommand extends AbstractCommand
{
  public RunNumberedProgramCommand(int aProgramIndex)
  {
    programIndex = aProgramIndex;
  }

  @Override
  public void execute(Fx702pEmulator anEmulator)
  {
    anEmulator.endProgram();
    anEmulator.setActiveProgramIndex(programIndex);
    anEmulator.runActiveProgram();
  }

  private int programIndex;
}
