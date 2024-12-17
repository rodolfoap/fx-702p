package com.fx702p.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.*;


public class SwingUtils
{
  static int getAcceleratorMaskForCurrentPlatform()
  {
    if (isMacOSX())
    {
      return InputEvent.META_DOWN_MASK;
    }
    else
    {
      return InputEvent.CTRL_DOWN_MASK;
    }
  }

  static int getDefaultModifierForCurrentPlatform()
  {
    if (isMacOSX())
    {
      return InputEvent.META_MASK;
    }
    else
    {
      return InputEvent.CTRL_MASK;
    }
  }

  static int getFx702pKeyboardModifierForCurrentPlatform()
  {
    if (isMacOSX())
    {
      return InputEvent.META_MASK | InputEvent.SHIFT_MASK;
    }
    else
    {
      return InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK;
    }
  }

  static public boolean isMacOSX()
  {
    String osName = System.getProperty("os.name");
    return osName.equals("Mac OS X");
  }

  static public ImageIcon createImageIcon(Class<?> aClass, String aPath, String aDescription)
  {
    java.net.URL url = aClass.getResource(aPath);
    if (url != null)
    {
      return new ImageIcon(url, aDescription);
    }
    else
    {
      System.err.println("Couldn't find file: " + aPath);
      return null;
    }
  }

  static public Point getPopupClickPoint(EventObject anEvent)
  {
    if (anEvent.getSource() instanceof ExtendedPopupMenu)
    {
      return ((ExtendedPopupMenu)anEvent.getSource()).getClickPoint();
    }
    else if (anEvent.getSource() instanceof JComponent && ((JComponent)anEvent.getSource()).getParent() instanceof ExtendedPopupMenu)
    {
      return ((ExtendedPopupMenu)((JComponent)anEvent.getSource()).getParent()).getClickPoint();
    }
    else
    {
      return null;
    }
  }

  static public JPopupMenu buildPopupMenu(JFrame aFrame, Component aComponent)
  {
    return buildPopupMenu(aFrame, aComponent, null);
  }

  static public ExtendedPopupMenu buildPopupMenu(Window aWindow, Component aComponent, MouseListener anExtraMouseListener)
  {
    ExtendedPopupMenu popupMenu = new ExtendedPopupMenu();

    addPopupMenuListener(popupMenu);
    aComponent.addMouseListener(new ClickListener(popupMenu, anExtraMouseListener));

    return popupMenu;
  }

  static public void enableMenuItems(Component[] theComponents)
  {
    enableMenuItems(theComponents, null);
  }

  static public void enableMenuItems(Component[] theComponents, Point aClickPoint)
  {
    for (Component component : theComponents)
    {
      if (component instanceof IntelligentMenuItem)
      {
        component.setEnabled(((IntelligentMenuItem)component).isReallyEnabled(aClickPoint));
      }
    }
  }

  static public void addPopupMenuListener(final JPopupMenu aPopupMenu)
  {
    aPopupMenu.addPopupMenuListener(new PopupMenuListener()
      {
        public void popupMenuCanceled(PopupMenuEvent aPopupMenuEvent)
        {
          if (aPopupMenu instanceof ExtendedPopupMenu)
          {
            ((ExtendedPopupMenu)aPopupMenu).setPopupMenuJustClosed(true);
          }
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent aPopupMenuEvent)
        {
        }

        public void popupMenuWillBecomeVisible(PopupMenuEvent aPopupMenuEvent)
        {
          enableMenuItems(aPopupMenu.getComponents(), getPopupClickPoint(aPopupMenuEvent));
        }
      });
  }

  static protected class ClickListener extends MouseAdapter
  {
    public ClickListener(ExtendedPopupMenu aPopupMenu, MouseListener anExtraMouseListener)
    {
      popupMenu = aPopupMenu;
      extraMouseListener = anExtraMouseListener;
    }

    @Override
    public void mousePressed(MouseEvent aMouseEvent)
    {
      if (aMouseEvent.isPopupTrigger())
      {
        popupMenu.setClickPoint(aMouseEvent.getPoint());
        popupMenu.show(aMouseEvent.getComponent(), aMouseEvent.getX(), aMouseEvent.getY());
      }
      else if (!popupMenu.isPopupMenuJustClosed() && extraMouseListener != null)
      {
        extraMouseListener.mousePressed(aMouseEvent);
      }

      popupMenu.setPopupMenuJustClosed(false);
    }

    @Override
    public void mouseReleased(MouseEvent aMouseEvent)
    {
      if (aMouseEvent.isPopupTrigger())
      {
        popupMenu.show(aMouseEvent.getComponent(), aMouseEvent.getX(), aMouseEvent.getY());
      }
      else if (extraMouseListener != null)
      {
        extraMouseListener.mouseReleased(aMouseEvent);
      }
    }

    protected ExtendedPopupMenu popupMenu;
    protected MouseListener extraMouseListener;
  }

  @SuppressWarnings("serial")
  static protected class ExtendedPopupMenu extends JPopupMenu
  {
    public boolean isPopupMenuJustClosed()
    {
      return popupMenuJustClosed;
    }

    public void setPopupMenuJustClosed(boolean aPopupMenuJustClosed)
    {
      popupMenuJustClosed = aPopupMenuJustClosed;
    }

    public Point getClickPoint()
    {
      return clickPoint;
    }

    protected void setClickPoint(Point aClickPoint)
    {
      clickPoint = aClickPoint;
    }

    protected boolean popupMenuJustClosed = false;
    protected Point clickPoint;
  }

  static public final Color DEFAULT_SELECTION_COLOR = new Color(165, 195, 233);
  static public final Color DEFAULT_SUBSELECTION_COLOR = new Color(206, 131, 246);
}
