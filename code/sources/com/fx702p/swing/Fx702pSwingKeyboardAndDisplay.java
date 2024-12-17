package com.fx702p.swing;

import static com.fx702p.emulator.Fx702pConstants.FILENAME_SUFFIX;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Timer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import com.fx702p.emulator.*;
import com.fx702p.emulator.Console;
import com.fx702p.emulator.exceptions.Fx702pLoadingException;
import com.fx702p.emulator.implementation.*;
import com.fx702p.interpreters.Fx702pException;

public class Fx702pSwingKeyboardAndDisplay extends Fx702pAbstractEmulatorComponent implements Runnable, Fx702pDisplay
{
  public Fx702pSwingKeyboardAndDisplay(String[] theArguments)
  {
    if (theArguments.length == 1)
    {
      autoRun = theArguments[0];
    }
    else if (theArguments.length == 2 && "-show".equals(theArguments[0]))
    {
      showAtStartup = true;
      autoRun = theArguments[1];
    }

    setLookAndFeel();
    swingConsole = new SwingConsole(this);
    buildMenuBar(swingConsole);
    Console.setImplementation(swingConsole);

    setInitialBehavior();
    emulator = new Fx702pDefaultEmulator(this);
  }

  public Fx702pEmulator getEmulator()
  {
    return emulator;
  }

  public JFrame getMainFrame()
  {
    return mainFrame;
  }

  public SwingConsole getConsole()
  {
    return swingConsole;
  }

  protected void setBehavior(KeyboardAndDisplayBehavior aBehavior)
  {
    behavior = aBehavior;
  }

  protected void setInitialBehavior()
  {
    setBehavior(RUN_MODE_BEHAVIOR);
  }

  protected void setLookAndFeel()
  {
    // Nimbus does not work well with MacOSX yet. Bindings are on Control
    // not Command
    if (!SwingUtils.isMacOSX())
    {
      for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels())
      {
        if ("Nimbus".equalsIgnoreCase(laf.getName()))
        {
          try
          {
            UIManager.setLookAndFeel(laf.getClassName());
          }
          catch (Exception exception)
          {
          }
        }
      }
    }
  }

  public void run()
  {
    try
    {
      mainFrame = new JFrame("Fx702p Emulator");

      fx702pPanel = new Fx702pPanel(this);

      final JPanel panel = new JPanel();
      panel.add(fx702pPanel);
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      mainFrame.getRootPane().setContentPane(panel);

      buildPopupMenu();
      buildMenuBar(mainFrame);

      mainFrame.setResizable(false);
      mainFrame.pack();
      mainFrame.setSize(mainFrame.getPreferredSize());
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      mainFrame.addWindowListener(new WindowAdapter()
        {
          @Override
          public void windowActivated(WindowEvent aWindowEvent)
          {
            if (moveToFront == 0)
            {
              if (debugWindow != null && debugWindow.isVisible() && !debugWindow.isActive())
              {
                debugWindow.toFront();
                mainFrame.toFront();
                moveToFront = 2;
              }
            }
            else
            {
              moveToFront--;
            }
          }
        });

      mainFrame.setIconImage(fx702pPanel.getIcon());
      mainFrame.setVisible(true);

      Thread emulatorThread = new Thread(emulator, "Emulator");
      emulatorThread.start();

      if (autoRun != null)
      {
        SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              try
              {
                File autoRunFile = new File(autoRun);
                if (autoRunFile.exists())
                {
                  emulator.loadProgram(autoRunFile);
                }
                if (showAtStartup)
                {
                  showProgram(emulator.getActiveProgram(), emulator.getActiveProgramIndex());
                  moveToFront = 2;
                  mainFrame.toFront();
                }
                else
                {
                  emulator.runActiveProgram();
                }
              }
              catch (Throwable throwable)
              {
                reportError("Cannot start autorun program file " + autoRun, null, throwable);
                throwable.printStackTrace(System.err);
              }
            }
          });
      }
    }
    catch (Throwable throwable)
    {
      reportError("Cannot start ", null, throwable);
    }
  }

  public boolean isEmpty()
  {
    return fx702pPanel.isEmpty();
  }

  public void setConsolePosition(SwingConsole aConsole)
  {
    Rectangle mainFrameBounds = mainFrame.getBounds();
    Dimension consoleSize = aConsole.getSize();
    Rectangle maximumWindowBounds = getMaximumWindowBounds(mainFrame);

    // Can the console be below the main frame?
    int spaceBelow = maximumWindowBounds.height - maximumWindowBounds.y - mainFrameBounds.y - mainFrameBounds.height;
    if (consoleSize.height + SPACE_BETWEEN_FRAMES < spaceBelow)
    {
      aConsole.setLocation(mainFrameBounds.x, mainFrameBounds.y + mainFrameBounds.height + SPACE_BETWEEN_FRAMES);
    }
    // Can the console be above the main frame?
    else
    {
      int spaceAbove = mainFrameBounds.y - maximumWindowBounds.y;
      if (spaceAbove - consoleSize.height - SPACE_BETWEEN_FRAMES >= 0)
      {
        aConsole.setLocation(mainFrameBounds.x, mainFrameBounds.y - consoleSize.height - SPACE_BETWEEN_FRAMES);
      }
      else
      {
        if (spaceBelow > spaceAbove)
        {
          aConsole.setLocation(mainFrameBounds.x, mainFrameBounds.y + mainFrameBounds.height + SPACE_BETWEEN_FRAMES);
        }
        else
        {
          aConsole.setLocation(mainFrameBounds.x, mainFrameBounds.y - consoleSize.height - SPACE_BETWEEN_FRAMES);
        }
        aConsole.setSize(aConsole.getPreferredSize().width, Math.max(spaceBelow, spaceAbove) - SPACE_BETWEEN_FRAMES);
      }
    }
  }

  public void setDebugWindowPosition(JFrame aDebugWindow)
  {
    Rectangle mainFrameBounds = mainFrame.getBounds();
    Dimension basicSourceCodeWindowSize = aDebugWindow.getSize();
    Rectangle maximumWindowBounds = getMaximumWindowBounds(mainFrame);

    int spaceLeft = mainFrameBounds.x - maximumWindowBounds.x;
    int spaceRight = maximumWindowBounds.width - maximumWindowBounds.x - mainFrameBounds.x - mainFrameBounds.width;

    int x;
    int y = Math.max(maximumWindowBounds.y, mainFrameBounds.y - Math.max(0, mainFrameBounds.y + basicSourceCodeWindowSize.height - maximumWindowBounds.height));
    int width = basicSourceCodeWindowSize.width;
    int height = basicSourceCodeWindowSize.height;
    if (spaceRight > spaceLeft)
    {
      if (spaceRight < basicSourceCodeWindowSize.width + SPACE_BETWEEN_FRAMES)
      {
        width = spaceRight - SPACE_BETWEEN_FRAMES;
      }
      x = mainFrameBounds.x + mainFrameBounds.width + SPACE_BETWEEN_FRAMES;
    }
    else
    {
      if (spaceLeft < basicSourceCodeWindowSize.width + SPACE_BETWEEN_FRAMES)
      {
        width = spaceLeft - SPACE_BETWEEN_FRAMES;
      }
      x = mainFrameBounds.x - basicSourceCodeWindowSize.width - SPACE_BETWEEN_FRAMES;
    }
    if (x + width > maximumWindowBounds.width)
    {
      width = maximumWindowBounds.width - x;
    }
    if (y + height > maximumWindowBounds.height)
    {
      height = maximumWindowBounds.height - y;
    }
    aDebugWindow.setBounds(x, y, width, height);
  }

  protected Rectangle getMaximumWindowBounds(JFrame aFrame)
  {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();
    return maximumWindowBounds;
  }

  protected void buildMenuBar(JFrame aFrame)
  {
    int acceleratorMask = SwingUtils.getAcceleratorMaskForCurrentPlatform();

    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new IntelligentMenu(FILE_MENU);
    menuBar.add(fileMenu);
    JMenuItem openMenuItem = buildOpenMenuItem();
    openMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', acceleratorMask));
    fileMenu.add(openMenuItem);

    JMenuItem reloadMenuItem = buildReloadMenuItem();
    reloadMenuItem.setAccelerator(KeyStroke.getKeyStroke('L', acceleratorMask));
    fileMenu.add(reloadMenuItem);

    JMenuItem reloadAllMenuItem = buildReloadAllMenuItem();
    fileMenu.add(reloadAllMenuItem);

    JMenuItem saveMenuItem = buildSaveMenuItem();
    saveMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', acceleratorMask));
    fileMenu.add(saveMenuItem);

    JMenuItem saveAsMenuItem = buildSaveAsMenuItem();
    fileMenu.add(saveAsMenuItem);

    JMenuItem saveAllMenuItem = buildSaveAllMenuItem();
    fileMenu.add(saveAllMenuItem);

    JMenuItem saveAllAsMenuItem = buildSaveAllAsMenuItem();
    fileMenu.add(saveAllAsMenuItem);

    fileMenu.addSeparator();
    JMenuItem quitMenuItem = buildQuitMenuItem();
    quitMenuItem.setAccelerator(KeyStroke.getKeyStroke('Q', acceleratorMask));
    fileMenu.add(quitMenuItem);

    fileMenu.addMenuListener(new MenuEnablingListener());

    JMenu programMenu = new IntelligentMenu(PROGRAM_MENU);
    menuBar.add(programMenu);

    JMenuItem runMenuItem = buildRunMenuItem();
    runMenuItem.setAccelerator(KeyStroke.getKeyStroke('R', acceleratorMask));
    programMenu.add(runMenuItem);

    JMenuItem debugMenuItem = buildDebugMenuItem();
    debugMenuItem.setAccelerator(KeyStroke.getKeyStroke('D', acceleratorMask));
    programMenu.add(debugMenuItem);

    programMenu.addMenuListener(new MenuEnablingListener());

    aFrame.setJMenuBar(menuBar);
  }

  protected void buildPopupMenu()
  {
    popupMenu = SwingUtils.buildPopupMenu(mainFrame, fx702pPanel, new MouseAdapter()
      {
        @Override
        public void mousePressed(MouseEvent aMouseEvent)
        {
          processMousePressed(aMouseEvent);
        }

        @Override
        public void mouseReleased(MouseEvent aMouseEvent)
        {
          processMouseReleased(aMouseEvent);
        }
      });

    popupMenu.add(buildOpenMenuItem());
    popupMenu.add(buildReloadMenuItem());
    popupMenu.add(buildReloadAllMenuItem());
    popupMenu.add(buildSaveMenuItem());
    popupMenu.add(buildSaveAsMenuItem());
    popupMenu.add(buildSaveAllMenuItem());
    popupMenu.add(buildSaveAllAsMenuItem());
    popupMenu.addSeparator();
    popupMenu.add(buildRunMenuItem());
    popupMenu.add(buildDebugMenuItem());
  }

  protected void processMousePressed(MouseEvent aMouseEvent)
  {
    Point keyCoordinates = fx702pPanel.getKeyCoordinates(aMouseEvent.getX(), aMouseEvent.getY());
    if (keyCoordinates != null)
    {
      keyPressed = Fx702pKeyboardLayout.getKey(keyCoordinates.x, keyCoordinates.y);
      fx702pPanel.keyPressed(keyPressed, keyCoordinates);
      emulator.keyPressed(keyPressed);
      startAutorepeat();
    }
    else
    {
      keyPressed = null;
    }
  }

  protected void processMouseReleased(MouseEvent aMouseEvent)
  {
    cancelAutorepeat();
    if (keyPressed != null)
    {
      Point keyCoordinates = fx702pPanel.getKeyCoordinates(aMouseEvent.getX(), aMouseEvent.getY());
      fx702pPanel.keyReleased(keyPressed, keyCoordinates);
      if (keyCoordinates != null)
      {
        emulator.keyReleased(keyPressed);
      }
      keyPressed = null;
    }
  }
  protected void startAutorepeat()
  {
    if (keyPressed == Fx702pKey.LEFT_ARROW || keyPressed == Fx702pKey.RIGHT_ARROW)
    {
      autorepeatTimer = new Timer("Autorepeat keypressed on mouse event");
      autorepeatTimer.schedule(new AutoRepeatTask(), AUTOREPEAT_FIRST_DELAY, AUTOREPEAT_DELAY);
    }
  }

  protected void cancelAutorepeat()
  {
    if (autorepeatTimer != null)
    {
      autorepeatTimer.cancel();
    }
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildOpenMenuItem()
  {
    IntelligentMenuItem openMenuItem = new IntelligentMenuItem(OPEN_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode();
        }
      };
    openMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          loadProgram();
        }
      });
    return openMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildReloadMenuItem()
  {
    IntelligentMenuItem reloadMenuItem = new IntelligentMenuItem(RELOAD_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode() && emulator.getActiveProgram().canBeLoaded();
        }
      };
    reloadMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          reloadProgram();
        }
      });
    return reloadMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildReloadAllMenuItem()
  {
    IntelligentMenuItem reloadAllMenuItem = new IntelligentMenuItem(RELOAD_ALL_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode() && emulator.getMemory().canBeLoaded();
        }
      };
    reloadAllMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          reloadAll();
        }
      });
    return reloadAllMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildSaveMenuItem()
  {
    IntelligentMenuItem saveMenuItem = new IntelligentMenuItem(SAVE_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode() && emulator.getActiveProgram().canBeSaved(emulator.getMemory());
        }
      };
    saveMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          saveProgram();
        }
      });
    return saveMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildSaveAsMenuItem()
  {
    IntelligentMenuItem saveMenuItem = new IntelligentMenuItem(SAVE_AS_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode();
        }
      };
    saveMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          saveAsProgram();
        }
      });
    return saveMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildSaveAllMenuItem()
  {
    IntelligentMenuItem saveMenuItem = new IntelligentMenuItem(SAVE_ALL_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode() && emulator.getMemory().canBeSaved();
        }
      };
    saveMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          saveAll();
        }
      });
    return saveMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildSaveAllAsMenuItem()
  {
    IntelligentMenuItem saveMenuItem = new IntelligentMenuItem(SAVE_ALL_AS_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isProgramRunning() && !isErrorMode();
        }
      };
    saveMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          saveAllAs();
        }
      });
    return saveMenuItem;
  }

  @SuppressWarnings("serial")
  public IntelligentMenuItem buildQuitMenuItem()
  {
    IntelligentMenuItem quitMenuItem = new IntelligentMenuItem(QUIT_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return true;
        }
      };
    quitMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          quit();
        }
      });
    return quitMenuItem;
  }

  @SuppressWarnings("serial")
  protected IntelligentMenuItem buildRunMenuItem()
  {
    IntelligentMenuItem runMenuItem = new IntelligentMenuItem(RUN_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return isProgramLoaded() && !isProgramRunning() && !isErrorMode();
        }
      };
    runMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          emulator.runActiveProgram();
        }
      });
    return runMenuItem;
  }

  @SuppressWarnings("serial")
  protected IntelligentMenuItem buildDebugMenuItem()
  {
    IntelligentMenuItem debugMenuItem = new IntelligentMenuItem(DEBUG_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return isProgramLoaded() && !isProgramRunning() && !isErrorMode();
        }
      };
    debugMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          debugActiveProgram();
        }
      });
    return debugMenuItem;
  }

  protected void debugActiveProgram()
  {
    showProgram(emulator.getActiveProgram(), emulator.getActiveProgramIndex());
  }

  protected boolean isProgramLoaded()
  {
    return emulator.getActiveProgram().getParsedProgram() != null;
  }

  protected boolean isProgramRunning()
  {
    return behavior.isProgramRunning();
  }

  protected boolean isErrorMode()
  {
    return behavior.isErrorMode();
  }

  protected void loadProgram()
  {
    File programFile = getLoadFile();
    if (programFile != null)
    {
      try
      {
        emulator.loadProgram(programFile);
      }
      catch (Throwable throwable)
      {
        reportFileError(throwable);
      }
    }
  }

  public void reloadProgram()
  {
    try
    {
      emulator.reloadProgram();
    }
    catch (Throwable aThrowable)
    {
      reportFileError(aThrowable);
    }
  }

  public void reloadAll()
  {
    try
    {
      emulator.reloadAll();
    }
    catch (Throwable aThrowable)
    {
      reportFileError(aThrowable);
    }
  }

  public void saveProgram()
  {
    try
    {
      emulator.saveProgram();
    }
    catch (Throwable aThrowable)
    {
      reportFileError(aThrowable);
    }
  }

  public void saveAsProgram()
  {
    FileSaveInfos fileSaveInfos = getSaveFile(emulator.getActiveProgram().isSavingVariables(), emulator.getActiveProgram().isSavingBreakpoints());
    if (fileSaveInfos != null && fileSaveInfos.programFile != null && confirmOverwrite(fileSaveInfos.programFile))
    {
      try
      {
        emulator.saveProgramAs(fileSaveInfos.programFile, fileSaveInfos.saveVariables, fileSaveInfos.saveBreakpoints);
      }
      catch (Throwable aThrowable)
      {
        reportFileError(aThrowable);
      }
    }
  }


  public void saveAll()
  {
    try
    {
      emulator.saveAll();
    }
    catch (Throwable aThrowable)
    {
      reportFileError(aThrowable);
    }
  }

  public void saveAllAs()
  {
    FileSaveInfos fileSaveInfos = getSaveFile(emulator.getMemory().isSavingVariables(), emulator.getMemory().isSavingBreakpoints());
    if (fileSaveInfos != null && fileSaveInfos.programFile != null && confirmOverwrite(fileSaveInfos.programFile))
    {
      try
      {
        emulator.saveAllAs(fileSaveInfos.programFile, fileSaveInfos.saveVariables, fileSaveInfos.saveBreakpoints);
      }
      catch (Throwable aThrowable)
      {
        reportFileError(aThrowable);
      }
    }
  }

  protected boolean confirmOverwrite(File aFile)
  {
    return askConfirmation("Confirm Overwrite?", "File " + aFile.getName() + " already exists\nOverwrite?");
  }

  public void quit()
  {
    System.exit(0);
  }

  public File getLoadFile()
  {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fileChooser.setCurrentDirectory(getDefaultDirectory());
    fileChooser.setFileFilter(FX702P_FILE_FILTER);
    int chooserResult = fileChooser.showOpenDialog(mainFrame);

    if (chooserResult == JFileChooser.APPROVE_OPTION)
    {
      return fileChooser.getSelectedFile();
    }
    else
    {
      return null;
    }
  }

  public FileSaveInfos getSaveFile(boolean aSaveVariables, boolean aSaveBreakpoints)
  {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fileChooser.setCurrentDirectory(getDefaultDirectory());
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    JCheckBox saveVariablesCheckbox = new JCheckBox("Save Variables");
    saveVariablesCheckbox.setSelected(aSaveVariables);
    panel.add(saveVariablesCheckbox);
    JCheckBox saveBreakpointsCheckbox = new JCheckBox("Save Breakpoints");
    saveBreakpointsCheckbox.setSelected(aSaveBreakpoints);
    panel.add(saveBreakpointsCheckbox);
    fileChooser.setAccessory(panel);
    int chooserResult = fileChooser.showSaveDialog(mainFrame);

    if (chooserResult == JFileChooser.APPROVE_OPTION)
    {
      File file = fileChooser.getSelectedFile();
      if (!file.getName().endsWith(FILENAME_SUFFIX))
      {
        file = new File(file.getPath() + FILENAME_SUFFIX);
      }
      return new FileSaveInfos(file, saveVariablesCheckbox.isSelected(), saveBreakpointsCheckbox.isSelected());
    }
    else
    {
      return null;
    }
  }

  protected File getDefaultDirectory()
  {
    if (defaultDirectory == null)
    {
      String home;
      if (System.getProperty("os.name").startsWith("Windows"))
      {
        home = System.getProperty("HOMEPATH");
      }
      else
      {
        home = System.getProperty("HOME");
      }
      if (home == null || home.length() == 0)
      {
        home = ".";
      }
      defaultDirectory = new File(home);
    }
    return defaultDirectory;
  }


  public void print(final char[] theCharactersToDisplay)
  {
    try
    {
      if (SwingUtilities.isEventDispatchThread())
      {
        fx702pPanel.print(theCharactersToDisplay);
      }
      else
      {
        SwingUtilities.invokeAndWait(new Runnable()
          {
            public void run()
            {
              fx702pPanel.print(theCharactersToDisplay);
            }
          });
      }
    }
    catch (Exception exception)
    {
      reportError("Internal error", "Unable to print", exception);
    }
  }

  public void setCursorVisible(boolean aVisibleFlag)
  {
    fx702pPanel.setCursorVisible(aVisibleFlag);
  }

  public int getCursorPosition()
  {
    return fx702pPanel.getCursorPosition();
  }

  public void setCursorPosition(int aCursorPosition)
  {
    fx702pPanel.setCursorPosition(aCursorPosition);
  }

  public void moveCursorRight()
  {
    fx702pPanel.moveCursorRight();
  }

  public void moveCursorRight(int aCount)
  {
    fx702pPanel.moveCursorRight(aCount);
  }

  public void moveCursorLeft()
  {
    fx702pPanel.moveCursorLeft();
  }

  public void moveCursorLeft(int aCount)
  {
    fx702pPanel.moveCursorLeft(aCount);
  }

  @Override
  public void reportFx702pError(Fx702pException anError)
  {
    fx702pPanel.clearDisplay();
    fx702pPanel.setCursorVisible(false);
    fx702pPanel.showBusy(false);
    fx702pPanel.print((anError).getMessage().toCharArray());
    behavior.reportFx702pError(anError);
  }

  public void reportError(String aTitle, String aMessage, Throwable anError)
  {
    StringBuilder errorMessage = new StringBuilder();
    if (aMessage != null)
    {
      errorMessage.append(aMessage);
      errorMessage.append(": ");
    }
    if (anError != null)
    {
      if (anError.getMessage() != null)
      {
        errorMessage.append(anError.getMessage());
      }
      else
      {
        errorMessage.append(anError.getClass().getSimpleName());
      }
    }

    int lines = 0;
    String shortMessage = null;
    for (int i = 0; i < errorMessage.length(); i++)
    {
      if (errorMessage.charAt(i) == '\n')
      {
        lines++;
        if (lines >= MAX_ERROR_LINES)
        {
          shortMessage = errorMessage.substring(0, i + 1) + "...";
          break;
        }
      }
    }
    if (shortMessage == null)
    {
      shortMessage = errorMessage.toString();
    }


    JOptionPane.showMessageDialog(mainFrame, shortMessage, aTitle, JOptionPane.ERROR_MESSAGE);
  }

  protected void reportFileError(Throwable aThrowable)
  {
    if (aThrowable instanceof FileNotFoundException)
    {
      reportError("Unable to load selected file", null, aThrowable);
    }
    else if (aThrowable instanceof Fx702pLoadingException)
    {
      reportError("Error in selected file", null, aThrowable);
    }
    else if (aThrowable instanceof Fx702pException)
    {
      reportError("Syntax Error in selected file", null, aThrowable);
    }
    else if (aThrowable instanceof RuntimeException)
    {
      reportError("Unknown Error in selected file", null, aThrowable);
    }
  }

  public boolean askConfirmation(String aQuestion, String aDetailedQuestion)
  {
    return JOptionPane.showConfirmDialog(mainFrame, aDetailedQuestion, aQuestion, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
  }

  public void showPrefixes(boolean aF1Prefix, boolean aF2Prefix, boolean anArcPrefix, boolean anHypPrefix, boolean aModePrefix)
  {
    fx702pPanel.showPrefixes(aF1Prefix, aF2Prefix, anArcPrefix, anHypPrefix, aModePrefix);
  }

  public void showDeg(boolean aFlag)
  {
    fx702pPanel.showDeg(aFlag);
  }

  public void showRad(boolean aFlag)
  {
    fx702pPanel.showRad(aFlag);
  }

  public void showGrd(boolean aFlag)
  {
    fx702pPanel.showGrd(aFlag);
  }

  public void showTrace(boolean aFlag)
  {
    fx702pPanel.showTrace(aFlag);
  }

  public void showPrt(boolean aFlag)
  {
    fx702pPanel.showPrt(aFlag);
  }

  public void showRun(boolean aFlag)
  {
    fx702pPanel.showRun(aFlag);
  }

  public void showStop(boolean aFlag)
  {
    fx702pPanel.showStop(aFlag);
  }

  public void showBusy(boolean aShowBusy)
  {
    fx702pPanel.showBusy(aShowBusy);
  }

  public void showSteps(boolean aShowSteps)
  {
    fx702pPanel.showSteps(aShowSteps);
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
  public void stepInProgram()
  {
    behavior.stepInProgram();
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
  public void endProgram()
  {
    behavior.endProgram();
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
  public void input(String anInputPrompt)
  {
    behavior.input(anInputPrompt);
  }

  public void showProgram(Fx702pBasicProgram aBasicProgram, int aProgramIndex)
  {
    if (debugWindow != null)
    {
      debugWindow.dispose();
    }
    debugWindow = new DebugWindow(aBasicProgram, this, aProgramIndex);
    buildMenuBar(debugWindow);
    debugWindow.setIconImage(fx702pPanel.getIcon());
    debugWindow.addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowActivated(WindowEvent aWindowEvent)
        {
          if (moveToFront == 0)
          {
            if (mainFrame != null && mainFrame.isVisible() && !mainFrame.isActive())
            {
              mainFrame.toFront();
              debugWindow.toFront();
              moveToFront = 2;
            }
          }
          else
          {
            moveToFront--;
          }
        }
      });

    debugWindow.setVisible(true);
  }

  public void select(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    if (debugWindow != null)
    {
      debugWindow.select(aBasicLine, aSelectionStart, aSelectionEnd);
    }
  }

  public void clearSelection()
  {
    if (debugWindow != null)
    {
      debugWindow.clearSelection();
    }
  }

  public boolean processKey(KeyEvent aKeyEvent)
  {
    if (aKeyEvent.getModifiers() == SwingUtils.getDefaultModifierForCurrentPlatform())
    {
      return false;
    }

    Fx702pKey fx702pKey = null;
    if (aKeyEvent.getKeyCode() == KeyEvent.VK_F1)
    {
      fx702pKey = Fx702pKey.F1;
    }
    else if (aKeyEvent.getKeyCode() == KeyEvent.VK_F2)
    {
      fx702pKey = Fx702pKey.F2;
    }
    else if (aKeyEvent.getKeyCode() == KeyEvent.VK_ENTER)
    {
      fx702pKey = Fx702pKey.EXE;
    }
    else if (aKeyEvent.getKeyCode() == KeyEvent.VK_LEFT)
    {
      fx702pKey = Fx702pKey.LEFT_ARROW;
    }
    else if (aKeyEvent.getKeyCode() == KeyEvent.VK_RIGHT)
    {
      fx702pKey = Fx702pKey.RIGHT_ARROW;
    }
    else if (aKeyEvent.getKeyCode() == KeyEvent.VK_DEAD_CIRCUMFLEX)
    {
      // We ignore the dead circumflex. Supporting as a normal key would be
      // better but it seems this is not easy or even not possible in Swing/AWT
      // especially on multiple platforms
      fx702pKey = null;
    }
    else
    {
      fx702pKey = Fx702pKey.convertCharToFx702pKey(getFixedKeyEvent(aKeyEvent).getKeyChar());
    }

    if (fx702pKey != null)
    {
      if (aKeyEvent.getID() == KeyEvent.KEY_PRESSED)
      {
        if (lastKeyPressed == null || lastKeyPressed != fx702pKey)
        {
          emulator.keyPressed(fx702pKey);
          lastKeyPressed = fx702pKey;
        }
        else
        {
          emulator.keyRepeated(fx702pKey);
        }
      }
      else if (aKeyEvent.getID() == KeyEvent.KEY_RELEASED)
      {
        emulator.keyReleased(fx702pKey);
        lastKeyPressed = null;
      }
      return true;
    }
    else
    {
      return false;
    }
  }

  protected KeyEvent getFixedKeyEvent(KeyEvent aKeyEvent)
  {
    if (aKeyEvent.getModifiers() == SwingUtils.getFx702pKeyboardModifierForCurrentPlatform())
    {
      int keyCode = aKeyEvent.getKeyCode();
      if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z)
      {
        return new KeyEvent((Component)aKeyEvent.getSource(), aKeyEvent.getID(), aKeyEvent.getWhen(), aKeyEvent.getModifiers(), keyCode, (char)(keyCode - KeyEvent.VK_A + 1));
      }
      else
      {
        return aKeyEvent;
      }
    }
    else
    {
      return aKeyEvent;
    }
  }

  static protected class MenuEnablingListener implements MenuListener
  {
    public void menuCanceled(MenuEvent aMenuEvent)
    {
    }

    public void menuDeselected(MenuEvent aMenuEvent)
    {
    }

    public void menuSelected(MenuEvent aMenuEvent)
    {
      SwingUtils.enableMenuItems(((JMenu)aMenuEvent.getSource()).getPopupMenu().getComponents());
    }
  }

  public interface KeyboardAndDisplayBehavior extends Fx702pEmulatorComponent
  {
    public boolean isProgramRunning();
    public boolean isErrorMode();
  }

  public class AbstractKeyboardAndDisplayBehavior extends Fx702pAbstractEmulatorComponent implements KeyboardAndDisplayBehavior
  {
    @Override
    public void runProgram()
    {
      showStop(false);
      showBusy(true);
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void endProgram()
    {
      showStop(false);
      showBusy(false);
      setBehavior(RUN_MODE_BEHAVIOR);
    }

    @Override
    public void contProgram()
    {
      showBusy(true);
    }

    @Override
    public void stopProgram()
    {
      showBusy(false);
    }

    @Override
    public void startScroll()
    {
      setBehavior(new ScrollBehavior(this));
    }

    @Override
    public void endScroll()
    {
      showBusy(false);
    }

    @Override
    public void reportFx702pError(Fx702pException anError)
    {
      setBehavior(ERROR_BEHAVIOR);
    }

    public boolean isProgramRunning()
    {
      return false;
    }

    public boolean isErrorMode()
    {
      return false;
    }

    @Override
    public void input(String anInputPrompt)
    {
      showBusy(false);
      mainFrame.toFront();
    }

    @Override
    public void setRunMode()
    {
      showBusy(false);
    }
  }

  public class RunModeBehavior extends AbstractKeyboardAndDisplayBehavior
  {
    @Override
    public void home()
    {
      setCursorPosition(0);
    }
  }

  public class ProgramRunningBehavior extends AbstractKeyboardAndDisplayBehavior
  {
    @Override
    public void stopProgram()
    {
      showStop(true);
      showBusy(false);
      setBehavior(PROGRAM_STOPPED_BEHAVIOR);
    }

    @Override
    public boolean isProgramRunning()
    {
      return true;
    }
  }

  public class ProgramStoppedBehavior extends AbstractKeyboardAndDisplayBehavior
  {
    @Override
    public void contProgram()
    {
      showStop(false);
      showBusy(true);
      setBehavior(PROGRAM_RUNNING_BEHAVIOR);
    }

    @Override
    public void stepInProgram()
    {
      contProgram();
    }

    @Override
    public void home()
    {
      setCursorPosition(0);
    }

    @Override
    public void setRunMode()
    {
      super.setRunMode();
      showStop(false);

    }
  }

  public class ScrollBehavior extends AbstractKeyboardAndDisplayBehavior
  {
    public ScrollBehavior(KeyboardAndDisplayBehavior aPreviousBehavior)
    {
      previousBehavior = aPreviousBehavior;
    }

    @Override
    public void endScroll()
    {
      setBehavior(previousBehavior);
    }

    protected KeyboardAndDisplayBehavior previousBehavior;
  }

  public class ErrorBehavior extends AbstractKeyboardAndDisplayBehavior
  {
    @Override
    public void runProgram()
    {
    }

    @Override
    public void endProgram()
    {
    }

    @Override
    public void startScroll()
    {
      showBusy(true);
    }

    @Override
    public void allClear()
    {
      setBehavior(RUN_MODE_BEHAVIOR);
    }

    @Override
    public boolean isErrorMode()
    {
      return true;
    }
  }

  public class AutoRepeatTask extends TimerTask
  {
    @Override
    public void run()
    {
      Point currentPosition = MouseInfo.getPointerInfo().getLocation();
      SwingUtilities.convertPointFromScreen(currentPosition, fx702pPanel);
      Point keyCoordinates = fx702pPanel.getKeyCoordinates(currentPosition.x, currentPosition.y);
      if (keyCoordinates != null)
      {
        Fx702pKey currentKeyPressed = Fx702pKeyboardLayout.getKey(keyCoordinates.x, keyCoordinates.y);
        if (currentKeyPressed == keyPressed)
        {
          emulator.keyRepeated(keyPressed);
        }
        else
        {
          cancelAutorepeat();
        }
      }
      else
      {
        cancelAutorepeat();
      }
    }
  }

  static public class Fx702pFileFilter extends FileFilter
  {
    @Override
    public boolean accept(File aFile)
    {
      if (aFile.isDirectory())
      {
        return true;
      }

      return aFile.getName().endsWith(FILENAME_SUFFIX);
    }

    @Override
    public String getDescription()
    {
      return "Fx702p Program Files";
    }
  }

  static protected class FileSaveInfos
  {
    public FileSaveInfos(File aProgramFile, boolean aSaveVariables, boolean aSaveBreakpoints)
    {
      programFile = aProgramFile;
      saveVariables = aSaveVariables;
      saveBreakpoints = aSaveBreakpoints;
    }

    public File programFile = null;
    public boolean saveVariables = true;
    public boolean saveBreakpoints = true;
  }

  protected KeyboardAndDisplayBehavior behavior;
  protected JPopupMenu popupMenu;
  protected Fx702pPanel fx702pPanel;
  protected JFrame mainFrame;
  protected File defaultDirectory;
  protected boolean showAtStartup = false;
  protected String autoRun = null;
  protected Fx702pEmulator emulator;
  protected boolean popupMenuJustClosed = false;
  protected DebugWindow debugWindow = null;
  protected int moveToFront = 0;
  protected Fx702pKey keyPressed = null;
  protected Timer autorepeatTimer;
  protected Fx702pKey lastKeyPressed = null;
  protected SwingConsole swingConsole;

  protected final KeyboardAndDisplayBehavior RUN_MODE_BEHAVIOR = new RunModeBehavior();
  protected final KeyboardAndDisplayBehavior PROGRAM_RUNNING_BEHAVIOR = new ProgramRunningBehavior();
  protected final KeyboardAndDisplayBehavior PROGRAM_STOPPED_BEHAVIOR = new ProgramStoppedBehavior();
  protected final KeyboardAndDisplayBehavior ERROR_BEHAVIOR = new ErrorBehavior();

  protected final AutoRepeatTask AUTOREPEAT_TASK = new AutoRepeatTask();

  static public final long AUTOREPEAT_FIRST_DELAY = 1000;
  static public final long AUTOREPEAT_DELAY = 100;
  static public final int SPACE_BETWEEN_FRAMES = 10;
  static public final int MAX_ERROR_LINES = 10;

  static public final int SPECIAL_KEYS_MODIFIERS = KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK | KeyEvent.META_DOWN_MASK;

  static public final String FILE_MENU = "File";
  static public final String PROGRAM_MENU = "Program";

  static public final String OPEN_MENU_ITEM = "Open File...";
  static public final String RELOAD_MENU_ITEM = "Reload";
  static public final String RELOAD_ALL_MENU_ITEM = "Reload All";
  static public final String SAVE_MENU_ITEM = "Save";
  static public final String SAVE_AS_MENU_ITEM = "Save As...";
  static public final String SAVE_ALL_MENU_ITEM = "Save All";
  static public final String SAVE_ALL_AS_MENU_ITEM = "Save All As...";
  static public final String QUIT_MENU_ITEM = "Quit";
  static public final String RUN_MENU_ITEM = "Run";
  static public final String DEBUG_MENU_ITEM = "Debug";

  static public final Fx702pFileFilter FX702P_FILE_FILTER = new Fx702pFileFilter();
}
