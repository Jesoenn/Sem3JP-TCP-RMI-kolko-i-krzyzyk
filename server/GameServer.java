package server;

import server.models.Room;
import server.models.User;
import shared.IGameServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameServer implements IGameServer {
    private ArrayList<User> users;
    private ArrayList<Room> rooms;

    public GameServer() {
        System.out.println("Server created.");
        users = new ArrayList<>();
        rooms = new ArrayList<>();
    }

    @Override
    public boolean connect(String username) throws RemoteException {
        System.out.print("New user ");
        ArrayList<String> usernames = new ArrayList<>();
        for(User user: users) {
            usernames.add(user.getUsername());
        }

        if(!usernames.contains(username)){
            users.add(new User(username));
            System.out.println(username+" connected.");
            return true;
        }
        else{
            System.out.println(username+". Connection rejected. User is already logged in.");
            return false;
        }
    }

    @Override
    public int createRoom(String username) throws RemoteException {
        for(User user: users) {
            if(user.getUsername().equals(username) && user.getRoom()==-1){
                Room newRoom = new Room(user);
                user.setRoom(newRoom.getId());
                rooms.add(newRoom);
                System.out.println(username+" created a room <"+user.getRoom()+">");
                return user.getRoom();
            }
        }
        System.out.println(username+": creating room failed");
        return -1;
    }

    @Override
    public boolean joinRoom(String username, int roomId) throws RemoteException {
        User player = searchUser(username);
        Room room;
        if(player != null && player.getRoom()==-1){
            room = searchRoom(roomId);

            if(room!= null && room.getPlayers().size()<2){
                room.addPlayer(player);
                player.setRoom(room.getId());
                System.out.println(username+" joined room <"+roomId+">");
                return true;
            }
            else
                System.out.println(username + " failed to join room <"+roomId+">");
        }
        return false;
    }

    @Override
    public String getOpponentUsername(String username) throws RemoteException {
        User player = searchUser(username);
        Room room = searchRoom(Objects.requireNonNull(player).getRoom());
        for(User user: room.getPlayers() ){
            if(!user.getUsername().equals(username))
                return user.getUsername();
        }
        return null;
    }

    @Override
    public boolean isRoomGameActive(String username) throws RemoteException {
        User player = searchUser(username);
        if(player == null)
            return false;
        Room room = searchRoom(player.getRoom());
        if(room == null)
            return false;

        return room.isGameActive();
    }

    @Override
    public void leaveRoom(String username) throws RemoteException {
        User user = searchUser(username);
        Room room;
        if(user == null)
            return;
        room = searchRoom(user.getRoom());
        if(room == null)
            return;
        user.setRoom(-1);
        room.removePlayer(user);
        user.setNotReady();
        if(room.isGameActive()){
            System.out.println(username+" left active room <"+room.getId()+">");
            user.addLose();
            room.getPlayers().getFirst().addWin();
            System.out.println(room.getPlayers().getFirst().getUsername()+" has won in room <"+room.getId()+">");
            room.endGame();
            //Koncze gre jakos jeszcze
        }
        else{
            System.out.println(username+" left room <"+room.getId()+">");
        }
        if(!room.getPlayers().isEmpty()){
            room.getPlayers().getFirst().setNotReady();
        }
        else
            deleteRoom(room);

        //Uwzglednione:
        //Uzytkownik ma pokoj -1
        //Pokoj usuwa uzytkownika
        //Jezeli aktywna gra to lose i win przydzielony, koniec gry
        //Jezeli lobby puste to usuwane jest
        //Jezeli dal ze jest gotowy, to juz nie jest


        //Przy wyjsciu
        // aktualizuje pokoj, gra nieaktywna, aktualizuje klienta.
    }

    @Override
    public void setReady(String username) throws RemoteException {
        System.out.println(username+" is ready");
        User user = searchUser(username);
        if(user == null)
            return;
        user.setReady();
        readyRoomCheck(user.getRoom());
    }

    @Override
    public boolean isReady(String username) throws RemoteException {
        return Objects.requireNonNull(searchUser(username)).isReady();
    }

    @Override
    public String[][] getBoard(String username) throws RemoteException {
        User player = searchUser(username);
        Room room = searchRoom(player.getRoom());
        return room.getBoard();
    }

    //0 tile taken/wrong tile
    //1 not your turn
    //2 turn completed
    //3 win
    //4 draw
    @Override
    public int makeMove(String username, int row, int column) throws RemoteException {
        if(checkBoardValues(row) || checkBoardValues(column))
            return 0;
        User player = Objects.requireNonNull(searchUser(username));
        Room room = Objects.requireNonNull(searchRoom(player.getRoom()));
        if(!room.isPlayersTurn(player))
            return 1;
        boolean moveCompleted = room.makeMove(row, column, player.getSymbol());
        if(moveCompleted){
            if(room.checkWin(player.getSymbol())){
                System.out.println("User "+username+" has won in Room <"+player.getRoom()+">");
                player.addWin();
                for(User user: room.getPlayers()){
                    user.setNotReady();
                    if(!user.getUsername().equals(username))
                        user.addLose();
                }
                room.endGame();
                return 3;
            }
            else if(room.checkDraw()){
                System.out.println("There has been a draw in Room <"+player.getRoom()+">");
                room.endGame();
                for(User user: room.getPlayers()){
                    user.setNotReady();
                    user.addDraw();
                }
                return 4;
            }
            return 2;
        }
        return 0;
    }

    @Override
    public boolean isPlayersTurn(String username) throws RemoteException {
        User player = Objects.requireNonNull(searchUser(username));
        Room room = Objects.requireNonNull(searchRoom(player.getRoom()));
        return room.isPlayersTurn(player);
    }

    @Override
    public void leave(String username) throws RemoteException {
        User user = searchUser(username);
        if(user == null)
            return;
        System.out.println(username+" left the game.");
        //Not in any room
        if(user.getRoom() == -1){
            users.remove(user);
            return;
        }
        Room room = searchRoom(user.getRoom());
        if(room == null)
            return;
        //In active game
        if(room.isGameActive()){
            room.removePlayer(user);
            User winner = room.getPlayers().getFirst();
            System.out.println("User "+winner.getUsername()+" has won in Room <"+user.getRoom()+">");
            winner.setNotReady();
            room.endGame();
        }
        //If was in room alone
        else if(room.getPlayers().size() == 1)
            deleteRoom(room);
        //If was in room with somebody
        else{
            room.removePlayer(user);
            room.getPlayers().getFirst().setNotReady();
        }
        users.remove(user);
    }

    @Override
    public ArrayList<String> getAvailableRoomList() {
        ArrayList<String> roomIds = new ArrayList<>();
        for(Room room: rooms){
            if(room.getPlayers().size()<2)
                roomIds.add(String.valueOf(room.getId()));
        }
        return roomIds;
    }

    @Override
    public HashMap<String, ArrayList<Integer>> getPlayerStats() throws RemoteException {
        HashMap<String, ArrayList<Integer>> playerStats=new HashMap<>();
        for(User user: users){
            ArrayList<Integer> stats = new ArrayList<>();
            stats.add(user.getWins());
            stats.add(user.getDraws());
            stats.add(user.getLoses());
            playerStats.put(user.getUsername(),stats);
        }
        return playerStats;
    }


    private void readyRoomCheck(int id){
        Room room = searchRoom(id);
        if(room == null)
            return;
        if(room.getPlayers().size()==2){
            for(User user: room.getPlayers()){
                if(!user.isReady())
                    return;
            }
            room.startGame();
            System.out.println("Room <"+id+"> is ready. Starting game.");
        }
    }

    private User searchUser(String username){
        for(User user: users)
            if(user.getUsername().equals(username))
                return user;
        return null;
    }

    private Room searchRoom(int roomId){
        for(Room room: rooms)
            if(room.getId()==roomId)
                return room;
        return null;
    }

    private void deleteRoom(Room room){
        System.out.println("Room <"+room.getId()+"> deleted.");
        rooms.remove(room);
    }
    private boolean checkBoardValues(int value){
        return value <= 2 && value >= 0;
    }
    public void gameEndOperations(){

    }
}
