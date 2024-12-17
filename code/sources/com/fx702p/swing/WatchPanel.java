package com.fx702p.swing;

import static com.fx702p.emulator.Fx702pConstants.MATH_CONTEXT;

import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;

import com.fx702p.Fx702pFullParser;
import com.fx702p.debug.WatchedVariableHelper;
import com.fx702p.emulator.*;
import com.fx702p.emulator.Fx702pMemory.WatchedVariable;
import com.fx702p.parser.Node;

@SuppressWarnings("serial")
public class WatchPanel extends JSplitPane implements Fx702pMemory.Watcher, ListSelectionListener
{
  public WatchPanel(Fx702pSwingKeyboardAndDisplay anFx702pSwingKeyboardAndDisplay, Fx702pBasicProgram aBasicProgram)
  {
    super(JSplitPane.VERTICAL_SPLIT);

    fx702pSwingKeyboardAndDisplay = anFx702pSwingKeyboardAndDisplay;
    emulator = fx702pSwingKeyboardAndDisplay.getEmulator();
    memory = emulator.getMemory();

    setActiveProgram(aBasicProgram);

    setTopComponent(buildNormalVariablesPanel());
    setBottomComponent(buildStatVariablesPanel());
    setResizeWeight(1);
  }

  public void setActiveProgram(Fx702pBasicProgram aBasicProgram)
  {
    basicProgram = aBasicProgram;
    buildWatchVariablesByVariable();
    fireWatchedVariablesTableModelModified();
  }

  protected void buildWatchVariablesByVariable()
  {
    watchedVariablesByVariable.clear();
    for (WatchedVariable watchedVariable : getWatchedVariables())
    {
      watchedVariablesByVariable.put(watchedVariable.getVariable(), watchedVariable);
    }
  }

  public void setInitialDividerLocation()
  {
    int scrollBarHeight = statVariablesScrollPane.getHorizontalScrollBar().getHeight();
    setDividerLocation(getSize().height - getBottomComponent().getMinimumSize().height - getInsets().bottom - getDividerSize() - scrollBarHeight);
  }

  protected List<WatchedVariable> getWatchedVariables()
  {
    return basicProgram.getWatchedVariables();
  }

  protected JPanel buildNormalVariablesPanel()
  {
    JPanel normalVariablesPanel = new JPanel();
    normalVariablesPanel.setLayout(new BoxLayout(normalVariablesPanel, BoxLayout.Y_AXIS));

    JPanel watchSelectionPanel = new JPanel();
    watchSelectionPanel.setLayout(new BoxLayout(watchSelectionPanel, BoxLayout.X_AXIS));

    watchedVariableTextField = new JTextField(WATCH_VARIABLE_NAME_COLUMNS);
    watchedVariableTextField.setMinimumSize(watchedVariableTextField.getPreferredSize());
    watchedVariableTextField.setMaximumSize(watchedVariableTextField.getPreferredSize());
    validWatchedVariableTextFieldForeground = watchedVariableTextField.getForeground();
    watchedVariableTextField.getDocument().addDocumentListener(new DocumentListener()
      {
        public void insertUpdate(DocumentEvent aDocumentEvent)
        {
          validateTextField();
        }

        public void removeUpdate(DocumentEvent aDocumentEvent)
        {
          validateTextField();
        }

        public void changedUpdate(DocumentEvent aDocumentEvent)
        {
          validateTextField();
        }
      });
    watchedVariableTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          addWatchedVariable();
        }
      });
    ((AbstractDocument)watchedVariableTextField.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
    watchSelectionPanel.add(watchedVariableTextField);

    watchButton = new JButton("Watch");
    watchButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          addWatchedVariable();
        }
      });
    watchSelectionPanel.add(watchButton);

    JButton watchAtoZButton = new JButton("Watch A to Z and $");
    watchAtoZButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          watchedNormalVariables();
        }
      });
    watchSelectionPanel.add(watchAtoZButton);

    removeButton = new JButton("Remove");
    removeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent anActionEvent)
        {
          removeWatchedVariable();
        }
      });
    watchSelectionPanel.add(removeButton);

    watchSelectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, watchSelectionPanel.getPreferredSize().height));
    watchSelectionPanel.setMinimumSize(watchSelectionPanel.getPreferredSize());
    watchSelectionPanel.setAlignmentX(LEFT_ALIGNMENT);
    normalVariablesPanel.add(watchSelectionPanel);

    watchedVariablesTable = new JTable(new WatchedVariablesTableModel())
      {
        @Override
        public TableCellRenderer getCellRenderer(int aRow, int aColumn)
        {
          if (isLastModified(aRow, aColumn))
          {
            if (getWatchedVariables().get(aRow).isWatchoint())
            {
              return WATCHPOINT_CELL_RENDERER;
            }
            else
            {
              return LAST_MODIFIED_CELL_RENDERER;
            }
          }
          else
          {
            return super.getCellRenderer(aRow, aColumn);
          }
        }
      };
    // Fix for Nimbus L&F
    ((JComponent)watchedVariablesTable.getDefaultRenderer(Boolean.class)).setOpaque(true);
    watchedVariablesTable.setCellSelectionEnabled(false);
    watchedVariablesTable.setRowSelectionAllowed(true);
    watchedVariablesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    setColumnSizes();
    watchedVariablesTable.getSelectionModel().addListSelectionListener(this);

    JScrollPane watchedVariablesTableScrollPanel = new JScrollPane(watchedVariablesTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    watchedVariablesTable.setPreferredScrollableViewportSize(watchedVariablesTable.getPreferredSize());
    watchedVariablesTableScrollPanel.setAlignmentX(LEFT_ALIGNMENT);
    watchedVariablesTableScrollPanel.setMinimumSize(watchedVariablesTableScrollPanel.getPreferredSize());
    normalVariablesPanel.add(watchedVariablesTableScrollPanel);

    tableExtraVerticalBox = Box.createVerticalStrut(tableFontHeight * 2);
    normalVariablesPanel.add(tableExtraVerticalBox);

    allowNothing();

    return normalVariablesPanel;
  }

  protected JScrollPane buildStatVariablesPanel()
  {
    JPanel statVariablesPanel = new JPanel();
    statVariablesPanel.setLayout(new BoxLayout(statVariablesPanel, BoxLayout.Y_AXIS));

    JCheckBox suspendOnStatModifiedCheckBox = new JCheckBox("Stat Watchpoint");
    suspendOnStatModifiedCheckBox.setSelected(basicProgram.isSuspendOnStatVariables());
    suspendOnStatModifiedCheckBox.setAlignmentX(LEFT_ALIGNMENT);
    suspendOnStatModifiedCheckBox.addItemListener(new ItemListener()
      {
        public void itemStateChanged(ItemEvent anItemEvent)
        {
          basicProgram.setSuspendOnStatVariables(anItemEvent.getStateChange() == ItemEvent.SELECTED);
        }
      });
    statVariablesPanel.add(suspendOnStatModifiedCheckBox);

    JPanel valuesPanel = new JPanel();
    valuesPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    valuesPanel.setLayout(new FormLayout(3, false));
    valuesPanel.setAlignmentX(LEFT_ALIGNMENT);

    buildStatVariableValuePanel(valuesPanel, "CNT", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          return memory.getStatCounter();
        }
      });
    buildStatVariableValuePanel(valuesPanel, "MX", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          if (memory.getStatCounter().compareTo(BigDecimal.ZERO) == 0)
          {
            return null;
          }
          else
          {
            return memory.getSumX().divide(memory.getStatCounter(), MATH_CONTEXT);
          }
        }
      });
    buildStatVariableValuePanel(valuesPanel, "MY", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          if (memory.getStatCounter().compareTo(BigDecimal.ZERO) == 0)
          {
            return null;
          }
          else
          {
            return memory.getSumY().divide(memory.getStatCounter(), MATH_CONTEXT);
          }
        }
      });
    buildStatVariableValuePanel(valuesPanel, "SX", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          return memory.getSumX();
        }
      });
    buildStatVariableValuePanel(valuesPanel, "SY", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          return memory.getSumY();
        }
      });
    buildStatVariableValuePanel(valuesPanel, "SX2", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          return memory.getSumX2();
        }
      });
    buildStatVariableValuePanel(valuesPanel, "SY2", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          return memory.getSumY2();
        }
      });
    buildStatVariableValuePanel(valuesPanel, "SXY", new StatValueGetter()
      {
        public BigDecimal getValue()
        {
          return memory.getSumXY();
        }
      });

    defaultStatValueBackground = statValuesTextFields.get(0).getBackground();
    statVariablesPanel.add(valuesPanel);

    statVariablesScrollPane = new JScrollPane(statVariablesPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    statVariablesScrollPane.setMinimumSize(new Dimension(statVariablesScrollPane.getPreferredSize().width, suspendOnStatModifiedCheckBox.getPreferredSize().height));

    setStatValues();

    return statVariablesScrollPane;
  }

  protected void buildStatVariableValuePanel(Container aContainer, String aStatVariableName, StatValueGetter aStatValueGetter)
  {
    JLabel valueLabel = new JLabel(aStatVariableName);
    aContainer.add(valueLabel);
    JTextField valueTextField = new JTextField(NUMBER_WIDTH);
    valueTextField.setEditable(false);
    valueTextField.putClientProperty(STAT_VALUE_GETTER_PROPERTY, aStatValueGetter);
    statValuesTextFields.add(valueTextField);
    aContainer.add(valueTextField);
  }

  protected void allowNothing()
  {
    watchButton.setEnabled(false);
    removeButton.setEnabled(false);
  }

  protected void allowWatch()
  {
    watchButton.setEnabled(true);
    removeButton.setEnabled(false);
  }

  protected void allowRemove()
  {
    watchButton.setEnabled(false);
    removeButton.setEnabled(true);
  }

  protected void addWatchedVariable()
  {
    Variable variable = getValidatedTextFieldVariable();
    if (variable != null)
    {
      variableJustWatched = variable;
      watchVariable(watchedVariableTextField.getText(), variable);
      watchedVariableTextField.selectAll();
    }
  }

  public void valueChanged(ListSelectionEvent aListSelectionEvent)
  {
    if (!ignoreSelectionChanges)
    {
      int[] selectedRows = watchedVariablesTable.getSelectedRows();
      if (selectedRows.length == 1)
      {
        WatchedVariable watchedVariable = getWatchedVariables().get(selectedRows[0]);
        if (watchedVariable.getVariable() != variableJustWatched)
        {
          userSelectionChange = true;
          try
          {
            watchedVariableTextField.setText(watchedVariable.getAlias());
          }
          finally
          {
            userSelectionChange = false;
          }
        }
        allowRemove();
      }
      else
      {
        if (selectedRows.length > 0)
        {
          allowRemove();
        }
        userSelectionChange = true;
        try
        {
          // setText(null) does not work for an unknown reason
          watchedVariableTextField.setText("");
        }
        finally
        {
          userSelectionChange = false;
        }
      }
    }
  }

  protected void setColumnSizes()
  {
    watchedVariablesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    Font font = watchedVariablesTable.getCellRenderer(0, 0).getTableCellRendererComponent(watchedVariablesTable, "A", false, false, 0, 0).getFont();
    FontMetrics fontMetrics = getFontMetrics(font);
    int xWidth = fontMetrics.charWidth('X');
    watchedVariablesTable.getColumnModel().getColumn(NAME_COLUMN_INDEX).setPreferredWidth(xWidth * NAME_WIDTH);
    watchedVariablesTable.getColumnModel().getColumn(NUMBER_COLUMN_INDEX).setPreferredWidth(xWidth * NUMBER_WIDTH);
    watchedVariablesTable.getColumnModel().getColumn(STRING_COLUMN_INDEX).setPreferredWidth(xWidth * STRING_WIDTH);
    tableFontHeight = fontMetrics.getHeight();
  }

  protected boolean isLastModified(int aRow, int aColumn)
  {
    if (getWatchedVariables().isEmpty())
    {
      return false;
    }
    else
    {
      WatchedVariable watchedVariable = getWatchedVariables().get(aRow);
      if (watchedVariable.getVariable() == lastModifiedVariable)
      {
        switch (aColumn)
        {
          case NAME_COLUMN_INDEX:
          {
            return false;
          }
          case NUMBER_COLUMN_INDEX:
          {
            return !watchedVariable.getVariable().isStringVariable();
          }
          case STRING_COLUMN_INDEX:
          {
            return watchedVariable.getVariable().isStringVariable();
          }
          default:
          {
            return false;
          }
        }
      }
      else
      {
        return false;
      }
    }
  }

  protected void validateTextField()
  {
    Variable variable = getValidatedTextFieldVariable();
    variableJustWatched = null;
    if (!userSelectionChange)
    {
      ignoreSelectionChanges = true;
      try
      {
        watchedVariablesTable.getSelectionModel().clearSelection();
      }
      finally
      {
        ignoreSelectionChanges = false;
      }
    }
    if (variable != null)
    {
      if (watchedVariablesByVariable.containsKey(variable))
      {
        allowRemove();
      }
      else
      {
        allowWatch();
      }
    }
    else
    {
      allowNothing();
    }
  }

  protected Variable getValidatedTextFieldVariable()
  {
    String variableName = watchedVariableTextField.getText();
    if (variableName == null || variableName.length() == 0)
    {
      watchedVariableTextField.setForeground(validWatchedVariableTextFieldForeground);
      return null;
    }
    else
    {
      Variable variable = null;
      try
      {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(variableName.getBytes());
        Fx702pFullParser parser = new Fx702pFullParser(inputStream);
        Node node = parser.WatchedVariable();
        if (node != null)
        {
          variable = WatchedVariableHelper.getVariable(node, memory);
        }
      }
      catch (Throwable exception)
      {
      }
      if (variable != null)
      {
        watchedVariableTextField.setForeground(validWatchedVariableTextFieldForeground);
      }
      else
      {
        watchedVariableTextField.setForeground(INVALID_WATCHED_VARIABLE_TEXT_FIELD_FOREGROUND);
      }
      return variable;
    }
  }

  public void allCleared()
  {
    lastModifiedVariable = null;
    fireWatchedVariablesTableModelModified();
  }

  public void variableModified(Variable aVariable)
  {
    lastModifiedVariable = aVariable;
    WatchedVariable watchedVariable = watchedVariablesByVariable.get(aVariable);
    if (watchedVariable != null)
    {
      int row = watchedVariable.getIndex();
      int column = aVariable.isStringVariable() ? STRING_COLUMN_INDEX : NUMBER_COLUMN_INDEX;
      watchedVariablesTable.scrollRectToVisible(watchedVariablesTable.getCellRect(row, column, false));
      fireWatchedVariablesTableModelModified();
      if (watchedVariable.isWatchoint() && fx702pSwingKeyboardAndDisplay.isProgramRunning())
      {
        emulator.suspendProgram();
      }
    }
  }

  public void statVariablesCleared()
  {
    statVariablesModified();
  }

  public void statVariablesModified()
  {
    if (basicProgram.isSuspendOnStatVariables() && fx702pSwingKeyboardAndDisplay.isProgramRunning())
    {
      emulator.suspendProgram();
      for (JTextField statValueTextField : statValuesTextFields)
      {
        statValueTextField.setBackground(WATCHPOINT_COLOR);
      }
    }
    setStatValues();
  }

  protected void setStatValues()
  {
    for (JTextField statValueTextField : statValuesTextFields)
    {
      StatValueGetter statValueGetter = (StatValueGetter)statValueTextField.getClientProperty(STAT_VALUE_GETTER_PROPERTY);
      if (statValueGetter != null)
      {
        BigDecimal value = statValueGetter.getValue();
        if (value != null)
        {
          String valueAsString = Fx702pConstants.NORMAL_FORMATTER.format(value);
          statValueTextField.setText(valueAsString);
        }
        else
        {
          statValueTextField.setText("");
        }
      }
    }
  }

  protected void watchedNormalVariables()
  {
    Vector<WatchedVariable> normalVariables = new Vector<Fx702pMemory.WatchedVariable>();

    normalVariables.add(basicProgram.buildWatchVariable("$", memory.getDollarVariable()));
    for (int i = 0; i < 26; i++)
    {
      char letter[] =
        {
          (char)('A' + i)
        };

      normalVariables.add(basicProgram.buildWatchVariable(new String(letter), memory.getVariable(i)));
    }
    basicProgram.addWatchVariables(normalVariables);
    for (WatchedVariable watchedVariable : normalVariables)
    {
      watchedVariablesByVariable.put(watchedVariable.getVariable(), watchedVariable);
    }
    lastModifiedVariable = null;
    fireWatchedVariablesTableModelModified();
    tableExtraVerticalBox.setVisible(false);
  }

  protected void watchVariable(String aName, Variable aVariable)
  {
    WatchedVariable watchVariable = watchedVariablesByVariable.get(aVariable);
    if (watchVariable == null)
    {
      watchVariable = basicProgram.addWatchVariable(aName, aVariable);
      watchedVariablesByVariable.put(aVariable, watchVariable);
    }
    else
    {
      watchVariable.addAlias(aName);
    }
    int index = watchedVariablesByVariable.get(aVariable).getIndex();
    lastModifiedVariable = null;
    ignoreSelectionChanges = true;
    try
    {
      fireWatchedVariablesTableModelModified();
      watchedVariablesTable.getSelectionModel().setSelectionInterval(index, index);
      allowRemove();
    }
    finally
    {
      ignoreSelectionChanges = false;
    }
    watchedVariablesTable.scrollRectToVisible(watchedVariablesTable.getCellRect(index, 0, false));
    tableExtraVerticalBox.setVisible(false);
  }

  protected void removeWatchedVariable()
  {
    boolean removed = false;
    Variable variable = getValidatedTextFieldVariable();
    if (variable != null)
    {
      WatchedVariable watchVariable = watchedVariablesByVariable.get(variable);
      if (watchVariable != null)
      {
        basicProgram.removeWatchVariable(watchVariable);
        watchedVariablesByVariable.remove(variable);
        removed = true;
      }
    }
    else
    {
      int indexes[] = watchedVariablesTable.getSelectedRows();
      if (indexes.length > 0)
      {
        Vector<WatchedVariable> removedWatchVariables = new Vector<WatchedVariable>();
        for (int i = indexes.length - 1; i >= 0; i--)
        {
          WatchedVariable watchedVariable = getWatchedVariables().get(i);
          watchedVariablesByVariable.remove(watchedVariable.getVariable());
          removedWatchVariables.add(watchedVariable);
        }
        basicProgram.removeWatchVariables(removedWatchVariables);
        removed = true;
      }
    }

    if (removed)
    {
      lastModifiedVariable = null;
      fireWatchedVariablesTableModelModified();
      // setText(null) does not work for an unknown reason
      watchedVariableTextField.setText("");
      validateTextField();
      if (getWatchedVariables().isEmpty())
      {
        tableExtraVerticalBox.setVisible(true);
      }
    }
  }

  protected void fireWatchedVariablesTableModelModified()
  {
    if (watchedVariablesTable != null && watchedVariablesTable.getModel() != null)
    {
      ((AbstractTableModel)watchedVariablesTable.getModel()).fireTableDataChanged();
    }
  }

  protected void resetStatValuesBackground()
  {
    for (JTextField statValueTextField : statValuesTextFields)
    {
      statValueTextField.setBackground(defaultStatValueBackground);
    }
  }

  public void setRunMode()
  {
    resetStatValuesBackground();
  }

  public void setWrtMode()
  {
    resetStatValuesBackground();
  }

  public void runProgram()
  {
    resetStatValuesBackground();
  }

  public void endProgram()
  {
    resetStatValuesBackground();
  }

  public void contProgram()
  {
    resetStatValuesBackground();
  }

  public void resumeProgram()
  {
    resetStatValuesBackground();
  }

  public void stepInProgram()
  {
    resetStatValuesBackground();
  }

  public void debugAndStepActiveProgram()
  {
    resetStatValuesBackground();
  }

  protected class WatchedVariablesTableModel extends AbstractTableModel
  {
    @Override
    public String getColumnName(int aColumn)
    {
      return WATCHED_VARIABLES_COLUMNS_NAMES[aColumn];
    }

    public int getRowCount()
    {
      return getWatchedVariables().size();
    }

    public int getColumnCount()
    {
      return WATCHED_VARIABLES_COLUMNS_NAMES.length;
    }

    @Override
    public Class<? extends Object> getColumnClass(int aColumn)
    {
      switch (aColumn)
      {
        case NAME_COLUMN_INDEX:
        {
          return String.class;
        }
        case NUMBER_COLUMN_INDEX:
        {
          return String.class;
        }
        case STRING_COLUMN_INDEX:
        {
          return String.class;
        }
        case WATCHPOINT_COLUMN_INDEX:
        {
          return Boolean.class;
        }
        default:
        {
          return Object.class;
        }
      }
    }
    public Object getValueAt(int aRow, int aColumn)
    {
      if (getWatchedVariables().isEmpty())
      {
        return null;
      }
      else
      {
        WatchedVariable watchedVariable = getWatchedVariables().get(aRow);
        switch (aColumn)
        {
          case NAME_COLUMN_INDEX:
          {
            return watchedVariable.getName();
          }
          case NUMBER_COLUMN_INDEX:
          {
            if (!watchedVariable.getVariable().isStringVariable())
            {
              return Fx702pConstants.NORMAL_FORMATTER.format((BigDecimal)watchedVariable.getVariable().getValue());
            }
            else
            {
              return null;
            }
          }
          case STRING_COLUMN_INDEX:
          {
            if (watchedVariable.getVariable().isStringVariable())
            {
              return watchedVariable.getVariable().getValue().toString();
            }
            else
            {
              return null;
            }
          }
          case WATCHPOINT_COLUMN_INDEX:
          {
            return watchedVariable.isWatchoint();
          }
          default:
          {
            return null;
          }
        }
      }
    }

    @Override
    public boolean isCellEditable(int aRow, int aColumn)
    {
      return aColumn == WATCHPOINT_COLUMN_INDEX;
    }

    @Override
    public void setValueAt(Object aValue, int aRow, int aColumn)
    {
      getWatchedVariables().get(aRow).setWatchpoint((Boolean)aValue);
    }
  }


  static protected class LastModifiedCellRenderer extends DefaultTableCellRenderer
  {
    @Override
    public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aSelectedFlag, boolean aFocusFlag, int aRow, int aColumn)
    {
      Component rendererComponent = super.getTableCellRendererComponent(aTable, aValue, aSelectedFlag, aFocusFlag, aRow, aColumn);
      rendererComponent.setBackground(LAST_CHANGED_COLOR);
      return rendererComponent;
    }
  }

  static protected class WatchpointCellRenderer extends DefaultTableCellRenderer
  {
    @Override
    public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aSelectedFlag, boolean aFocusFlag, int aRow, int aColumn)
    {
      Component rendererComponent = super.getTableCellRendererComponent(aTable, aValue, aSelectedFlag, aFocusFlag, aRow, aColumn);
      rendererComponent.setBackground(WATCHPOINT_COLOR);
      return rendererComponent;
    }
  }

  static protected class UppercaseDocumentFilter extends DocumentFilter
  {
    @Override
    public void insertString(DocumentFilter.FilterBypass aFilterBypass, int anOffset, String aText, AttributeSet anAttributeSet) throws BadLocationException
    {
      if (aText != null)
      {
        aFilterBypass.insertString(anOffset, aText.toUpperCase(), anAttributeSet);
      }
    }

    @Override
    public void replace(DocumentFilter.FilterBypass aFilterBypass, int anOffset, int aLength, String aText, AttributeSet anAttributeSet) throws BadLocationException
    {
      if (aText != null)
      {
        aFilterBypass.replace(anOffset, aLength, aText.toUpperCase(), anAttributeSet);
      }
    }
  }

  static protected interface StatValueGetter
  {
    public BigDecimal getValue();
  }

  protected Fx702pSwingKeyboardAndDisplay fx702pSwingKeyboardAndDisplay;
  protected Fx702pBasicProgram basicProgram;
  protected Fx702pEmulator emulator;
  protected Fx702pMemory memory;
  protected HashMap<Variable, WatchedVariable> watchedVariablesByVariable = new HashMap<Variable, WatchedVariable>();
  protected JTextField watchedVariableTextField;
  protected JTable watchedVariablesTable;
  protected Variable lastModifiedVariable = null;
  protected Color validWatchedVariableTextFieldForeground;
  protected Variable variableJustWatched = null;
  protected JButton watchButton;
  protected JButton removeButton;
  protected int tableFontHeight;
  protected Component tableExtraVerticalBox;
  protected JScrollPane statVariablesScrollPane;
  protected boolean ignoreSelectionChanges = false;
  protected boolean userSelectionChange = false;
  protected Vector<JTextField> statValuesTextFields = new Vector<JTextField>();
  protected Color defaultStatValueBackground;

  static protected final LastModifiedCellRenderer LAST_MODIFIED_CELL_RENDERER = new LastModifiedCellRenderer();
  static protected final WatchpointCellRenderer WATCHPOINT_CELL_RENDERER = new WatchpointCellRenderer();
  static protected final String STAT_VALUE_GETTER_PROPERTY = "com.fx702p.StatValueGetter";

  static public final Color LAST_CHANGED_COLOR = SwingUtils.DEFAULT_SELECTION_COLOR;
  static public final Color WATCHPOINT_COLOR = SwingUtils.DEFAULT_SUBSELECTION_COLOR;

  static public final Color INVALID_WATCHED_VARIABLE_TEXT_FIELD_FOREGROUND = new Color(255, 0, 0);

  static public final int WATCH_VARIABLE_NAME_COLUMNS = 20;
  static public final String[] WATCHED_VARIABLES_COLUMNS_NAMES =
    {
      "Name",
      "Number",
      "String",
      "Watchpoint"
    };

  static public final int NAME_COLUMN_INDEX = 0;
  static public final int NUMBER_COLUMN_INDEX = 1;
  static public final int STRING_COLUMN_INDEX = 2;
  static public final int WATCHPOINT_COLUMN_INDEX = 3;

  static public final int NAME_WIDTH = 8;
  static public final int NUMBER_WIDTH = 15;
  static public final int STRING_WIDTH = 21;
}
