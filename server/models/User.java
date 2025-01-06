package server.models;

public class User {
    private String username;
    private int wins,draws,loses;
    private int room;
    private boolean ready;
    private Board.Symbol symbol;
    public User(String username){
        this.username = username;
        room = -1;
        wins = 0;
        draws = 0;
        loses = 0;
        ready = false;
    }


    public String getUsername() {
        return username;
    }
    public void setRoom(int room) {
        this.room = room;
    }
    public int getRoom() {
        return room;
    }
    public int getWins() {
        return wins;
    }
    public int getDraws() {
        return draws;
    }
    public int getLoses() {
        return loses;
    }
    public void addWin(){
        wins++;
    }
    public void addDraw(){
        draws++;
    }
    public void addLose(){
        loses++;
    }
    public boolean isReady() {
        return ready;
    }
    public void setReady() {
        ready = true;
    }
    public void setNotReady() {
        ready = false;
    }
    public Board.Symbol getSymbol() {
        return symbol;
    }
    public void setSymbol(Board.Symbol symbol) {
        this.symbol = symbol;
    }

}
