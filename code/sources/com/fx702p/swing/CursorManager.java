package com.fx702p.swing;

import javax.swing.SwingUtilities;

public class CursorManager extends Thread
{
  public CursorManager(Fx702pPanel aFx702pPanel)
  {
    super("Cursor Manager");
    fx702pPanel = aFx702pPanel;
    start();
  }

  @Override
  public void run()
  {
    for (;;)
    {
      try
      {
        sleep(CURSOR_BLINK_TIME);
        blink();
      }
      catch (InterruptedException exception)
      {
      }
    }
  }

  protected void blink()
  {
    if (!suspended)
    {
      cursorBlinkFlag = !cursorBlinkFlag;
      showCursor();
    }
  }

  public void setCursorVisible(boolean aCursorVisibleFlag)
  {
    cursorVisibleFlag = aCursorVisibleFlag;
    showCursor();
  }

  public void suspendBlinking()
  {
    if (!suspended)
    {
      suspended = true;
      cursorBlinkFlag = true;
      interrupt();
      showCursor();
    }
  }

  public void restartBlinking()
  {
    if (suspended)
    {
      cursorBlinkFlag = true;
      suspended = false;
      interrupt();
      showCursor();
    }
  }

  protected void showCursor()
  {
    if (SwingUtilities.isEventDispatchThread())
    {
      fx702pPanel.showCursor(cursorBlinkFlag & cursorVisibleFlag);
    }
    else
    {
      try
      {
        SwingUtilities.invokeAndWait(new Runnable()
          {
            public void run()
            {
              fx702pPanel.showCursor(cursorBlinkFlag & cursorVisibleFlag);
            }
          });
      }
      catch (Throwable exception)
      {
      }
    }
  }

  protected boolean cursorVisibleFlag = false;
  protected boolean cursorBlinkFlag = false;
  protected boolean suspended = false;
  protected Fx702pPanel fx702pPanel;

  static public final long CURSOR_BLINK_TIME = 400;
}
