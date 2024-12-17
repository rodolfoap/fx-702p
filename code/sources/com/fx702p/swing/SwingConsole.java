package com.fx702p.swing;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.text.*;

import com.fx702p.emulator.Console;

@SuppressWarnings("serial")
public class SwingConsole extends JFrame implements Console.Implementation
{
  public SwingConsole(Fx702pSwingKeyboardAndDisplay aPositionProvider)
  {
    super(NAME);
    positionProvider = aPositionProvider;

    JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    textPane = new JTextPane();
    textPane.setEditorKit(new WrapEditorKit());

    textPane.setEditable(false);

    scrollPane.setViewportView(textPane);
    JPanel panel = new JPanel();
    setContentPane(panel);
    panel.add(scrollPane);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    buildFontAttributes();
    setDimensions(DEFAULT_COLUMNS, DEFAULT_LINES);

    buildPopupMenu();

    PipedInputStream piOut = new PipedInputStream();
    PipedInputStream piErr = new PipedInputStream();
    PipedOutputStream poOut = new PipedOutputStream();
    PipedOutputStream poErr = new PipedOutputStream();
    piOut = new PipedInputStream();
    try
    {
      poOut = new PipedOutputStream(piOut);
      piErr = new PipedInputStream();
      poErr = new PipedOutputStream(piErr);
      System.setOut(new PrintStream(poOut, true));
      System.setErr(new PrintStream(poErr, true));
    }
    catch (IOException exception)
    {
    }

    new ConsoleReaderThread(piOut, "System.out redirecting", normalAttributes).start();
    new ConsoleReaderThread(piErr, "System.err redirecting", errorAttributes).start();

    pack();
    setVisible(false);

    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
  }

  public SwingConsole()
  {
    super(NAME);

    JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    textPane = new JTextPane();
    textPane.setEditorKit(new WrapEditorKit());

    textPane.setEditable(false);

    scrollPane.setViewportView(textPane);
    JPanel panel = new JPanel();
    setContentPane(panel);
    panel.add(scrollPane);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    buildFontAttributes();
    setDimensions(DEFAULT_COLUMNS, DEFAULT_LINES);

    buildPopupMenu();

    PipedInputStream piOut = new PipedInputStream();
    PipedInputStream piErr = new PipedInputStream();
    PipedOutputStream poOut = new PipedOutputStream();
    PipedOutputStream poErr = new PipedOutputStream();
    piOut = new PipedInputStream();
    try
    {
      poOut = new PipedOutputStream(piOut);
      piErr = new PipedInputStream();
      poErr = new PipedOutputStream(piErr);
      System.setOut(new PrintStream(poOut, true));
      System.setErr(new PrintStream(poErr, true));
    }
    catch (IOException exception)
    {
    }

    new ConsoleReaderThread(piOut, "System.out redirecting", normalAttributes).start();
    new ConsoleReaderThread(piErr, "System.err redirecting", errorAttributes).start();

    pack();
    setVisible(false);

    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
  }

  protected void buildPopupMenu()
  {
    popupMenu = SwingUtils.buildPopupMenu(this, textPane);
    popupMenu.add(buildClearMenuItem());
  }

  protected IntelligentMenuItem buildClearMenuItem()
  {
    IntelligentMenuItem clearMenuItem = new IntelligentMenuItem(CLEAR_MENU_ITEM);
    clearMenuItem.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          clear();
        }
      });
    return clearMenuItem;
  }

  protected void buildFontAttributes()
  {
    textFont = new Font("Monospaced", Font.PLAIN, DEFAULT_FONT_SIZE);

    normalAttributes = new SimpleAttributeSet(textPane.getInputAttributes());

    // Set the font family, size, and style, based on properties of
    // the Font object. Note that JTextPane supports a number of
    // character attributes beyond those supported by the Font class.
    // For example, underline, strike-through, super- and sub-script.
    StyleConstants.setFontFamily(normalAttributes, textFont.getFamily());
    StyleConstants.setFontSize(normalAttributes, textFont.getSize());
    StyleConstants.setItalic(normalAttributes, (textFont.getStyle() & Font.ITALIC) != 0);
    StyleConstants.setBold(normalAttributes, (textFont.getStyle() & Font.BOLD) != 0);

    // Set the font color
    StyleConstants.setForeground(normalAttributes, Color.BLACK);

    // Retrieve the pane's document object
    StyledDocument document = textPane.getStyledDocument();
    errorAttributes = new SimpleAttributeSet(normalAttributes);
    StyleConstants.setForeground(errorAttributes, Color.RED);
  }

  public void clear()
  {
    try
    {
      textPane.getDocument().remove(0, textPane.getDocument().getLength());
    }
    catch (BadLocationException exception)
    {
    }
  }

  public void reportError(String anError)
  {
    Date date = new Date();
    long timeStamp = date.getTime();
    if (timeStamp - lastErrorTimeStamp >= TIMESTAMP_DIFFERENCE)
    {
      System.err.println(date + ": " + anError);
    }
    else
    {
      System.err.println(anError);
    }
    lastErrorTimeStamp = timeStamp;
  }

  public void setDimensions(int aColumnCount, int aRowCount)
  {
    StyleContext styleContext = StyleContext.getDefaultStyleContext();
    FontMetrics fontMetrics = styleContext.getFontMetrics(textFont);
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < aColumnCount; i++)
    {
      buffer.append("w");
    }

    // Estimated width of the columns is width of that many 'w' characters
    int width = fontMetrics.stringWidth(buffer.toString());
    int height = fontMetrics.getHeight() * aRowCount;
    Dimension preferredSize = new Dimension(width, height);
    textPane.setMinimumSize(preferredSize);
    textPane.setPreferredSize(preferredSize);
  }

  protected class ConsoleReaderThread extends Thread
  {
    ConsoleReaderThread(PipedInputStream anInputStream, String aName, AttributeSet anAttributeSet)
    {
      super(aName);
      inputStream = anInputStream;
      attributeSet = anAttributeSet;
    }

    @Override
    public void run()
    {
      byte[] buffer = new byte[1024];
      try
      {
        while (true)
        {
          int length = inputStream.read(buffer);
          if (length > 0)
          {
            SwingUtilities.invokeLater(new ConsoleWriter(buffer, length));
          }
        }
      }
      catch (IOException exception)
      {
      }
    }

    protected class ConsoleWriter implements Runnable
    {
      public ConsoleWriter(byte[] aBuffer, int aLength)
      {
        buffer = new byte[aLength];
        System.arraycopy(aBuffer, 0, buffer, 0, aLength);
      }

      public void run()
      {
        try
        {
          textPane.getDocument().insertString(textPane.getDocument().getLength(), new String(buffer, 0, buffer.length), attributeSet);
          textPane.setCaretPosition(textPane.getDocument().getLength());

          for (byte b : buffer)
          {
            if (b == '\n')
            {
              lineCount++;
            }
          }

          if (lineCount > MAX_LINES)
          {
            String text = textPane.getText();
            int lines = 0;
            int position = 0;
            while (lines < EXTRA_LINES)
            {
              if (text.charAt(position) == '\n')
              {
                lines++;
              }
              position++;
            }
            textPane.getDocument().remove(0, position);

            lineCount -= lines;
          }
          if (!isVisible())
          {
            if (positionProvider != null)
            {
              positionProvider.setConsolePosition(SwingConsole.this);
            }
            addWindowFocusListener(new ReturnFocusListener());
            setVisible(true);
          }
        }
        catch (Exception exception)
        {
          // Do nothing, we do not have any way to report an error here yet.
        }
      }

      protected byte[] buffer;
    }

    protected int lineCount = 0;
    protected PipedInputStream inputStream;
    protected AttributeSet attributeSet;
  }

  static public class NoWrapParagraphView extends ParagraphView
  {
    public NoWrapParagraphView(Element elem)
    {
      super(elem);
    }

    @Override
    public void layout(int width, int height)
    {
      super.layout(Short.MAX_VALUE, height);
    }

    @Override
    public float getMinimumSpan(int axis)
    {
      return super.getPreferredSpan(axis);
    }
  }

  static public class WrapLabelView extends LabelView
  {
    public WrapLabelView(Element elem)
    {
      super(elem);
    }

    @Override
    public int getBreakWeight(int axis, float pos, float len)
    {
      if (axis == View.X_AXIS)
      {
        checkPainter();
        int p0 = getStartOffset();
        int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
        if (p1 == p0)
        {
          // can't even fit a single character
          return View.BadBreakWeight;
        }
        try
        {
          // if the view contains line break char return forced break
          if (getDocument().getText(p0, p1 - p0).indexOf("\r") >= 0)
          {
            return View.ForcedBreakWeight;
          }
        }
        catch (BadLocationException ex)
        {
          // should never happen
        }
      }
      return super.getBreakWeight(axis, pos, len);
    }

    @Override
    public View breakView(int axis, int p0, float pos, float len)
    {
      if (axis == View.X_AXIS)
      {
        checkPainter();
        int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
        try
        {
          // if the view contains line break char break the view
          int index = getDocument().getText(p0, p1 - p0).indexOf("\r");
          if (index >= 0)
          {
            GlyphView v = (GlyphView)createFragment(p0, p0 + index + 1);
            return v;
          }
        }
        catch (BadLocationException ex)
        {
          // should never happen
        }
      }
      return super.breakView(axis, p0, pos, len);
    }
  }

  static public class WrapEditorKit extends StyledEditorKit
  {
    ViewFactory defaultFactory = new WrapColumnFactory();

    @Override
    public ViewFactory getViewFactory()
    {
      return defaultFactory;
    }

    @Override
    public MutableAttributeSet getInputAttributes()
    {
      MutableAttributeSet mAttrs = super.getInputAttributes();
      mAttrs.removeAttribute(LINE_BREAK_ATTRIBUTE_NAME);
      return mAttrs;
    }

    public static final String LINE_BREAK_ATTRIBUTE_NAME = "line_break_attribute";

  }

  static public class WrapColumnFactory implements ViewFactory
  {
    public View create(Element elem)
    {
      String kind = elem.getName();
      if (kind != null)
      {
        if (kind.equals(AbstractDocument.ContentElementName))
        {
          return new WrapLabelView(elem);
        }
        else if (kind.equals(AbstractDocument.ParagraphElementName))
        {
          return new NoWrapParagraphView(elem);
        }
        else if (kind.equals(AbstractDocument.SectionElementName))
        {
          return new BoxView(elem, View.Y_AXIS);
        }
        else if (kind.equals(StyleConstants.ComponentElementName))
        {
          return new ComponentView(elem);
        }
        else if (kind.equals(StyleConstants.IconElementName))
        {
          return new IconView(elem);
        }
      }

      // default to text display
      return new LabelView(elem);
    }
  }

  protected class ReturnFocusListener implements WindowFocusListener
  {
    public void windowLostFocus(WindowEvent aWindowEvent)
    {
    }

    public void windowGainedFocus(WindowEvent aWindowEvent)
    {
      if (aWindowEvent.getOppositeWindow() != null)
      {
        aWindowEvent.getOppositeWindow().toFront();
      }
      SwingConsole.this.removeWindowFocusListener(this);
    }
  }

  protected JTextPane textPane;
  protected Font textFont;
  protected long lastErrorTimeStamp = 0;
  protected Fx702pSwingKeyboardAndDisplay positionProvider;
  protected MutableAttributeSet normalAttributes;
  protected MutableAttributeSet errorAttributes;
  protected JPopupMenu popupMenu;
  static public final int MAX_LINES = 1000;
  static public final int EXTRA_LINES = 100;

  static public final int DEFAULT_FONT_SIZE = 10;
  static public final int DEFAULT_COLUMNS = 64;
  static public final int DEFAULT_LINES = 25;

  static public final String NAME = "Fx702p Console";

  static public final String CLEAR_MENU_ITEM = "Clear";

  static public final long TIMESTAMP_DIFFERENCE = 1000;
}
