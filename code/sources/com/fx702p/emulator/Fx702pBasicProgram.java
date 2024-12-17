package com.fx702p.emulator;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

import com.fx702p.Fx702pFullParser;
import com.fx702p.debug.WatchedVariableHelper;
import com.fx702p.emulator.Fx702pMemory.WatchedVariable;
import com.fx702p.emulator.exceptions.Fx702pLoadingException;
import com.fx702p.interpreters.*;
import com.fx702p.parser.*;
import com.fx702p.parser.ASTLine.LineInfos;

public class Fx702pBasicProgram
{
  public Fx702pBasicProgram(String aName)
  {
    name = aName;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String aName)
  {
    name = aName;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String aPassword)
  {
    password = aPassword;
  }

  public boolean isEmpty()
  {
    return parsedProgram == null || empty;
  }

  public String getProgramFileName()
  {
    return programFile == null ? "" : programFile.getName();
  }

  public boolean canBeLoaded()
  {
    return programFile != null;
  }

  public boolean canBeSaved(Fx702pMemory aMemory)
  {
    return programFile != null && isModified();
  }

  public boolean isModified()
  {
    return modified;
  }

  public boolean isSavingVariables()
  {
    return saveVariables;
  }

  public boolean isSavingBreakpoints()
  {
    return saveBreakpoints;
  }

  public void load(File aProgramFile, Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws FileNotFoundException, UnsupportedEncodingException, ParseException
  {
    Fx702pFullParser parser = new Fx702pFullParser(new FileInputStream(aProgramFile));
    ASTVariablesAndProgram variablesAndProgram = parser.VariablesAndProgram();
    load(aProgramFile, variablesAndProgram, parser.getBasicSourceCode(), aCalculator, aDisplay);
  }

  public void load(File aProgramFile, ASTVariablesAndProgram theVariablesAndProgram, Fx702pBasicSourceCode aBasicSourceCode, Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws UnsupportedEncodingException, ParseException
  {
    programFile = aProgramFile;
    modified = false;
    saveVariables = false;
    saveBreakpoints = false;
    empty = true;
    breakpoints.clear();
    setName(aProgramFile.getName());

    basicSourceCode = aBasicSourceCode;
    BasicProgramVisitor basicProgramVisitor = new BasicProgramVisitor(aCalculator);
    theVariablesAndProgram.jjtAccept(basicProgramVisitor, null);
    loadBreakpoints(basicProgramVisitor.getProgramSignature(), basicProgramVisitor.getLoadedBreakpoints(), aDisplay);
    buildWatchedVariablesIndex();
    aCalculator.getMemory().markAsModified();
  }

  public void load(String aName, ASTSingleProgram aSingleProgram, Fx702pBasicSourceCode aBasicSourceCode, String aPassword, Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws UnsupportedEncodingException, ParseException
  {
    empty = true;
    saveVariables = false;
    saveBreakpoints = false;
    breakpoints.clear();
    setName(aName);
    setPassword(aPassword);

    basicSourceCode = aBasicSourceCode;
    BasicProgramVisitor basicProgramVisitor = new BasicProgramVisitor(aCalculator);
    aSingleProgram.jjtAccept(basicProgramVisitor, null);
    loadBreakpoints(basicProgramVisitor.getProgramSignature(), basicProgramVisitor.getLoadedBreakpoints(), aDisplay);
    buildWatchedVariablesIndex();
  }

  protected void loadBreakpoints(String aProgramSignature, Collection<BasicInstructionIndex> theBreakpoints, Fx702pDisplay aDisplay)
  {
    boolean signatureMatch = aProgramSignature == null || aProgramSignature.equalsIgnoreCase(getStringSignature());
    boolean breakpointsValid = true;

    for (BasicInstructionIndex breakpoint : theBreakpoints)
    {
      if (!breakpoint.isValid())
      {
        breakpointsValid = false;
        break;
      }
    }

    String detailedMessage = null;
    if (!signatureMatch)
    {
      if (breakpointsValid)
      {
        detailedMessage = "Program signature does not match. Try to load breakpoints anyway?";
      }
      else
      {
        detailedMessage = "Program signature does not match and some breakpoints are not valid. Try to load them anyway?";
      }
    }
    else if (!breakpointsValid)
    {
      detailedMessage = "Some breakpoints are not valid. Try to load them anyway?";
    }

    if (detailedMessage != null)
    {
      if (!aDisplay.askConfirmation("Confirm breakpoints loading", detailedMessage))
      {
        return;
      }
    }

    reallyloadBreakpoints(theBreakpoints);
  }

  protected void reallyloadBreakpoints(Collection<BasicInstructionIndex> theBreakpoints)
  {
    for (BasicInstructionIndex breakpoint : theBreakpoints)
    {
      toggleBreakpoint(breakpoint);
    }
  }

  public void reload(final Fx702pCalculator aCalculator, Fx702pDisplay aDisplay) throws FileNotFoundException, UnsupportedEncodingException, ParseException
  {
    if (programFile != null)
    {
      load(programFile, aCalculator, aDisplay);
    }
  }

  public void save(Fx702pEmulator anEmulator) throws IOException
  {
    boolean confirmed = true;
    if (hasComments)
    {
      confirmed = anEmulator.getDisplay().askConfirmation("Confirm Save", "Comments will be lost. Save anyway?");
    }
    if (confirmed)
    {
      saveAs(anEmulator, null, saveVariables, saveBreakpoints);
    }
  }

  public void saveAs(Fx702pEmulator anEmulator, File aProgramFile, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag) throws IOException
  {
    if (aProgramFile == null)
    {
      aProgramFile = programFile;
    }
    File tempFile = File.createTempFile('_' + aProgramFile.getName(), null, aProgramFile.getAbsoluteFile().getParentFile());
    try
    {
      FileOutputStream outputStream = new FileOutputStream(tempFile);
      save(anEmulator.getMemory(), outputStream, aSaveVariablesFlag, aSaveBreakpointsFlag);

      if (tempFile.renameTo(aProgramFile))
      {
        programFile = aProgramFile;
        saveVariables = aSaveVariablesFlag;
        saveBreakpoints = aSaveBreakpointsFlag;
        modified = false;
      }
      else
      {
        throw new IOException("Cannot rename temporary file " + tempFile.getPath() + " to " + programFile.getPath());
      }
    }
    finally
    {
      tempFile.delete();
    }
  }

  public void write(Fx702pMemory aMemory, PrintWriter aWriter, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag)
  {
    if (aSaveVariablesFlag)
    {
      saveVariables(aMemory, aWriter);
    }
    saveProgram(aWriter);
    if (aSaveBreakpointsFlag)
    {
      saveBreakpoints(aWriter);
      saveWatchpoints(aWriter);
    }
  }

  protected void save(Fx702pMemory aMemory, OutputStream anOutputStream, boolean aSaveVariablesFlag, boolean aSaveBreakpointsFlag)
  {
    PrintWriter writer = new PrintWriter(anOutputStream);
    write(aMemory, writer, aSaveVariablesFlag, aSaveBreakpointsFlag);
    writer.flush();
    writer.close();
  }

  protected void saveVariables(Fx702pMemory aMemory, PrintWriter aPrintWriter)
  {
    aMemory.saveVariables(aPrintWriter);
  }

  protected void saveProgram(PrintWriter aPrintWriter)
  {
    parsedProgram.jjtAccept(new Fx702pListVisitor(aPrintWriter), null);
    aPrintWriter.println();
  }

  protected void saveBreakpoints(PrintWriter aPrintWriter)
  {
    if (!breakpoints.isEmpty())
    {
      aPrintWriter.println(SIGNATURE_TEXT + " \"" + getStringSignature() + '"');
      aPrintWriter.println();
      Vector<BasicInstructionIndex> sortedBreakpoints = new Vector<BasicInstructionIndex>();
      for (BasicInstructionIndex basicInstructionIndex : breakpoints)
      {
        int lineNumber = parsedProgram.getLine(basicInstructionIndex.getLineIndex()).getLine();
        BasicInstructionIndex breakpoint = new BasicInstructionIndex(lineNumber, basicInstructionIndex.getInstructionIndex());
        sortedBreakpoints.add(breakpoint);
      }

      Collections.sort(sortedBreakpoints);

      for (BasicInstructionIndex breakpoint : sortedBreakpoints)
      {
        aPrintWriter.println(BREAKPOINT_TEXT + ' ' + breakpoint.getLineIndex() + ", " + breakpoint.getInstructionIndex());
      }
      aPrintWriter.println();
    }
  }

  protected void saveWatchpoints(PrintWriter aPrintWriter)
  {
    for (WatchedVariable watchedVariable : watchedVariables)
    {
      String keyword = watchedVariable.isWatchoint() ? WATCHPOINT_TEXT : WATCH_TEXT;
      aPrintWriter.println(keyword + " " + watchedVariable.getAlias());
    }
    if (suspendOnStatVariables)
    {
      aPrintWriter.println(STATWATCHPOINT_TEXT);
    }
  }

  public ASTProgram getParsedProgram()
  {
    return parsedProgram;
  }

  public Fx702pBasicSourceCode getBasicSourceCode()
  {
    return basicSourceCode;
  }

  public void clear()
  {
    parsedProgram = null;
    empty = true;
    breakpoints.clear();
    watchedVariables.clear();
    buildWatchedVariablesIndex();
  }

  public void toggleBreakpoint(BasicInstructionIndex aBasicInstructionIndex)
  {
    if (breakpoints.contains(aBasicInstructionIndex))
    {
      breakpoints.remove(aBasicInstructionIndex);
    }
    else
    {
      breakpoints.add(aBasicInstructionIndex);
    }
    modified = true;
  }

  public void addWatchVariables(Collection<WatchedVariable> theWatchedVariables)
  {
    watchedVariables.addAll(theWatchedVariables);
    buildWatchedVariablesIndex();
    modified = true;
  }

  public WatchedVariable addWatchVariable(String anAlias, Variable aVariable)
  {
    WatchedVariable watchedVariable = buildWatchVariable(anAlias, aVariable);
    watchedVariables.add(watchedVariable);
    buildWatchedVariablesIndex();
    modified = true;
    return watchedVariable;
  }

  public WatchedVariable buildWatchVariable(String anAlias, Variable aVariable)
  {
    return new DefaultWatchedVariable(anAlias, aVariable);
  }

  public void removeWatchVariable(WatchedVariable aWatchedVariable)
  {
    watchedVariables.remove(aWatchedVariable.getIndex());
    buildWatchedVariablesIndex();
    modified = true;
  }

  public void removeWatchVariables(Collection<WatchedVariable> theWatchedVariables)
  {
    watchedVariables.removeAll(theWatchedVariables);
    buildWatchedVariablesIndex();
    modified = true;
  }

  protected void buildWatchedVariablesIndex()
  {
    Collections.sort(watchedVariables);
    for (int i = 0; i < getWatchedVariables().size(); i++)
    {
      getWatchedVariables().get(i).setIndex(i);
    }
  }

  public boolean containsBreakpoint(BasicInstructionIndex aBasicInstructionIndex)
  {
    return breakpoints.contains(aBasicInstructionIndex);
  }

  public Collection<BasicInstructionIndex> getBreakpoints()
  {
    return Collections.unmodifiableCollection(breakpoints);
  }

  public List<WatchedVariable> getWatchedVariables()
  {
    return watchedVariables;
  }

  public boolean isSuspendOnStatVariables()
  {
    return suspendOnStatVariables;
  }

  public void setSuspendOnStatVariables(boolean aSuspendOnStatVariables)
  {
    suspendOnStatVariables = aSuspendOnStatVariables;
  }

  public void removeAllBreakpoints()
  {
    if (!breakpoints.isEmpty())
    {
      breakpoints.clear();
      modified = true;
    }
  }

  protected byte[] getSignature()
  {
    try
    {
      MessageDigest signature = MessageDigest.getInstance("MD5");
      parsedProgram.jjtAccept(new SignatureVisitor(), signature);
      return signature.digest();
    }
    catch (NoSuchAlgorithmException exception)
    {
      return null;
    }
  }

  protected String getStringSignature()
  {
    return new BigInteger(getSignature()).toString(16).toUpperCase();
  }

  protected class LineVisitor extends Fx702pAbstractParserVisitor
  {
    @Override
    public Object visit(ASTVariablesAndProgram aNode, Object aData)
    {
      aNode.childrenAccept(this, aData);
      return null;
    }

    @Override
    public Object visit(ASTProgram aProgram, Object aData)
    {
      parsedProgram = aProgram;
      parsedProgram.childrenAccept(this, aData);
      for (int i = 0, last = aProgram.jjtGetNumChildren(); i < last; i++)
      {
        Node node = aProgram.jjtGetChild(i);
        if (node instanceof ASTLine)
        {
          ((ASTLine)node).setLineIndexInProgram(i);
        }
      }
      return null;
    }

    @Override
    public Object visit(ASTLine aLine, Object aData)
    {
      if (aLine.getLine() <= lastLine)
      {
        throw new Fx702pLoadingException("Invalid line number " + aLine.getLine() + ": should be greater than the previous one.\nSee line " + aLine.getLineIndexInSourceCode() + " in " + getName());
      }
      else
      {
        lastLine = aLine.getLine();
      }
      LineInfos lineInfos = new LineInfos();
      for (int i = 0, last = aLine.jjtGetNumChildren(); i < last; i++)
      {
        aLine.visitAndResynchronize(i, this, lineInfos);
      }
      aLine.resynchronizePositions(basicSourceCode);

      fixBoundaries(aLine, lineInfos);
      basicSourceCode.getLine(aLine.getLineIndexInSourceCode()).setParsedLine(aLine);
      return null;
    }

    protected void fixBoundaries(ASTLine aLine, LineInfos theLineInfos)
    {
      for (int i = 0, last = aLine.jjtGetNumChildren(); i < last; i++)
      {
        Node node = aLine.jjtGetChild(i);
        if (theLineInfos.boundariesToFix.containsKey(node))
        {
          theLineInfos.boundariesToFix.get(node).end = aLine.getInstructionBoundary(i).end;
        }
      }
    }

    @Override
    public Object visit(ASTPrint aPrint, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        aPrint.visitAndResynchronize(this, (LineInfos)aData);
        aPrint.resynchronizePositions(basicSourceCode, (LineInfos)aData);
      }
      else
      {
        aPrint.childrenAccept(this, aData);
        aPrint.resynchronizePositions(basicSourceCode, null);
      }
      return null;
    }

    @Override
    public Object visit(ASTInput anInput, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        anInput.visitAndResynchronize(this, (LineInfos)aData);
        anInput.resynchronizePositions(basicSourceCode, (LineInfos)aData);
      }
      else
      {
        anInput.childrenAccept(this, aData);
        anInput.resynchronizePositions(basicSourceCode, null);
      }
      return null;
    }

    @Override
    public Object visit(ASTString aString, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        ((LineInfos)aData).columnDelta += aString.delta;
      }
      return null;
    }

    @Override
    public Object visit(ASTFloat aFloat, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        ((LineInfos)aData).columnDelta += aFloat.delta;
      }
      return null;
    }

    @Override
    public Object visit(ASTPi aPi, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        ((LineInfos)aData).columnDelta += aPi.delta;
      }
      return null;
    }

    @Override
    public Object visit(ASTNotEqual aNotEqual, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        ((LineInfos)aData).columnDelta += aNotEqual.delta;
      }
      return null;
    }

    @Override
    public Object visit(ASTLesserOrEqual aLesserOrEqual, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        ((LineInfos)aData).columnDelta += aLesserOrEqual.delta;
      }
      return null;
    }

    @Override
    public Object visit(ASTGreaterOrEqual aGreaterOrEqual, Object aData)
    {
      if (aData instanceof LineInfos)
      {
        ((LineInfos)aData).columnDelta += aGreaterOrEqual.delta;
      }
      return null;
    }

    @Override
    public Object visit(ASTComment aNode, Object aData)
    {
      hasComments = true;
      return null;
    }

    protected int lastLine = -1;
  }

  protected class BasicProgramVisitor extends LineVisitor
  {
    public BasicProgramVisitor(Fx702pCalculator aCalculator)
    {
      calculator = aCalculator;
      lineVisitor = new LineVisitor();
    }

    @Override
    public Object visit(ASTLine aLine, Object aData)
    {
      aLine.jjtAccept(lineVisitor, aData);
      empty = false;
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
    public Object visit(ASTProgramSignature aNode, Object aData)
    {
      programSignature = aNode.signature;
      saveBreakpoints = true;
      return null;
    }

    @Override
    public Object visit(ASTBreakpoint aNode, Object aData)
    {
      ASTBreakpoint astBreakpoint = aNode;
      int lineIndex = parsedProgram.getLineIndexByLineNumberAndCheckInstructionIndex(astBreakpoint.line, astBreakpoint.instructionIndex);
      BasicInstructionIndex breakpoint = new BasicInstructionIndex(lineIndex, astBreakpoint.instructionIndex);
      loadedBreakpoints.add(breakpoint);
      saveBreakpoints = true;
      return null;
    }

    @Override
    public Object visit(ASTWatch aNode, Object aData)
    {
      watchedVariables.add(buildWatchedVariable(aNode));
      saveBreakpoints = true;
      return null;
    }

    @Override
    public Object visit(ASTWatchpoint aNode, Object aData)
    {
      WatchedVariable watchedVariable = buildWatchedVariable(aNode);
      watchedVariable.setWatchpoint(true);
      watchedVariables.add(watchedVariable);
      saveBreakpoints = true;
      return null;
    }

    protected WatchedVariable buildWatchedVariable(SimpleNode aNode)
    {
      if (aNode.jjtGetNumChildren() != 1)
      {
        throw new Fx702pInternalError("Invalid syntax for " + WATCH_TEXT + ", line " + aNode.jjtGetValue());
      }

      Variable variable = WatchedVariableHelper.getVariable(aNode.jjtGetChild(0), calculator.getMemory());
      if (variable == null)
      {
        throw new Fx702pInternalError("Invalid variable in " + WATCH_TEXT + ", line " + aNode.jjtGetValue());
      }
      return new DefaultWatchedVariable(WatchedVariableHelper.getAlias(aNode.jjtGetChild(0)), variable);
    }

    @Override
    public Object visit(ASTStatWatchpoint aNode, Object aData)
    {
      suspendOnStatVariables = true;
      saveBreakpoints = true;
      return null;
    }

    public String getProgramSignature()
    {
      return programSignature;
    }

    public Collection<BasicInstructionIndex> getLoadedBreakpoints()
    {
      return loadedBreakpoints;
    }

    protected Fx702pParserVisitor lineVisitor;
    protected Fx702pCalculator calculator;
    protected String programSignature;
    protected Vector<BasicInstructionIndex> loadedBreakpoints = new Vector<BasicInstructionIndex>();
  }

  protected class SignatureVisitor extends Fx702pAbstractParserVisitor
  {
    protected void visitDigest(SimpleNode aNode, Object aData)
    {
      ((MessageDigest)aData).update(aNode.getClass().getSimpleName().getBytes());
    }

    @Override
    public Object visit(ASTMode aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTRom aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTClear aNode, Object aData)
    {
      aNode.childrenAccept(this, aData);
      return null;
    }

    @Override
    public Object visit(ASTAssignment aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTKey aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTGoto aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTGotoProgram aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTGsb aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTGsbProgram aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTReturn aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTPrint aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTInput aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTSet aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTVac aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTSac aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTStop aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTEnd aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTWait aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTStat aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTRPC aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTPRC aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTDMS aNode, Object aData)
    {
      aNode.childrenAccept(this, aData);
      return null;
    }

    @Override
    public Object visit(ASTDeg aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTLoad aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTGet aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTPut aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTIf aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTFor aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }

    @Override
    public Object visit(ASTNext aNode, Object aData)
    {
      visitDigest(aNode, aData);
      return null;
    }
  }

  public class DefaultWatchedVariable implements WatchedVariable
  {
    public DefaultWatchedVariable(String anAlias, Variable aVariable)
    {
      addAlias(anAlias);
      variable = aVariable;
      watchpoint = false;
    }

    public String getName()
    {
      return name;
    }

    public String getAlias()
    {
      if (aliases.isEmpty())
      {
        return null;
      }
      else
      {
        return aliases.first();
      }
    }

    public Variable getVariable()
    {
      return variable;
    }

    public boolean isWatchoint()
    {
      return watchpoint;
    }

    public void setWatchpoint(boolean aWatchpoint)
    {
      modified = (watchpoint != aWatchpoint);
      watchpoint = aWatchpoint;
    }

    public boolean isEmpty()
    {
      return aliases.isEmpty();
    }

    public int getIndex()
    {
      return index;
    }

    public void setIndex(int anIndex)
    {
      index = anIndex;
    }

    public void addAlias(String anAlias)
    {
      if (aliases.add(anAlias))
      {
        buildName();
        modified = true;
      }
    }

    public void removeAlias(String anAlias)
    {
      if (aliases.remove(anAlias))
      {
        buildName();
        modified = true;
      }
    }

    public int compareTo(WatchedVariable aWatchedVariable)
    {
      return variable.compareTo(aWatchedVariable.getVariable());
    }

    protected void buildName()
    {
      StringBuilder builder = new StringBuilder();
      boolean first = true;
      for (String alias : aliases)
      {
        if (first)
        {
          first = false;
        }
        else
        {
          builder.append("; ");
        }
        builder.append(alias);
      }
      name = builder.toString();
    }

    protected String name;
    protected TreeSet<String> aliases = new TreeSet<String>();
    protected Variable variable;
    protected int index;
    protected boolean watchpoint;
  }

  protected ASTProgram parsedProgram = null;
  protected Fx702pBasicSourceCode basicSourceCode = null;
  protected String name;
  protected String password;
  protected boolean empty = true;
  protected HashSet<BasicInstructionIndex> breakpoints = new HashSet<BasicInstructionIndex>();
  protected boolean modified = false;
  protected File programFile;
  protected Vector<WatchedVariable> watchedVariables = new Vector<WatchedVariable>();
  protected boolean suspendOnStatVariables = false;
  protected boolean hasComments = false;
  protected boolean saveVariables = false;
  protected boolean saveBreakpoints = false;

  static public final String SIGNATURE_TEXT = "SIGNATURE";
  static public final String BREAKPOINT_TEXT = "BREAKPOINT";
  static public final String WATCH_TEXT = "WATCH";
  static public final String WATCHPOINT_TEXT = "WATCHPOINT";
  static public final String STATWATCHPOINT_TEXT = "STATWATCHPOINT";
}
