package com.fx702p.emulator.implementation;

import java.io.*;

import com.fx702p.debug.Fx702pDebugger;
import com.fx702p.emulator.*;
import com.fx702p.emulator.commands.*;
import com.fx702p.emulator.implementation.Fx702pEmulatorComponent.MethodCaller;
import com.fx702p.interpreters.*;
import com.fx702p.parser.ParseException;

public class Fx702pDefaultEmulator implements Fx702pEmulator
{
  public Fx702pDefaultEmulator(Fx702pDisplay aFx702pDisplay)
  {
    display = aFx702pDisplay;
    memory = new Fx702pDefaultMemory();
    modes = new Fx702pModes(this);

    keyboard = new Fx702pKeyboard(this);
    cursor = new Fx702pCursor(this);
    displayBuffer = new Fx702pDisplayBuffer(this);
    inputBuffer = new Fx702pInputBuffer(this);
    interpreterHolder = new Fx702pInterpreterHolder(this);

    engine = new Fx702pEngine(this);
  }

  protected void callMethod(MethodCaller aMethodCaller)
  {
    // Warning: call order is important
    aMethodCaller.callMethod(keyboard);
    aMethodCaller.callMethod(displayBuffer);
    aMethodCaller.callMethod(inputBuffer);
    aMethodCaller.callMethod(cursor);
    aMethodCaller.callMethod(display);
    if (debugger != null)
    {
      aMethodCaller.callMethod(debugger);
    }
    interpreterHolder.forwardCall(aMethodCaller);
    aMethodCaller.callMethod(engine);
  }

  public Fx702pDisplay getDisplay()
  {
    return display;
  }

  public Fx702pMemory getMemory()
  {
    return memory;
  }

  public void run()
  {
    displayRunModeMessage();
    engine.run();
  }

  public void setMode(int aMode)
  {
    modes.setMode(aMode);
  }

  public void setRunMode()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.setRunMode();
        }
      });
    displayRunModeMessage();
  }

  public void setWrtMode()
  {
  }

  public void setDefm(int aDefm)
  {
    memory.setDefm(aDefm);
    print("VAR: " + memory.getVariablesCount() + " PRG: " + memory.getProgramStepsCount());
    resultPrinted();
  }

  public void keyPressed(Fx702pKey aKey)
  {
    keyboard.keyPressed(aKey);
  }

  public void keyRepeated(Fx702pKey aKey)
  {
    keyboard.keyRepeated(aKey);
  }

  public void keyReleased(Fx702pKey aKey)
  {
    keyboard.keyReleased(aKey);
  }

  public void clearPrefixes()
  {
    keyboard.clearPrefixes();
  }

  public void f1Prefix()
  {
    keyboard.f1Prefix();
  }

  public void f2Prefix()
  {
    keyboard.f2Prefix();
  }

  public void modePrefix()
  {
    keyboard.modePrefix();
  }

  public void arcPrefix()
  {
    keyboard.arcPrefix();
  }

  public void hypPrefix()
  {
    keyboard.hypPrefix();
  }

  public Character getKey()
  {
    return keyboard.getKey();
  }

  public void allClear()
  {
    setReusableResult(false);
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.allClear();
        }
      });
  }

  public void loadProgram(File aProgramFile) throws Exception
  {
    try
    {
      memory.load(aProgramFile, interpreterHolder.getCalculator(), display);
      afterLoad();
    }
    catch (ParseException exception)
    {
      display.reportError("Cannot load Programs File", "Cannot parse file " + aProgramFile.getName(), exception);
    }
    catch (Throwable exception)
    {
      display.reportError("Cannot load Programs File", "Error while reading " + aProgramFile.getName() + getLoadErrorLine(exception), exception);
    }
  }

  public void reloadProgram() throws Exception
  {
    if (memory.getActiveProgram().canBeLoaded())
    {
      try
      {
        memory.getActiveProgram().reload(interpreterHolder.getCalculator(), display);
        afterLoad();
      }
      catch (FileNotFoundException exception)
      {
        display.reportError("Cannot reload", "Error whilst reloading from file", exception);
      }
      catch (ParseException exception)
      {
        display.reportError("Cannot reload", "Cannot parse file " + memory.getActiveProgram().getProgramFileName(), exception);
      }
      catch (Throwable exception)
      {
        display.reportError("Cannot reload", "Error while reading " + memory.getActiveProgram().getProgramFileName() + getLoadErrorLine(exception), exception);
      }
    }
  }

  protected String getLoadErrorLine(Throwable anException)
  {
    if (anException instanceof Fx702pException)
    {
      int line = ((Fx702pException)anException).getLine();
      if (line >= 0)
      {
        return ", line " + line;
      }
    }
    return "";
  }

  public void reloadAll() throws Exception
  {
    if (memory.canBeLoaded())
    {
      try
      {
        memory.reload(interpreterHolder.getCalculator(), display);
        afterLoad();
      }
      catch (FileNotFoundException exception)
      {
        display.reportError("Cannot reload All", "Error whilst reloading All from file", exception);
      }
      catch (ParseException exception)
      {
        display.reportError("Cannot reload All", "Cannot parse file " + memory.getProgramsFileName(), exception);
      }
      catch (Throwable exception)
      {
        display.reportError("Cannot reload All", "Error while reading " + memory.getProgramsFileName() + getLoadErrorLine(exception), exception);
      }
    }
  }

  protected void afterLoad()
  {
    if (memory.isAllLoaded())
    {
      setActiveProgramIndex(0);
      displayRunModeMessage();
    }

    if (!getActiveProgram().isEmpty())
    {
      callMethod(new MethodCaller()
        {
          public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
          {
            aFx702pEmulatorComponent.loadProgram(getActiveProgramIndex());
          }
        });
    }
    else
    {
      callMethod(new MethodCaller()
        {
          public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
          {
            aFx702pEmulatorComponent.clearProgram(getActiveProgramIndex());
          }
        });
    }
  }

  public void saveProgram()
  {
    if (memory.getActiveProgram().canBeSaved(memory))
    {
      try
      {
        memory.getActiveProgram().save(this);
      }
      catch (IOException exception)
      {
        display.reportError("Cannot save", "Error whilst saving to file", exception);
      }
    }
  }

  public void saveProgramAs(File aProgramFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag)
  {
    try
    {
      memory.getActiveProgram().saveAs(this, aProgramFile, aSaveVariablesFlag, aSaveBreakpointsFlag);
    }
    catch (IOException exception)
    {
      display.reportError("Cannot save", "Error whilst saving to file", exception);
    }
  }

  public void saveAll()
  {
    if (memory.canBeSaved())
    {
      try
      {
        memory.saveAll(getDisplay());
      }
      catch (IOException exception)
      {
        display.reportError("Cannot save", "Error whilst saving to file", exception);
      }
    }
  }

  public void saveAllAs(File aProgramsFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag)
  {
    try
    {
      memory.saveAllAs(aProgramsFile, aSaveVariablesFlag, aSaveBreakpointsFlag);
    }
    catch (IOException exception)
    {
      display.reportError("Cannot save", "Error whilst saving to file", exception);
    }
  }


  public void runActiveProgram()
  {
    // This command can be called from both the IO Thread and the Emulator one
    // In the first case, we add a command to call ourselves again in the
    // right thread
    // When in the emulator thread, we effectively run the active program.
    if (Thread.currentThread().equals(this))
    {
      setProgramRunningMode();
    }
    else
    {
      addCommand(new AbstractCommand()
        {
          @Override
          public void execute(Fx702pEmulator anEmulator)
          {
            setProgramRunningMode();
          }
        });
    }
  }

  protected void forwardDebugAndStepActiveProgram()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.debugAndStepActiveProgram();
        }
      });
  }

  public void setDebugger(Fx702pDebugger aDebugger)
  {
    debugger = aDebugger;
  }

  public void debugActiveProgram()
  {
    if (debugger != null)
    {
      interpreterHolder.getBasicInterpreter().setExtraVisitor(debugger);
      runActiveProgram();
    }
  }

  public void debugAndStepActiveProgram()
  {
    if (debugger != null)
    {
      interpreterHolder.getBasicInterpreter().setExtraVisitor(debugger);
      if (Thread.currentThread().equals(this))
      {
        debugAndStepActiveProgram();
      }
      else
      {
        addCommand(new AbstractCommand()
          {
            @Override
            public void execute(Fx702pEmulator anEmulator)
            {
              forwardDebugAndStepActiveProgram();
            }
          });
      }
    }
  }

  public void endDebug()
  {
    debugger = null;
  }

  public void stepInProgram()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.stepInProgram();
        }
      });
  }

  public ProgramStatus continueExecution()
  {
    return interpreterHolder.getBasicInterpreter().continueExecution();
  }

  public boolean isContinuationDebuggable()
  {
    return interpreterHolder.getBasicInterpreter().isContinuationDebuggable();
  }

  public void setSpeed(int aSpeed)
  {
    engine.setSpeed(aSpeed);
  }

  public void nextLoop(final Variable aVariable)
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.nextLoop(aVariable);
        }
      });
  }

  public void endLoop(final Variable aVariable)
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.endLoop(aVariable);
        }
      });
  }

  protected void setProgramRunningMode()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.runProgram();
        }
      });
  }

  public void endProgram()
  {
    interpreterHolder.getBasicInterpreter().setExtraVisitor(null);
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.endProgram();
        }
      });
  }

  public void stopProgram()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.stopProgram();
        }
      });
  }

  public void contProgram()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.contProgram();
        }
      });
  }

  public void suspendProgram()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.suspendProgram();
        }
      });
  }


  public void resumeProgram()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.resumeProgram();
        }
      });
  }


  public void stop()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.stop();
        }
      });
  }

  public void lastAnswer()
  {
    interpreterHolder.lastAnswer();
  }

  public void cont()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.cont();
        }
      });
  }

  public void printResult(String aString)
  {
    if (aString == null)
    {
      clearDisplay();
    }
    else
    {
      print(aString);
      resultPrinted();
    }
  }

  protected void resultPrinted()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.resultPrinted();
        }
      });
  }

  public void print(String aString)
  {
    printAndExecute(aString, null);
  }

  public void print(MultiLinePrinter aMultiLinePrinter)
  {
    display.showBusy(true);
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.startMultiLinePrint();
        }
      });
    printNextLine(aMultiLinePrinter);
  }

  public void printNextLine(MultiLinePrinter aMultiLinePrinter)
  {
    String line = aMultiLinePrinter.getLine();
    if (line != null)
    {
      int timeToWait = aMultiLinePrinter.getTimeToWait();
      aMultiLinePrinter.next();
      clearDisplay();
      printAndExecute(line, new MultiLineWaitCommand(timeToWait, aMultiLinePrinter));
    }
    else
    {
      callMethod(new MethodCaller()
        {
          public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
          {
            aFx702pEmulatorComponent.endMultiLinePrint();
          }
        });
      display.showBusy(false);
    }
  }


  public void printAndExecute(String aString, Command aCommand)
  {
    String stillToPrint = displayBuffer.print(aString);
    if (stillToPrint != null)
    {
      startScroll();
      engine.addToPrintAndScroll(stillToPrint, aCommand);
    }
    else
    {
      engine.addCommand(aCommand);
    }
  }

  protected void startScroll()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.startScroll();
        }
      });
  }

  public void printAndScroll(String aString)
  {
    String stillToPrint = displayBuffer.printAndScroll(aString);
    if (stillToPrint == null)
    {
      endScroll();
    }
    else
    {
      engine.addToPrintAndScroll(stillToPrint);
    }
  }

  public void fastScroll()
  {
    engine.fastScroll();
  }

  public void normalScroll()
  {
    engine.normalScroll();
  }

  public void endScroll()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.endScroll();
        }
      });
  }

  protected void endPrint()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.resultPrinted();
        }
      });
  }

  public void printWait(final int aPrintWait, final Command aCommand)
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.waitAfterPrint(aPrintWait, aCommand);
        }
      });
  }

  public void endWaitAfterPrint()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.endWaitAfterPrint();
        }
      });
  }

  public void cancelWaitAfterPrint()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.cancelWaitAfterPrint();
        }
      });
    display.showBusy(false);
  }

  public void setCursorPosition(int aCursorPosition)
  {
    display.setCursorPosition(aCursorPosition);
  }

  public void clearDisplay()
  {
    setReusableResult(false);
    displayBuffer.clearDisplay();
  }

  public boolean isReusableResult()
  {
    return reusableResult;
  }

  public void setReusableResult(boolean aReusableResultFlag)
  {
    reusableResult = aReusableResultFlag;
  }

  public void input(final String anInputPrompt)
  {
    printAndExecute(anInputPrompt + Fx702pConstants.INPUT_PROMPT, new StartInputCommand(anInputPrompt));
  }

  public void startInput(final String anInputPrompt)
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.input(anInputPrompt);
        }
      });
  }

  public void enterString(final String aString)
  {
    setReusableResult(false);
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.enterString(aString);
        }
      });
  }

  public void clear()
  {
    inputBuffer.clear();
  }

  public void insertSpace()
  {
    inputBuffer.insertSpace();
  }

  public void moveCursorRight()
  {
    inputBuffer.moveCursorRight();
  }

  public void moveCursorLeft()
  {
    inputBuffer.moveCursorLeft();
  }

  public void home()
  {
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.home();
        }
      });
  }

  public void reportError(final Throwable anError)
  {
    if (anError instanceof Fx702pException)
    {
      callMethod(new MethodCaller()
        {
          public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
          {
            aFx702pEmulatorComponent.reportFx702pError((Fx702pException)anError);
          }
        });
    }
    else
    {
      display.reportError("Internal Error", "Unknown errror", anError);
    }
  }

  public boolean askConfirmation(String aQuestion, String aDetailedQuestion)
  {
    return display.askConfirmation(aQuestion, aDetailedQuestion);
  }

  public void execute()
  {
    final String toExecute = inputBuffer.toString();
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.execute(toExecute);
        }
      });
  }

  public Fx702pBasicProgram getProgram(int aProgramIndex)
  {
    return getMemory().getProgram(aProgramIndex);
  }

  public Fx702pBasicProgram getActiveProgram()
  {
    return getMemory().getActiveProgram();
  }

  public int getActiveProgramIndex()
  {
    return getMemory().getActiveProgramIndex();
  }

  public void setActiveProgramIndex(final int aProgramIndex)
  {
    getMemory().setActiveProgramIndex(aProgramIndex);
    callMethod(new MethodCaller()
      {
        public void callMethod(Fx702pEmulatorComponent aFx702pEmulatorComponent)
        {
          aFx702pEmulatorComponent.setActiveProgramIndex(aProgramIndex);
        }
      });
  }

  public void addCommand(Command aCommand)
  {
    engine.addCommand(aCommand);
  }

  public void stat()
  {
    getCurrentInterpreter().stat(inputBuffer.toString());
  }

  public void del()
  {
    getCurrentInterpreter().del(inputBuffer.toString());
  }

  public void astat()
  {
    getCurrentInterpreter().astat();
  }

  public void sac()
  {
    getCurrentInterpreter().sac();
  }

  protected Fx702pAbstractInterpreter getCurrentInterpreter()
  {
    return interpreterHolder.getCurrentInterpreter();
  }

  public void displayRunModeMessage()
  {
    clearDisplay();
    print("READY P" + interpreterHolder.getBasicInterpreter().getActiveProgramIndex());
    // Fx702p behavior after the Run Mode message is displayed is the same than
    // after a result is printed
    resultPrinted();
  }

  protected Fx702pDisplay display;
  protected Fx702pMemory memory;

  protected Fx702pKeyboard keyboard;
  protected Fx702pModes modes;

  protected Fx702pCursor cursor;
  protected Fx702pDisplayBuffer displayBuffer;
  protected Fx702pInputBuffer inputBuffer;

  protected Fx702pEngine engine;
  protected Fx702pInterpreterHolder interpreterHolder;
  protected Fx702pDebugger debugger = null;

  protected boolean clearDisplayBeforeNextPrint = false;
  protected boolean setCursorVisibleAfterNextChar = false;
  protected boolean reusableResult = false;
}
