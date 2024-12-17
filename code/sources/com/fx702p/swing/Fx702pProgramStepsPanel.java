package com.fx702p.swing;

import java.awt.*;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Fx702pProgramStepsPanel extends JPanel
{
  public Fx702pProgramStepsPanel(Rectangle theBounds)
  {
    setBounds(theBounds);
    buildSegments();
  }

  public void setSteps(int aSteps)
  {
    steps = aSteps;
  }

  public void showBusy(boolean aShowBusy)
  {
    if (aShowBusy != showBusy)
    {
      repaint();
    }
    showBusy = aShowBusy;
  }

  public void showSteps(boolean aShowSteps)
  {
    if (aShowSteps != showSteps)
    {
      repaint();
    }
    showSteps = aShowSteps;
  }

  protected void buildSegments()
  {
    int xOffset = 0;
    for (int characterIndex = 0; characterIndex < CHARACTERS_COUNT; characterIndex++)
    {
      for (int segmentIndex = 0; segmentIndex < SEGMENT_POINTS.length; segmentIndex++)
      {
        int x[] = new int[SEGMENT_POINTS[segmentIndex].length];
        int y[] = new int[SEGMENT_POINTS[segmentIndex].length];

        for (int pointIndex = 0; pointIndex < SEGMENT_POINTS[segmentIndex].length; pointIndex++)
        {
          x[pointIndex] = SEGMENT_POINTS[segmentIndex][pointIndex].x + xOffset;
          y[pointIndex] = SEGMENT_POINTS[segmentIndex][pointIndex].y;
        }
        segments[characterIndex][segmentIndex] = new Polygon(x, y, SEGMENT_POINTS[segmentIndex].length);
      }
      xOffset += ADVANCE;
    }
  }

  @Override
  public void paintComponent(Graphics aGraphics)
  {
    aGraphics.setColor(Fx702pLinePanel.DEFAULT_FONT_BACKGROUND);
    aGraphics.fillRect(0, 0, getBounds().width, getBounds().height);
    aGraphics.setColor(SEGMENT_COLOR);
    if (showBusy)
    {
      aGraphics.fillPolygon(segments[BUSY_CHARACTER_INDEX][BUSY_CHARACTER_SEGMENT_INDEX]);
    }
    else if (showSteps)
    {
      if (aGraphics instanceof Graphics2D)
      {
        ((Graphics2D)aGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }
      int currentSteps = steps;
      for (int i = CHARACTERS_COUNT - 1; i >= 0; i--)
      {
        int digit = currentSteps % 10;
        currentSteps /= 10;
        for (int j = 0; j < segments[i].length; j++)
        {
          if (NUMBER_SEGMENTS[digit][j])
          {
            aGraphics.fillPolygon(segments[i][j]);
          }
        }
      }
    }
  }

  protected boolean showBusy = false;
  protected boolean showSteps = false;
  protected Polygon segments[][] = new Polygon[CHARACTERS_COUNT][SEGMENTS_COUNT];
  protected int steps = 0;

  static public final int BUSY_CHARACTER_INDEX = 3;
  static public final int BUSY_CHARACTER_SEGMENT_INDEX = 3;

  static public final Point SEGMENT_POINTS[][] =
    {
        {
          new Point(4, 1),
          new Point(11, 1),
          new Point(9, 3),
          new Point(6, 3),
        },
        {
          new Point(3, 3),
          new Point(5, 4),
          new Point(4, 7),
          new Point(2, 9),
        },
        {
          new Point(12, 2),
          new Point(11, 9),
          new Point(9, 7),
          new Point(10, 4),
        },
        {
          new Point(4, 9),
          new Point(5, 8),
          new Point(8, 8),
          new Point(9, 9),
          new Point(8, 10),
          new Point(5, 10),
        },
        {
          new Point(2, 10),
          new Point(4, 11),
          new Point(3, 14),
          new Point(1, 16),
        },
        {
          new Point(11, 9),
          new Point(10, 17),
          new Point(8, 15),
          new Point(9, 11),
        },
        {
          new Point(2, 16),
          new Point(7, 16),
          new Point(8, 18),
          new Point(3, 18),
          new Point(2, 17),
        },
    };

  static public final boolean NUMBER_SEGMENTS[][] =
    {
        {
          true,
          true,
          true,
          false,
          true,
          true,
          true,
        },
        {
          false,
          false,
          true,
          false,
          false,
          true,
          false
        },
        {
          true,
          false,
          true,
          true,
          true,
          false,
          true,
        },
        {
          true,
          false,
          true,
          true,
          false,
          true,
          true,
        },
        {
          false,
          true,
          true,
          true,
          false,
          true,
          false,
        },
        {
          true,
          true,
          false,
          true,
          false,
          true,
          true,
        },
        {
          true,
          true,
          false,
          true,
          true,
          true,
          true,
        },
        {
          true,
          false,
          true,
          false,
          false,
          true,
          false,
        },
        {
          true,
          true,
          true,
          true,
          true,
          true,
          true,
        },
        {
          true,
          true,
          true,
          true,
          false,
          true,
          true,
        }
    };

  static public final int CHARACTERS_COUNT = 4;
  static public final int SEGMENTS_COUNT = 7;
  static public final int ADVANCE = 14;

  static public final Color SEGMENT_COLOR = new Color(0, 0, 0, 180);
}
