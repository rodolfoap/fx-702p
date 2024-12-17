package com.fx702p.emulator.implementation;

import static com.fx702p.emulator.Fx702pConstants.ARRAY_COUNT_MAX;
import static com.fx702p.emulator.Fx702pConstants.DEFAULT_PROGRAM_STEPS_COUNT;
import static com.fx702p.emulator.Fx702pConstants.DEFAULT_ROUNDING_MODE;
import static com.fx702p.emulator.Fx702pConstants.DEFAULT_VARIABLES_COUNT;
import static com.fx702p.emulator.Fx702pConstants.DEFM_MAX;
import static com.fx702p.emulator.Fx702pConstants.DEG_CONVERSION;
import static com.fx702p.emulator.Fx702pConstants.DEG_MAX;
import static com.fx702p.emulator.Fx702pConstants.FILENAME_MAX_LENGTH;
import static com.fx702p.emulator.Fx702pConstants.NORMAL_FORMATTER;
import static com.fx702p.emulator.Fx702pConstants.PROGRAM_COUNT;

import java.io.*;
import java.math.*;

import com.fx702p.Fx702pFullParser;
import com.fx702p.emulator.*;
import com.fx702p.emulator.exceptions.*;
import com.fx702p.interpreters.*;
import com.fx702p.parser.*;

public class Fx702pDefaultMemory implements Fx702pMemory
{
  public Fx702pDefaultMemory()
  {
    int comparisonIndex = 0;
    dollar = new DollarVariable(this, comparisonIndex++);
    for (int i = 0; i < DEFAULT_VARIABLES_COUNT; i++)
    {
      variable[i] = new FullVariable(this, comparisonIndex++);
    }

    for (int i = 0; i < ARRAY_COUNT_MAX; i++)
    {
      array[i] = new ArrayVariable(this, comparisonIndex++);
    }
    for (int i = 0; i < PROGRAM_COUNT; i++)
    {
      program[i] = new Fx702pBasicProgram("P" + i);
    }
  }

  public void clearAllPrograms()
  {
    for (int i = 0; i < PROGRAM_COUNT; i++)
    {
      program[i].clear();
    }
  }

  public void clearAllVariables()
  {
    dollar.clear();
    for (int i = 0; i < DEFAULT_VARIABLES_COUNT; i++)
    {
      variable[i].clear();
    }
    for (int i = 0; i < ARRAY_COUNT_MAX; i++)
    {
      array[i].clear();
    }
    watcher.allCleared();
  }

  public DollarVariable getDollarVariable()
  {
    return dollar;
  }

  public FullVariable getVariable(int anIndex)
  {
    return variable[anIndex];
  }

  public FullVariable getArrayVariable(int anIndex)
  {
    if (anIndex < 0 || anIndex >= ARRAY_COUNT_MAX)
    {
      throw new Fx702pErr5Exception();
    }
    if (anIndex >= variablesCount - DEFAULT_VARIABLES_COUNT)
    {
      throw new Fx702pErr6Exception();
    }
    return array[anIndex];
  }

  public Fx702pBasicProgram getProgram(int anIndex)
  {
    return program[anIndex];
  }

  public Fx702pBasicProgram getActiveProgram()
  {
    return program[activeProgramIndex];
  }

  public int getActiveProgramIndex()
  {
    return activeProgramIndex;
  }

  public void setActiveProgramIndex(int aProgramIndex)
  {
    activeProgramIndex = aProgramIndex;
  }

  public double getTrigonometricConversionFactor()
  {
    return trigonometricConversionFactor;
  }

  public void setTrigonometricConversionFactor(double aTrigonometricConversionFactor)
  {
    trigonometricConversionFactor = aTrigonometricConversionFactor;
  }

  public double getTrigonometricMaxArgument()
  {
    return trigonometricMaxArgument;
  }

  public void setTrigonometricMaxArgument(double aTrigonometricMaxArgument)
  {
    trigonometricMaxArgument = aTrigonometricMaxArgument;
  }

  public void clearStatVariables()
  {
    statCounter = BigDecimal.ZERO;
    sumX = sumY = sumX2 = sumY2 = sumXY = BigDecimal.ZERO;
    lastY = null;
    watcher.statVariablesCleared();
  }

  protected void clearAll()
  {
    clearAllPrograms();
    clearAllVariables();
  }

  public BigDecimal getStatCounter()
  {
    return statCounter;
  }

  public BigDecimal getSumX()
  {
    return sumX;
  }

  public BigDecimal getSumY()
  {
    return sumY;
  }

  public BigDecimal getSumX2()
  {
    return sumX2;
  }

  public BigDecimal getSumY2()
  {
    return sumY2;
  }

  public BigDecimal getSumXY()
  {
    return sumXY;
  }

  public BigDecimal getLastY()
  {
    return lastY;
  }

  public void setLastY(BigDecimal aLastY)
  {
    lastY = aLastY;
  }

  public void setStatVariables(BigDecimal aStatCounter, BigDecimal aSumX, BigDecimal aSumY, BigDecimal aSumX2, BigDecimal aSumY2, BigDecimal aSumXY)
  {
    statCounter = aStatCounter;
    sumX = aSumX;
    sumY = aSumY;
    sumX2 = aSumX2;
    sumY2 = aSumY2;
    sumXY = aSumXY;
    watcher.statVariablesModified();
  }

  public BigDecimal getLastResult()
  {
    return lastResult;
  }

  public void setLastResult(BigDecimal aLastResult)
  {
    lastResult = aLastResult;
  }

  public Fx702pFormatter getFormatter()
  {
    return formatter;
  }

  public void setFormatter(Fx702pFormatter aFx702pFormatter)
  {
    formatter = aFx702pFormatter;
  }

  public RoundingMode getRoundingMode()
  {
    return roundingMode;
  }

  public void setRoundingMode(RoundingMode aRoundingMode)
  {
    roundingMode = aRoundingMode;
  }

  public Watcher getWatcher()
  {
    return watcher;
  }

  public void setWatcher(Watcher aWatcher)
  {
    if (aWatcher == null)
    {
      watcher = EMPTY_WATCHER;
    }
    else
    {
      watcher = aWatcher;
    }
  }

  public boolean isSavingVariables()
  {
    return saveVariables;
  }

  public boolean isSavingBreakpoints()
  {
    return saveBreakpoints;
  }

  public void load(File aProgramsFile, Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws FileNotFoundException, UnsupportedEncodingException, ParseException, Fx702pLoadingException
  {
    try
    {
      programsFile = aProgramsFile;
      saveVariables = false;
      saveBreakpoints = false;
      Fx702pFullParser parser = new Fx702pFullParser(new FileInputStream(programsFile));
      ASTProgramsFile programsFile = parser.ProgramsFile();
      ProgramFilesVisitor programFilesVisitor = new ProgramFilesVisitor(parser.getBasicSourceCode(), aCalculator, aDisplay);
      programsFile.jjtAccept(programFilesVisitor, null);
    }
    finally
    {
      modified = false;
    }
  }

  public void reload(Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws FileNotFoundException, UnsupportedEncodingException, Fx702pLoadingException, ParseException
  {
    load(programsFile, aCalculator, aDisplay);
  }

  public void saveAll(Fx702pDisplay aDisplay) throws IOException
  {
    boolean confirmed = true;
    if (hasComments)
    {
      confirmed = aDisplay.askConfirmation("Confirm Save", "Comments will be lost. Save anyway?");
    }
    if (confirmed)
    {
      saveAllAs(null, saveVariables, saveBreakpoints);
    }
  }


  public void saveAllAs(File aProgramsFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag) throws IOException
  {
    if (aProgramsFile == null)
    {
      aProgramsFile = programsFile;
    }
    File tempFile = File.createTempFile('_' + aProgramsFile.getName(), null, aProgramsFile.getAbsoluteFile().getParentFile());
    try
    {
      FileOutputStream outputStream = new FileOutputStream(tempFile);
      save(outputStream, buildName(aProgramsFile), aSaveVariablesFlag, aSaveBreakpointsFlag);
      outputStream.flush();
      outputStream.close();

      if (tempFile.renameTo(aProgramsFile))
      {
        programsFile = aProgramsFile;
        saveVariables = aSaveVariablesFlag;
        saveBreakpoints = aSaveBreakpointsFlag;
        hasComments = false;
        modified = false;
      }
      else
      {
        throw new IOException("Cannot rename temporary file " + tempFile.getPath() + " to " + programsFile.getPath());
      }
    }
    finally
    {
      tempFile.delete();
    }
  }

  protected String buildName(File aFile)
  {
    String name = aFile.getName();
    int lastDot = name.lastIndexOf('.');
    if (lastDot > 0)
    {
      name = name.substring(0, lastDot);
    }
    return name.substring(0, Math.min(name.length(), FILENAME_MAX_LENGTH)).toUpperCase();
  }

  protected void save(OutputStream anOutputStream, String aName, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag)
  {
    PrintWriter writer = new PrintWriter(anOutputStream);
    saveName(writer, aName);
    saveDefm(writer);
    if (aSaveVariablesFlag)
    {
      saveVariables(writer);
    }
    savePrograms(writer, aSaveBreakpointsFlag);
    writer.flush();
    writer.close();
  }

  protected void saveName(PrintWriter aWriter, String aName)
  {
    aWriter.println("All programs: \"" + aName + '"');
    aWriter.println();
  }

  protected void saveDefm(PrintWriter aWriter)
  {
    aWriter.print("DEFM ");
    aWriter.println((variablesCount - DEFAULT_VARIABLES_COUNT) / 10);
    aWriter.println();
  }

  public void saveVariables(PrintWriter aWriter)
  {
    saveVariable("", "", dollar, aWriter); // No need to give the name, $ is appended anyway
    for (int i = 0; i < DEFAULT_VARIABLES_COUNT; i++)
    {
      saveVariable("" + (char)('A' + i), "", variable[i], aWriter);
    }
    for (int i = 0; i < variablesCount - DEFAULT_VARIABLES_COUNT; i++)
    {
      saveVariable("A", "(" + i + ")", array[i], aWriter);
    }
    aWriter.println();
  }

  protected void saveVariable(String aName, String anIndex, Variable aVariable, PrintWriter aWriter)
  {
    boolean empty = (aVariable.isStringVariable() && aVariable.getValue().equals("")) || (!aVariable.isStringVariable() && aVariable.getValue().equals(BigDecimal.ZERO));
    if (!empty)
    {
      aWriter.print(aName);
      if (aVariable.isStringVariable())
      {
        aWriter.print('$');
        aWriter.print(anIndex);
        aWriter.print("=\"");
        aWriter.print((String)aVariable.getValue());
        aWriter.print('"');
      }
      else
      {
        aWriter.print(anIndex);
        aWriter.print('=');
        aWriter.print(NORMAL_FORMATTER.format((BigDecimal)aVariable.getValue()));
      }
      aWriter.print('\n');
    }
  }

  protected void savePrograms(PrintWriter aWriter, boolean aSaveBreakpointsFlag)
  {
    for (int i = 0; i < 10; i++)
    {
      Fx702pBasicProgram program = getProgram(i);
      if (!program.isEmpty())
      {
        aWriter.println("P" + i);
        aWriter.println();
        program.write(this, aWriter, false, aSaveBreakpointsFlag);
        aWriter.println();
      }
    }
  }

  public boolean areVariablesModified()
  {
    return modified;
  }

  public boolean isModified()
  {
    if (modified)
    {
      return true;
    }
    else
    {
      for (int i = 0; i < PROGRAM_COUNT; i++)
      {
        if (program[i].isModified())
        {
          return true;
        }
      }
    }
    return false;
  }

  public void markAsModified()
  {
    modified = true;
  }

  public String getProgramsFileName()
  {
    return programsFile == null ? "" : programsFile.getName();
  }

  public boolean canBeSaved()
  {
    return programsFile != null && isModified();
  }

  public boolean canBeLoaded()
  {
    return isAllLoaded();
  }

  public boolean isAllLoaded()
  {
    return programsFile != null;
  }

  public void setDefm(int aDefm)
  {
    if (aDefm < 0 || aDefm > DEFM_MAX)
    {
      throw new Fx702pErr5Exception();
    }
    programStepsCount = DEFAULT_PROGRAM_STEPS_COUNT - 80 * aDefm;
    int newVariablesCount = DEFAULT_VARIABLES_COUNT + 10 * aDefm;
    if (newVariablesCount > variablesCount)
    {
      for (int i = variablesCount - DEFAULT_VARIABLES_COUNT; i < newVariablesCount - DEFAULT_VARIABLES_COUNT; i++)
      {
        array[i].clear();
      }
    }
    variablesCount = newVariablesCount;
  }

  public int getVariablesCount()
  {
    return variablesCount;
  }

  public int getProgramStepsCount()
  {
    return programStepsCount;
  }

  protected class ProgramFilesVisitor extends Fx702pAbstractParserVisitor
  {
    public ProgramFilesVisitor(Fx702pBasicSourceCode aBasicSourceCode, Fx702pCalculator aCalculator, Fx702pDisplay aDisplay)
    {
      basicSourceCode = aBasicSourceCode;
      calculator = aCalculator;
      display = aDisplay;
    }

    @Override
    public Object visit(ASTMultiplePrograms aMultiplePrograms, Object aData)
    {
      clearAll();
      aMultiplePrograms.childrenAccept(this, aData);
      globalBasicSourceCode = new Fx702pBasicSourceCode(basicSourceCode, aMultiplePrograms.getBeginLine(), aMultiplePrograms.getEndLine());
      return null;
    }

    @Override
    public Object visit(ASTSavedDefm aSavedDefm, Object aData)
    {
      try
      {
        if (aSavedDefm.jjtGetNumChildren() != 1 || !(aSavedDefm.jjtGetChild(0) instanceof ASTInteger))
        {
          throw new Fx702pErr2Exception();
        }
        int value = ((ASTInteger)aSavedDefm.jjtGetChild(0)).value.intValue();
        setDefm(value);
        return null;
      }
      catch (Fx702pException exception)
      {
        exception.setLine((Integer)aSavedDefm.jjtGetValue());
        throw exception;
      }
    }

    @Override
    public Object visit(ASTSingleProgram aSingleProgram, Object aData)
    {
      int programNumber = aSingleProgram.getProgramNumber();
      String name = "P" + programNumber + " in " + programsFile.getName();
      String password = aSingleProgram.getPassword();
      Fx702pBasicSourceCode programSourceCode = new Fx702pBasicSourceCode(basicSourceCode, aSingleProgram.getBeginLine(), aSingleProgram.getEndLine());
      try
      {
        program[programNumber].load(name, aSingleProgram, programSourceCode, password, calculator, display);
        saveBreakpoints |= program[programNumber].isSavingBreakpoints();
      }
      catch (Exception exception)
      {
        exception.printStackTrace();
      }
      return null;
    }

    @Override
    public Object visit(ASTVariablesAndProgram aNode, Object aData)
    {
      try
      {
        getActiveProgram().load(programsFile, aNode, basicSourceCode, calculator, display);
        saveBreakpoints |= getActiveProgram().isSavingBreakpoints();
        programsFile = null;
      }
      catch (Exception exception)
      {
        throw new Fx702pLoadingException(exception.getMessage());
      }
      return null;
    }

    @Override
    public Object visit(ASTAssignment anAssignment, Object aData)
    {
      try
      {
        calculator.visit(anAssignment, aData);
        saveVariables = true;
        return null;
      }
      catch (Fx702pException exception)
      {
        exception.setLine((Integer)anAssignment.jjtGetValue());
        throw exception;
      }
    }

    @Override
    public Object visit(ASTComment aNode, Object aData)
    {
      hasComments = true;
      return null;
    }

    protected Fx702pBasicSourceCode basicSourceCode;
    protected Fx702pCalculator calculator;
    protected Fx702pDisplay display;
  }

  protected DollarVariable dollar;
  protected FullVariable variable[] = new FullVariable[DEFAULT_VARIABLES_COUNT];
  protected FullVariable array[] = new FullVariable[ARRAY_COUNT_MAX];
  protected int activeProgramIndex = 0;
  protected Fx702pBasicProgram[] program = new Fx702pBasicProgram[PROGRAM_COUNT];
  protected BigDecimal statCounter = BigDecimal.ZERO;
  protected BigDecimal sumX = BigDecimal.ZERO, sumY = BigDecimal.ZERO, sumX2 = BigDecimal.ZERO, sumY2 = BigDecimal.ZERO, sumXY = BigDecimal.ZERO;
  protected BigDecimal lastY = null;
  protected double trigonometricConversionFactor = DEG_CONVERSION;
  protected double trigonometricMaxArgument = DEG_MAX;
  protected BigDecimal lastResult = BigDecimal.ZERO;
  protected Fx702pFormatter formatter = NORMAL_FORMATTER;
  protected RoundingMode roundingMode = DEFAULT_ROUNDING_MODE;
  protected Watcher watcher = EMPTY_WATCHER;
  protected File programsFile = null;
  protected boolean modified = false;
  protected Fx702pBasicSourceCode globalBasicSourceCode;
  protected boolean hasComments = false;
  protected int programStepsCount = DEFAULT_PROGRAM_STEPS_COUNT;
  protected int variablesCount = DEFAULT_VARIABLES_COUNT;
  protected boolean saveVariables = false;
  protected boolean saveBreakpoints = false;
}
