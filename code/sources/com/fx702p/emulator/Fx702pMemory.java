package com.fx702p.emulator;

import java.io.*;
import java.math.*;

import com.fx702p.emulator.exceptions.Fx702pLoadingException;
import com.fx702p.interpreters.*;
import com.fx702p.parser.ParseException;

public interface Fx702pMemory
{
  public void clearAllPrograms();
  public void clearAllVariables();
  public DollarVariable getDollarVariable();
  public FullVariable getVariable(int anIndex);
  public FullVariable getArrayVariable(int anIndex);

  public Fx702pBasicProgram getProgram(int anIndex);
  public Fx702pBasicProgram getActiveProgram();
  public int getActiveProgramIndex();
  public void setActiveProgramIndex(int aProgramIndex);

  public double getTrigonometricConversionFactor();
  public void setTrigonometricConversionFactor(double aTrigonometricConversionFactor);
  public double getTrigonometricMaxArgument();
  public void setTrigonometricMaxArgument(double aTrigonometricMaxArgument);

  // Stat variables
  public void clearStatVariables();
  public BigDecimal getStatCounter();
  public BigDecimal getSumX();
  public BigDecimal getSumY();
  public BigDecimal getSumX2();
  public BigDecimal getSumY2();
  public BigDecimal getSumXY();
  public BigDecimal getLastY();
  public void setLastY(BigDecimal aLastY);
  public void setStatVariables(BigDecimal aStatCounter, BigDecimal aSumX, BigDecimal aSumY, BigDecimal aSumX2, BigDecimal aSumY2, BigDecimal aSumXY);

  // ANS variable
  public BigDecimal getLastResult();
  public void setLastResult(BigDecimal aLastResult);

  public Fx702pFormatter getFormatter();
  public void setFormatter(Fx702pFormatter aFx702pFormatter);
  public RoundingMode getRoundingMode();
  public void setRoundingMode(RoundingMode aRoundingMode);

  public Watcher getWatcher();
  public void setWatcher(Watcher aWatcher);

  public boolean isSavingVariables();
  public boolean isSavingBreakpoints();

  public void load(File aProgramFile, final Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws FileNotFoundException, UnsupportedEncodingException, ParseException;
  public void reload(Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws FileNotFoundException, UnsupportedEncodingException, Fx702pLoadingException, ParseException;
  public boolean isModified();
  public void markAsModified();
  public String getProgramsFileName();
  public boolean areVariablesModified();
  public boolean canBeSaved();
  public boolean canBeLoaded();
  public boolean isAllLoaded();
  public void saveAll(Fx702pDisplay aDisplay) throws IOException;
  public void saveAllAs(File aProgramsFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag) throws IOException;
  public void saveVariables(PrintWriter aWriter);

  public void setDefm(int aDefm);
  public int getVariablesCount();
  public int getProgramStepsCount();

  public interface Watcher
  {
    public void allCleared();
    public void variableModified(Variable aVariable);
    public void statVariablesCleared();
    public void statVariablesModified();
  }

  public class EmptyWatcher implements Watcher
  {
    public void allCleared()
    {
    }

    public void variableModified(Variable aVariable)
    {
    }

    public void statVariablesCleared()
    {
    }

    public void statVariablesModified()
    {
    }
  }

  public interface WatchedVariable extends Comparable<WatchedVariable>
  {
    public String getName();
    public String getAlias();
    public Variable getVariable();
    public boolean isWatchoint();
    public void setWatchpoint(boolean aWatchpoint);
    public boolean isEmpty();
    public int getIndex();
    public void setIndex(int anIndex);
    public void addAlias(String anAlias);
    public void removeAlias(String anAlias);
  }

  static public final EmptyWatcher EMPTY_WATCHER = new EmptyWatcher();
}
