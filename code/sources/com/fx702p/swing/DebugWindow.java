package com.fx702p.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.fx702p.emulator.*;
import com.fx702p.emulator.commands.Command;
import com.fx702p.emulator.implementation.Fx702pEmulatorComponent;
import com.fx702p.interpreters.Fx702pException;

@SuppressWarnings("serial")
public class DebugWindow extends JFrame implements Fx702pEmulatorComponent
{
  public DebugWindow(final Fx702pBasicProgram aBasicProgram, Fx702pSwingKeyboardAndDisplay aFx702pSwingKeyboardAndDisplay, int aProgramIndex)
  {
    super("Fx702p Debugger " + 'P' + aProgramIndex + ' ' + aBasicProgram.getName());
    fx702pSwingKeyboardAndDisplay = aFx702pSwingKeyboardAndDisplay;
    programIndex = aProgramIndex;

    Fx702pSwingDebugger debugger = new Fx702pSwingDebugger(getEmulator(), this);
    getEmulator().setDebugger(debugger);

    JPanel controlAndSourcePanel = new JPanel();
    controlAndSourcePanel.setLayout(new BoxLayout(controlAndSourcePanel, BoxLayout.Y_AXIS));

    debugControlPanel = new DebugControlPanel(this);
    debugControlPanel.setAlignmentX(LEFT_ALIGNMENT);
    controlAndSourcePanel.add(debugControlPanel);

    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setAlignmentX(LEFT_ALIGNMENT);
    controlAndSourcePanel.add(splitPane);

    sourceCode = new SourceCodePanel(this, aBasicProgram);
    JScrollPane sourceScrollPane = new JScrollPane(sourceCode, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    splitPane.setTopComponent(sourceScrollPane);
    sourceScrollPane.setBorder(BorderFactory.createTitledBorder("Source Code"));

    watchPanel = new WatchPanel(fx702pSwingKeyboardAndDisplay, aBasicProgram);

    splitPane.setBottomComponent(watchPanel);
    splitPane.setResizeWeight(0.8);

    getRootPane().setContentPane(controlAndSourcePanel);
    pack();

    addKeyEventDispatcher();

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowOpened(WindowEvent aWindowEvent)
        {
          watchPanel.setInitialDividerLocation();
        }

        @Override
        public void windowClosing(WindowEvent aWindowEvent)
        {
          closeDebugWindow();
        }
      });

    fx702pSwingKeyboardAndDisplay.setDebugWindowPosition(this);
    fx702pSwingKeyboardAndDisplay.getEmulator().getMemory().setWatcher(watchPanel);
  }

  public void startKeysForwarding()
  {
    forwardKeys = true;
  }

  public void stopKeysForwarding()
  {
    forwardKeys = false;
  }

  protected void addKeyEventDispatcher()
  {
    keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    keyEventDispatcher = new KeyEventDispatcher()
      {
        public boolean dispatchKeyEvent(KeyEvent aKeyEvent)
        {
          return dispatchGlobalDebuggerKeyEvent(aKeyEvent);
        }
      };
    keyboardFocusManager.addKeyEventDispatcher(keyEventDispatcher);
  }

  protected void removeKeyEventDispatcher()
  {
    if (keyboardFocusManager != null && keyEventDispatcher != null)
    {
      keyboardFocusManager.removeKeyEventDispatcher(keyEventDispatcher);
    }
  }

  protected boolean dispatchGlobalDebuggerKeyEvent(KeyEvent aKeyEvent)
  {
    // If the focus is somewhere in the watchPanel,
    // we do not handle the KeyEvent
    if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == null || SwingUtilities.getWindowAncestor(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner()) instanceof JDialog || SwingUtilities.isDescendingFrom(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), watchPanel) || SwingUtilities.isDescendingFrom(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), fx702pSwingKeyboardAndDisplay.getConsole()))
    {
      return false;
    }

    if (aKeyEvent.getID() == KeyEvent.KEY_PRESSED)
    {
      switch (aKeyEvent.getKeyCode())
      {
        case END_GLOBAL_ACCELERATOR_CODE:
        {
          if (aKeyEvent.getModifiers() == END_GLOBAL_ACCELERATOR_MODIFIERS)
          {
            endProgram();
            return true;
          }
          break;
        }
        case SUSPEND_GLOBAL_ACCELERATOR_CODE:
        {
          if (aKeyEvent.getModifiers() == SUSPEND_GLOBAL_ACCELERATOR_MODIFIERS)
          {
            suspendProgram();
            return true;
          }
          break;
        }
        case STEP_OR_START_GLOBAL_ACCELERATOR_CODE:
        {
          if (aKeyEvent.getModifiers() == STEP_OR_START_GLOBAL_ACCELERATOR_MODIFIERS)
          {
            stepOrStart();
            return true;
          }
          break;
        }
        case REPLAY_GLOBAL_ACCELERATOR_CODE:
        {
          if (aKeyEvent.getModifiers() == REPLAY_GLOBAL_ACCELERATOR_MODIFIERS)
          {
            replay();
            return true;
          }
          break;
        }
        case PLAY_GLOBAL_ACCELERATOR_CODE:
        {
          if (aKeyEvent.getModifiers() == PLAY_GLOBAL_ACCELERATOR_MODIFIERS)
          {
            debugActiveProgram();
            return true;
          }
          break;
        }
        default:
        {
          return fx702pSwingKeyboardAndDisplay.processKey(aKeyEvent);
        }
      }
    }
    else if (aKeyEvent.getID() == KeyEvent.KEY_RELEASED)
    {
      return fx702pSwingKeyboardAndDisplay.processKey(aKeyEvent);
    }
    return false;
  }

  public Fx702pSwingKeyboardAndDisplay getSwingKeyboardAndDisplay()
  {
    return fx702pSwingKeyboardAndDisplay;
  }

  public Fx702pEmulator getEmulator()
  {
    return fx702pSwingKeyboardAndDisplay.getEmulator();
  }

  protected void closeDebugWindow()
  {
    fx702pSwingKeyboardAndDisplay.getEmulator().getMemory().setWatcher(null);
    if (debugControlPanel.isDebugActive())
    {
      int answer = JOptionPane.showConfirmDialog(this, "Debugged program is running. Stop it?", "Closing confirmation", JOptionPane.OK_CANCEL_OPTION);
      if (answer == JOptionPane.OK_OPTION)
      {
        getEmulator().endProgram();
        getEmulator().endDebug();
        dispose();
      }
    }
    else
    {
      getEmulator().endDebug();
      dispose();
    }
    removeKeyEventDispatcher();
  }

  public void selectBreakpoint(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    sourceCode.selectBreakpoint(aBasicLine, aSelectionStart, aSelectionEnd);
  }

  public void select(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    sourceCode.select(aBasicLine, aSelectionStart, aSelectionEnd);
  }

  public void subSelect(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    sourceCode.subSelect(aBasicLine, aSelectionStart, aSelectionEnd);
  }

  public void clearSelection()
  {
    sourceCode.clearSelection();
  }

  public void debugActiveProgram()
  {
    debugControlPanel.debugActiveProgram();
  }

  public void allClear()
  {
    debugControlPanel.allClear();
    sourceCode.allClear();
  }

  public void home()
  {
  }

  public void cont()
  {
    debugControlPanel.cont();
  }

  public void contProgram()
  {
    debugControlPanel.contProgram();
    watchPanel.contProgram();
  }

  public void endProgram()
  {
    sourceCode.clearSelection();
    debugControlPanel.endProgram();
    watchPanel.endProgram();
  }

  public void endScroll()
  {
    debugControlPanel.endScroll();
  }

  public void endWaitAfterPrint()
  {
    debugControlPanel.endWaitAfterPrint();
  }

  public void cancelWaitAfterPrint()
  {
    debugControlPanel.cancelWaitAfterPrint();
  }

  public void enterString(String aString)
  {
    debugControlPanel.enterString(aString);
  }

  public void execute(String aString)
  {
    debugControlPanel.execute(aString);
  }

  public void input(String anInputPrompt)
  {
    debugControlPanel.input(anInputPrompt);
  }

  public void reportFx702pError(Fx702pException anError)
  {
    debugControlPanel.reportFx702pError(anError);
  }

  public void resultPrinted()
  {
    debugControlPanel.resultPrinted();
  }

  public void startMultiLinePrint()
  {
  }

  public void endMultiLinePrint()
  {
  }

  public void runProgram()
  {
    debugControlPanel.runProgram();
    watchPanel.runProgram();
  }

  public void setRunMode()
  {
    debugControlPanel.setRunMode();
    watchPanel.setRunMode();
  }

  public void setWrtMode()
  {
    debugControlPanel.setWrtMode();
    watchPanel.setWrtMode();
  }

  public void startScroll()
  {
    debugControlPanel.startScroll();
  }

  public void stepInProgram()
  {
    debugControlPanel.stepInProgram();
    watchPanel.stepInProgram();
  }

  public void stepOrStart()
  {
    debugControlPanel.stepOrStart();
  }

  public void debugAndStepActiveProgram()
  {
    debugControlPanel.debugAndStepActiveProgram();
    watchPanel.debugAndStepActiveProgram();
  }

  public void nextLoop(Variable aVariable)
  {
    debugControlPanel.nextLoop(aVariable);
  }

  public void endLoop(Variable aVariable)
  {
    debugControlPanel.endLoop(aVariable);
  }

  public void stop()
  {
    debugControlPanel.stop();
  }

  public void stopProgram()
  {
    debugControlPanel.stopProgram();
  }

  public void suspendProgram()
  {
    debugControlPanel.suspendProgram();
  }

  public void resumeProgram()
  {
    debugControlPanel.resumeProgram();
    watchPanel.resumeProgram();
  }

  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
    debugControlPanel.waitAfterPrint(aPrintWait, null);
  }

  public void loadProgram(int aProgramIndex)
  {
    if (aProgramIndex == programIndex || aProgramIndex == Fx702pConstants.ALL_PROGRAMS_LOADED)
    {
      debugControlPanel.loadProgram(aProgramIndex);
      sourceCode.setActiveProgram(getEmulator().getActiveProgram());
      fixSize();
    }
  }

  public void replay()
  {
    debugControlPanel.replay();
  }

  public void clearProgram(int aProgramIndex)
  {
    if (aProgramIndex == programIndex || aProgramIndex == Fx702pConstants.ALL_PROGRAMS_LOADED)
    {
      debugControlPanel.clearProgram(aProgramIndex);
      sourceCode.clearProgram();
      fixSize();
    }
  }

  public void setActiveProgramIndex(int aProgramIndex)
  {
    if (aProgramIndex != programIndex)
    {
      programIndex = aProgramIndex;
      debugControlPanel.setActiveProgramIndex(aProgramIndex);
      Fx702pBasicProgram basicProgram = getEmulator().getProgram(aProgramIndex);
      sourceCode.setActiveProgram(basicProgram);
      watchPanel.setActiveProgram(basicProgram);
      fixSize();
    }
  }

  protected void fixSize()
  {
    setSize(getPreferredSize());
    invalidate();
  }

  protected boolean forwardKeys = true;
  protected DebugControlPanel debugControlPanel;
  protected SourceCodePanel sourceCode;
  protected WatchPanel watchPanel;
  protected JSplitPane splitPane;
  protected Fx702pSwingKeyboardAndDisplay fx702pSwingKeyboardAndDisplay;
  protected int programIndex;
  protected KeyboardFocusManager keyboardFocusManager;
  protected KeyEventDispatcher keyEventDispatcher;

  static public final int PLAY_GLOBAL_ACCELERATOR_CODE = KeyEvent.VK_F11;
  static public final int PLAY_GLOBAL_ACCELERATOR_MODIFIERS = 0;

  static public final int END_GLOBAL_ACCELERATOR_CODE = KeyEvent.VK_F2;
  static public final int END_GLOBAL_ACCELERATOR_MODIFIERS = SwingUtils.getDefaultModifierForCurrentPlatform();

  static public final int SUSPEND_GLOBAL_ACCELERATOR_CODE = KeyEvent.VK_F3;
  static public final int SUSPEND_GLOBAL_ACCELERATOR_MODIFIERS = 0;

  static public final int STEP_OR_START_GLOBAL_ACCELERATOR_CODE = KeyEvent.VK_F6;
  static public final int STEP_OR_START_GLOBAL_ACCELERATOR_MODIFIERS = 0;

  static public final int REPLAY_GLOBAL_ACCELERATOR_CODE = KeyEvent.VK_F7;
  static public final int REPLAY_GLOBAL_ACCELERATOR_MODIFIERS = 0;
}
