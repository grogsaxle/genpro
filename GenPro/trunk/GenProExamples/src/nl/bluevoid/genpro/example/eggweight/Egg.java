package nl.bluevoid.genpro.example.eggweight;

public class Egg {
  // The resulting formula for egg volume, V, was V =
  // (0.6057 - 0.0018B)LB2 in which L is the egg length in
  // millimeters, and B is the egg maximum breadth in millimeters.
  public static double volume(double l, double b) {
    return (0.6057 - 0.0018 * b) * l * b * b;
  }
}