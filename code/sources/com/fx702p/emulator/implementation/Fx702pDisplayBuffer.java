package com.fx702p.emulator.implementation;

import static com.fx702p.emulator.Fx702pConstants.DISPLAY_SIZE;

import java.util.Arrays;

import com.fx702p.emulator.*;
import com.fx702p.emulator.commands.Command;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pDisplayBuffer extends Fx702pAbstractEmulatorComponent
{
  public Fx702pDisplayBuffer(Fx702pEmulator anEmulator)
  {
    emulator = anEmulator;
    setInitialBehavior();
    clearDisplayBuffer();
  }

  protected void setInitialBehavior()
  {
    setBehavior(PRINTING_BEHAVIOR);
  }

  protected void setBehavior(DisplayBufferBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  public void clearDisplay()
  {
    behavior.clearDisplay();
  }

  protected void clearDisplayBuffer()
  {
    Arrays.fill(displayBufferArray, 0, DISPLAY_SIZE, ' ');
  }

  public String print(String aString)
  {
    return behavior.print(aString);
  }

  public String printAndScroll(String aString)
  {
    return behavior.printAndScroll(aString);
  }

  @Override
  public void setRunMode()
  {
    behavior.setRunMode();
  }

  @Override
  public void resultPrinted()
  {
    behavior.resultPrinted();
  }

  @Override
  public void reportFx702pError(Fx702pException anError)
  {
    behavior.reportFx702pError(anError);
  }

  @Override
  public void enterString(String aString)
  {
    behavior.enterString(aString);
  }

  @Override
  public void execute(String aString)
  {
    behavior.execute(aString);
  }

  @Override
  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  @Override
  public void runProgram()
  {
    behavior.runProgram();
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
  public void endProgram()
  {
    behavior.endProgram();
  }

  @Override
  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
    behavior.waitAfterPrint(aPrintWait, null);
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
  public void allClear()
  {
    behavior.allClear();
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

  protected interface DisplayBufferBehavior extends Fx702pEmulatorComponent
  {
    public void clearDisplay();
    public String print(String aString);
    public String printAndScroll(String aString);
  }

  protected class AbstractDisplayBufferBehavior extends Fx702pAbstractEmulatorComponent implements DisplayBufferBehavior
  {
    public void clearDisplay()
    {
      clearDisplayBuffer();
      emulator.getDisplay().print(displayBufferArray);
      emulator.setCursorPosition(0);
    }

    public String print(String aString)
    {
      String stillToPrint = null;
      Fx702pDisplay display = emulator.getDisplay();
      int cursorPosition = display.getCursorPosition();
      for (int i = 0, last = aString.length(); i < last; i++)
      {
        if (cursorPosition >= DISPLAY_SIZE)
        {
          stillToPrint = aString.substring(i);
          break;
        }
        else
        {
          displayBufferArray[cursorPosition++] = aString.charAt(i);
        }
      }
      display.print(displayBufferArray);
      display.setCursorPosition(cursorPosition);
      return stillToPrint;
    }

    public String printAndScroll(String aString)
    {
      String stillToPrint = null;

      System.arraycopy(displayBufferArray, 1, displayBufferArray, 0, DISPLAY_SIZE - 1);
      displayBufferArray[DISPLAY_SIZE - 1] = aString.charAt(0);
      emulator.getDisplay().print(displayBufferArray);
      if (aString.length() > 1)
      {
        stillToPrint = aString.substring(1);
      }

      return stillToPrint;
    }

    @Override
    public void resultPrinted()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_ENTERING_STRING_BEHAVIOR);
    }

    @Override
    public void runProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      clearDisplay();
    }

    @Override
    public void contProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      clearDisplay();
    }

    @Override
    public void endProgram()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_ENTERING_STRING_BEHAVIOR);
      clearDisplay();
    }

    @Override
    public void execute(String aString)
    {
      if (aString != null && aString.length() != 0)
      {
        clearDisplay();
      }
    }

    @Override
    public void input(String anInputPrompt)
    {
      setBehavior(START_INPUT_BEHAVIOR);
    }

    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setBehavior(new ErrorBehavior(this));
    }

    @Override
    public void startScroll()
    {
      setBehavior(new ScrollBehavior(this));
    }
  }

  protected class PrintingBehavior extends AbstractDisplayBufferBehavior
  {
    @Override
    public void stopProgram()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_PRINT_OR_ENTER_BEHAVIOR);
    }

    @Override
    public void endWaitAfterPrint()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_PRINT_OR_ENTER_BEHAVIOR);
    }

    @Override
    public void endProgram()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_PRINT_OR_ENTER_BEHAVIOR);
    }
  }

  protected class ClearDisplayBeforeEnteringStringBehavior extends PrintingBehavior
  {
    @Override
    public void enterString(String aString)
    {
      clearDisplay();
      setBehavior(PRINTING_BEHAVIOR);
    }
  }

  protected class ClearDisplayBeforePrintOrEnterBehavior extends ClearDisplayBeforeEnteringStringBehavior
  {
    @Override
    public void allClear()
    {
      setBehavior(PRINTING_BEHAVIOR);
    }

    @Override
    public String print(String aString)
    {
      clearDisplay();
      String stillToPrint = super.print(aString);
      setBehavior(PRINTING_BEHAVIOR);
      return stillToPrint;
    }
  }

  protected class ProgramRunningBehavior extends PrintingBehavior
  {
    @Override
    public void resultPrinted()
    {
    }

    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setBehavior(new ProgramErrorBehavior(this));
    }
  }

  protected class StartInputBehavior extends ClearDisplayBeforeEnteringStringBehavior
  {
    @Override
    public void enterString(String aString)
    {
      clearDisplay();
      setBehavior(INPUT_BEHAVIOR);
    }


    @Override
    public void endProgram()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_PRINT_OR_ENTER_BEHAVIOR);
      clearDisplay();
    }
  }

  protected class InputBehavior extends PrintingBehavior
  {
    @Override
    public void contProgram()
    {
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
      clearDisplay();
    }

    @Override
    public void endProgram()
    {
      setBehavior(CLEAR_DISPLAY_BEFORE_PRINT_OR_ENTER_BEHAVIOR);
      clearDisplay();
    }
  }

  protected class ErrorBehavior extends AbstractDisplayBufferBehavior
  {
    public ErrorBehavior(DisplayBufferBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    protected DisplayBufferBehavior previousBehavior;
  }

  protected class ProgramErrorBehavior extends ErrorBehavior
  {
    public ProgramErrorBehavior(DisplayBufferBehavior aPreviousBehavior)
    {
      super(aPreviousBehavior);
    }

    @Override
    public void allClear()
    {
      clearDisplay();
      super.allClear();
    }
  }

  protected class ScrollBehavior extends PrintingBehavior
  {
    public ScrollBehavior(DisplayBufferBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void cont()
    {
    }

    @Override
    public void stop()
    {
    }

    @Override
    public void endScroll()
    {
      setBehavior(previousBehavior);
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
    }

    @Override
    public void resultPrinted()
    {
      // We are being called before endScroll. So we will be returning to
      // this behavior after the scroll
      previousBehavior = CLEAR_DISPLAY_BEFORE_ENTERING_STRING_BEHAVIOR;
    }

    protected DisplayBufferBehavior previousBehavior;
  }

  protected Fx702pEmulator emulator;
  protected char[] displayBufferArray = new char[DISPLAY_SIZE];
  protected DisplayBufferBehavior behavior;

  protected final PrintingBehavior PRINTING_BEHAVIOR = new PrintingBehavior();
  protected final ClearDisplayBeforeEnteringStringBehavior CLEAR_DISPLAY_BEFORE_ENTERING_STRING_BEHAVIOR = new ClearDisplayBeforeEnteringStringBehavior();
  protected final ClearDisplayBeforePrintOrEnterBehavior CLEAR_DISPLAY_BEFORE_PRINT_OR_ENTER_BEHAVIOR = new ClearDisplayBeforePrintOrEnterBehavior();
  protected final ProgramRunningBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final StartInputBehavior START_INPUT_BEHAVIOR = new StartInputBehavior();
  protected final InputBehavior INPUT_BEHAVIOR = new InputBehavior();
}
