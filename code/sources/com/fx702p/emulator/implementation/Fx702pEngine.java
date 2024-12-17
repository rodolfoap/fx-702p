package com.fx702p.emulator.implementation;

import static com.fx702p.emulator.Fx702pConstants.FAST_PRINT_SCROLL_DELAY;
import static com.fx702p.emulator.Fx702pConstants.NORMAL_PRINT_SCROLL_DELAY;
import static com.fx702p.emulator.Fx702pConstants.PRINT_WAIT_DELAY;

import java.util.*;

import com.fx702p.emulator.Fx702pEmulator;
import com.fx702p.emulator.commands.*;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pEngine extends Fx702pAbstractEmulatorComponent
{
  public Fx702pEngine(Fx702pDefaultEmulator anEmulator)
  {
    emulator = anEmulator;
    setInitialBehavior();
  }

  protected void setInitialBehavior()
  {
    behavior = RUN_MODE_BEHAVIOR;
  }

  protected void setBehavior(EngineBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  public void run()
  {
    commandLoop();
  }

  @Override
  public void setRunMode()
  {
    behavior.setRunMode();
  }

  @Override
  public void runProgram()
  {
    behavior.runProgram();
  }

  @Override
  public void stop()
  {
    behavior.stop();
  }

  @Override
  public void cont()
  {
    behavior.cont();
  }

  @Override
  public void endProgram()
  {
    behavior.endProgram();
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
  public void suspendProgram()
  {
    behavior.suspendProgram();
  }

  @Override
  public void resumeProgram()
  {
    behavior.resumeProgram();
  }

  @Override
  public void stepInProgram()
  {
    behavior.stepInProgram();
  }

  @Override
  public void debugAndStepActiveProgram()
  {
    behavior.debugAndStepActiveProgram();
  }

  @Override
  public void startScroll()
  {
    behavior.startScroll();
  }

  @Override
  public void endScroll()
  {
    behavior.endScroll();
  }

  public void fastScroll()
  {
    behavior.fastScroll();
  }

  public void normalScroll()
  {
    behavior.normalScroll();
  }

  public void addToPrintAndScroll(String aString)
  {
    behavior.addToPrintAndScroll(aString);
  }

  public void addToPrintAndScroll(String aString, Command aCommand)
  {
    afterScrollCommand = aCommand;
    addToPrintAndScroll(aString);
  }

  @Override
  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
    afterWaitCommand = aCommand;
    setBehavior(new WaitAfterPrintBehavior(behavior));
    processContinuationCommand = false;
    addDelayedCommand(new EndWaitAfterPrintCommand(), aPrintWait * PRINT_WAIT_DELAY);
  }

  @Override
  public void endWaitAfterPrint()
  {
    behavior.endWaitAfterPrint();
  }

  @Override
  public void cancelWaitAfterPrint()
  {
    behavior.cancelWaitAfterPrint();
  }

  @Override
  public void startMultiLinePrint()
  {
  }

  @Override
  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  @Override
  public void reportFx702pError(Fx702pException anError)
  {
    behavior.reportFx702pError(anError);
  }

  @Override
  public void allClear()
  {
    behavior.allClear();
  }

  public void setSpeed(int aSpeed)
  {
    temporizer.setSpeed(aSpeed);
  }

  protected long getPrintScrollDelay()
  {
    return printScrollDelay;
  }

  protected void setNormalPrintScrollDelay()
  {
    printScrollDelay = NORMAL_PRINT_SCROLL_DELAY;
  }

  protected void setFastPrintScrollDelay()
  {
    printScrollDelay = FAST_PRINT_SCROLL_DELAY;
  }

  protected void commandLoop()
  {
    for (;;)
    {
      Command command = null;
      synchronized (commandsQueue)
      {
        if (commandsQueue.isEmpty() && !processContinuationCommand)
        {
          try
          {
            commandsQueue.wait();
          }
          catch (InterruptedException exception)
          {
          }
        }
        else if (!commandsQueue.isEmpty())
        {
          command = commandsQueue.removeFirst();
        }
      }
      if (command != null)
      {
        executeCommand(command);
      }
      else if (processContinuationCommand && continuationCommand != null)
      {
        continuationCommand.execute(emulator);
        temporizer.temporize();
      }
    }
  }

  protected void executeCommand(Command aCommand)
  {
    aCommand.preExecute(emulator);
    aCommand.execute(emulator);
    aCommand.postExecute(emulator);
  }

  public void addCommand(Command aCommand)
  {
    if (aCommand != null)
    {
      synchronized (commandsQueue)
      {
        if (aCommand instanceof ContinuationCommand)
        {
          addCommand((ContinuationCommand)aCommand);
        }
        else
        {
          commandsQueue.addLast(aCommand);
          commandsQueue.notify();
        }
      }
    }
  }

  protected void addCommand(ContinuationCommand aContinuationCommand)
  {
    synchronized (commandsQueue)
    {
      continuationCommand = aContinuationCommand;
      commandsQueue.notify();
    }
  }

  protected void stopProcessingContinuationCommands()
  {
    processContinuationCommand = false;
    continuationCommand = null;
    cancelDelayedCommand();
  }

  protected synchronized void addDelayedCommand(final Command aCommand, long aDelay)
  {
    if (delayedCommand != null)
    {
      delayedCommand.cancel();
    }
    delayedCommand = new Timer(DELAYED_COMMAND_NAME);
    delayedTimerTask = new DelayedTimerTask(aCommand);
    delayedCommand.schedule(delayedTimerTask, aDelay);
  }

  protected synchronized void cancelDelayedCommand()
  {
    if (delayedCommand != null)
    {
      delayedCommand.cancel();
    }
    delayedCommand = null;
    delayedTimerTask = null;
  }

  protected synchronized void cancelAfterWaitCommand()
  {
    afterWaitCommand = null;
  }

  protected synchronized void resetDelayedCommand()
  {
    delayedCommand = null;
    delayedTimerTask = null;
  }

  protected synchronized void runDelayedCommand()
  {
    if (delayedCommand != null & delayedTimerTask != null)
    {
      delayedCommand.cancel();
      if (!delayedTimerTask.isExecuted())
      {
        executeCommand(delayedTimerTask.getCommand());
      }
      resetDelayedCommand();
    }
  }

  protected class ContinuationCommand extends AbstractCommand
  {
    @Override
    public void execute(Fx702pEmulator anEmulator)
    {
      Fx702pEmulator.ProgramStatus programStatus = anEmulator.continueExecution();
      switch (programStatus)
      {
        case RUNNING:
        {
          addCommand(new ContinuationCommand());
          break;
        }
        case SUSPENDED:
        {
          anEmulator.suspendProgram();
          break;
        }
        case ENDED:
        {
          anEmulator.endProgram();
          break;
        }
      }
    }

    @Override
    public void postExecute(Fx702pEmulator anEmulator)
    {
    }
  }

  protected class EndProgramCommand extends AbstractCommand
  {
    @Override
    public void execute(Fx702pEmulator anEmulator)
    {
      anEmulator.endProgram();
    }
  }

  protected class StepCommand extends ContinuationCommand
  {
    @Override
    public void execute(Fx702pEmulator anEmulator)
    {
      Fx702pEmulator.ProgramStatus programStatus = anEmulator.continueExecution();
      switch (programStatus)
      {
        case RUNNING:
        {
          if (anEmulator.isContinuationDebuggable())
          {
            anEmulator.suspendProgram();
          }
          else
          {
            addCommand(new StepCommand());
          }
          break;
        }
        case SUSPENDED:
        {
          processContinuationCommand = true;
          addCommand(new BreakpointStepCommand());
          break;
        }
        case ENDED:
        {
          anEmulator.endProgram();
          break;
        }
      }
    }
  }

  protected class BreakpointStepCommand extends StepCommand
  {
    @Override
    public void execute(Fx702pEmulator anEmulator)
    {
      processContinuationCommand = false;
      super.execute(anEmulator);
    }
  }

  protected class DelayedTimerTask extends TimerTask
  {
    public DelayedTimerTask(Command aCommand)
    {
      command = aCommand;
    }

    @Override
    public void run()
    {
      if (!executed)
      {
        addCommand(command);
        executed = true;
      }
    }

    public Command getCommand()
    {
      return command;
    }

    public boolean isExecuted()
    {
      return executed;
    }

    protected Command command;
    protected boolean executed = false;
  }

  protected interface EngineBehavior
  {
    public void setRunMode();
    public void runProgram();
    public void endProgram();
    public void stopProgram();
    public void contProgram();
    public void suspendProgram();
    public void resumeProgram();
    public void stepInProgram();
    public void debugAndStepActiveProgram();
    public void stop();
    public void cont();
    public void input(String anInputPrompt);
    public void reportFx702pError(Fx702pException anError);
    public void allClear();
    public void startScroll();
    public void endScroll();
    public void fastScroll();
    public void normalScroll();
    public void endWaitAfterPrint();
    public void cancelWaitAfterPrint();
    public void addToPrintAndScroll(String aString);
  }

  protected abstract class EmptyBehavior implements EngineBehavior
  {
    public void allClear()
    {
    }

    public void cont()
    {
    }

    public void contProgram()
    {
    }

    public void endProgram()
    {
    }

    public void endScroll()
    {
    }

    public void endWaitAfterPrint()
    {
    }

    public void cancelWaitAfterPrint()
    {
    }

    public void input(String anInputPrompt)
    {
    }

    public void reportFx702pError(Fx702pException aAnError)
    {
    }

    public void resumeProgram()
    {
    }

    public void runProgram()
    {
    }

    public void setRunMode()
    {
    }

    public void startScroll()
    {
    }

    public void fastScroll()
    {
    }

    public void normalScroll()
    {
    }

    public void stepInProgram()
    {
    }

    public void debugAndStepActiveProgram()
    {
    }

    public void stop()
    {
    }

    public void stopProgram()
    {
    }

    public void suspendProgram()
    {
    }

    public void addToPrintAndScroll(String aString)
    {

    }
  }

  protected abstract class BasicBehavior extends EmptyBehavior
  {
    @Override
    public void stopProgram()
    {
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
      stopProcessingContinuationCommands();
    }

    @Override
    public void suspendProgram()
    {
      setBehavior(PROGRAM_SUSPENDED_BEHAVIOR);
      processContinuationCommand = false;
      continuationCommand = null;
    }

    @Override
    public void startScroll()
    {
      processContinuationCommand = false;
      setBehavior(new ScrollBehavior(this));
    }

    @Override
    public void addToPrintAndScroll(String aString)
    {
      addDelayedCommand(new PrintAndScrollCommand(aString), getPrintScrollDelay());
    }
  }

  protected class RunModeBehavior extends BasicBehavior
  {
    @Override
    public void runProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      processContinuationCommand = true;
      addCommand(new ContinuationCommand());
    }

    @Override
    public void contProgram()
    {
      runProgram();
    }

    @Override
    public void debugAndStepActiveProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      processContinuationCommand = true;
      addCommand(new StepCommand());
    }
  }

  protected class ProgramRunningBehavior extends BasicBehavior
  {
    @Override
    public void setRunMode()
    {
      setBehavior(RUN_MODE_BEHAVIOR);
      stopProcessingContinuationCommands();
    }

    @Override
    public void endProgram()
    {
      setRunMode();
    }

    @Override
    public void stop()
    {
      emulator.stopProgram();
    }

    @Override
    public void input(String anInputPrompt)
    {
      setBehavior(INPUT_BEHAVIOR);
      processContinuationCommand = false;
      inputMode = true;
    }

    @Override
    public void reportFx702pError(Fx702pException aAnError)
    {
      setBehavior(ERROR_IN_PROGRAM_BEHAVIOR);
      stopProcessingContinuationCommands();
    }
  }

  protected class ErrorInProgramBehavior extends EmptyBehavior
  {
    @Override
    public void endProgram()
    {
      setBehavior(RUN_MODE_BEHAVIOR);
      stopProcessingContinuationCommands();
    }

    @Override
    public void allClear()
    {
      addCommand(new EndProgramCommand());
    }
  }

  protected class ProgramStoppedBehavior extends RunModeBehavior
  {
    @Override
    public void cont()
    {
      emulator.contProgram();
    }

    @Override
    public void stepInProgram()
    {
      processContinuationCommand = true;
      inputMode = false;
      addCommand(new StepCommand());
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void setRunMode()
    {
      setBehavior(RUN_MODE_BEHAVIOR);
      stopProcessingContinuationCommands();
    }
  }

  protected class ProgramSuspendedBehavior extends EmptyBehavior
  {
    @Override
    public void resumeProgram()
    {
      processContinuationCommand = true;
      inputMode = false;
      addCommand(new ContinuationCommand());
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void stepInProgram()
    {
      runDelayedCommand();
      processContinuationCommand = true;
      inputMode = false;
      addCommand(new StepCommand());
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void startScroll()
    {
      setBehavior(new ScrollBehavior(this));
    }

    @Override
    public void endProgram()
    {
      setBehavior(RUN_MODE_BEHAVIOR);
      stopProcessingContinuationCommands();
    }

    @Override
    public void addToPrintAndScroll(String aString)
    {
      addDelayedCommand(new PrintAndScrollCommand(aString), getPrintScrollDelay());
    }

    @Override
    public void input(String anInputPrompt)
    {
      setBehavior(PROGRAM_SUSPENDED_INPUT_BEHAVIOR);
      processContinuationCommand = false;
      inputMode = true;
    }
  }

  protected class ProgramSuspendedInputBehavior extends InputBehavior
  {

    @Override
    public void contProgram()
    {
      processContinuationCommand = true;
      inputMode = false;
      addCommand(new StepCommand());
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }
  }

  protected class InputBehavior extends ProgramRunningBehavior
  {
    @Override
    public void stop()
    {
    }

    @Override
    public void contProgram()
    {
      processContinuationCommand = true;
      inputMode = false;
      addCommand(new ContinuationCommand());
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }
  }

  protected class ErrorBehavior extends BasicBehavior
  {
    public ErrorBehavior(EngineBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
      previousProcessContinuationCommand = processContinuationCommand;
      processContinuationCommand = false;
    }

    @Override
    public void allClear()
    {
      processContinuationCommand = previousProcessContinuationCommand;
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    protected EngineBehavior previousBehavior;
    protected boolean previousProcessContinuationCommand;
  }

  protected class ScrollBehavior extends BasicBehavior
  {
    public ScrollBehavior(EngineBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void endScroll()
    {
      setNormalPrintScrollDelay();
      processContinuationCommand = true;
      resetDelayedCommand();
      setBehavior(previousBehavior);
      if (afterScrollCommand != null)
      {
        addCommand(afterScrollCommand);
      }
    }

    @Override
    public void allClear()
    {
      cancelDelayedCommand();
      setBehavior(previousBehavior);
    }

    @Override
    public void fastScroll()
    {
      setFastPrintScrollDelay();
      resetScrolling();
    }

    @Override
    public void normalScroll()
    {
      setNormalPrintScrollDelay();
      resetScrolling();
    }

    protected void resetScrolling()
    {
      String stillToPrint = null;
      if (delayedTimerTask != null && delayedTimerTask.getCommand() instanceof PrintAndScrollCommand)
      {
        stillToPrint = ((PrintAndScrollCommand)delayedTimerTask.getCommand()).getStringToPrintAndScroll();
      }
      cancelDelayedCommand();
      addDelayedCommand(new PrintAndScrollCommand(stillToPrint), getPrintScrollDelay());
    }

    @Override
    public void stop()
    {
      setBehavior(new ScrollStoppedBehavior(previousBehavior));
    }

    protected EngineBehavior previousBehavior;
  }

  protected class ScrollStoppedBehavior extends ScrollBehavior
  {
    public ScrollStoppedBehavior(EngineBehavior aPreviousBehavior)
    {
      super(aPreviousBehavior);
      if (delayedTimerTask != null && delayedTimerTask.getCommand() instanceof PrintAndScrollCommand)
      {
        stillToPrint = ((PrintAndScrollCommand)delayedTimerTask.getCommand()).getStringToPrintAndScroll();
      }
      cancelDelayedCommand();
    }

    @Override
    public void cont()
    {
      setBehavior(new ScrollBehavior(previousBehavior));
      addToPrintAndScroll(stillToPrint);
    }

    protected String stillToPrint = null;
  }

  protected class WaitAfterPrintBehavior extends BasicBehavior
  {
    public WaitAfterPrintBehavior(EngineBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void allClear()
    {
      emulator.cancelWaitAfterPrint();
    }

    @Override
    public void cont()
    {
      cancelDelayedCommand();
    }

    @Override
    public void stop()
    {
      cancelDelayedCommand();
      setBehavior(new WaitAfterPrintStopped(previousBehavior));
    }

    @Override
    public void endWaitAfterPrint()
    {
      setBehavior(previousBehavior);
      if (afterWaitCommand != null)
      {
        addCommand(afterWaitCommand);
        afterWaitCommand = null;
      }
      else
      {
        processContinuationCommand = true;
      }
    }

    @Override
    public void cancelWaitAfterPrint()
    {
      setBehavior(previousBehavior);
      cancelDelayedCommand();
      cancelAfterWaitCommand();
      processContinuationCommand = true;
    }


    @Override
    public void endProgram()
    {
      setBehavior(RUN_MODE_BEHAVIOR);
      stopProcessingContinuationCommands();
    }

    protected EngineBehavior previousBehavior;
  }

  protected class WaitAfterPrintStopped extends WaitAfterPrintBehavior
  {
    public WaitAfterPrintStopped(EngineBehavior aPreviousBehavior)
    {
      super(aPreviousBehavior);
    }

    @Override
    public void stop()
    {
    }
  }

  protected LinkedList<Command> commandsQueue = new LinkedList<Command>();
  protected ContinuationCommand continuationCommand;
  protected boolean processContinuationCommand = false;
  protected boolean inputMode = false;
  protected Timer delayedCommand = null;
  protected DelayedTimerTask delayedTimerTask = null;
  protected Command afterScrollCommand = null;
  protected Command afterWaitCommand = null;
  protected long printScrollDelay = NORMAL_PRINT_SCROLL_DELAY;

  protected Fx702pDefaultEmulator emulator;
  protected EngineBehavior behavior;
  protected Fx702pTemporizer temporizer = new Fx702pTemporizer();

  static public final String DELAYED_COMMAND_NAME = "Delayed Command";

  protected final RunModeBehavior RUN_MODE_BEHAVIOR = new RunModeBehavior();
  protected final ProgramRunningBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final ErrorInProgramBehavior ERROR_IN_PROGRAM_BEHAVIOR = new ErrorInProgramBehavior();
  protected final ProgramStoppedBehavior PROGRAM_STOPPED_BEHAVIOR = new ProgramStoppedBehavior();
  protected final ProgramSuspendedBehavior PROGRAM_SUSPENDED_BEHAVIOR = new ProgramSuspendedBehavior();
  protected final ProgramSuspendedInputBehavior PROGRAM_SUSPENDED_INPUT_BEHAVIOR = new ProgramSuspendedInputBehavior();
  protected final InputBehavior INPUT_BEHAVIOR = new InputBehavior();
}
