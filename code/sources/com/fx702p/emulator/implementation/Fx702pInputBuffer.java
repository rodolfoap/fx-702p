package com.fx702p.emulator.implementation;

import static com.fx702p.emulator.Fx702pConstants.DISPLAY_SIZE;
import static com.fx702p.emulator.Fx702pConstants.INPUT_BUFFER_MAX_SIZE;

import java.util.Arrays;

import com.fx702p.emulator.*;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pInputBuffer extends Fx702pAbstractEmulatorComponent
{
  public Fx702pInputBuffer(Fx702pEmulator anEmulator)
  {
    emulator = anEmulator;
    setInitialBehavior();
  }

  protected void setInitialBehavior()
  {
    setBehavior(CALCULATOR_BEHAVIOR);
  }

  protected void setBehavior(InputBufferBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  @Override
  public String toString()
  {
    return new String(inputBufferArray, 0, inputBufferSize);
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
  public void execute(String aString)
  {
    behavior.execute(aString);
  }

  @Override
  public void enterString(String aString)
  {
    behavior.enterString(aString);
  }

  @Override
  public void home()
  {
    behavior.home();
  }

  public void insertSpace()
  {
    behavior.insertSpace();
  }

  public void moveCursorRight()
  {
    behavior.moveCursorRight();
  }

  public void moveCursorLeft()
  {
    behavior.moveCursorLeft();
  }

  public void clear()
  {
    behavior.clear();
  }

  @Override
  public void allClear()
  {
    behavior.allClear();
  }

  @Override
  public void reportFx702pError(Fx702pException anError)
  {
    behavior.reportFx702pError(anError);
  }

  @Override
  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  protected Fx702pDisplay getDisplay()
  {
    return emulator.getDisplay();
  }

  protected int enterChar(char aChar, int aPosition)
  {
    if (inputBufferSize < INPUT_BUFFER_MAX_SIZE)
    {
      int inputBufferCurrentPosition = inputBufferFirstDisplayedIndex + getDisplay().getCursorPosition() + aPosition;
      inputBufferArray[inputBufferCurrentPosition] = aChar;
      if (inputBufferCurrentPosition == inputBufferSize)
      {
        inputBufferSize++;
      }

      if (getDisplay().getCursorPosition() < DISPLAY_SIZE - 1)
      {
        return 1;
      }
      else
      {
        inputBufferFirstDisplayedIndex++;
        return 0;
      }
    }
    else
    {
      return 0;
    }
  }

  protected void displayInputBuffer()
  {
    displayInputBuffer(0);
  }

  protected void displayInputBuffer(int aCursorRightMoves)
  {
    int displayedLength = Math.min(inputBufferSize - inputBufferFirstDisplayedIndex, DISPLAY_SIZE);
    char[] inputBufferToPrint = new char[displayedLength];
    System.arraycopy(inputBufferArray, inputBufferFirstDisplayedIndex, inputBufferToPrint, 0, displayedLength);
    getDisplay().print(inputBufferToPrint);
    getDisplay().moveCursorRight(aCursorRightMoves);
  }

  protected void clearInputBuffer()
  {
    Arrays.fill(inputBufferArray, 0, INPUT_BUFFER_MAX_SIZE, ' ');
    inputBufferSize = 0;
    inputBufferFirstDisplayedIndex = 0;
  }

  protected interface InputBufferBehavior extends Fx702pEmulatorComponent
  {
    public void home();
    public void insertSpace();
    public void moveCursorRight();
    public void moveCursorLeft();
    public void clear();
  }

  protected abstract class AbstractInputBufferBehavior extends Fx702pAbstractEmulatorComponent implements InputBufferBehavior
  {
    @Override
    public void setRunMode()
    {
      setBehavior(CALCULATOR_BEHAVIOR);
      clearInputBuffer();
    }

    @Override
    public void runProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      clearInputBuffer();
    }

    @Override
    public void endProgram()
    {
      setBehavior(CALCULATOR_BEHAVIOR);
      clearInputBuffer();
    }

    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setBehavior(new ErrorBehavior(this));
    }

    @Override
    public void execute(String aString)
    {
    }

    @Override
    public void enterString(String aString)
    {
    }

    @Override
    public void home()
    {
    }

    public void insertSpace()
    {
    }

    public void moveCursorRight()
    {
    }

    public void moveCursorLeft()
    {
    }

    public void clear()
    {
    }
  }

  protected class CalculatorBehavior extends AbstractInputBufferBehavior
  {
    @Override
    public void execute(String aString)
    {
      clearInputBuffer();
    }

    @Override
    public void enterString(String aString)
    {
      int cursorRightMoves = 0;
      for (int i = 0, last = aString.length(); i < last; i++)
      {
        cursorRightMoves += enterChar(aString.charAt(i), cursorRightMoves);
      }
      displayInputBuffer(cursorRightMoves);
    }

    @Override
    public void home()
    {
      inputBufferFirstDisplayedIndex = 0;
      displayInputBuffer();
    }

    @Override
    public void insertSpace()
    {
      if (inputBufferSize > 0 && inputBufferSize < INPUT_BUFFER_MAX_SIZE)
      {
        int inputBufferCurrentPosition = inputBufferFirstDisplayedIndex + getDisplay().getCursorPosition();
        if (inputBufferCurrentPosition != inputBufferSize)
        {
          for (int i = inputBufferSize; i > inputBufferCurrentPosition; i--)
          {
            inputBufferArray[i] = inputBufferArray[i - 1];
          }
          inputBufferSize++;
          inputBufferArray[inputBufferCurrentPosition] = ' ';

          displayInputBuffer();
        }
      }
    }

    @Override
    public void moveCursorRight()
    {
      if (getDisplay().getCursorPosition() == DISPLAY_SIZE - 1 && inputBufferFirstDisplayedIndex + DISPLAY_SIZE <= inputBufferSize)
      {
        inputBufferFirstDisplayedIndex++;
        displayInputBuffer();
      }
      else if (getDisplay().getCursorPosition() < DISPLAY_SIZE)
      {
        getDisplay().moveCursorRight();
      }
    }

    @Override
    public void moveCursorLeft()
    {
      if (inputBufferFirstDisplayedIndex > 0)
      {
        inputBufferFirstDisplayedIndex--;
        displayInputBuffer();
      }
      else
      {
        getDisplay().moveCursorLeft();
      }
    }

    @Override
    public void clear()
    {
      int inputBufferCurrentPosition = inputBufferFirstDisplayedIndex + getDisplay().getCursorPosition();
      if (inputBufferCurrentPosition > 0 && inputBufferSize > 0)
      {
        for (int i = inputBufferCurrentPosition; i < inputBufferSize; i++)
        {
          inputBufferArray[i - 1] = inputBufferArray[i];
        }
        inputBufferSize--;
        inputBufferArray[inputBufferSize] = ' ';

        if (inputBufferFirstDisplayedIndex > 0)
        {
          inputBufferFirstDisplayedIndex--;
        }
        else
        {
          getDisplay().moveCursorLeft();
        }
        displayInputBuffer();
      }
    }

    @Override
    public void allClear()
    {
      clearInputBuffer();
      displayInputBuffer();
    }
  }

  protected class ProgramRunningBehavior extends AbstractInputBufferBehavior
  {
    @Override
    public void stopProgram()
    {
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
    }

    @Override
    public void input(String anInputPrompt)
    {
      setBehavior(INPUT_BEHAVIOR);
      clearInputBuffer();
    }
  }

  protected class ProgramStoppedBehavior extends CalculatorBehavior
  {
    @Override
    public void contProgram()
    {
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
      clearInputBuffer();
    }

    @Override
    public void stepInProgram()
    {
      contProgram();
    }
  }

  protected class InputModeBehavior extends CalculatorBehavior
  {
    @Override
    public void stopProgram()
    {
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
    }

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
      clearInputBuffer();
      getDisplay().print(Fx702pConstants.INPUT_PROMPT.toCharArray());
    }

    @Override
    public void home()
    {
      inputBufferFirstDisplayedIndex = 0;
      displayInputBuffer();
    }
  }

  protected class ErrorBehavior extends Fx702pAbstractEmulatorComponent implements InputBufferBehavior
  {
    public ErrorBehavior(InputBufferBehavior anInputBufferBehavior)
    {
      previousBehavior = anInputBufferBehavior;
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    public void clear()
    {
    }

    @Override
    public void home()
    {
    }

    public void insertSpace()
    {
    }

    public void moveCursorLeft()
    {
    }

    public void moveCursorRight()
    {
    }

    protected InputBufferBehavior previousBehavior;
  }

  protected Fx702pEmulator emulator;
  protected InputBufferBehavior behavior;

  protected char[] inputBufferArray = new char[INPUT_BUFFER_MAX_SIZE];
  protected int inputBufferSize = 0;
  protected int inputBufferFirstDisplayedIndex = 0;

  protected final CalculatorBehavior CALCULATOR_BEHAVIOR = new CalculatorBehavior();
  protected final ProgramRunningBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final ProgramStoppedBehavior PROGRAM_STOPPED_BEHAVIOR = new ProgramStoppedBehavior();
  protected final InputModeBehavior INPUT_BEHAVIOR = new InputModeBehavior();
}
