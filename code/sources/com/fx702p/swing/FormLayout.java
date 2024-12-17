package com.fx702p.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class FormLayout implements LayoutManager
{
  public FormLayout()
  {
    this(DEFAULT_HORIZONTAL_GAP, DEFAULT_VERTICAL_GAP);
    columnCount = 1;
  }

  public FormLayout(int anHorizontalGap, int aVerticalGap)
  {
    horizontalGap = anHorizontalGap;
    verticalGap = aVerticalGap;
    columnCount = 1;
  }

  public FormLayout(int aColumnCount)
  {
    this();
    if (aColumnCount > MINIMUM_COLUMN_COUNT)
    {
      columnCount = aColumnCount;
    }
  }

  public FormLayout(int aColumnCount, boolean anHorizontallyCenteredFlag)
  {
    this(aColumnCount);
    horizontallyCentered = anHorizontallyCenteredFlag;
  }

  public FormLayout(int aColumnCount, int anHorizontalGap, int aVerticalGap)
  {
    this(anHorizontalGap, aVerticalGap);
    if (aColumnCount > MINIMUM_COLUMN_COUNT)
    {
      columnCount = aColumnCount;
    }
  }

  public FormLayout(int aColumnCount, int anHorizontalGap, int aVerticalGap, boolean anHorizontallyCenteredFlag)
  {
    this(aColumnCount, anHorizontalGap, aVerticalGap);
    horizontallyCentered = anHorizontallyCenteredFlag;
  }

  public void reset()
  {
    preferredSize = null;
  }

  public void setVerticalExtraSpace(int aVerticalExtraSpace)
  {
    sharedVerticalExtraSpace = aVerticalExtraSpace;
  }

  public void addLayoutComponent(String name, Component comp)
  {
  }

  public void removeLayoutComponent(Component comp)
  {
  }

  public Dimension preferredLayoutSize(Container parent)
  {
    if (preferredSize == null)
    {
      checkComponentCount(parent);
      computeSizes(parent, columnCount);
    }

    return preferredSize;
  }

  protected void checkComponentCount(Container aParentContainer)
  {
    if (aParentContainer.getComponentCount() % 2 != 0)
    {
      StringBuffer stringBuffer = new StringBuffer("Layout component count " + aParentContainer.getComponentCount() + " cannot be odd number");

      for (int i = 0, last = aParentContainer.getComponentCount(); i < last; i++)
      {
        stringBuffer.append("Component " + i + " value " + aParentContainer.getComponent(i) + "\n");
      }

      System.err.println(stringBuffer);
    }
  }

  public Dimension minimumLayoutSize(Container parent)
  {
    return preferredLayoutSize(parent);
  }

  public void layoutContainer(Container parent)
  {
    checkComponentCount(parent);
    computeSizes(parent, columnCount);

    Insets insets = parent.getInsets();
    Dimension parentSize = parent.getSize();
    int verticalExtraSpace;

    buildHorizontalExtraSpaceAndPadding(parentSize);

    if (sharedVerticalExtraSpace != NO_SHARED_VERTICAL_EXTRA_SPACE)
    {
      verticalExtraSpace = sharedVerticalExtraSpace;
    }
    else
    {
      if (parentSize.height > preferredSize.height)
      {
        int verticalSpaces = ((parent.getComponentCount() + 1) / 2) + 1;
        verticalExtraSpace = (parentSize.height - preferredSize.height) / verticalSpaces;
      }
      else
      {
        verticalExtraSpace = 0;
      }
    }

    int rowVerticalPosition = insets.top + verticalExtraSpace;
    int labelColumnHorizontalPosition = insets.left + horizontalExtraSpace;
    int currentRow = 0;
    int currentColumn = 0;

    int[] previousElementHeight = new int[columnCount];

    for (int i = 0, last = parent.getComponentCount(); (i + 1) < last; i += 2)
    {
      Component label = parent.getComponent(i);
      Component field = parent.getComponent(i + 1);

      currentRow = i / (2 * columnCount);
      currentColumn = (i / 2) % columnCount;

      Dimension labelDimension = null;
      Dimension fieldDimension = null;

      if (label.isVisible())
      {
        labelDimension = label.getPreferredSize();
      }
      else
      {
        labelDimension = new Dimension(0, 0);
      }

      fieldDimension = getFieldDimension(field, currentRow, currentColumn);

      int previousColumnsWidth = 0;
      for (int j = 0; j < currentColumn; j++)
      {
        previousColumnsWidth += columnWidth[j] + horizontalGap + horizontalPadding;
      }

      int height = 0;
      if (currentRow > 0)
      {
        height = previousElementHeight[currentColumn];
      }

      previousElementHeight[currentColumn] += rowHeight[currentRow];

      int labelPositionX = labelColumnHorizontalPosition + previousColumnsWidth;
      int labelPositionY = rowVerticalPosition + height + (rowHeight[currentRow] - labelDimension.height) / 2;
      int fieldPositionX = labelPositionX + columnLabelWidth[currentColumn] + horizontalGap;
      int fieldPositionY = rowVerticalPosition + height + (rowHeight[currentRow] - fieldDimension.height) / 2;
      label.setBounds(labelPositionX, labelPositionY, labelDimension.width, labelDimension.height);
      field.setBounds(fieldPositionX, fieldPositionY, fieldDimension.width, fieldDimension.height);
    }

    if ((parent.getComponentCount() % 2) != 0)
    {
      Component label = parent.getComponent(parent.getComponentCount() - 1);

      if (label.isVisible())
      {
        Dimension labelDimension = label.getPreferredSize();
        label.setBounds(labelColumnHorizontalPosition, rowVerticalPosition, labelDimension.width, labelDimension.height);
      }
    }
  }

  protected void buildHorizontalExtraSpaceAndPadding(Dimension aParentSize)
  {
    if (horizontallyCentered && aParentSize.width > preferredSize.width)
    {
      horizontalExtraSpace = (aParentSize.width - preferredSize.width) / 2;
    }
    else
    {
      horizontalExtraSpace = 0;
    }
    horizontalPadding = 0;
  }

  protected Dimension getLabelDimension(Component aLabel, int aRow, int aColumn)
  {
    return getComponentDefaultDimension(aLabel, aRow, aColumn);
  }

  protected Dimension getFieldDimension(Component aField, int aRow, int aColumn)
  {
    return getComponentDefaultDimension(aField, aRow, aColumn);
  }

  protected Dimension getComponentDefaultDimension(Component aComponent, int aRow, int aColumn)
  {
    if (aComponent.isVisible())
    {
      return aComponent.getPreferredSize();
    }
    else
    {
      return new Dimension(0, 0);
    }
  }

  protected void computeSizes(Container aParentContainer, int aColumnCount)
  {
    columnHeight = new int[aColumnCount];
    columnWidth = new int[aColumnCount];
    columnLabelWidth = new int[aColumnCount];

    int nbElement = aParentContainer.getComponentCount() / 2;
    int nbRow = (nbElement <= 0) ? 0 : (nbElement - 1) / aColumnCount + 1;
    rowHeight = new int[nbRow];
    columnFieldWidth = new int[aColumnCount];

    int labelAndFieldIteration = 0;
    for (int i = 0, last = aParentContainer.getComponentCount(); (i + 1) < last; i += 2)
    {
      Component label = aParentContainer.getComponent(i);
      Component field = aParentContainer.getComponent(i + 1);

      Dimension fieldDimension = field.getPreferredSize();
      int fieldWidth = fieldDimension.width;
      int fieldHeight = fieldDimension.height;

      Dimension labelDimension = label.getPreferredSize();
      int labelWidth = labelDimension.width;
      int labelHeight = labelDimension.height;

      int column = labelAndFieldIteration % aColumnCount;
      columnFieldWidth[column] = Math.max(columnFieldWidth[column], fieldWidth);

      columnLabelWidth[column] = Math.max(columnLabelWidth[column], labelWidth);
      columnWidth[column] = Math.max(columnWidth[column], columnFieldWidth[column] + columnLabelWidth[column] + horizontalGap);
      columnHeight[column] += Math.max(labelHeight, fieldHeight) + verticalGap;

      int row = labelAndFieldIteration / aColumnCount;
      rowHeight[row] = Math.max(rowHeight[row], Math.max(labelHeight, fieldHeight));

      labelAndFieldIteration++;
    }

    Insets insets = aParentContainer.getInsets();
    int parentWidth = insets.left + insets.right;
    for (int width : columnWidth)
    {
      parentWidth += width + horizontalGap;
    }
    int parentHeight = 0;
    for (int i = 0; i < rowHeight.length; i++)
    {
      // parentHeight = Math.max(parentHeight,height) ;
      rowHeight[i] += verticalGap;
      parentHeight += rowHeight[i];
    }
    parentHeight += insets.top + insets.bottom;


    preferredSize = new Dimension(parentWidth, parentHeight);
  }

  protected int[] columnLabelWidth;
  protected int[] columnHeight;
  protected int[] columnWidth;
  protected int[] rowHeight;
  protected int[] columnFieldWidth;

  protected int columnCount = MINIMUM_COLUMN_COUNT;
  protected Dimension preferredSize = null;
  protected final int horizontalGap;
  protected final int verticalGap;
  protected int sharedVerticalExtraSpace = NO_SHARED_VERTICAL_EXTRA_SPACE;
  protected int horizontalExtraSpace;
  protected int horizontalPadding;
  protected boolean horizontallyCentered = true;

  static public final int MINIMUM_COLUMN_COUNT = 1;
  static public final int DEFAULT_HORIZONTAL_GAP = 10;
  static public final int DEFAULT_VERTICAL_GAP = 5;
  static public final int NO_SHARED_VERTICAL_EXTRA_SPACE = -1;
}
