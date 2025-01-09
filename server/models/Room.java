package server.models;

import java.util.ArrayList;

public class Room {
    private static int roomCount = 0;
    private boolean gameActive;
    private boolean gameJustEnded;
    private Board.Symbol winner;
    private int checks;
    private final int id;
    private ArrayList<User> players;
    private User whoseTurn;
    private Board board;
    public Room(User creator){
        roomCount++;
        gameActive = false;
        this.id = roomCount;
        players = new ArrayList<>();
        players.add(creator);
    }

    public void addPlayer(User player){
        players.add(player);
    }
    public void removePlayer(User player){
        players.remove(player);
    }
    public int getId(){
        return id;
    }
    public ArrayList<User> getPlayers(){
        return players;
    }
    public boolean isGameActive(){
        return gameActive;
    }
    public void startGame(){
        gameActive = true;
        whoseTurn = players.getFirst();
        players.getFirst().setSymbol(Board.Symbol.X);
        players.getLast().setSymbol(Board.Symbol.O);
        board = new Board();
    }
    public void endGame(){
        gameActive = false;
        whoseTurn = null;
        gameJustEnded = true;
        checks=2;
    }
    public boolean makeMove(int row, int column, Board.Symbol symbol){
        boolean moveSuccess=board.makeMove(row, column, symbol);
        if(moveSuccess){
            if(whoseTurn == players.getFirst())
                whoseTurn = players.getLast();
            else whoseTurn = players.getFirst();
        }
        return moveSuccess;
    }
    public String[][] getBoard(){
        return board.getBoard();
    }
    public boolean checkWin(Board.Symbol symbol){
        boolean win = board.checkWin(symbol);
        if(win){
            gameJustEnded = true;
            winner = symbol;
            return true;
        }
        return false;
    }
    public boolean checkDraw(){
        return board.checkDraw();
    }
    public boolean isPlayersTurn(User user){
        return whoseTurn == user;
    }
    public int isWinner(User user){
        // 0 draw
        // 1 win
        // 2 lose
        if(checks>0){
            int toReturn=-1;
            if(winner == null)
                toReturn=0;
            else if(user.getSymbol() == winner)
                toReturn=1;
            else if(user.getSymbol() != winner)
                toReturn=2;
            checks--;
            if(checks==0)
                winner=null;
            return toReturn;
        }
        return -1;
    }
    public boolean getGameJustEnded(){
        return gameJustEnded;
    }
    public void setGameJustEndedFalse(){
        gameJustEnded = false;
    }
}
