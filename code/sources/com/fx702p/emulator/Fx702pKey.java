package com.fx702p.emulator;

import com.fx702p.emulator.commands.*;
import static com.fx702p.emulator.Fx702pKeyConstants.*;

public enum Fx702pKey
{
  A("A", "LOG ", "IF "),
  B("B", "LN ", "THEN "),
  C("C", "EXP ", "GOTO "),
  D("D", "SQR ", "GSB "),
  E("E", "SGN ", "RET "),
  F("F", "INT ", "INP "),
  G("G", "FRAC ", "WAIT "),
  H("H", "ABS ", "RPC "),
  I("I", "RND(", "PRC "),
  J("J", "DEG(", "DMS "),
  K("K", "LEN(", "SET "),
  L("L", "CSR ", "VAC "),
  M("M", "MID(", "STOP "),
  N("N", "KEY ", "END "),
  O("O", "SDX ", "SAVE "),
  P("P", "SDY ", "LOAD "),
  Q("Q", "SDXN ", "PUT "),
  R("R", "SDYN ", "GET "),
  S("S", "LRA ", "VER "),
  T("T", "LRB ", "DEFM "),
  U("U", "COR ", "PASS "),
  V("V", "EOX ", "RUN "),
  W("W", "EOY ", "LIST "),
  X,
  Y,
  Z,
  DIGIT_0(StringCommand("0"), RunProgram(0), RunModeCommand.RUN_MODE_COMMAND),
  DIGIT_1(StringCommand("1"), RunProgram(1), WrtModeCommand.WRT_MODE_COMMAND),
  DIGIT_2(StringCommand("2"), RunProgram(2), TraceOnModeCommand.TRACE_ON_MODE_COMMAND),
  DIGIT_3(StringCommand("3"), RunProgram(3), TraceOffModeCommand.TRACE_OFF_MODE_COMMAND),
  DIGIT_4(StringCommand("4"), RunProgram(4), DegModeCommand.DEG_MODE_COMMAND),
  DIGIT_5(StringCommand("5"), RunProgram(5), RadModeCommand.RAD_MODE_COMMAND),
  DIGIT_6(StringCommand("6"), RunProgram(6), GraModeCommand.GRA_MODE_COMMAND),
  DIGIT_7(StringCommand("7"), RunProgram(7), PrintOnModeCommand.PRINT_ON_MODE_COMMAND),
  DIGIT_8(StringCommand("8"), RunProgram(8), PrintOffModeCommand.PRINT_OFF_MODE_COMMAND),
  DIGIT_9(StringCommand("9"), RunProgram(9)),
  SPACE(" "),
  PLUS(ReuseResultCommand("+"), StringCommand("?"), NULL_COMMAND),
  MINUS(ReuseResultCommand("-"), StringCommand("!"), NULL_COMMAND),
  STAR(ReuseResultCommand("*"), StringCommand("~"), NULL_COMMAND), // Not equal symbol is ~
  SLASH(ReuseResultCommand("/"), StringCommand("}"), NULL_COMMAND), // Lower or Equal is {
  POWER(ReuseResultCommand("^"), StringCommand("{"), NULL_COMMAND), // Upper arrow is ^, Greater or Equal is }
  COMMA(StringCommand(","), SacCommand.SAC_COMMAND),
  DECIMAL_POINT(".", "RAN#"),
  DOUBLE_QUOTE(StringCommand("\""), ArcCommand.ARC_COMMAND, StringCommand("FOR "), NULL_ARC_COMMAND, ArcCommand.ARC_COMMAND, ArcCommand.ARC_COMMAND,
      ArcCommand.ARC_COMMAND),
  SHARP(StringCommand("#"), HypCommand.HYP_COMMAND, StringCommand("TO "), NULL_ARC_COMMAND, HypCommand.HYP_COMMAND, HypCommand.HYP_COMMAND,
      HypCommand.HYP_COMMAND),
  COLON(":", "COS ", "NEXT ", "ACS ", "HCS ", "AHC "),
  DOLLAR("$", "SIN ", "STEP ", "ASN ", "HSN ", "AHS "),
  SEMICOLON(";", "TAN ", "PRT ", "ATN ", "HTN ", "AHT "),
  EQUAL("="),
  GREATER(">"),
  LOWER("<"),
  GREATER_OR_EQUAL("}"),
  LOWER_OR_EQUAL("{"),
  DIFFERENT("~"),
  EXCLAMATION("!"),
  INTERROGATION("?"),
  PI("p"),
  EXPONENT("e", "p"),
  LEFT_PARENTHESIS("(", "<"),
  RIGHT_PARENTHESIS(")", ">"),
  LEFT_ARROW(LeftArrowCommand.LEFT_ARROW_COMMAND, HomeCommand.HOME_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND,
      NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  RIGHT_ARROW(RightArrowCommand.RIGHT_ARROW_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND,
      NULL_KEY_COMMAND),
  F1(F1Command.F1_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  F2(F2Command.F2_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  MODE(ModeCommand.MODE_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  CONT(ContCommand.CONT_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  STOP(StopCommand.STOP_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND,
      StopCommand.STOP_COMMAND),
  EXE(ExeCommand.EXE_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  CLEAR(ClearCommand.CLEAR_COMMAND, InsertCommand.INSERT_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND,
      NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  ALL_CLEAR(AllClearCommand.ALL_CLEAR_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND, NULL_ARC_HYP_COMMAND,
      AllClearCommand.ALL_CLEAR_COMMAND),
  STAT(StatCommand.STAT_COMMAND, DelCommand.DEL_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND,
      NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND),
  ANS(AnswerCommand.ANSWER_COMMAND, AStatCommand.ASTAT_COMMAND, NULL_F2_COMMAND, NULL_MODE_COMMAND, NULL_ARC_COMMAND, NULL_HYP_COMMAND,
      NULL_ARC_HYP_COMMAND, NULL_KEY_COMMAND);

  private Command command = null;
  private Command f1Command = null;
  private Command f2Command = null;
  private Command modeCommand = null;
  private Command arcCommand = null;
  private Command hypCommand = null;
  private Command arcHypCommand = null;
  private Command keyCommand = null;

  Fx702pKey()
  {
    command = StringCommand(toString());
  }

  Fx702pKey(String aString)
  {
    this(StringCommand(aString));
  }

  Fx702pKey(String aString, String aF1String)
  {
    this(StringCommand(aString), StringCommand(aF1String));
  }

  Fx702pKey(String aString, String aF1String, String aF2String)
  {
    this(StringCommand(aString), StringCommand(aF1String), StringCommand(aF2String), null, null, null, null);
  }

  Fx702pKey(String aString, String aF1String, String aF2String, String anArcString, String anHypString, String anArcHypString)
  {
    this(StringCommand(aString), StringCommand(aF1String), StringCommand(aF2String), null, StringCommand(anArcString), StringCommand(anHypString), StringCommand(anArcHypString));
  }

  Fx702pKey(Command aCommand)
  {
    this(aCommand, null, null, null, null, null, null);
  }

  Fx702pKey(Command aCommand, Command aF1Command)
  {
    this(aCommand, aF1Command, null, null, null, null, null);
  }

  Fx702pKey(Command aCommand, Command aF1Command, Command aModeCommand)
  {
    this(aCommand, aF1Command, null, aModeCommand, null, null, null);
  }


  Fx702pKey(Command aCommand, Command aF1Command, Command aF2Command, Command aModeCommand, Command anArcCommand, Command anHypCommand, Command anArcHypCommand)
  {
    this(aCommand, aF1Command, aF2Command, aModeCommand, anArcCommand, anHypCommand, anArcHypCommand, aCommand);
  }

  Fx702pKey(Command aCommand, Command aF1Command, Command aF2Command, Command aModeCommand, Command anArcCommand, Command anHypCommand, Command anArcHypCommand, Command aKeyCommand)
  {
    command = aCommand;
    f1Command = aF1Command;
    f2Command = aF2Command;
    modeCommand = aModeCommand;
    arcCommand = anArcCommand;
    hypCommand = anHypCommand;
    arcHypCommand = anArcHypCommand;
    keyCommand = aKeyCommand;
  }

  public Command getCommand()
  {
    return command;
  }

  public Command getF1Command()
  {
    return f1Command;
  }

  public Command getF2Command()
  {
    return f2Command;
  }

  public Command getModeCommand()
  {
    return modeCommand;
  }

  public Command getArcCommand()
  {
    return arcCommand;
  }

  public Command getHypCommand()
  {
    return hypCommand;
  }

  public Command getArcHypCommand()
  {
    return arcHypCommand;
  }

  public Command getKeyCommand()
  {
    return keyCommand;
  }

  static public Command StringCommand(String aString)
  {
    return new StringCommand(aString);
  }

  static public Command RunProgram(int aProgramIndex)
  {
    return new RunNumberedProgramCommand(aProgramIndex);
  }
  
  static public Command ReuseResultCommand(String aString)
  {
    return new ReuseResultCommand(aString);
  }


  static public Fx702pKey convertCharToFx702pKey(char aChar)
  {
    switch (aChar)
    {
      case 'a':
      case 'A':
      {
        return A;
      }
      case 'b':
      case 'B':
      {
        return B;
      }
      case 'c':
      case 'C':
      {
        return C;
      }
      case 'd':
      case 'D':
      {
        return D;
      }
      case 'e':
      case 'E':
      {
        return E;
      }
      case 'f':
      case 'F':
      {
        return F;
      }
      case 'g':
      case 'G':
      {
        return G;
      }
      case 'h':
      case 'H':
      {
        return H;
      }
      case 'i':
      case 'I':
      {
        return I;
      }
      case 'j':
      case 'J':
      {
        return J;
      }
      case 'k':
      case 'K':
      {
        return K;
      }
      case 'l':
      case 'L':
      {
        return L;
      }
      case 'm':
      case 'M':
      {
        return M;
      }
      case 'n':
      case 'N':
      {
        return N;
      }
      case 'o':
      case 'O':
      {
        return O;
      }
      case 'p':
      case 'P':
      {
        return P;
      }
      case 'q':
      case 'Q':
      {
        return Q;
      }
      case 'r':
      case 'R':
      {
        return R;
      }
      case 's':
      case 'S':
      {
        return S;
      }
      case 't':
      case 'T':
      {
        return T;
      }
      case 'u':
      case 'U':
      {
        return U;
      }
      case 'v':
      case 'V':
      {
        return V;
      }
      case 'w':
      case 'W':
      {
        return W;
      }
      case 'x':
      case 'X':
      {
        return X;
      }
      case 'y':
      case 'Y':
      {
        return Y;
      }
      case 'z':
      case 'Z':
      {
        return Z;
      }
      case '0':
      {
        return DIGIT_0;
      }
      case '1':
      {
        return DIGIT_1;
      }
      case '2':
      {
        return DIGIT_2;
      }
      case '3':
      {
        return DIGIT_3;
      }
      case '4':
      {
        return DIGIT_4;
      }
      case '5':
      {
        return DIGIT_5;
      }
      case '6':
      {
        return DIGIT_6;
      }
      case '7':
      {
        return DIGIT_7;
      }
      case '8':
      {
        return DIGIT_8;
      }
      case '9':
      {
        return DIGIT_9;
      }
      case ' ':
      {
        return SPACE;
      }
      case '+':
      {
        return PLUS;
      }
      case '-':
      {
        return MINUS;
      }
      case '*':
      {
        return STAR;
      }
      case '/':
      {
        return SLASH;
      }
      case ',':
      {
        return COMMA;
      }
      case '.':
      {
        return DECIMAL_POINT;
      }
      case ':':
      {
        return COLON;
      }
      case ';':
      {
        return SEMICOLON;
      }
      case '"':
      {
        return DOUBLE_QUOTE;
      }
      case '#':
      {
        return SHARP;
      }
      case '$':
      {
        return DOLLAR;
      }
      case '=':
      {
        return EQUAL;
      }
      case '>':
      {
        return GREATER;
      }
      case '<':
      {
        return LOWER;
      }
      case '}':
      case '\007':
      {
        return GREATER_OR_EQUAL;
      } // Ctrl-G
      case '{':
      case '\014':
      {
        return LOWER_OR_EQUAL;
      } // Ctrl-L
      case '~':
      case '\004':
      {
        return DIFFERENT;
      } // Crtl-D
      case '!':
      {
        return EXCLAMATION;
      }
      case '?':
      {
        return INTERROGATION;
      }
      case '\020':
      {
        return PI;
      } // Ctrl-P
      case '\005':
      {
        return EXPONENT;
      } // Ctrl-E
      case '(':
      {
        return LEFT_PARENTHESIS;
      }
      case ')':
      {
        return RIGHT_PARENTHESIS;
      }
      case '^':
      {
        return POWER;
      }
      case '\002':
      {
        return LEFT_ARROW;
      } // Ctrl-B, Emacs binding
      case '\006':
      {
        return RIGHT_ARROW;
      } // Ctrl-F, Emacs binding
        // No bindings for F1, F2 & MODE as they are prefix keys
        // case ' ': { return F1; }
        // case ' ': { return F2; }
        // case ' ': { return MODE; }
      case '\021':
      {
        return CONT;
      } // Ctrl-Q
      case '\023':
      {
        return STOP;
      } // Ctrl-S
      case '\n':
      {
        return EXE;
      }
      case '\010':
      {
        return CLEAR;
      } // Ctrl-H
      case '\003':
      {
        return ALL_CLEAR;
      } // Ctrl-C
      case '\024':
      {
        return STAT;
      } // Ctrl-T
      case '\001':
      {
        return ANS;
      } // Ctrl-A
      case '\017':
      {
        return MODE;
      } // Ctrl-O
      case '\031':
      {
        return F1;
      } // Ctrl-Y
      case '\032':
      {
        return F2;
      } // Ctrl-Z

    }
    return null;
  }
}
