package com.fx702p.swing;

import javax.swing.JMenu;

@SuppressWarnings("serial")
public class IntelligentMenu extends JMenu
{
  public IntelligentMenu(String aMenuName)
  {
    super(aMenuName);
    SwingUtils.addPopupMenuListener(getPopupMenu());
  }
}
