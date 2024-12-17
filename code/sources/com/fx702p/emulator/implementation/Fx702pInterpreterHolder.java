package com.fx702p.emulator.implementation;

import com.fx702p.emulator.Variable;
import com.fx702p.emulator.commands.Command;
import com.fx702p.interpreters.*;

public class Fx702pInterpreterHolder implements Fx702pEmulatorComponent
{
  public Fx702pInterpreterHolder(Fx702pDefaultEmulator aFx702pDefaultEmulator)
  {
    emulator = aFx702pDefaultEmulator;
    calculator = new Fx702pCalculator(aFx702pDefaultEmulator);
    basicInterpreter = new Fx702pBasicInterpreter(aFx702pDefaultEmulator);
    basicEditor = new Fx702pBasicEditor(aFx702pDefaultEmulator);
    currentInterpreter = calculator;
    setInitialBehavior();
  }

  public void forwardCall(MethodCaller aMethodCaller)
  {
    aMethodCaller.callMethod(this);
    aMethodCaller.callMethod(getCurrentInterpreter());
  }

  protected void setInitialBehavior()
  {
    setBehavior(CALCULATOR_BEHAVIOR);
  }

  protected void setBehavior(InterpreterHolderBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  public Fx702pCalculator getCalculator()
  {
    return calculator;
  }

  public Fx702pBasicInterpreter getBasicInterpreter()
  {
    return basicInterpreter;
  }

  public Fx702pBasicEditor getBasicEditor()
  {
    return basicEditor;
  }

  public Fx702pAbstractInterpreter getCurrentInterpreter()
  {
    return currentInterpreter;
  }

  public void allClear()
  {
    behavior.allClear();
  }

  public void home()
  {
  }

  public void enterString(String aString)
  {
    behavior.enterString(aString);
  }

  public void execute(String aString)
  {
    behavior.execute(aString);
  }

  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  public void resultPrinted()
  {
    behavior.resultPrinted();
  }

  public void startMultiLinePrint()
  {
    behavior.startMultiLinePrint();
  }

  public void endMultiLinePrint()
  {
    behavior.endMultiLinePrint();
  }

  public void reportFx702pError(Fx702pException anError)
  {
    behavior.reportFx702pError(anError);
  }

  public void endWaitAfterPrint()
  {
    behavior.endWaitAfterPrint();
  }

  public void cancelWaitAfterPrint()
  {
    behavior.cancelWaitAfterPrint();
  }

  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
    behavior.waitAfterPrint(aPrintWait, null);
  }

  public void endProgram()
  {
    behavior.endProgram();
  }

  public void runProgram()
  {
    behavior.runProgram();
  }

  public void stopProgram()
  {
    behavior.stopProgram();
  }

  public void contProgram()
  {
    behavior.contProgram();
  }

  public void suspendProgram()
  {
    behavior.suspendProgram();
  }

  public void resumeProgram()
  {
    behavior.resumeProgram();
  }

  public void stepInProgram()
  {
    behavior.stepInProgram();
  }

  public void debugAndStepActiveProgram()
  {
    behavior.debugAndStepActiveProgram();
  }

  public void cont()
  {
    behavior.cont();
  }

  public void stop()
  {
    behavior.stop();
  }

  public void setRunMode()
  {
    behavior.setRunMode();
  }

  public void setWrtMode()
  {
    behavior.setWrtMode();
  }


  public void startScroll()
  {
    behavior.startScroll();
  }

  public void endScroll()
  {
    behavior.endScroll();
  }

  public void lastAnswer()
  {
    behavior.lastAnswer();
  }

  public void loadProgram(int aProgramIndex)
  {
  }

  public void clearProgram(int aProgramIndex)
  {
  }

  public void setActiveProgramIndex(int aProgramIndex)
  {
  }

  public void nextLoop(Variable aVariable)
  {
  }

  public void endLoop(Variable aVariable)
  {
  }


  protected interface InterpreterHolderBehavior extends Fx702pEmulatorComponent
  {
    public void lastAnswer();
  }

  protected class AbstractInterpreterHolderBehavior extends Fx702pAbstractEmulatorComponent implements InterpreterHolderBehavior
  {
    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setBehavior(new ErrorBehavior(this));
    }

    public void lastAnswer()
    {
    }
  }

  protected class CalculatorBehavior extends AbstractInterpreterHolderBehavior
  {
    @Override
    public void runProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      currentInterpreter.runProgram();
      currentInterpreter = basicInterpreter;
    }

    @Override
    public void setWrtMode()
    {
      currentInterpreter.setWrtMode();
      currentInterpreter = basicEditor;
    }

    @Override
    public void lastAnswer()
    {
      currentInterpreter.lastAnswer();
    }
  }

  protected class ProgramRunningBehavior extends AbstractInterpreterHolderBehavior
  {
    @Override
    public void setRunMode()
    {
      setBehavior(CALCULATOR_BEHAVIOR);
      currentInterpreter.setRunMode();
      currentInterpreter = calculator;
    }

    @Override
    public void endProgram()
    {
      setBehavior(CALCULATOR_BEHAVIOR);
      currentInterpreter.endProgram();
      currentInterpreter = calculator;
    }

    @Override
    public void stopProgram()
    {
      setBehavior(PROGRAM_STOPPPED_BEHAVIOR);
      currentInterpreter.stop();
      currentInterpreter = calculator;
    }

    @Override
    public void allClear()
    {
      emulator.endProgram();
    }

    @Override
    public void input(String anInputPrompt)
    {
      setBehavior(INPUT_BEHAVIOR);
    }
  }

  protected class ProgramStoppedBehavior extends CalculatorBehavior
  {
    @Override
    public void contProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      currentInterpreter.cont();
      currentInterpreter = basicInterpreter;
    }

    @Override
    public void stepInProgram()
    {
      contProgram();
    }

    @Override
    public void setRunMode()
    {
      setBehavior(CALCULATOR_BEHAVIOR);
      currentInterpreter.setRunMode();
      currentInterpreter = calculator;
    }
  }

  protected class InputBehavior extends ProgramRunningBehavior
  {
    @Override
    public void allClear()
    {
    }

    @Override
    public void contProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      currentInterpreter.cont();
      currentInterpreter = basicInterpreter;
    }

    @Override
    public void stepInProgram()
    {
      contProgram();
    }

    @Override
    public void lastAnswer()
    {
      currentInterpreter.lastAnswer();
    }
  }

  protected class ErrorBehavior extends AbstractInterpreterHolderBehavior
  {
    public ErrorBehavior(InterpreterHolderBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    protected InterpreterHolderBehavior previousBehavior;
  }

  protected Fx702pDefaultEmulator emulator;
  protected InterpreterHolderBehavior behavior;

  protected Fx702pCalculator calculator;
  protected Fx702pBasicInterpreter basicInterpreter;
  protected Fx702pBasicEditor basicEditor;
  protected Fx702pAbstractInterpreter currentInterpreter;

  protected final CalculatorBehavior CALCULATOR_BEHAVIOR = new CalculatorBehavior();
  protected final ProgramRunningBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final ProgramStoppedBehavior PROGRAM_STOPPPED_BEHAVIOR = new ProgramStoppedBehavior();
  protected final InputBehavior INPUT_BEHAVIOR = new InputBehavior();
}
