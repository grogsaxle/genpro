package nl.bluevoid.genpro.example.tictactoe;

public enum Player {
  CROSS("X"), CIRCLE("O");
  final String symbol;

  private Player(String symbol) {
    this.symbol = symbol;
  }
}
