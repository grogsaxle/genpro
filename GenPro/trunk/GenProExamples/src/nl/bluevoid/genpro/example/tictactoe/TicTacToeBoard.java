package nl.bluevoid.genpro.example.tictactoe;

public class TicTacToeBoard {

  private static final int MAX_ROW_COLUMN = 3;
  private static final int MAX_MOVES = MAX_ROW_COLUMN * MAX_ROW_COLUMN;
  private Player[][] board = new Player[MAX_ROW_COLUMN][MAX_ROW_COLUMN];
  private int movesDone = 0;

  private Player turn = Player.CROSS;

  public TicTacToeBoard() {
  }

  public boolean isOccupied(int x, int y) {
    return board[x][y] != null;
  }

  public boolean isFree(int x, int y) {
    return board[x][y] == null;
  }

  public boolean isOwnColor(int x, int y, Player p) {
    return p.equals(board[x][y]);
  }

  public void putStone(final int x, final int y, final Player p) throws PlayerWonException,
      ItIsADrawException {
    if (turn.equals(p)) {
      switch (p) {
      case CIRCLE:
        turn = Player.CROSS;
        break;
      case CROSS:
        turn = Player.CIRCLE;
      }
    } else {
      throw new IllegalStateException("Wrong turn! should be " + turn + " instead of " + p);
    }
    if (isFree(x, y)) {
      board[x][y] = p;
      movesDone++;
      checkWin(p);
      checkDraw();
    } else {
      throw new IllegalStateException("place taken x,y " + x + ", " + y);
    }
  }

  private void checkDraw() throws ItIsADrawException {
    if (movesDone == MAX_MOVES)
      throw new ItIsADrawException();
  }

  private void checkWin(Player p) throws PlayerWonException {
    if (movesDone < MAX_ROW_COLUMN)
      return;
    // check vertical
    for (int x = 0; x < MAX_ROW_COLUMN; x++) {
      for (int y = 0; y < MAX_ROW_COLUMN; y++) {
        if (board[x][y] != p) {
          break;
        }
        if (y == MAX_ROW_COLUMN - 1) {
          throw new PlayerWonException(p);
        }
      }
    }
    // check horizontal
    for (int y = 0; y < MAX_ROW_COLUMN; y++) {
      for (int x = 0; x < MAX_ROW_COLUMN; x++) {
        if (board[x][y] != p) {
          break;
        }
        if (x == MAX_ROW_COLUMN - 1) {
          throw new PlayerWonException(p);
        }
      }
    }
    // check diagonal
    for (int xy = 0; xy < MAX_ROW_COLUMN; xy++) {
      if (board[xy][xy] != p) {
        break;
      }
      if (xy == MAX_ROW_COLUMN - 1) {
        throw new PlayerWonException(p);
      }
    }
    // check diagonal
    for (int xy = 0; xy < MAX_ROW_COLUMN; xy++) {
      if (board[xy][MAX_ROW_COLUMN - 1 - xy] != p) {
        break;
      }
      if (xy == MAX_ROW_COLUMN - 1) {
        throw new PlayerWonException(p);
      }
    }
  }

  public String getBoard() {
    StringBuffer b = new StringBuffer();
    for (int x = 0; x < MAX_ROW_COLUMN; x++) {
      for (int y = 0; y < MAX_ROW_COLUMN; y++) {
        Player p = board[x][y];
        if (p == null) {
          b.append("  ");
        } else {
          b.append(p.symbol + " ");
        }
      }
      b.append("\n");
    }
    return b.toString();
  }

  public void printBoard() {
    System.out.println(getBoard());
  }

}