package com.fx702p.swing;

import java.awt.Point;

import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class IntelligentMenuItem extends JMenuItem
{
  public IntelligentMenuItem(String aLabel)
  {
    super(aLabel);
  }

  public boolean isReallyEnabled(Point aClickPoint)
  {
    return true;
  }
}
