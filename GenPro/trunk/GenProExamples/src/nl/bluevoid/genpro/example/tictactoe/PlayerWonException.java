package nl.bluevoid.genpro.example.tictactoe;

@SuppressWarnings("serial")
public class PlayerWonException extends Exception {

  public final Player p;

  public PlayerWonException(Player p) {
    this.p = p;
    // TODO Auto-generated constructor stub
  }

}
