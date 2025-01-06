package server.models;

import java.util.ArrayList;

public class Room {
    private static int roomCount = 0;
    private boolean gameActive;
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
        return board.checkWin(symbol);
    }
    public boolean checkDraw(){
        return board.checkDraw();
    }
    public boolean isPlayersTurn(User user){
        return whoseTurn == user;
    }
}
