package com.fx702p.emulator.implementation;

import com.fx702p.emulator.*;
import com.fx702p.emulator.commands.*;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pKeyboard extends Fx702pAbstractEmulatorComponent
{
  public Fx702pKeyboard(Fx702pEmulator anEmulator)
  {
    emulator = anEmulator;

    // We do not call clearPrefixes or setBehavior here because it will do
    // a displayPrefixes and the display may not be initialized
    behavior = NO_PREFIX_BEHAVIOR;
  }

  @Override
  public void allClear()
  {
    behavior.allClear();
  }

  @Override
  public void setRunMode()
  {
    setBehavior(NO_PREFIX_BEHAVIOR);
  }

  @Override
  public void endScroll()
  {
    behavior.endScroll();
  }

  @Override
  public void startScroll()
  {
    behavior.startScroll();
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
    setRunMode();
  }

  @Override
  public void input(String anInputPrompt)
  {
    setBehavior(NO_PREFIX_BEHAVIOR);
  }

  @Override
  public void runProgram()
  {
    potentialKeyTime = NO_POTENTIAL_KEY;
    setBehavior(PROGRAM_RUNNNING_BEHAVIOR);
  }

  @Override
  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
  }

  @Override
  public void endWaitAfterPrint()
  {
  }

  @Override
  public void setWrtMode()
  {
    setBehavior(NO_PREFIX_BEHAVIOR);
  }

  protected void setPotentialKey(char aKey)
  {
    potentialKey = aKey;
    clearPotentialKeyAfterRead = false;
    potentialKeyTime = System.currentTimeMillis();
  }

  protected void endPotentialKey(char aKey)
  {
    potentialKey = aKey;
    clearPotentialKeyAfterRead = true;
    potentialKeyTime = System.currentTimeMillis();
  }

  public Character getKey()
  {
    try
    {
      if (potentialKeyTime != NO_POTENTIAL_KEY)
      {
        if (clearPotentialKeyAfterRead)
        {
          long delay = System.currentTimeMillis() - potentialKeyTime;
          potentialKeyTime = NO_POTENTIAL_KEY;
          clearPotentialKeyAfterRead = false;
          if (delay >= POTENTIAL_KEY_DELAY)
          {
            return null;
          }
        }
        return potentialKey;
      }
      else
      {
        potentialKeyTime = NO_POTENTIAL_KEY;
        return null;
      }
    }
    finally
    {
      try
      {
        Thread.sleep(KEY_EXTRA_SLEEP_TIME);
      }
      catch (InterruptedException exception)
      {
      }
    }
  }

  public void setBehavior(KeyboardBehavior aBehavior)
  {
    behavior = aBehavior;
    behavior.displayPrefixes(emulator.getDisplay());
  }

  public void clearPrefixes()
  {
    behavior.clearPrefixes();
  }

  public void keyPressed(Fx702pKey aKey)
  {
    behavior.keyPressed(aKey);
  }

  public void keyRepeated(Fx702pKey aKey)
  {
    behavior.keyRepeated(aKey);
  }

  public void keyReleased(Fx702pKey aKey)
  {
    behavior.keyReleased(aKey);
  }

  public void addCommand(Command aCommand)
  {
    emulator.addCommand(aCommand);
  }

  public void addCommand(Fx702pKey aKey, Command aCommand)
  {
    if (aCommand == null)
    {
      addCommand(aKey.getCommand());
    }
    else
    {
      addCommand(aCommand);
    }
  }

  public void f1Prefix()
  {
    behavior.f1Prefix();
  }

  public void f2Prefix()
  {
    behavior.f2Prefix();
  }

  public void modePrefix()
  {
    behavior.modePrefix();
  }

  public void arcPrefix()
  {
    behavior.arcPrefix();
  }

  public void hypPrefix()
  {
    behavior.hypPrefix();
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
  public void reportFx702pError(Fx702pException anError)
  {
    behavior.reportFx702pError(anError);
  }

  protected interface KeyboardBehavior
  {
    public void keyPressed(Fx702pKey aKey);
    public void keyRepeated(Fx702pKey aKey);
    public void keyReleased(Fx702pKey aKey);
    public void displayPrefixes(Fx702pDisplay aDisplay);

    public void f1Prefix();
    public void f2Prefix();
    public void modePrefix();
    public void arcPrefix();
    public void hypPrefix();

    public void clearPrefixes();
    public void allClear();
    public void stop();
    public void cont();
    public void stopProgram();
    public void contProgram();
    public void startScroll();
    public void endScroll();
    public void reportFx702pError(Fx702pException anError);
  }

  protected class AbstractBehavior implements KeyboardBehavior
  {
    public void allClear()
    {
    }

    public void arcPrefix()
    {
    }

    public void clearPrefixes()
    {
    }

    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
    }

    public void f1Prefix()
    {
    }

    public void f2Prefix()
    {
    }

    public void hypPrefix()
    {
    }

    public void keyPressed(Fx702pKey aKey)
    {
    }

    public void keyRepeated(Fx702pKey aKey)
    {
      if (aKey == Fx702pKey.LEFT_ARROW || aKey == Fx702pKey.RIGHT_ARROW)
      {
        keyPressed(aKey);
      }
    }

    public void keyReleased(Fx702pKey aKey)
    {
    }

    public void modePrefix()
    {
      setBehavior(MODE_PREFIX_BEHAVIOR);
    }

    public void reportFx702pError(Fx702pException anError)
    {
    }

    public void stop()
    {
    }

    public void cont()
    {
    }

    public void stopProgram()
    {
      setRunMode();
    }

    public void contProgram()
    {
      setBehavior(PROGRAM_RUNNNING_BEHAVIOR);
    }

    public void startScroll()
    {
      setBehavior(new ScrollBehavior(this));
    }

    public void endScroll()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }
  }

  protected class NoPrefixBehavior extends AbstractBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey.getCommand());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(false, false, false, false, false);
    }

    @Override
    public void f1Prefix()
    {
      setBehavior(F1_PREFIX_BEHAVIOR);
    }

    @Override
    public void f2Prefix()
    {
      setBehavior(F2_PREFIX_BEHAVIOR);
    }

    @Override
    public void clearPrefixes()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }

    @Override
    public void allClear()
    {
      clearPrefixes();
    }

    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setBehavior(ERROR_BEHAVIOR);
    }
  }

  protected class F1PrefixBehavior extends NoPrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey, aKey.getF1Command());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(true, false, false, false, false);
    }

    @Override
    public void f1Prefix()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }

    @Override
    public void arcPrefix()
    {
      setBehavior(ARC_PREFIX_BEHAVIOR);
    }

    @Override
    public void hypPrefix()
    {
      setBehavior(HYP_PREFIX_BEHAVIOR);
    }
  }

  protected class F2PrefixBehavior extends NoPrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey, aKey.getF2Command());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(false, true, false, false, false);
    }

    @Override
    public void f2Prefix()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }
  }

  protected class ModePrefixBehavior extends NoPrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey, aKey.getModeCommand());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(false, false, false, false, true);
    }
  }

  protected class ArcPrefixBehavior extends F1PrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey, aKey.getArcCommand());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(true, false, true, false, false);
    }

    @Override
    public void arcPrefix()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }

    @Override
    public void hypPrefix()
    {
      setBehavior(ARC_HYP_PREFIX_BEHAVIOR);
    }
  }

  protected class HypPrefixBehavior extends F1PrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey, aKey.getHypCommand());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(true, false, false, true, false);
    }

    @Override
    public void arcPrefix()
    {
      setBehavior(ARC_HYP_PREFIX_BEHAVIOR);
    }

    @Override
    public void hypPrefix()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }
  }

  protected class ArcHypPrefixBehavior extends ArcPrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      addCommand(aKey, aKey.getArcHypCommand());
    }

    @Override
    public void displayPrefixes(Fx702pDisplay aDisplay)
    {
      aDisplay.showPrefixes(true, false, true, true, false);
    }

    @Override
    public void arcPrefix()
    {
      setBehavior(HYP_PREFIX_BEHAVIOR);
    }

    @Override
    public void hypPrefix()
    {
      setBehavior(ARC_PREFIX_BEHAVIOR);
    }
  }

  protected class ProgramRunningBehavior extends NoPrefixBehavior
  {
    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      Command command = aKey.getKeyCommand();
      if (command instanceof StringCommand)
      {
        String key = ((StringCommand)command).getString();
        if (key.length() == 1)
        {
          setPotentialKey(key.charAt(0));
        }
      }
      else
      {
        addCommand(aKey.getKeyCommand());
      }
    }

    @Override
    public void keyRepeated(Fx702pKey aKey)
    {
      keyPressed(aKey);
    }

    @Override
    public void keyReleased(Fx702pKey aKey)
    {
      Command command = aKey.getKeyCommand();
      if (command instanceof StringCommand)
      {
        String key = ((StringCommand)command).getString();
        if (key.length() == 1)
        {
          endPotentialKey(key.charAt(0));
        }
      }
    }

    @Override
    public void f1Prefix()
    {
    }

    @Override
    public void f2Prefix()
    {
    }

    @Override
    public void modePrefix()
    {
    }

    @Override
    public void arcPrefix()
    {
    }

    @Override
    public void hypPrefix()
    {
    }

    @Override
    public void clearPrefixes()
    {
    }

    @Override
    public void allClear()
    {
    }

    public void suspendProgram()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
    }
  }

  protected class ErrorBehavior extends AbstractBehavior
  {
    @Override
    public void allClear()
    {
      setBehavior(NO_PREFIX_BEHAVIOR);
      behavior.allClear();
    }

    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      if (aKey == Fx702pKey.ALL_CLEAR)
      {
        addCommand(aKey.getCommand());
      }
    }
  }

  protected class ScrollBehavior extends AbstractBehavior
  {
    public ScrollBehavior(KeyboardBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      if (aKey == Fx702pKey.CONT)
      {
        addCommand(new FastScrollCommand());
      }
      else if (aKey == Fx702pKey.ALL_CLEAR || aKey == Fx702pKey.STOP)
      {
        addCommand(aKey.getCommand());
      }
    }

    @Override
    public void keyReleased(Fx702pKey aKey)
    {
      if (aKey == Fx702pKey.CONT)
      {
        addCommand(new NormalScrollCommand());
      }
    }


    @Override
    public void stop()
    {
      setBehavior(new ScrollStoppedBehavior(previousBehavior));
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    @Override
    public void endScroll()
    {
      setBehavior(previousBehavior);
    }

    protected KeyboardBehavior previousBehavior;
  }

  protected class ScrollStoppedBehavior extends AbstractBehavior
  {
    public ScrollStoppedBehavior(KeyboardBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void keyPressed(Fx702pKey aKey)
    {
      if (aKey == Fx702pKey.ALL_CLEAR || aKey == Fx702pKey.CONT)
      {
        addCommand(aKey.getCommand());
      }
    }

    @Override
    public void cont()
    {
      setBehavior(new ScrollBehavior(previousBehavior));
    }

    @Override
    public void allClear()
    {
      setBehavior(previousBehavior);
      behavior.allClear();
    }

    @Override
    public void endScroll()
    {
      setBehavior(previousBehavior);
    }

    protected KeyboardBehavior previousBehavior;
  }

  protected KeyboardBehavior behavior;
  protected Fx702pEmulator emulator;

  protected boolean clearPotentialKeyAfterRead;
  protected char potentialKey;
  protected long potentialKeyTime = NO_POTENTIAL_KEY;

  static public final long NO_POTENTIAL_KEY = 0;
  static public final long POTENTIAL_KEY_DELAY = 40; // 1/40th of a sec for
                                                     // KEY
  static public final long KEY_EXTRA_SLEEP_TIME = 30; // And we wait 1/30th of a
                                                      // sec after KEY to
                                                      // emulate real hardware

  protected final KeyboardBehavior NO_PREFIX_BEHAVIOR = new NoPrefixBehavior();
  protected final KeyboardBehavior F1_PREFIX_BEHAVIOR = new F1PrefixBehavior();
  protected final KeyboardBehavior F2_PREFIX_BEHAVIOR = new F2PrefixBehavior();
  protected final KeyboardBehavior MODE_PREFIX_BEHAVIOR = new ModePrefixBehavior();
  protected final KeyboardBehavior ARC_PREFIX_BEHAVIOR = new ArcPrefixBehavior();
  protected final KeyboardBehavior HYP_PREFIX_BEHAVIOR = new HypPrefixBehavior();
  protected final KeyboardBehavior ARC_HYP_PREFIX_BEHAVIOR = new ArcHypPrefixBehavior();
  protected final KeyboardBehavior PROGRAM_RUNNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final KeyboardBehavior ERROR_BEHAVIOR = new ErrorBehavior();
}
