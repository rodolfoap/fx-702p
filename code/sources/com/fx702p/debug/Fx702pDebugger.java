package com.fx702p.debug;

import com.fx702p.emulator.commands.Command;
import com.fx702p.emulator.implementation.Fx702pEmulatorComponent;
import com.fx702p.interpreters.*;

public interface Fx702pDebugger extends ExtraVisitor, Fx702pEmulatorComponent
{
  public abstract class AbstractFx702pDebugger extends Fx702pAbstractParserVisitor implements Fx702pDebugger
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

    public void enterString(String aString)
    {
    }

    public void execute(String aString)
    {
    }

    public void input(String aAnInputPrompt)
    {
    }

    public void reportFx702pError(Fx702pException aAnError)
    {
    }

    public void resultPrinted()
    {
    }

    public void runProgram()
    {
    }

    public void setRunMode()
    {
    }

    public void setWrtMode()
    {
    }

    public void startScroll()
    {
    }

    public void stop()
    {
    }

    public void stopProgram()
    {
    }

    public void waitAfterPrint(int aPrintWait, Command aCommand)
    {
    }

    public void cancelWaitAfterPrint()
    {
    }
  }
}
