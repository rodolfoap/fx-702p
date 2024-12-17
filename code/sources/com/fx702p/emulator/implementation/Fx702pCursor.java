package com.fx702p.emulator.implementation;

import com.fx702p.emulator.Fx702pEmulator;
import com.fx702p.emulator.commands.Command;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pCursor extends Fx702pAbstractEmulatorComponent
{
  public Fx702pCursor(Fx702pEmulator anEmulator)
  {
    emulator = anEmulator;
    setInitialBehavior();
  }

  protected void setInitialBehavior()
  {
    setBehavior(CALCULATOR_BEHAVIOR);
  }

  protected void setBehavior(CursorBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  protected void setCursorVisible()
  {
    emulator.getDisplay().setCursorVisible(true);
  }

  protected void setCursorInvisible()
  {
    emulator.getDisplay().setCursorVisible(false);
  }

  @Override
  public void enterString(String aString)
  {
    behavior.enterString(aString);
  }

  @Override
  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  @Override
  public void allClear()
  {
    behavior.allClear();
  }

  @Override
  public void home()
  {
    behavior.home();
  }

  @Override
  public void execute(String aString)
  {
    behavior.execute(aString);
  }

  @Override
  public void reportFx702pError(Fx702pException anError)
  {
    behavior.reportFx702pError(anError);
  }

  @Override
  public void cont()
  {
    behavior.cont();
  }

  @Override
  public void stop()
  {
    behavior.stop();
  }

  @Override
  public void endProgram()
  {
    behavior.endProgram();
  }

  @Override
  public void runProgram()
  {
    behavior.runProgram();
  }

  @Override
  public void contProgram()
  {
    behavior.contProgram();
  }

  @Override
  public void stopProgram()
  {
    behavior.stopProgram();
  }

  @Override
  public void setRunMode()
  {
    behavior.setRunMode();
  }

  @Override
  public void endWaitAfterPrint()
  {
    behavior.endWaitAfterPrint();
  }

  @Override
  public void setWrtMode()
  {
    behavior.setWrtMode();
  }

  @Override
  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
    behavior.waitAfterPrint(aPrintWait, null);
  }

  @Override
  public void resultPrinted()
  {
    behavior.resultPrinted();
  }

  @Override
  public void startMultiLinePrint()
  {
    behavior.startMultiLinePrint();
  }

  protected interface CursorBehavior extends Fx702pEmulatorComponent
  {
  }

  protected class AbstractCursorBehavior extends Fx702pAbstractEmulatorComponent implements CursorBehavior
  {
    @Override
    public void setRunMode()
    {
      setBehavior(CALCULATOR_BEHAVIOR);
    }

    @Override
    public void endProgram()
    {
      setCursorDefaultVisibility();
      setBehavior(CALCULATOR_BEHAVIOR);
    }

    @Override
    public void runProgram()
    {
      setCursorInvisible();
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void allClear()
    {
      setCursorDefaultVisibility();
      emulator.setCursorPosition(0);
    }

    protected void setCursorDefaultVisibility()
    {
      if (emulator.getDisplay().isEmpty())
      {
        setCursorVisible();
      }
    }

    @Override
    public void input(String anInputPrompt)
    {
      setBehavior(INPUT_BEHAVIOR);
      setCursorInvisible();
    }

    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setCursorInvisible();
      setBehavior(new ErrorBehavior(this));
    }

    @Override
    public void startMultiLinePrint()
    {
      setCursorInvisible();
    }
  }

  protected class CalculatorBehavior extends AbstractCursorBehavior
  {
    @Override
    public void enterString(String aString)
    {
      setCursorVisible();
    }

    @Override
    public void resultPrinted()
    {
      setCursorInvisible();
    }

    @Override
    public void home()
    {
      setCursorVisible();
    }
  }

  protected class ProgramRunningBehavior extends AbstractCursorBehavior
  {
    @Override
    public void stopProgram()
    {
      setCursorDefaultVisibility();
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
    }
  }

  protected class ProgramStoppedBehavior extends CalculatorBehavior
  {
    @Override
    public void contProgram()
    {
      setCursorInvisible();
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void stepInProgram()
    {
      contProgram();
    }
  }

  protected class InputBehavior extends CalculatorBehavior
  {
    @Override
    public void stopProgram()
    {
      setCursorDefaultVisibility();
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
    }

    @Override
    public void contProgram()
    {
      setCursorInvisible();
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void stepInProgram()
    {
      contProgram();
    }

    @Override
    public void allClear()
    {
      setCursorInvisible();
    }
  }

  protected class ErrorBehavior extends Fx702pAbstractEmulatorComponent implements CursorBehavior
  {
    public ErrorBehavior(CursorBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    protected CursorBehavior previousBehavior;
  }

  protected Fx702pEmulator emulator;
  protected CursorBehavior behavior;

  protected final CalculatorBehavior CALCULATOR_BEHAVIOR = new CalculatorBehavior();
  protected final ProgramRunningBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final ProgramStoppedBehavior PROGRAM_STOPPED_BEHAVIOR = new ProgramStoppedBehavior();
  protected final InputBehavior INPUT_BEHAVIOR = new InputBehavior();
}
