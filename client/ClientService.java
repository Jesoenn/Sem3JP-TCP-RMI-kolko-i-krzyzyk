package client;

import client.chat.ChatConnection;
import client.chat.ChatReceiverThread;
import shared.IGameServer;

import java.lang.reflect.Array;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientService {
    private IGameServer server;
    private String username, opponentUsername;
    private boolean activeGame, ready, playersTurn;
    private String[][] board;
    private int roomId;
    private ChatConnection chat;
    private UI ui;
    public ClientService(IGameServer server, String username) throws RemoteException {
        this.server = server;
        this.username = username;
        ui = new UI();
        chat = new ChatConnection();
        addUserIP();
        Runtime.getRuntime().addShutdownHook(new Thread(this::deleteUser));

        start();
    }
    private void start() throws RemoteException {
        UI.Display currentFrame = UI.Display.MAIN_MENU;
        while(true){
            ui.verticalSpacer();
            currentFrame = switch(currentFrame){
                case MAIN_MENU -> ui.viewMainMenu();
                case CREATE_ROOM -> {
                    createRoom();
                    yield UI.Display.ROOM; }
                case JOIN_ROOM -> {
                    if(!joinRoom())
                        yield ui.errorStatus(UI.Display.JOIN_ROOM);
                    yield UI.Display.ROOM;
                }
                case ROOM_LIST -> ui.viewRooms(getAvailableRooms());
                case PLAYER_STATS -> ui.viewPlayerStats(getPlayerStats());
                //CO KAZDE ODPALENIE UPDATE CZY KTOS DOLACZYL ITD
                case ROOM -> {
                    getGameInfo();
                    yield ui.viewRoomInfo(username,opponentUsername,String.valueOf(roomId),activeGame,ready, board,playersTurn);}
                case SEND_MESSAGE -> {
                    sendMessage();
                    yield UI.Display.ROOM;
                }
                case MAKE_MOVE -> {
                    int answer = makeMove();
                    if(answer == 2)
                        yield UI.Display.ROOM;
                    else if (answer == 0){
                        ui.errorStatus(UI.Display.TILE_TAKEN);
                        yield UI.Display.ROOM;
                    }
                    else if(answer == 3)
                        ui.errorStatus(UI.Display.WINNER);
                    yield UI.Display.ROOM;
                }
                case LEAVE_ROOM -> { leaveRoom(); yield ui.viewMainMenu(); }
                case MARK_READY -> { markReady(); yield UI.Display.ROOM; }
                default -> { System.out.println("Unexpected error."); yield ui.viewMainMenu(); }
            };
        }
    }
    private int makeMove() throws RemoteException{
        int[] move = ui.getMove();
        return server.makeMove(username,move[0],move[1]);
    }
    private void createRoom() throws RemoteException {
        roomId=server.createRoom(username);
        startReceivingMessages();
    }
    private boolean joinRoom() throws RemoteException {
        boolean success = server.joinRoom(username, ui.getRoomId());
        if(success){
            roomId = ui.getRoomId();
            startReceivingMessages();
        }
        return success;
    }
    private void leaveRoom() throws RemoteException {
        chat.endConnection();
        roomId = -1;
        server.leaveRoom(username);
    }
    private void markReady() throws RemoteException {
        server.setReady(username);
    }
    private void isPlayerReady() throws RemoteException {
        ready = server.isReady(username);
    }
    private void sendMessage() {
        String message = username+": "+ui.getMessage();
        chat.sendMessage(message);

    }
    private void isPlayersTurn() throws RemoteException {
        playersTurn = server.isPlayersTurn(username);
    }

    private void getOpponentUsername() throws RemoteException {
        opponentUsername = server.getOpponentUsername(username);
    }
    private void getGameInfo() throws RemoteException {
        if(server.isRoomFull(username) && !chat.isConnectionEstablished()){
            chat.connectToOpponent(server.getOpponentIP(username),roomId);
        }
        if(!server.isRoomFull(username) && chat.isServerWorking()){
            chat.endConnection();
            startReceivingMessages();
        }
        ui.viewMessages(chat.getMessages());
        getOpponentUsername();
        isPlayerReady();
        isPlayersTurn();
        activeGame=server.isRoomGameActive(username);
        if(activeGame){
            board=server.getBoard(username);
        }
    }
    private void deleteUser() {
        try {
            server.leave(username);
            chat.endConnection();
        } catch (RemoteException e) {
            System.out.println("Error leaving the game.");
        }
    }
    private void addUserIP() throws RemoteException {
        server.addUserIP(username,chat.getLocalIP());
    }
    private ArrayList<String> getAvailableRooms() throws RemoteException{
        return server.getAvailableRoomList();
    }
    private HashMap<String, ArrayList<String>> getPlayerStats() throws RemoteException{
        HashMap<String, ArrayList<Integer>> playerStats = server.getPlayerStats();
        HashMap<String, ArrayList<String>> formattedPlayerStats = new HashMap<>();
        for(Map.Entry<String, ArrayList<Integer>> set: playerStats.entrySet()) {
            ArrayList<Integer> values = set.getValue();
            ArrayList<String> formattedValues = new ArrayList<>();
            for(int value: values)
                formattedValues.add(String.valueOf(value));
            formattedPlayerStats.put(set.getKey(),formattedValues);
        }
        return formattedPlayerStats;
    }
    private void startReceivingMessages(){
        ChatReceiverThread receiverThread = new ChatReceiverThread(chat, roomId);
        receiverThread.start();
    }
}
