package com.fx702p.emulator;

import static com.fx702p.emulator.Fx702pKey.A;
import static com.fx702p.emulator.Fx702pKey.ALL_CLEAR;
import static com.fx702p.emulator.Fx702pKey.ANS;
import static com.fx702p.emulator.Fx702pKey.B;
import static com.fx702p.emulator.Fx702pKey.C;
import static com.fx702p.emulator.Fx702pKey.CLEAR;
import static com.fx702p.emulator.Fx702pKey.COLON;
import static com.fx702p.emulator.Fx702pKey.COMMA;
import static com.fx702p.emulator.Fx702pKey.CONT;
import static com.fx702p.emulator.Fx702pKey.D;
import static com.fx702p.emulator.Fx702pKey.DECIMAL_POINT;
import static com.fx702p.emulator.Fx702pKey.DIGIT_0;
import static com.fx702p.emulator.Fx702pKey.DIGIT_1;
import static com.fx702p.emulator.Fx702pKey.DIGIT_2;
import static com.fx702p.emulator.Fx702pKey.DIGIT_3;
import static com.fx702p.emulator.Fx702pKey.DIGIT_4;
import static com.fx702p.emulator.Fx702pKey.DIGIT_5;
import static com.fx702p.emulator.Fx702pKey.DIGIT_6;
import static com.fx702p.emulator.Fx702pKey.DIGIT_7;
import static com.fx702p.emulator.Fx702pKey.DIGIT_8;
import static com.fx702p.emulator.Fx702pKey.DIGIT_9;
import static com.fx702p.emulator.Fx702pKey.DOLLAR;
import static com.fx702p.emulator.Fx702pKey.DOUBLE_QUOTE;
import static com.fx702p.emulator.Fx702pKey.E;
import static com.fx702p.emulator.Fx702pKey.EQUAL;
import static com.fx702p.emulator.Fx702pKey.EXE;
import static com.fx702p.emulator.Fx702pKey.EXPONENT;
import static com.fx702p.emulator.Fx702pKey.F;
import static com.fx702p.emulator.Fx702pKey.F1;
import static com.fx702p.emulator.Fx702pKey.F2;
import static com.fx702p.emulator.Fx702pKey.G;
import static com.fx702p.emulator.Fx702pKey.H;
import static com.fx702p.emulator.Fx702pKey.I;
import static com.fx702p.emulator.Fx702pKey.J;
import static com.fx702p.emulator.Fx702pKey.K;
import static com.fx702p.emulator.Fx702pKey.L;
import static com.fx702p.emulator.Fx702pKey.LEFT_ARROW;
import static com.fx702p.emulator.Fx702pKey.LEFT_PARENTHESIS;
import static com.fx702p.emulator.Fx702pKey.M;
import static com.fx702p.emulator.Fx702pKey.MINUS;
import static com.fx702p.emulator.Fx702pKey.MODE;
import static com.fx702p.emulator.Fx702pKey.N;
import static com.fx702p.emulator.Fx702pKey.O;
import static com.fx702p.emulator.Fx702pKey.P;
import static com.fx702p.emulator.Fx702pKey.PLUS;
import static com.fx702p.emulator.Fx702pKey.POWER;
import static com.fx702p.emulator.Fx702pKey.Q;
import static com.fx702p.emulator.Fx702pKey.R;
import static com.fx702p.emulator.Fx702pKey.RIGHT_ARROW;
import static com.fx702p.emulator.Fx702pKey.RIGHT_PARENTHESIS;
import static com.fx702p.emulator.Fx702pKey.S;
import static com.fx702p.emulator.Fx702pKey.SEMICOLON;
import static com.fx702p.emulator.Fx702pKey.SHARP;
import static com.fx702p.emulator.Fx702pKey.SLASH;
import static com.fx702p.emulator.Fx702pKey.SPACE;
import static com.fx702p.emulator.Fx702pKey.STAR;
import static com.fx702p.emulator.Fx702pKey.STAT;
import static com.fx702p.emulator.Fx702pKey.STOP;
import static com.fx702p.emulator.Fx702pKey.T;
import static com.fx702p.emulator.Fx702pKey.U;
import static com.fx702p.emulator.Fx702pKey.V;
import static com.fx702p.emulator.Fx702pKey.W;
import static com.fx702p.emulator.Fx702pKey.X;
import static com.fx702p.emulator.Fx702pKey.Y;
import static com.fx702p.emulator.Fx702pKey.Z;

import java.awt.Point;

public class Fx702pKeyboardLayout
{
  static public Fx702pKey getKey(int x, int y)
  {
    assert (x >= 0 && x < LAYOUT_WIDTH && y >= 0 && y < LAYOUT_HEIGHT) : "Invalid key coordinates " + x + ", " + y + "; must be between 0 and " + LAYOUT_WIDTH + ", " + LAYOUT_HEIGHT;

    return LAYOUT[y][x];
  }

  static protected final Fx702pKey[][] LAYOUT =
    {
        {
          F1,
          F2,
          DOUBLE_QUOTE,
          SHARP,
          DOLLAR,
          COLON,
          SEMICOLON,
          MODE,
          LEFT_PARENTHESIS,
          RIGHT_PARENTHESIS,
          POWER,
          CLEAR,
          ALL_CLEAR
        },
        {
          A,
          B,
          C,
          D,
          E,
          F,
          G,
          DIGIT_7,
          DIGIT_8,
          DIGIT_9,
          SLASH,
          STAT,
          STOP
        },
        {
          H,
          I,
          J,
          K,
          L,
          M,
          N,
          DIGIT_4,
          DIGIT_5,
          DIGIT_6,
          STAR,
          ANS,
          CONT
        },
        {
          O,
          P,
          Q,
          R,
          S,
          T,
          U,
          DIGIT_1,
          DIGIT_2,
          DIGIT_3,
          MINUS,
          COMMA,
          LEFT_ARROW
        },
        {
          V,
          W,
          X,
          Y,
          Z,
          EQUAL,
          SPACE,
          DIGIT_0,
          DECIMAL_POINT,
          EXPONENT,
          PLUS,
          EXE,
          RIGHT_ARROW
        },
    };

  static public final int LAYOUT_WIDTH = LAYOUT[0].length;
  static public final int LAYOUT_HEIGHT = LAYOUT.length;

  static public Point F1_KEY_POSITION = new Point(0, 0);
  static public Point F2_KEY_POSITION = new Point(1, 0);
  static public Point MODE_KEY_POSITION = new Point(7, 0);
}
