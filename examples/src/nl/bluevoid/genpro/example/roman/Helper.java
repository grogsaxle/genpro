package nl.bluevoid.genpro.example.roman;

public class Helper {
  public static int getValue(char c) {
    switch (c) {
    case 'X':
      return 10;
    case 'V':
      return 5;
    case 'I':
      return 1;
    case 'M':
      return 1000;
    case 'C':
      return 100;
    case 'D':
      return 500;
    case 'L':
      return 50;
    default:
      throw new IllegalStateException("unsupported character:" + c);
    }
  }
}
