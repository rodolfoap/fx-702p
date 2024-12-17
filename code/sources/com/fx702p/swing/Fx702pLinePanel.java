package com.fx702p.swing;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;

import com.fx702p.emulator.*;
import com.fx702p.interpreters.BasicInstructionIndex;

@SuppressWarnings("serial")
public class Fx702pLinePanel extends JPanel
{
  public Fx702pLinePanel(int aSizeX, int aSizeY, int aBoxSpace, int aCharacterSpace)
  {
    this(DEFAULT_DISPLAY_SIZE, aSizeX, aSizeY, aBoxSpace, aCharacterSpace);
    setDefaultColors();
  }

  public Fx702pLinePanel(int aDisplaySize, Color aFontForeground, Color aFontBackGround, int aSizeX, int aSizeY, int aBoxSpace, int aCharacterSpace)
  {
    this(aDisplaySize, aSizeX, aSizeY, aBoxSpace, aCharacterSpace);
    fontForeground = aFontForeground;
    fontBackground = aFontBackGround;
    setDefaultColors();
  }

  private Fx702pLinePanel(int aDisplaySize, int aSizeX, int aSizeY, int aBoxSpace, int aCharacterSpace)
  {
    displaySize = aDisplaySize;
    display = new char[displaySize];
    printedSize = 0;
    sizeX = aSizeX;
    sizeY = aSizeY;
    boxSpace = aBoxSpace;
    characterSpace = aCharacterSpace;

    Dimension characterDimension = getCharacterDimension(aSizeX, aSizeY, aBoxSpace);
    preferredSize = new Dimension(displaySize * (characterDimension.width + characterSpace), characterDimension.height);
  }

  public Fx702pBasicLine getBasicLine()
  {
    return basicLine;
  }

  static public Dimension getCharacterDimension(int aSizeX, int aSizeY, int aBoxSpace)
  {
    return new Dimension((aSizeX + aBoxSpace) * Characters.WIDTH, (aSizeY + aBoxSpace) * Characters.HEIGHT);
  }

  @Override
  public Dimension getPreferredSize()
  {
    return preferredSize;
  }

  public int getPrintedSize()
  {
    return printedSize;
  }

  protected void setDefaultColors()
  {
    fontForeground = getDefaultColor(fontForeground, DEFAULT_FONT_FOREGROUND);
    breakpointFontForeground = getDefaultColor(breakpointFontForeground, DEFAULT_BREAKPOINT_FONT_FOREGROUND);
    selectedBreakpointFontForeground = getDefaultColor(selectedBreakpointFontForeground, DEFAULT_SELECTED_BREAKPOINT_FONT_FOREGROUND);
    fontBackground = getDefaultColor(fontBackground, DEFAULT_FONT_BACKGROUND);
    selectedFondBackground = getDefaultColor(selectedFondBackground, DEFAULT_SELECTED_FONT_BACKGROUND);
    subSelectedFondBackground = getDefaultColor(subSelectedFondBackground, DEFAULT_SUBSELECTED_FONT_BACKGROUND);
  }

  protected Color getDefaultColor(Color aColor, Color aDefaultColor)
  {
    if (aColor != null)
    {
      return aColor;
    }
    else
    {
      return aDefaultColor;
    }
  }

  public void print(char[] theCharacters)
  {
    setLine(theCharacters);
    repaint();
  }

  public void setLine(char[] theCharacters)
  {
    printedSize = theCharacters.length;
    System.arraycopy(theCharacters, 0, display, 0, Math.min(displaySize, printedSize));
    for (int i = printedSize; i < displaySize; i++)
    {
      display[i] = ' ';
    }
    setRefreshNeeded();
  }

  public void setLine(String aString)
  {
    setLine(aString.toCharArray());
  }

  public void setBasicInformations(Fx702pBasicProgram aBasicProgram, Fx702pBasicLine aBasicLine)
  {
    basicProgram = aBasicProgram;
    basicLine = aBasicLine;
    instructionBoundaries = basicLine.getParsedLine().getInstructionBoundaries();
  }

  public char getCharAt(int anIndex)
  {
    return display[anIndex];
  }

  public void setCharAt(int anIndex, char aChar)
  {
    display[anIndex] = aChar;
    setRefreshNeeded();
  }

  public void setCursorAt(int anIndex, char aChar)
  {
    display[anIndex] = aChar;
    cursorToRefresh = anIndex;
    repaint(getCharacterBounds(cursorToRefresh));
  }

  protected Rectangle getCharacterBounds(int aPosition)
  {
    int width = Characters.WIDTH * (sizeX + boxSpace) + characterSpace;
    int x = aPosition * width;
    return new Rectangle(x, 0, width, getSize().height);
  }

  public boolean isEmpty()
  {
    for (char c : display)
    {
      if (c != ' ')
      {
        return false;
      }
    }
    return true;
  }

  public int getInstructionIndex(int x)
  {
    int index = getCharacterIndex(x);
    if (instructionBoundaries == null || instructionBoundaries.isEmpty() || index < 0 || index >= instructionBoundaries.get(instructionBoundaries.size() - 1).end)
    {
      return -1;
    }
    else
    {
      int first = 0;
      int last = instructionBoundaries.size() - 1;
      int middle = -1;

      while (middle != first || last > first + 1)
      {
        middle = first + (last - first) / 2;
        if (index >= instructionBoundaries.get(middle).begin)
        {
          first = middle;
        }
        else
        {
          last = middle;
        }
      }
      if (index >= instructionBoundaries.get(first).begin && index < instructionBoundaries.get(first).end)
      {
        return first;
      }
      else if (index >= instructionBoundaries.get(last).begin && index < instructionBoundaries.get(last).end)
      {
        return last;
      }
      else
      {
        return -1;
      }
    }
  }

  public void select(int aSelectionStart, int aSelectionEnd, boolean aBreakpointSelected)
  {
    selectionStart = aSelectionStart;
    selectionEnd = aSelectionEnd;
    breakpointSelected = aBreakpointSelected;
    setRefreshNeeded();
    repaint();
  }

  public void subSelect(int aSubSelectionStart, int aSubSelectionEnd)
  {
    subSelectionStart = aSubSelectionStart;
    subSelectionEnd = aSubSelectionEnd;
    setRefreshNeeded();
    repaint();
  }

  public void clearSelection()
  {
    selectionStart = NO_SELECTION;
    selectionEnd = NO_SELECTION;
    subSelectionStart = NO_SELECTION;
    subSelectionEnd = NO_SELECTION;
    breakpointSelected = false;
    setRefreshNeeded();
    repaint();
  }

  protected int getCharacterIndex(int x)
  {
    return x / (Characters.WIDTH * (sizeX + boxSpace) + characterSpace);
  }

  protected void clear()
  {
    Arrays.fill(display, ' ');
    setRefreshNeeded();
  }

  @Override
  public void paintComponent(Graphics aGraphics)
  {
    // We put the display in a buffer because it is a slow process.
    // Not really a problem with the Emulator screen, much more with the Source
    // Code.
    boolean refreshed = false;
    if (isRefreshNeeded())
    {
      refreshed = true;
      refreshNeeded = false;
      imageWidth = getSize().width;
      image = createImage(imageWidth, getSize().height);
      refreshNeeded = false;
      Graphics imageGraphics = image.getGraphics();
      imageGraphics.setColor(fontBackground);
      imageGraphics.fillRect(0, 0, getSize().width, getSize().height);

      if (selectionStart != NO_SELECTION)
      {
        int firstX = selectionStart * (Characters.WIDTH * (sizeX + boxSpace) + characterSpace);
        int lastX = selectionEnd * (Characters.WIDTH * (sizeX + boxSpace) + characterSpace) - characterSpace;
        imageGraphics.setColor(selectedFondBackground);
        imageGraphics.fillRect(firstX, 0, lastX - firstX, getSize().height);
      }

      if (subSelectionStart != NO_SELECTION)
      {
        int firstX = subSelectionStart * (Characters.WIDTH * (sizeX + boxSpace) + characterSpace);
        int lastX = subSelectionEnd * (Characters.WIDTH * (sizeX + boxSpace) + characterSpace) - characterSpace;
        imageGraphics.setColor(subSelectedFondBackground);
        imageGraphics.fillRect(firstX, 0, lastX - firstX, getSize().height);
      }

      buildBreakpointsBoundaries();

      for (int i = 0; i < displaySize; i++)
      {
        paintCharacterDrawings(imageGraphics, Characters.getCharacterDrawings(display[i]), i);
      }
    }
    else if (cursorToRefresh >= 0)
    {
      Graphics imageGraphics = image.getGraphics();
      paintCharacterDrawings(imageGraphics, Characters.getCharacterDrawings(display[cursorToRefresh]), cursorToRefresh);
    }

    // First we draw the bufferedImage of our text and its background. We keep
    // this in a image
    // for performance reasons.
    aGraphics.drawImage(image, 0, 0, null);

    // Then we draw the rest of the alternative white/green background till the
    // end of the component
    // to handle the fact that we may have been resized since the creation of
    // the buffered image
    // Note: our size seems to be null sometimes the first time we are called
    // So we check it.
    if (getSize() != null && (refreshed || cursorToRefresh < 0))
    {
      aGraphics.setColor(fontBackground);
      aGraphics.fillRect(imageWidth, 0, getSize().width, getSize().height);
    }
    cursorToRefresh = -1;
  }

  protected void buildBreakpointsBoundaries()
  {
    breakpointInstructionBoundaries.clear();
    if (basicProgram != null && basicLine != null && instructionBoundaries != null)
    {
      for (int i = 0; i < instructionBoundaries.size(); i++)
      {
        BasicInstructionIndex basicInstructionIndex = new BasicInstructionIndex(basicLine.getParsedLine().getLineIndexInProgram(), i);
        if (basicProgram.containsBreakpoint(basicInstructionIndex))
        {
          breakpointInstructionBoundaries.add(instructionBoundaries.get(i));
        }
      }
    }
  }

  public synchronized boolean isRefreshNeeded()
  {
    return image == null || refreshNeeded;
  }

  public synchronized void setRefreshNeeded()
  {
    refreshNeeded = true;
  }

  protected void paintCharacterDrawings(Graphics aGraphics, String[] theDrawings, int aPosition)
  {
    int width = theDrawings[0].length();
    int height = theDrawings.length;

    Color characterForeground = fontForeground;
    for (InstructionBoundary instructionBoundary : breakpointInstructionBoundaries)
    {
      if (aPosition >= instructionBoundary.begin && aPosition < instructionBoundary.end)
      {
        if (breakpointSelected)
        {
          characterForeground = selectedBreakpointFontForeground;
        }
        else
        {
          characterForeground = breakpointFontForeground;
        }
        break;
      }
    }

    for (int i = 0; i < height; i++)
    {
      for (int j = 0; j < width; j++)
      {
        if (theDrawings[i].charAt(j) == ' ')
        {
          if (aPosition >= subSelectionStart && aPosition < subSelectionEnd)
          {
            aGraphics.setColor(subSelectedFondBackground);
          }
          else if (aPosition >= selectionStart && aPosition < selectionEnd)
          {
            aGraphics.setColor(selectedFondBackground);

          }
          else
          {
            aGraphics.setColor(fontBackground);
          }
        }
        else
        {
          aGraphics.setColor(characterForeground);
        }
        aGraphics.fillRect(aPosition * (width * (sizeX + boxSpace) + characterSpace) + j * (sizeX + boxSpace), i * (sizeY + boxSpace), sizeX, sizeY);
      }
    }
  }

  protected char display[];
  protected int displaySize;
  protected int printedSize;
  protected int sizeX;
  protected int sizeY;
  protected int boxSpace;
  protected int characterSpace;
  protected Dimension preferredSize;
  protected Image image = null;
  protected int imageWidth;
  protected boolean refreshNeeded = false;
  protected int cursorToRefresh = -1;
  protected List<InstructionBoundary> instructionBoundaries;
  protected List<InstructionBoundary> breakpointInstructionBoundaries = new Vector<InstructionBoundary>();
  protected Fx702pBasicProgram basicProgram = null;
  protected Fx702pBasicLine basicLine = null;

  protected Color fontForeground;
  protected Color breakpointFontForeground;
  protected Color selectedBreakpointFontForeground;
  protected Color fontBackground;
  protected Color selectedFondBackground;
  protected Color subSelectedFondBackground;

  protected int selectionStart = NO_SELECTION;
  protected int selectionEnd = NO_SELECTION;
  protected boolean breakpointSelected = false;

  public int subSelectionStart = NO_SELECTION;
  public int subSelectionEnd = NO_SELECTION;

  static public final int NO_SELECTION = -1;

  static public final int DEFAULT_DISPLAY_SIZE = 20;

  static public final Color DEFAULT_FONT_FOREGROUND = new Color(0, 0, 0);
  static public final Color DEFAULT_BREAKPOINT_FONT_FOREGROUND = new Color(225, 0, 0);
  static public final Color DEFAULT_SELECTED_BREAKPOINT_FONT_FOREGROUND = new Color(9, 0, 196);
  static public final Color DEFAULT_FONT_BACKGROUND = new Color(117, 134, 126);
  static public final Color DEFAULT_SELECTED_FONT_BACKGROUND = SwingUtils.DEFAULT_SELECTION_COLOR;
  static public final Color DEFAULT_SUBSELECTED_FONT_BACKGROUND = SwingUtils.DEFAULT_SUBSELECTION_COLOR;
}
