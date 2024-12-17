package com.fx702p.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.fx702p.emulator.*;
import com.fx702p.interpreters.*;
import com.fx702p.parser.*;
import com.fx702p.swing.SwingUtils.ExtendedPopupMenu;

@SuppressWarnings("serial")
public class SourceCodePanel extends JPanel implements Scrollable
{
  public SourceCodePanel(DebugWindow aDebugWindow, Fx702pBasicProgram aBasicProgram)
  {
    debugWindow = aDebugWindow;
    basicProgram = aBasicProgram;
    characterDimension = Fx702pLinePanel.getCharacterDimension(PIXEL_SIZE, PIXEL_SIZE, 0);
    setLayout(null);
    setBackground(Color.WHITE);

    buildLines();

    // Synchronize line widths so they are all the same
    // and as long as the SourceCodePanel when it is resized
    addComponentListener(new ComponentAdapter()
      {
        @Override
        public void componentResized(ComponentEvent e)
        {
          for (Component component : getComponents())
          {
            Rectangle bounds = component.getBounds();
            bounds.width = getSize().width - 2 * BORDER_SIZE;
            component.setBounds(bounds);
          }
        }
      });

    buildPopupMenuAndListeners();
  }

  protected void buildLines()
  {
    alternate = true;
    height = 0;
    maxWidth = 0;
    linePanels = new Vector<Fx702pLinePanel>();
    linePanelVerticalPositions = new Vector<Integer>();
    if (basicProgram.getParsedProgram() != null)
    {
      basicProgram.getParsedProgram().jjtAccept(new Fx702pAbstractParserVisitor()
        {
          @Override
          public Object visit(ASTProgram aProgram, Object aData)
          {
            aProgram.childrenAccept(this, aData);
            return null;
          }

          @Override
          public Object visit(ASTLine aLine, Object aData)
          {
            Fx702pBasicLine basicLine = basicProgram.getBasicSourceCode().getLine(aLine.getLineIndexInSourceCode());
            String lineAsString = basicLine.toString();
            Color background = alternate ? ALTERNATE_BACKGROUND : Color.WHITE;
            alternate = !alternate;
            Fx702pLinePanel linePanel = new Fx702pLinePanel(lineAsString.length(), Color.BLACK, background, PIXEL_SIZE, PIXEL_SIZE, 0, 2);
            linePanelsByBasicLine.put(basicLine, linePanel);
            linePanel.setLine(lineAsString);
            linePanel.setBasicInformations(basicProgram, basicLine);
            linePanel.setOpaque(false);
            int counter = getComponentCount();
            add(linePanel);
            int lineHeight = linePanel.getPreferredSize().height + LINE_SPACING;
            linePanel.setBounds(BORDER_SIZE, BORDER_SIZE + counter * lineHeight, linePanel.getPreferredSize().width, linePanel.getPreferredSize().height);
            linePanels.add(linePanel);
            linePanelVerticalPositions.add(height);
            height += lineHeight;
            maxWidth = Math.max(maxWidth, linePanel.getPreferredSize().width);
            return null;
          }
        }, null);
    }
    linePanelVerticalPositions.add(height);

    height += BORDER_SIZE;
    int panelWidth = maxWidth + 2 * BORDER_SIZE;

    // Synchronize line widths so they are all the same
    for (Component component : getComponents())
    {
      Rectangle bounds = component.getBounds();
      bounds.width = maxWidth;
      component.setBounds(bounds);
    }

    setBounds(0, 0, panelWidth, height);
    setPreferredSize(new Dimension(panelWidth, height));
  }

  protected void buildPopupMenuAndListeners()
  {
    Window window = SwingUtilities.getWindowAncestor(this);
    final ExtendedPopupMenu popupMenu = SwingUtils.buildPopupMenu(window, this, new MouseAdapter()
      {
        @Override
        public void mousePressed(MouseEvent aMouseEvent)
        {
          requestFocusInWindow();
          if (aMouseEvent.getClickCount() == 2)
          {
            toggleBreakpoint(aMouseEvent.getPoint());
          }
        }
      });

    JMenuItem loadMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildOpenMenuItem();
    popupMenu.add(loadMenuItem);

    JMenuItem reloadMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildReloadMenuItem();
    popupMenu.add(reloadMenuItem);

    JMenuItem reloadAllMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildReloadAllMenuItem();
    popupMenu.add(reloadAllMenuItem);

    JMenuItem saveMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildSaveMenuItem();
    popupMenu.add(saveMenuItem);

    JMenuItem saveAsMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildSaveAsMenuItem();
    popupMenu.add(saveAsMenuItem);

    JMenuItem saveAllMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildSaveAllMenuItem();
    popupMenu.add(saveAllMenuItem);

    JMenuItem saveAllAsMenuItem = debugWindow.getSwingKeyboardAndDisplay().buildSaveAllAsMenuItem();
    popupMenu.add(saveAllAsMenuItem);

    popupMenu.addSeparator();

    IntelligentMenuItem addBreakPointMenuItem = new IntelligentMenuItem(SET_BREAKPOINT_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          BasicInstructionIndex basicInstructionIndex = getBasicInstructionIndex(aClickPoint);
          if (basicInstructionIndex != null && !basicProgram.containsBreakpoint(basicInstructionIndex))
          {
            return true;
          }
          return false;
        }
      };
    addBreakPointMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          toggleBreakpoint(popupMenu.getClickPoint());
        }
      });
    popupMenu.add(addBreakPointMenuItem);

    IntelligentMenuItem removeBreakPointMenuItem = new IntelligentMenuItem(REMOVE_BREAKPOINT_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          BasicInstructionIndex basicInstructionIndex = getBasicInstructionIndex(aClickPoint);
          if (basicInstructionIndex != null && basicProgram.containsBreakpoint(basicInstructionIndex))
          {
            return true;
          }
          return false;
        }
      };
    removeBreakPointMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          toggleBreakpoint(popupMenu.getClickPoint());
        }
      });
    popupMenu.add(removeBreakPointMenuItem);

    IntelligentMenuItem removeAllBreakPointsMenuItem = new IntelligentMenuItem(REMOVE_ALL_BREAKPOINTS_MENU_ITEM)
      {
        @Override
        public boolean isReallyEnabled(Point aClickPoint)
        {
          return !isBreakpointsCollectionEmpty();
        }
      };
    removeAllBreakPointsMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          removeAllBreakpoints();
        }
      });
    popupMenu.add(removeAllBreakPointsMenuItem);


  }
  protected void removeAllBreakpoints()
  {
    int answer = JOptionPane.showConfirmDialog(this, "Really remove all breakpoints?", "Remove all breakpoints confirmation", JOptionPane.OK_CANCEL_OPTION);
    if (answer == JOptionPane.OK_OPTION)
    {
      for (BasicInstructionIndex basicInstructionIndex : basicProgram.getBreakpoints())
      {
        linePanels.get(basicInstructionIndex.getLineIndex()).setRefreshNeeded();
      }
      basicProgram.removeAllBreakpoints();
      repaint();
    }
  }

  protected void loadProgram()
  {
    debugWindow.getSwingKeyboardAndDisplay().loadProgram();
  }

  protected void reloadProgram()
  {
    debugWindow.getSwingKeyboardAndDisplay().reloadProgram();
  }

  protected void saveProgram()
  {
    debugWindow.getSwingKeyboardAndDisplay().saveProgram();
  }


  protected boolean isBreakpointsCollectionEmpty()
  {
    Collection<BasicInstructionIndex> breakpoints = basicProgram.getBreakpoints();
    return breakpoints == null || breakpoints.isEmpty();
  }

  protected void toggleBreakpoint(Point aPoint)
  {
    BasicInstructionIndex basicInstructionIndex = getBasicInstructionIndex(aPoint);
    if (basicInstructionIndex != null)
    {
      basicProgram.toggleBreakpoint(basicInstructionIndex);
      linePanels.get(basicInstructionIndex.getLineIndex()).setRefreshNeeded();
      repaint();
    }
  }

  protected BasicInstructionIndex getBasicInstructionIndex(Point aPoint)
  {
    int lineIndex = getLineIndex(aPoint.y);
    if (lineIndex >= 0)
    {
      Fx702pLinePanel linePanel = linePanels.get(lineIndex);
      int instructionIndex = linePanel.getInstructionIndex(aPoint.x);
      if (instructionIndex >= 0)
      {
        return new BasicInstructionIndex(linePanel.getBasicLine().getParsedLine().getLineIndexInProgram(), instructionIndex);
      }
    }
    return null;
  }

  protected int getLineIndex(int y)
  {
    if (linePanelVerticalPositions.isEmpty() || y < 0 || y >= linePanelVerticalPositions.lastElement())
    {
      return -1;
    }
    else
    {
      int first = 0;
      int last = linePanelVerticalPositions.size() - 1;
      int middle = -1;

      while (middle != first || last > first + 1)
      {
        middle = first + (last - first) / 2;
        if (y >= linePanelVerticalPositions.get(middle))
        {
          first = middle;
        }
        else
        {
          last = middle;
        }
      }
      return first;
    }
  }

  protected void showProgram()
  {
    removeAll();
    linePanelsByBasicLine.clear();
    buildLines();
    setVisible(true);
  }

  public void setActiveProgram(Fx702pBasicProgram aBasicProgram)
  {
    basicProgram = aBasicProgram;
    if (basicProgram != null && !basicProgram.isEmpty())
    {
      showProgram();
    }
    else
    {
      clearProgram();
    }
  }

  public void allClear()
  {
    if (selectedLinePanel != null)
    {
      selectedLinePanel.clearSelection();
      selectedLinePanel.setRefreshNeeded();
    }
    repaint();
  }

  public void clearProgram()
  {
    removeAll();
    linePanelsByBasicLine.clear();
    setVisible(false);
  }

  public void selectBreakpoint(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    select(aBasicLine, aSelectionStart, aSelectionEnd, true);
  }

  public void select(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    select(aBasicLine, aSelectionStart, aSelectionEnd, false);
  }

  protected void select(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd, boolean aBreakpointSelected)
  {
    breakpointSelected = aBreakpointSelected;
    if (selectedLinePanel != null)
    {
      selectedLinePanel.clearSelection();
    }
    Fx702pLinePanel linePanel = linePanelsByBasicLine.get(aBasicLine);
    if (linePanel != null)
    {
      linePanel.select(aSelectionStart, aSelectionEnd, breakpointSelected);
    }
    selectedLinePanel = linePanel;
    scrollRectToVisible(linePanel.getBounds());
  }

  public void subSelect(Fx702pBasicLine aBasicLine, int aSelectionStart, int aSelectionEnd)
  {
    if (selectedLinePanel != null)
    {
      selectedLinePanel.subSelect(aSelectionStart, aSelectionEnd);
    }
  }

  public void clearSelection()
  {
    if (selectedLinePanel != null)
    {
      selectedLinePanel.clearSelection();
    }
  }

  public Dimension getPreferredScrollableViewportSize()
  {
    return getPreferredSize();
  }

  public int getScrollableUnitIncrement(Rectangle aVisibleRectangle, int anOrientation, int aDirection)
  {
    if (anOrientation == SwingConstants.VERTICAL)
    {
      return characterDimension.height + LINE_SPACING;
    }
    else
    {
      return characterDimension.width;
    }
  }

  public int getScrollableBlockIncrement(Rectangle aVisibleRectangle, int anOrientation, int aDirection)
  {
    if (anOrientation == SwingConstants.VERTICAL)
    {
      return height / 3;
    }
    else
    {
      return maxWidth / 3;
    }
  }

  public boolean getScrollableTracksViewportHeight()
  {
    return getParent() instanceof JViewport && ((JViewport)getParent()).getSize().height > getPreferredScrollableViewportSize().height;
  }

  public boolean getScrollableTracksViewportWidth()
  {
    return getParent() instanceof JViewport && ((JViewport)getParent()).getSize().width > getPreferredScrollableViewportSize().width;
  }

  protected Fx702pBasicProgram basicProgram;

  protected Dimension characterDimension;

  protected int height;
  protected int maxWidth;
  protected Vector<Integer> linePanelVerticalPositions;
  protected Vector<Fx702pLinePanel> linePanels;
  protected boolean alternate = true;
  protected HashMap<Fx702pBasicLine, Fx702pLinePanel> linePanelsByBasicLine = new HashMap<Fx702pBasicLine, Fx702pLinePanel>();
  protected Fx702pLinePanel selectedLinePanel = null;
  protected boolean breakpointSelected = false;
  protected DebugWindow debugWindow;

  static public final int BORDER_SIZE = 5;
  static public final int LINE_SPACING = 5;
  static public final int PIXEL_SIZE = 2;

  static public final Color ALTERNATE_BACKGROUND = new Color(222, 251, 222);

  static public final String SET_BREAKPOINT_MENU_ITEM = "Set Breakpoint";
  static public final String REMOVE_BREAKPOINT_MENU_ITEM = "Remove Breakpoint";
  static public final String REMOVE_ALL_BREAKPOINTS_MENU_ITEM = "Remove All Breakpoints";
}
