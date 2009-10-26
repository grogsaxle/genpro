package nl.bluevoid.genpro.example.tictactoe;

import junit.framework.TestCase;

public class TicTacToeTest extends TestCase {
  public void testWinning() throws PlayerWonException, ItIsADrawException {
    {
      TicTacToeBoard tttb = new TicTacToeBoard();
      tttb.putStone(0, 0, Player.CROSS);
      tttb.putStone(2, 0, Player.CIRCLE);
      tttb.putStone(0, 1, Player.CROSS);
      tttb.putStone(1, 2, Player.CIRCLE);
      try {
        tttb.putStone(0, 2, Player.CROSS);
        fail();
      } catch (PlayerWonException e) {
        assertEquals(e.p, Player.CROSS);
      }
    }
    {
      TicTacToeBoard tttb = new TicTacToeBoard();
      tttb.putStone(0, 1, Player.CROSS);
      tttb.putStone(0, 2, Player.CIRCLE);
      tttb.putStone(1, 1, Player.CROSS);
      tttb.putStone(2, 2, Player.CIRCLE);
      try {
        tttb.putStone(2, 1, Player.CROSS);
        fail();
      } catch (PlayerWonException e) {
        assertEquals(e.p, Player.CROSS);
      }
    }
    {
      TicTacToeBoard tttb = new TicTacToeBoard();
      //tttb.putStone(1, 2, Player.CIRCLE);
      tttb.putStone(0, 0, Player.CROSS);
      tttb.putStone(2, 1, Player.CIRCLE);
      tttb.putStone(1, 1, Player.CROSS);
      tttb.putStone(1, 0, Player.CIRCLE);
      try {
        tttb.putStone(2, 2, Player.CROSS);
        fail();
      } catch (PlayerWonException e) {
        assertEquals(e.p, Player.CROSS);
      }
    }
    {
      TicTacToeBoard tttb = new TicTacToeBoard();
      tttb.putStone(0, 2, Player.CROSS);
      tttb.putStone(2, 1, Player.CIRCLE);
      tttb.putStone(1, 1, Player.CROSS);
      tttb.putStone(2, 2, Player.CIRCLE);
      try {
        tttb.putStone(2, 0, Player.CROSS);
        fail();
      } catch (PlayerWonException e) {
        assertEquals(e.p, Player.CROSS);
      }
    }
  }

  public void testWrongTurn() throws PlayerWonException, ItIsADrawException {
    TicTacToeBoard tttb = new TicTacToeBoard();
    tttb.putStone(1, 1, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(2, 2, Player.CIRCLE);
    tttb.printBoard();
    tttb.putStone(2, 1, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(0, 1, Player.CIRCLE);
    tttb.printBoard();
    tttb.putStone(0, 2, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(1, 0, Player.CIRCLE);
    tttb.printBoard();
    tttb.putStone(0, 0, Player.CROSS);
    tttb.printBoard();
    try {
      tttb.putStone(2, 0, Player.CROSS);
      fail();
    } catch (IllegalStateException e) {
    }
  }

  public void testDraw() throws PlayerWonException, ItIsADrawException {
    // X O X
    // O X X
    // O X O
    TicTacToeBoard tttb = new TicTacToeBoard();
    tttb.putStone(1, 1, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(2, 2, Player.CIRCLE);
    tttb.printBoard();
    tttb.putStone(2, 1, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(0, 1, Player.CIRCLE);
    tttb.printBoard();
    tttb.putStone(0, 2, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(1, 0, Player.CIRCLE);
    tttb.printBoard();
    tttb.putStone(0, 0, Player.CROSS);
    tttb.printBoard();
    tttb.putStone(2, 0, Player.CIRCLE);
    tttb.printBoard();
    try {
      tttb.putStone(1, 2, Player.CROSS);
      fail();
    } catch (ItIsADrawException e) {
    }
    tttb.printBoard();
  }
}
