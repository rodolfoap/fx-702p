package com.fx702p.swing;

import static com.fx702p.emulator.implementation.Fx702pTemporizer.DEFAULT_SPEED;
import static com.fx702p.emulator.implementation.Fx702pTemporizer.MAX_SPEED;
import static com.fx702p.emulator.implementation.Fx702pTemporizer.MIN_SPEED;

import java.awt.Dimension;
import java.awt.event.*;
import java.beans.*;
import java.text.ParseException;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.fx702p.emulator.*;
import com.fx702p.emulator.commands.Command;
import com.fx702p.emulator.implementation.Fx702pEmulatorComponent;
import com.fx702p.interpreters.Fx702pException;

@SuppressWarnings("serial")
public class DebugControlPanel extends JPanel implements Fx702pEmulatorComponent
{
  public DebugControlPanel(DebugWindow aDebugWindow)
  {
    debugWindow = aDebugWindow;
    behavior = RUN_MODE_BEHAVIOR;

    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    JPanel controlPanel = getControlPanel();
    JPanel speedPanel = getSpeedPanel();

    int height = Math.max(controlPanel.getPreferredSize().height, speedPanel.getPreferredSize().height);
    controlPanel.setMaximumSize(new Dimension(controlPanel.getMaximumSize().width, height));
    speedPanel.setMaximumSize(new Dimension(speedPanel.getMaximumSize().width, height));

    add(controlPanel);
    add(speedPanel);
    add(Box.createHorizontalGlue());

    setButtonsForRunMode();
  }

  public void debugActiveProgram()
  {
    behavior.playPressed();
  }

  protected Fx702pEmulator getEmulator()
  {
    return debugWindow.getEmulator();
  }

  protected JPanel getControlPanel()
  {
    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
    controlPanel.setBorder(BorderFactory.createTitledBorder("Control"));

    playButtonRunIcon = SwingUtils.createImageIcon(getClass(), PLAY_BUTTON_RUN_NAME, "Play Button Run");
    playButtonStoppedIcon = SwingUtils.createImageIcon(getClass(), PLAY_BUTTON_STOPPED_NAME, "Play Button Stopped");
    playButton = new JButton(playButtonRunIcon);
    playButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          behavior.playPressed();
        }
      });
    playButton.setToolTipText(buildButtonTooltip(PLAY_BUTTON_RUN_TOOLTIP, DebugWindow.PLAY_GLOBAL_ACCELERATOR_CODE, DebugWindow.PLAY_GLOBAL_ACCELERATOR_MODIFIERS));
    controlPanel.add(playButton);

    Icon icon = SwingUtils.createImageIcon(getClass(), STEP_BUTTON_NAME, "Step Button");
    stepButton = new JButton(icon);
    stepButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          behavior.stepPressed();
        }
      });
    stepButton.setToolTipText(buildButtonTooltip(STEP_BUTTON_TOOLTIP, DebugWindow.STEP_OR_START_GLOBAL_ACCELERATOR_CODE, DebugWindow.STEP_OR_START_GLOBAL_ACCELERATOR_MODIFIERS));
    controlPanel.add(stepButton);

    icon = SwingUtils.createImageIcon(getClass(), REPLAY_BUTTON_NAME, "Replay Button");
    replayButton = new JButton(icon);
    replayButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          behavior.replayPressed();
        }
      });
    replayButton.setToolTipText(buildButtonTooltip(REPLAY_BUTTON_TOOLTIP, DebugWindow.REPLAY_GLOBAL_ACCELERATOR_CODE, DebugWindow.REPLAY_GLOBAL_ACCELERATOR_MODIFIERS));
    controlPanel.add(replayButton);

    icon = SwingUtils.createImageIcon(getClass(), SUSPEND_BUTTON_NAME, "Stop Button");
    suspendButton = new JButton(icon);
    suspendButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          behavior.suspendPressed();
        }
      });
    suspendButton.setToolTipText(buildButtonTooltip(SUSPEND_BUTTON_TOOLTIP, DebugWindow.SUSPEND_GLOBAL_ACCELERATOR_CODE, DebugWindow.SUSPEND_GLOBAL_ACCELERATOR_MODIFIERS));
    controlPanel.add(suspendButton);

    icon = SwingUtils.createImageIcon(getClass(), END_BUTTON_NAME, "End Button");
    endButton = new JButton(icon);
    endButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          behavior.endPressed();
        }
      });
    controlPanel.add(endButton);
    endButton.setToolTipText(buildButtonTooltip(END_BUTTON_TOOLTIP, DebugWindow.END_GLOBAL_ACCELERATOR_CODE, DebugWindow.END_GLOBAL_ACCELERATOR_MODIFIERS));

    return controlPanel;
  }

  protected String buildButtonTooltip(String aTooltipText, int aGlobalAcceleratorCode, int aGlobalAcceleratorModifiers)
  {
    String modifierText = KeyEvent.getKeyModifiersText(aGlobalAcceleratorModifiers);
    String keycodeText = KeyEvent.getKeyText(aGlobalAcceleratorCode);
    if (modifierText == null || modifierText.length() == 0)
    {
      return aTooltipText + " [" + keycodeText + "]";
    }
    else
    {
      return aTooltipText + " [" + modifierText + '+' + keycodeText + "]";
    }
  }
  protected JPanel getSpeedPanel()
  {
    JPanel speedPanel = new JPanel();
    speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.X_AXIS));
    speedPanel.setBorder(BorderFactory.createTitledBorder("Speed"));

    final DocumentFilter documentFilter = new DocumentFilter()
      {
        @Override
        public void insertString(FilterBypass aFilterBypass, int anOffset, String aString, AttributeSet anAttributeSet) throws BadLocationException
        {
          int length = aFilterBypass.getDocument().getLength() + aString.length();
          if (checkString(aString, length))
          {
            super.insertString(aFilterBypass, anOffset, aString, anAttributeSet);
            synchronize();
          }
        }

        @Override
        public void replace(FilterBypass aFilterBypass, int anOffset, int aLength, String aString, AttributeSet anAttributeSet) throws BadLocationException
        {
          int length = aFilterBypass.getDocument().getLength() + aString.length() - aLength;
          if (checkString(aString, length))
          {
            super.replace(aFilterBypass, anOffset, aLength, aString, anAttributeSet);
            synchronize();
          }
        }

        @Override
        public void remove(FilterBypass aFilterBypass, int anOffset, int aLength) throws BadLocationException
        {
          // As long as there is only one character for speed, we refuse any
          // remove.

          // super.remove(aFilterBypass, anOffset, aLength);
          // synchronize();
        }

        protected boolean checkString(String aString, int aLength)
        {
          return aString.matches(SPEED_REGEXP) && aLength <= MAX_SPEED_CHARACTERS;
        }

        protected void synchronize()
        {
          try
          {
            speedTextField.commitEdit();
          }
          catch (ParseException exception)
          {
          }
          speedTextField.selectAll();
          speedSlider.setValue((Integer)speedTextField.getValue());
        }
      };

    DefaultFormatter formatter = new DefaultFormatter()
      {
        @Override
        public DocumentFilter getDocumentFilter()
        {
          return documentFilter;
        }
      };
    formatter.setValueClass(Integer.class);
    formatter.setCommitsOnValidEdit(true);
    formatter.setOverwriteMode(true);

    speedTextField = new JFormattedTextField(formatter)
      {
        @Override
        public void setCaretPosition(int aPosition)
        {
          super.setCaretPosition(0);
          moveCaretPosition(getDocument().getLength());
        }

        @Override
        public void select(int selectionStart, int selectionEnd)
        {
        }

        @Override
        public void selectAll()
        {
          setCaretPosition(0);
        }
      };
    speedTextField.setColumns(MAX_SPEED_CHARACTERS);
    speedTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    speedTextField.setValue(DEFAULT_SPEED);

    speedTextField.addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent aPropertyChangeEvent)
        {
          if ("value".equals(aPropertyChangeEvent.getPropertyName()))
          {
            getEmulator().setSpeed((Integer)speedTextField.getValue());
          }
        }
      });
    speedPanel.add(speedTextField);

    speedSlider = new JSlider();
    speedSlider.setMinimum(MIN_SPEED);
    speedSlider.setMaximum(MAX_SPEED);
    speedSlider.setPaintTicks(true);
    speedSlider.setMajorTickSpacing(SPEED_SLIDER_MAJOR_TICK_SPACING);
    speedSlider.setValue(DEFAULT_SPEED);
    speedSlider.setSnapToTicks(true);
    Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
    labels.put(MIN_SPEED, new JLabel("Slow"));
    labels.put(DEFAULT_SPEED, new JLabel("Normal"));
    labels.put(MAX_SPEED, new JLabel("Fast"));
    speedSlider.setLabelTable(labels);
    speedSlider.setPaintLabels(true);

    Dimension dimension = speedSlider.getPreferredSize();
    dimension.width = SPEED_SLIDER_WIDTH;
    speedSlider.setMinimumSize(dimension);
    speedSlider.setMaximumSize(dimension);
    speedSlider.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent aChangeEvent)
        {
          speedTextField.setValue(speedSlider.getValue());
        }
      });

    speedPanel.add(speedSlider);

    speedPanel.setMinimumSize(speedPanel.getPreferredSize());
    speedPanel.setMaximumSize(speedPanel.getPreferredSize());

    return speedPanel;
  }

  protected void setButtonsForProgramRunning()
  {
    playButton.setEnabled(false);
    playButton.setIcon(playButtonRunIcon);
    stepButton.setEnabled(false);
    suspendButton.setEnabled(true);
    endButton.setEnabled(true);
  }

  protected void setButtonsForRunMode()
  {
    boolean programExists = getEmulator().getActiveProgram() != null && !getEmulator().getActiveProgram().isEmpty();
    playButton.setEnabled(programExists);
    playButton.setIcon(playButtonRunIcon);
    playButton.setToolTipText(buildButtonTooltip(PLAY_BUTTON_RUN_TOOLTIP, DebugWindow.PLAY_GLOBAL_ACCELERATOR_CODE, DebugWindow.PLAY_GLOBAL_ACCELERATOR_MODIFIERS));
    stepButton.setEnabled(programExists);
    replayButton.setEnabled(false);
    suspendButton.setEnabled(false);
    endButton.setEnabled(false);
  }

  protected void setButtonsForStoppedMode()
  {
    playButton.setEnabled(true);
    playButton.setIcon(playButtonStoppedIcon);
    playButton.setToolTipText(buildButtonTooltip(PLAY_BUTTON_STOPPED_TOOLTIP, DebugWindow.PLAY_GLOBAL_ACCELERATOR_CODE, DebugWindow.PLAY_GLOBAL_ACCELERATOR_MODIFIERS));
    stepButton.setEnabled(true);
    replayButton.setEnabled(false);
    suspendButton.setEnabled(false);
    endButton.setEnabled(true);
  }

  protected void setButtonsForInputMode()
  {
    playButton.setEnabled(false);
    stepButton.setEnabled(false);
    replayButton.setEnabled(false);
    suspendButton.setEnabled(false);
    endButton.setEnabled(true);
  }

  protected void setButtonsForErrorMode()
  {
    playButton.setEnabled(false);
    stepButton.setEnabled(false);
    replayButton.setEnabled(false);
    suspendButton.setEnabled(false);
    endButton.setEnabled(false);
  }

  protected void setBehavior(DebugControlPanelBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  public boolean isDebugActive()
  {
    return behavior.isDebugActive();
  }

  public void stopProgram()
  {
    behavior.stopProgram();
  }

  public void contProgram()
  {
    behavior.contProgram();
  }

  public void stepInProgram()
  {
    behavior.stepInProgram();
  }

  public void debugAndStepActiveProgram()
  {
    behavior.debugAndStepActiveProgram();
  }

  public void nextLoop(Variable aVariable)
  {
    behavior.nextLoop(aVariable);
  }

  public void endLoop(Variable aVariable)
  {
    behavior.endLoop(aVariable);
  }

  public void suspendProgram()
  {
    behavior.suspendProgram();
  }

  public void resumeProgram()
  {
    behavior.resumeProgram();
  }

  public void endProgram()
  {
    behavior.endProgram();
  }

  public void loadProgram(int aProgramIndex)
  {
    setButtonsForRunMode();
    programEndedOnStep = false;
  }

  public void clearProgram(int aProgramIndex)
  {
    setButtonsForRunMode();
    programEndedOnStep = false;
  }

  public void setActiveProgramIndex(int aProgramIndex)
  {
    setButtonsForRunMode();
    programEndedOnStep = false;
  }

  public void allClear()
  {
    setButtonsForRunMode();
    programEndedOnStep = false;
  }

  public void home()
  {
  }

  public void cont()
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

  public void enterString(String aString)
  {
  }

  public void execute(String aString)
  {
    programEndedOnStep = false;
  }

  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  public void reportFx702pError(Fx702pException anError)
  {
    setButtonsForErrorMode();
  }

  public void resultPrinted()
  {
  }

  public void startMultiLinePrint()
  {
  }

  public void endMultiLinePrint()
  {
  }

  public void runProgram()
  {
    behavior.runProgram();
  }

  public void setRunMode()
  {
    behavior.setRunMode();
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


  public void waitAfterPrint(int aPrintWait, Command aCommand)
  {
  }

  public void stepOrStart()
  {
    behavior.stepPressed();
  }

  public void replay()
  {
    behavior.replayPressed();
  }

  protected boolean confirmDebugAndStep()
  {
    int answer = JOptionPane.showConfirmDialog(debugWindow, "Program just ended. Restart debug?", "Confirmation", JOptionPane.YES_NO_OPTION);
    return answer == 0;
  }

  protected interface DebugControlPanelBehavior
  {
    public void playPressed();
    public void suspendPressed();
    public void endPressed();
    public void stepPressed();
    public void replayPressed();

    public void runProgram();
    public void stopProgram();
    public void contProgram();
    public void endProgram();
    public void stepInProgram();
    public void debugAndStepActiveProgram();
    public void nextLoop(Variable aVariable);
    public void endLoop(Variable aVariable);
    public void suspendProgram();
    public void resumeProgram();
    public void setRunMode();

    public void input(String anInputPrompt);

    public void disableDebugging();

    public boolean isDebugActive();
  }

  protected abstract class AbstractBehavior implements DebugControlPanelBehavior
  {
    public void playPressed()
    {
    }

    public void suspendPressed()
    {
    }

    public void stepPressed()
    {
    }

    public void replayPressed()
    {
    }

    public void endPressed()
    {
      getEmulator().endProgram();
      programEndedOnStep = false;
    }

    public void runProgram()
    {
      setButtonsForProgramRunning();
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    public void contProgram()
    {
      resumeProgram();
    }

    public void stopProgram()
    {
      setButtonsForStoppedMode();
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
    }

    public void suspendProgram()
    {
      setButtonsForStoppedMode();
      setBehavior(PROGRAM_SUSPENDED_BEHAVIOR);
    }

    public void resumeProgram()
    {
      setButtonsForProgramRunning();
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    public void endProgram()
    {
      setButtonsForRunMode();
      setBehavior(RUN_MODE_BEHAVIOR);
    }

    public void setRunMode()
    {
    }

    public void input(String anInputPrompt)
    {
      setButtonsForInputMode();
      debugWindow.getSwingKeyboardAndDisplay().getMainFrame().toFront();
    }

    public void disableDebugging()
    {
      playButton.setEnabled(false);
      playButton.setIcon(playButtonRunIcon);
      stepButton.setEnabled(false);
      suspendButton.setEnabled(false);
      endButton.setEnabled(false);
    }

    public void stepInProgram()
    {
    }

    public void debugAndStepActiveProgram()
    {
      runProgram();
    }

    public void nextLoop(Variable aVariable)
    {
    }

    public void endLoop(Variable aVariable)
    {
    }

    public boolean isDebugActive()
    {
      return true;
    }
  }

  protected class RunModeBehavior extends AbstractBehavior
  {
    @Override
    public void playPressed()
    {
      getEmulator().debugActiveProgram();
    }

    @Override
    public void stepPressed()
    {
      if (programEndedOnStep)
      {
        if (confirmDebugAndStep())
        {
          programEndedOnStep = false;
          getEmulator().debugAndStepActiveProgram();
        }
      }
      else
      {
        getEmulator().debugAndStepActiveProgram();
      }
    }

    @Override
    public boolean isDebugActive()
    {
      return false;
    }
  }

  protected class ProgramRunningModeBehavior extends AbstractBehavior
  {
    @Override
    public void suspendPressed()
    {
      getEmulator().suspendProgram();
    }

    @Override
    public void endProgram()
    {
      super.endProgram();
      programEndedOnStep = true;
    }

    @Override
    public void setRunMode()
    {
      super.endProgram();
      programEndedOnStep = false;
    }
  }

  protected class ProgramStoppedModeBehavior extends AbstractBehavior
  {
    @Override
    public void playPressed()
    {
      getEmulator().contProgram();
    }

    @Override
    public void stepPressed()
    {
      getEmulator().stepInProgram();
    }

    @Override
    public void stepInProgram()
    {
      setBehavior(PROGRAM_SUSPENDED_BEHAVIOR);
    }

    @Override
    public void setRunMode()
    {
      super.endProgram();
      programEndedOnStep = false;
    }
  }

  protected class ProgramSuspendedModeBehavior extends AbstractBehavior
  {
    @Override
    public void playPressed()
    {
      lastStepWasNext = false;
      getEmulator().resumeProgram();
    }

    @Override
    public void stepPressed()
    {
      lastStepWasNext = false;
      getEmulator().stepInProgram();
    }

    @Override
    public void endProgram()
    {
      super.endProgram();
      programEndedOnStep = true;
    }

    @Override
    public void contProgram()
    {
      lastStepWasNext = false;
      setButtonsForStoppedMode();
      debugWindow.toFront();
      super.contProgram();
    }

    @Override
    public void suspendProgram()
    {
      replayButton.setEnabled(lastStepWasNext);
    }

    @Override
    public void replayPressed()
    {
      lastStepWasNext = false;
      replayButton.setEnabled(false);
      setBehavior(REPLAY_BEHAVIOR);
      getEmulator().resumeProgram();
    }

    @Override
    public void nextLoop(Variable aVariable)
    {
      lastStepWasNext = true;
      lastNextVariable = aVariable;
    }
  }

  protected class ReplayBehavior extends ProgramRunningModeBehavior
  {
    @Override
    public void nextLoop(Variable aVariable)
    {
    }

    @Override
    public void endLoop(Variable aVariable)
    {
      if (aVariable.equals(lastNextVariable))
      {
        getEmulator().suspendProgram();
      }
    }

    @Override
    public void resumeProgram()
    {
      playButton.setEnabled(false);
      playButton.setIcon(playButtonRunIcon);
      stepButton.setEnabled(false);
      suspendButton.setEnabled(true);
      endButton.setEnabled(true);
    }
  }

  protected DebugWindow debugWindow;
  protected JButton playButton;
  protected JButton stepButton;
  protected JButton replayButton;
  protected JButton suspendButton;
  protected JButton endButton;
  protected JFormattedTextField speedTextField;
  protected JSlider speedSlider;
  protected DebugControlPanelBehavior behavior;
  protected ImageIcon playButtonRunIcon;
  protected ImageIcon playButtonStoppedIcon;
  protected boolean programEndedOnStep;
  protected boolean lastStepWasNext = false;
  protected Variable lastNextVariable = null;

  protected final RunModeBehavior RUN_MODE_BEHAVIOR = new RunModeBehavior();
  protected final ProgramRunningModeBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningModeBehavior();
  protected final ProgramStoppedModeBehavior PROGRAM_STOPPED_BEHAVIOR = new ProgramStoppedModeBehavior();
  protected final ProgramSuspendedModeBehavior PROGRAM_SUSPENDED_BEHAVIOR = new ProgramSuspendedModeBehavior();
  protected final ReplayBehavior REPLAY_BEHAVIOR = new ReplayBehavior();

  static public final int MAX_SPEED_CHARACTERS = 1;
  static public final String SPEED_REGEXP = "[1-9]*";

  static public final int SPEED_SLIDER_WIDTH = 100;
  static public final int SPEED_SLIDER_MAJOR_TICK_SPACING = 1;

  static public final String PLAY_BUTTON_RUN_NAME = "icons/Play24.gif";
  static public final String PLAY_BUTTON_STOPPED_NAME = "icons/Replay24.gif";
  static public final String STEP_BUTTON_NAME = "icons/StepForward24.gif";
  static public final String REPLAY_BUTTON_NAME = "icons/FastForward24.gif";
  static public final String SUSPEND_BUTTON_NAME = "icons/Stop24.gif";
  static public final String END_BUTTON_NAME = "icons/End24.gif";

  static public final String PLAY_BUTTON_RUN_TOOLTIP = "Run";
  static public final String PLAY_BUTTON_STOPPED_TOOLTIP = "Continue";
  static public final String STEP_BUTTON_TOOLTIP = "Step";
  static public final String REPLAY_BUTTON_TOOLTIP = "Finish Loop";
  static public final String SUSPEND_BUTTON_TOOLTIP = "Suspend";
  static public final String END_BUTTON_TOOLTIP = "End";
}
