package com.fx702p.swing;

import static com.fx702p.swing.Fx702pLinePanel.DEFAULT_DISPLAY_SIZE;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.fx702p.emulator.*;

@SuppressWarnings("serial")
public class Fx702pPanel extends JLayeredPane
{
  public Fx702pPanel(Fx702pSwingKeyboardAndDisplay aSwingKeyboardAndDisplay) throws IOException
  {
    swingKeyboardAndDisplay = aSwingKeyboardAndDisplay;
    buildImages();


    dimension = new Dimension(fx702pImage.getWidth(), fx702pImage.getHeight());

    imagePanel = new Fx702pImagePanel();
    imagePanel.setBounds(0, 0, dimension.width, dimension.height);
    add(imagePanel, JLayeredPane.DEFAULT_LAYER);

    int lineX = getIntegerProperty(LINE_X_PROPERTY_NAME, DEFAULT_LINE_RECTANGLE.x);
    int lineY = getIntegerProperty(LINE_Y_PROPERTY_NAME, DEFAULT_LINE_RECTANGLE.y);
    int lineWidth = getIntegerProperty(LINE_WIDTH_PROPERTY_NAME, DEFAULT_LINE_RECTANGLE.width);
    int lineHeight = getIntegerProperty(LINE_HEIGHT_PROPERTY_NAME, DEFAULT_LINE_RECTANGLE.height);
    int boxSpace = getIntegerProperty(BOX_SPACE_PROPERTY_NAME, DEFAULT_BOX_SPACE);
    int characterSpace = getIntegerProperty(CHARACTER_SPACE_PROPERTY_NAME, DEFAULT_CHARACTER_SPACE);

    linePanel = new Fx702pLinePanel(lineWidth, lineHeight, boxSpace, characterSpace);
    linePanel.setOpaque(false);
    linePanel.setBounds(lineX, lineY, linePanel.getPreferredSize().width, linePanel.getPreferredSize().height);
    add(linePanel, JLayeredPane.PALETTE_LAYER, 0);

    int stepsX = getIntegerProperty(STEPS_X_PROPERTY_NAME, DEFAULT_STEPS_RECTANGLE.x);
    int stepsY = getIntegerProperty(STEPS_Y_PROPERTY_NAME, DEFAULT_STEPS_RECTANGLE.y);
    int stepsWidth = getIntegerProperty(STEPS_WIDTH_PROPERTY_NAME, DEFAULT_STEPS_RECTANGLE.width);
    int stepsHeight = getIntegerProperty(STEPS_HEIGHT_PROPERTY_NAME, DEFAULT_STEPS_RECTANGLE.height);
    programStepsPanel = new Fx702pProgramStepsPanel(new Rectangle(stepsX, stepsY, stepsWidth, stepsHeight));
    add(programStepsPanel, JLayeredPane.PALETTE_LAYER, 1);

    cursorManager = new CursorManager(this);
    clearDisplay();
  }

  public boolean isEmpty()
  {
    return linePanel.isEmpty();
  }

  public Image getIcon()
  {
    return fx702pIcon;
  }

  public void setSteps(int aSteps)
  {
    programStepsPanel.setSteps(aSteps);
  }

  public void showBusy(boolean aShowBusy)
  {
    programStepsPanel.showBusy(aShowBusy);
  }

  public void showSteps(boolean aShowSteps)
  {
    programStepsPanel.showSteps(aShowSteps);
  }

  public void keyPressed(Fx702pKey aKey, Point aKeyPosition)
  {
    if (aKey != Fx702pKey.MODE && aKey != Fx702pKey.F1 && aKey != Fx702pKey.F2)
    {
      keyPressedPosition = aKeyPosition;
      repaint();
    }
  }

  public void keyReleased(Fx702pKey aKey, Point aKeyPosition)
  {
    if (keyPressedPosition != null)
    {
      keyPressedPosition = null;
      repaint();
    }
  }

  protected void buildImages() throws IOException
  {
    fx702pImage = ImageIO.read(getClass().getResource(FX702P_IMAGE));
    fx702pIcon = ImageIO.read(getClass().getResource(FX702P_ICON));
    runImage = ImageIO.read(getClass().getResource(RUN_IMAGE));
    wrtImage = ImageIO.read(getClass().getResource(WRT_IMAGE));
    traceImage = ImageIO.read(getClass().getResource(TRACE_IMAGE));
    stopImage = ImageIO.read(getClass().getResource(STOP_IMAGE));
    degImage = ImageIO.read(getClass().getResource(DEG_IMAGE));
    radImage = ImageIO.read(getClass().getResource(RAD_IMAGE));
    grdImage = ImageIO.read(getClass().getResource(GRD_IMAGE));
    prtImage = ImageIO.read(getClass().getResource(PRT_IMAGE));
    f1Image = ImageIO.read(getClass().getResource(F1_IMAGE));
    f2Image = ImageIO.read(getClass().getResource(F2_IMAGE));
    arcImage = ImageIO.read(getClass().getResource(ARC_IMAGE));
    hypImage = ImageIO.read(getClass().getResource(HYP_IMAGE));
  }

  protected void clearDisplay()
  {
    setCursorPosition(0);
    linePanel.clear();
  }

  protected int getIntegerProperty(String aPropertyName, int aDefaultValue)
  {
    String value = System.getProperty(aPropertyName);
    if (value != null)
    {
      try
      {
        return Integer.parseInt(value);
      }
      catch (NumberFormatException exception)
      {
        Console.reportError("Bad format for integer " + aPropertyName + ": " + value);
      }
    }
    return aDefaultValue;
  }

  public Point getKeyCoordinates(int x, int y)
  {
    if (x >= FIRST_KEY_X && y >= FIRST_KEY_Y)
    {
      int keyX = (x - FIRST_KEY_X) / (KEY_WIDTH + WIDTH_BETWEEN_KEYS);
      if (keyX < Fx702pKeyboardLayout.LAYOUT_WIDTH && (x - FIRST_KEY_X) % (KEY_WIDTH + WIDTH_BETWEEN_KEYS) <= KEY_WIDTH)
      {
        int keyY = (y - FIRST_KEY_Y) / (KEY_HEIGHT + HEIGHT_BETWEEN_KEYS);
        if (keyY < Fx702pKeyboardLayout.LAYOUT_HEIGHT && (y - FIRST_KEY_Y) % (KEY_HEIGHT + HEIGHT_BETWEEN_KEYS) <= KEY_HEIGHT)
        {
          return new Point(keyX, keyY);
        }
      }
    }
    return null;
  }


  public void print(char[] theCharacters)
  {
    cursorManager.suspendBlinking();
    linePanel.print(theCharacters);
    saveCharacterUnderCursor();
    cursorManager.restartBlinking();
  }

  public void setCursorVisible(boolean aCursorVisibleFlag)
  {
    cursorManager.setCursorVisible(aCursorVisibleFlag);
    if (!aCursorVisibleFlag)
    {
      showCursor(false);
    }
  }

  public int getCursorPosition()
  {
    return cursorPosition;
  }

  public void setCursorPosition(int aCursorPosition)
  {
    if (aCursorPosition != cursorPosition)
    {
      cursorManager.suspendBlinking();
      restoreCharacterUnderCursor();
      cursorPosition = aCursorPosition;
      saveCharacterUnderCursor();
      cursorManager.restartBlinking();
    }
  }

  public void moveCursorRight()
  {
    if (cursorPosition < DEFAULT_DISPLAY_SIZE - 1 && cursorPosition < linePanel.getPrintedSize())
    {
      cursorManager.suspendBlinking();
      restoreCharacterUnderCursor();
      cursorPosition++;
      saveCharacterUnderCursor();
      cursorManager.restartBlinking();
    }
  }

  public void moveCursorRight(int aCount)
  {
    if (aCount > 0)
    {
      int newCursorPosition = Math.min(cursorPosition + aCount, Math.min(DEFAULT_DISPLAY_SIZE - 1, linePanel.getPrintedSize()));
      if (newCursorPosition != cursorPosition)
      {
        cursorManager.suspendBlinking();
        restoreCharacterUnderCursor();
        cursorPosition = newCursorPosition;
        saveCharacterUnderCursor();
        cursorManager.restartBlinking();
      }
    }
  }

  public void moveCursorLeft()
  {
    if (cursorPosition > 0)
    {
      cursorManager.suspendBlinking();
      restoreCharacterUnderCursor();
      cursorPosition--;
      saveCharacterUnderCursor();
      cursorManager.restartBlinking();
    }
  }

  public void moveCursorLeft(int aCount)
  {
    if (aCount > 0)
    {
      int newCursorPosition = Math.max(cursorPosition - aCount, 0);
      if (newCursorPosition != cursorPosition)
      {
        cursorManager.suspendBlinking();
        restoreCharacterUnderCursor();
        cursorPosition = Math.max(cursorPosition - aCount, 0);
        saveCharacterUnderCursor();
        cursorManager.restartBlinking();
      }
    }
  }

  protected void restoreCharacterUnderCursor()
  {
    if (cursorPosition < DEFAULT_DISPLAY_SIZE && linePanel.getCharAt(cursorPosition) == Characters.CURSOR_CHAR)
    {
      linePanel.setCharAt(cursorPosition, characterUnderCursor);
    }
  }

  protected void saveCharacterUnderCursor()
  {
    if (cursorPosition < DEFAULT_DISPLAY_SIZE)
    {
      characterUnderCursor = linePanel.getCharAt(cursorPosition);
    }
  }

  public void showCursor(boolean aCursorVisibleFlag)
  {
    if (cursorPosition < DEFAULT_DISPLAY_SIZE && (aCursorVisibleFlag != lastCursorVisible || cursorPosition != lastCursorPosition))
    {
      lastCursorVisible = aCursorVisibleFlag;
      lastCursorPosition = cursorPosition;
      if (aCursorVisibleFlag)
      {
        linePanel.setCursorAt(cursorPosition, Characters.CURSOR_CHAR);
      }
      else
      {
        linePanel.setCursorAt(cursorPosition, characterUnderCursor);
      }
    }
  }

  public void showPrefixes(boolean aF1Prefix, boolean aF2Prefix, boolean anArcPrefix, boolean anHypPrefix, boolean aModePrefix)
  {
    f1Prefix = aF1Prefix;
    f2Prefix = aF2Prefix;
    arcPrefix = anArcPrefix;
    hypPrefix = anHypPrefix;
    modePrefix = aModePrefix;
    repaint();
  }

  public void showDeg(boolean aFlag)
  {
    degMode = aFlag;
    if (degMode)
    {
      radMode = grdMode = false;
    }
    repaint();
  }

  public void showRad(boolean aFlag)
  {
    radMode = aFlag;
    if (radMode)
    {
      degMode = grdMode = false;
    }
    repaint();
  }

  public void showGrd(boolean aFlag)
  {
    grdMode = aFlag;
    if (grdMode)
    {
      degMode = radMode = false;
    }
    repaint();
  }

  public void showTrace(boolean aFlag)
  {
    traceMode = aFlag;
    repaint();
  }

  public void showPrt(boolean aFlag)
  {
    prtMode = aFlag;
    repaint();
  }

  public void showRun(boolean aFlag)
  {
    runMode = aFlag;
    repaint();
  }

  public void showStop(boolean aStopMode)
  {
    stopMode = aStopMode;
    repaint();
  }

  public void paintImage(Graphics aGraphics)
  {
    aGraphics.drawImage(fx702pImage, 0, 0, null);
    if (runMode)
    {
      aGraphics.drawImage(runImage, RUN_POSITION.x, RUN_POSITION.y, null);
    }
    else
    {
      aGraphics.drawImage(wrtImage, RUN_POSITION.x, RUN_POSITION.y, null);
    }
    if (stopMode)
    {
      aGraphics.drawImage(stopImage, STOP_POSITION.x, STOP_POSITION.y, null);
    }
    if (traceMode)
    {
      aGraphics.drawImage(traceImage, TRACE_POSITION.x, TRACE_POSITION.y, null);
    }
    if (degMode)
    {
      aGraphics.drawImage(degImage, DEG_POSITION.x, DEG_POSITION.y, null);
    }
    if (radMode)
    {
      aGraphics.drawImage(radImage, RAD_POSITION.x, RAD_POSITION.y, null);
    }
    if (grdMode)
    {
      aGraphics.drawImage(grdImage, GRD_POSITION.x, GRD_POSITION.y, null);
    }
    if (prtMode)
    {
      aGraphics.drawImage(prtImage, PRT_POSITION.x, PRT_POSITION.y, null);
    }
    if (f1Prefix)
    {
      aGraphics.drawImage(f1Image, F1_POSITION.x, F1_POSITION.y, null);
      invertKey(aGraphics, Fx702pKeyboardLayout.F1_KEY_POSITION.x, Fx702pKeyboardLayout.F1_KEY_POSITION.y);
    }
    if (f2Prefix)
    {
      aGraphics.drawImage(f2Image, F2_POSITION.x, F2_POSITION.y, null);
      invertKey(aGraphics, Fx702pKeyboardLayout.F2_KEY_POSITION.x, Fx702pKeyboardLayout.F2_KEY_POSITION.y);
    }
    if (arcPrefix)
    {
      aGraphics.drawImage(arcImage, ARC_POSITION.x, ARC_POSITION.y, null);
    }
    if (hypPrefix)
    {
      aGraphics.drawImage(hypImage, HYP_POSITION.x, HYP_POSITION.y, null);
    }
    if (modePrefix)
    {
      invertKey(aGraphics, Fx702pKeyboardLayout.MODE_KEY_POSITION.x, Fx702pKeyboardLayout.MODE_KEY_POSITION.y);
    }
    if (keyPressedPosition != null)
    {
      invertKey(aGraphics, keyPressedPosition.x, keyPressedPosition.y);
    }
  }

  protected void invertKey(Graphics aGraphics, int x, int y)
  {
    Graphics graphics = aGraphics.create();
    try
    {
      Rectangle rectangle = new Rectangle(FIRST_KEY_X + x * (KEY_WIDTH + WIDTH_BETWEEN_KEYS), FIRST_KEY_Y + y * (KEY_HEIGHT + HEIGHT_BETWEEN_KEYS), KEY_WIDTH, KEY_HEIGHT);
      graphics.setColor(Color.WHITE);
      graphics.setXORMode(Color.BLACK);
      graphics.fillRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, rectangle.width / 5, rectangle.height / 5);
    }
    finally
    {
      graphics.dispose();
    }
  }

  @Override
  public Dimension getPreferredSize()
  {
    return dimension;
  }

  @Override
  public Dimension getMinimumSize()
  {
    return dimension;
  }

  @Override
  public Dimension getMaximumSize()
  {
    return dimension;
  }

  protected class Fx702pImagePanel extends JPanel
  {
    public Fx702pImagePanel()
    {
      setFocusable(true);
      swingKeyboardAndDisplay.getMainFrame().addWindowListener(new WindowAdapter()
        {
          @Override
          public void windowActivated(WindowEvent aWindowEvent)
          {
            requestFocusInWindow();
          }
        });
      setLayout(null);
      buildTooltipComponents();
      setTooltips();
      enableEvents(AWTEvent.KEY_EVENT_MASK);
    }

    protected void buildTooltipComponents()
    {
      tooltipComponents = new JComponent[Fx702pKeyboardLayout.LAYOUT_WIDTH][];
      f1TooltipComponents = new JComponent[Fx702pKeyboardLayout.LAYOUT_WIDTH][];
      for (int x = 0; x < Fx702pKeyboardLayout.LAYOUT_WIDTH; x++)
      {
        tooltipComponents[x] = new JComponent[Fx702pKeyboardLayout.LAYOUT_HEIGHT];
        f1TooltipComponents[x] = new JComponent[Fx702pKeyboardLayout.LAYOUT_HEIGHT];
        for (int y = 0; y < Fx702pKeyboardLayout.LAYOUT_HEIGHT; y++)
        {
          tooltipComponents[x][y] = new JComponent()
            {
              @Override
              protected void processEvent(AWTEvent anEvent)
              {
                super.processEvent(anEvent);
                if (anEvent instanceof MouseEvent)
                {
                  int id = ((MouseEvent)anEvent).getID();
                  if (id == MouseEvent.MOUSE_PRESSED || id == MouseEvent.MOUSE_RELEASED)
                  {
                    Fx702pPanel.this.processEvent(SwingUtilities.convertMouseEvent(this, (MouseEvent)anEvent, Fx702pPanel.this));
                  }
                }
              }
            };
          add(tooltipComponents[x][y]);
          tooltipComponents[x][y].setBounds(FIRST_KEY_X + x * (KEY_WIDTH + WIDTH_BETWEEN_KEYS), FIRST_KEY_Y + y * (KEY_HEIGHT + HEIGHT_BETWEEN_KEYS), KEY_WIDTH, KEY_HEIGHT);

          f1TooltipComponents[x][y] = new JComponent()
            {
            };
          add(f1TooltipComponents[x][y]);
          f1TooltipComponents[x][y].setBounds(FIRST_KEY_X + x * (KEY_WIDTH + WIDTH_BETWEEN_KEYS), FIRST_KEY_Y + y * (KEY_HEIGHT + HEIGHT_BETWEEN_KEYS) - F1_LABEL_HEIGHT - 1, KEY_WIDTH, F1_LABEL_HEIGHT);
        }
      }
    }

    protected void setTooltips()
    {
      for (int i = 0; i < KEY_TOOLTIPS.length; i++)
      {
        KeyTooltip keyTooltip = KEY_TOOLTIPS[i];
        tooltipComponents[keyTooltip.x][keyTooltip.y].setToolTipText(keyTooltip.getTooltipText());
      }

      for (int i = 0; i < F1_LABEL_TOOLTIPS.length; i++)
      {
        KeyTooltip f1LabelTooltip = F1_LABEL_TOOLTIPS[i];
        f1TooltipComponents[f1LabelTooltip.x][f1LabelTooltip.y].setToolTipText(f1LabelTooltip.getTooltipText());
      }
    }

    @Override
    protected void processEvent(AWTEvent anEvent)
    {
      super.processEvent(anEvent);
      if (anEvent instanceof KeyEvent)
      {
        KeyEvent keyEvent = (KeyEvent)anEvent;
        if (!keyEvent.isConsumed() && (keyEvent.getID() == KeyEvent.KEY_PRESSED || keyEvent.getID() == KeyEvent.KEY_RELEASED))
        {
          swingKeyboardAndDisplay.processKey(keyEvent);
        }
      }
    }

    @Override
    public void paint(Graphics aGraphics)
    {
      paintImage(aGraphics);
    }
  }

  protected Fx702pSwingKeyboardAndDisplay swingKeyboardAndDisplay;
  protected BufferedImage fx702pImage;
  protected BufferedImage fx702pIcon;
  protected BufferedImage stopImage;
  protected BufferedImage runImage, wrtImage, traceImage, prtImage;
  protected BufferedImage degImage, radImage, grdImage;
  protected BufferedImage f1Image, f2Image, arcImage, hypImage;

  protected Dimension dimension;
  protected int cursorPosition = 0;
  protected int lastCursorPosition = 0;
  protected boolean lastCursorVisible = false;

  protected Fx702pImagePanel imagePanel;
  protected Fx702pLinePanel linePanel;
  protected Fx702pProgramStepsPanel programStepsPanel;
  protected char inputBuffer[] = new char[INPUT_BUFFER_SIZE];
  protected int inputBufferLength;
  protected int inputBufferWindowStart = 0;
  protected boolean degMode = true;
  protected boolean radMode = false;
  protected boolean grdMode = false;
  protected boolean stopMode = false;
  protected boolean runMode = true;
  protected boolean traceMode = false;
  protected boolean prtMode = false;
  protected boolean f1Prefix = false, f2Prefix = false, arcPrefix = false, hypPrefix = false, modePrefix = false;

  protected Point keyPressedPosition = null;

  protected CursorManager cursorManager;
  protected char characterUnderCursor = ' ';

  protected JComponent tooltipComponents[][];
  protected JComponent f1TooltipComponents[][];

  static public final int INPUT_BUFFER_SIZE = 62;
  static public final Rectangle DEFAULT_LINE_RECTANGLE = new Rectangle(75, 47, 2, 2);
  static public final int DEFAULT_BOX_SPACE = 1;
  static public final int DEFAULT_CHARACTER_SPACE = 5;

  static public final Rectangle DEFAULT_STEPS_RECTANGLE = new Rectangle(484, 40, 55, 20);

  static public final String LINE_X_PROPERTY_NAME = "com.fx702p.swing.line.x";
  static public final String LINE_Y_PROPERTY_NAME = "com.fx702p.swing.line.y";
  static public final String LINE_WIDTH_PROPERTY_NAME = "com.fx702p.swing.line.width";
  static public final String LINE_HEIGHT_PROPERTY_NAME = "com.fx702p.swing.line.height";
  static public final String BOX_SPACE_PROPERTY_NAME = "com.fx702p.swing.line.box_space";
  static public final String CHARACTER_SPACE_PROPERTY_NAME = "com.fx702p.swing.line.character_space";

  static public final String STEPS_X_PROPERTY_NAME = "com.fx702p.swing.steps.x";
  static public final String STEPS_Y_PROPERTY_NAME = "com.fx702p.swing.steps.y";
  static public final String STEPS_WIDTH_PROPERTY_NAME = "com.fx702p.swing.steps.width";
  static public final String STEPS_HEIGHT_PROPERTY_NAME = "com.fx702p.swing.steps.height";

  static public final String FX702P_IMAGE = "/pictures/fx702p.jpg";
  static public final String FX702P_ICON = "/pictures/icon.jpg";

  static public final int STATUS_Y = 34;
  static public final String RUN_IMAGE = "/pictures/run.jpg";
  static public final Point RUN_POSITION = new Point(208, STATUS_Y);
  static public final String WRT_IMAGE = "/pictures/wrt.jpg";
  static public final Point WRT_POSITION = new Point(232, STATUS_Y);
  static public final String STOP_IMAGE = "/pictures/stop.jpg";
  static public final Point STOP_POSITION = new Point(258, STATUS_Y);
  static public final String DEG_IMAGE = "/pictures/deg.jpg";
  static public final Point DEG_POSITION = new Point(311, STATUS_Y);
  static public final String RAD_IMAGE = "/pictures/rad.jpg";
  static public final Point RAD_POSITION = new Point(339, STATUS_Y);
  static public final String GRD_IMAGE = "/pictures/grd.jpg";
  static public final Point GRD_POSITION = new Point(360, STATUS_Y);
  static public final String TRACE_IMAGE = "/pictures/trace.jpg";
  static public final Point TRACE_POSITION = new Point(396, STATUS_Y);
  static public final String PRT_IMAGE = "/pictures/prt.jpg";
  static public final Point PRT_POSITION = new Point(438, STATUS_Y);

  static public final String F1_IMAGE = "/pictures/f1.jpg";
  static public final Point F1_POSITION = new Point(94, STATUS_Y);
  static public final String F2_IMAGE = "/pictures/f2.jpg";
  static public final Point F2_POSITION = new Point(110, STATUS_Y);
  static public final String ARC_IMAGE = "/pictures/arc.jpg";
  static public final Point ARC_POSITION = new Point(133, STATUS_Y);
  static public final String HYP_IMAGE = "/pictures/hyp.jpg";
  static public final Point HYP_POSITION = new Point(162, STATUS_Y);
  static public final int FIRST_KEY_Y = 130;
  static public final int KEY_HEIGHT = 25;
  static public final int F1_LABEL_HEIGHT = 12;
  static public final int HEIGHT_BETWEEN_KEYS = 20;
  static public final int FIRST_KEY_X = 18;
  static public final int KEY_WIDTH = 38;
  static public final int WIDTH_BETWEEN_KEYS = 16;

  static public class KeyTooltip
  {
    public KeyTooltip(int aKeycode, int aModifiers)
    {
      this(aKeycode, aModifiers, null);
    }

    public KeyTooltip(int aKeycode, int aModifiers, KeyTooltip aNextKeyTooltip)
    {
      this(-1, -1, aKeycode, aModifiers, aNextKeyTooltip);
    }

    public KeyTooltip(int anX, int anY, int aKeycode, int aModifiers)
    {
      this(anX, anY, aKeycode, aModifiers, null);
    }

    public KeyTooltip(int anX, int anY, int aKeycode, int aModifiers, KeyTooltip aNextKeyTooltip)
    {
      this(anX, anY, KeyEvent.getKeyText(aKeycode), KeyEvent.getKeyModifiersText(aModifiers), aNextKeyTooltip);
    }

    public KeyTooltip(int anX, int anY, String aKeyText, int aModifiers)
    {
      this(anX, anY, aKeyText, KeyEvent.getKeyModifiersText(aModifiers), null);
    }

    public KeyTooltip(int anX, int anY, String aKeyText, int aModifiers, KeyTooltip aNextKeyTooltip)
    {
      this(anX, anY, aKeyText, KeyEvent.getKeyModifiersText(aModifiers), aNextKeyTooltip);
    }

    public KeyTooltip(int anX, int anY, String aKeyText, String aModifiersText, KeyTooltip aNextKeyTooltip)
    {
      x = anX;
      y = anY;
      if (aModifiersText == null || aModifiersText.length() == 0)
      {
        tooltipText = aKeyText;
      }
      else
      {
        tooltipText = aModifiersText + '+' + aKeyText;
      }
      nextKeyKeyTooltip = aNextKeyTooltip;
    }

    public String getTooltipText()
    {
      if (nextKeyKeyTooltip == null)
      {
        return tooltipText;
      }
      else
      {
        return tooltipText + " or " + nextKeyKeyTooltip.getTooltipText();
      }
    }

    public final int x;
    public final int y;
    public final KeyTooltip nextKeyKeyTooltip;
    private final String tooltipText;
  }

  static public final KeyTooltip KEY_TOOLTIPS[] =
    {
      new KeyTooltip(0, 0, KeyEvent.VK_F1, 0, new KeyTooltip(KeyEvent.VK_Y, KeyEvent.CTRL_MASK)),
      new KeyTooltip(1, 0, KeyEvent.VK_F2, 0, new KeyTooltip(KeyEvent.VK_Z, KeyEvent.CTRL_MASK)),
      new KeyTooltip(7, 0, KeyEvent.VK_O, KeyEvent.CTRL_MASK),
      new KeyTooltip(9, 4, KeyEvent.VK_E, KeyEvent.CTRL_MASK),
      new KeyTooltip(10, 0, "^", 0),
      new KeyTooltip(11, 0, KeyEvent.VK_BACK_SPACE, 0, new KeyTooltip(KeyEvent.VK_H, KeyEvent.CTRL_MASK)),
      new KeyTooltip(11, 1, KeyEvent.VK_T, KeyEvent.CTRL_MASK),
      new KeyTooltip(11, 2, KeyEvent.VK_A, KeyEvent.CTRL_MASK),
      new KeyTooltip(11, 4, KeyEvent.VK_ENTER, 0),
      new KeyTooltip(12, 0, KeyEvent.VK_C, KeyEvent.CTRL_MASK),
      new KeyTooltip(12, 1, KeyEvent.VK_S, KeyEvent.CTRL_MASK),
      new KeyTooltip(12, 2, KeyEvent.VK_Q, KeyEvent.CTRL_MASK),
      new KeyTooltip(12, 3, KeyEvent.VK_LEFT, 0, new KeyTooltip(KeyEvent.VK_B, KeyEvent.CTRL_MASK)),
      new KeyTooltip(12, 4, KeyEvent.VK_RIGHT, 0, new KeyTooltip(KeyEvent.VK_F, KeyEvent.CTRL_MASK)),
    };

  static public final KeyTooltip F1_LABEL_TOOLTIPS[] =
    {
      new KeyTooltip(10, 0, "{", 0, new KeyTooltip(KeyEvent.VK_L, KeyEvent.CTRL_MASK)),
      new KeyTooltip(10, 1, "}", 0, new KeyTooltip(KeyEvent.VK_G, KeyEvent.CTRL_MASK)),
      new KeyTooltip(10, 2, "~", 0, new KeyTooltip(KeyEvent.VK_D, KeyEvent.CTRL_MASK)),
      new KeyTooltip(9, 4, KeyEvent.VK_P, KeyEvent.CTRL_MASK),
    };
}
