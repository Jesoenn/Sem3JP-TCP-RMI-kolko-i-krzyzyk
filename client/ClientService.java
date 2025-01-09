package client;

import client.chat.ChatConnection;
import client.chat.ChatReceiverThread;
import shared.IGameServer;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ClientService {
    private IGameServer server;
    private String username, opponentUsername;
    private boolean activeGame, ready, playersTurn;
    private int gameResult;
    private String[][] board;
    private int roomId;
    private ChatConnection chat;
    private UI ui;
    private ViewRoomThread viewRoomThread;
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
                case ROOM -> {
                    UI.Display gatherFrame=UI.Display.TEMP;
                    getGameInfo();
                    createViewRoomThread(gatherFrame);
                    while(true){
                        ui.sleep(1500);
                        if(server.isRoomChanged(username)){
                            stopRoomThread();
                            yield UI.Display.ROOM;
                        }
                        if(gatherFrame!=viewRoomThread.getDisplay()){
                            stopRoomThread();
                            yield viewRoomThread.getDisplay();
                        }
                    }
                }
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
    private void sendMessage() throws RemoteException {
        server.roomChanged(roomId,2);
        String message = username+": "+ui.getMessage();
        chat.sendMessage(message);
    }
    private void isPlayersTurn() throws RemoteException {
        playersTurn = server.isPlayersTurn(username);
    }

    public String getOpponentUsername() throws RemoteException {
        opponentUsername = server.getOpponentUsername(username);
        return opponentUsername;
    }
    private void getGameInfo() throws RemoteException {
        if(server.isRoomFull(username) && !chat.isConnectionEstablished()){
            chat.connectToOpponent(server.getOpponentIP(username),roomId);
        }
        if(!server.isRoomFull(username) && chat.isConnectionEstablished()){
            //System.out.println(chat.isServerWorking());
            chat.endConnection();
            startReceivingMessages();
        }
        ui.viewMessages(chat.getMessages());
        getOpponentUsername();
        isPlayerReady();
        isPlayersTurn();
        collectGameResult();
        activeGame=server.isRoomGameActive(username);
        if(activeGame){
            board=server.getBoard(username);
        }
    }
    private void collectGameResult() throws RemoteException{
        gameResult = server.getGameResult(username);
    }
    public int getGameResult() {
        return gameResult;
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
        CountDownLatch latch = new CountDownLatch(1);
        ChatReceiverThread receiverThread = new ChatReceiverThread(chat, roomId, latch);
        receiverThread.start();
        //Czekam na stworzenie serwera, bo po tym się włacza UI pokoju, które sprawdza czy serwer
        // już istnieje i jest wyścig wątków. Mogą się stworzyć 2 serwery.
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("Latch error");
        }

    }

    public void createViewRoomThread(UI.Display display){
        //System.out.println("Tworze watek do ogladania");
        viewRoomThread = new ViewRoomThread(this, display, ui);
        viewRoomThread.start();
    }
    public void stopRoomThread(){
        //System.out.println("Usuwam watek do ogladania");
        viewRoomThread.interrupt();
    }


    public String getUsername(){
        return username;
    }
    public String getRoomId(){
        return String.valueOf(roomId);
    }
    public boolean isActiveGame(){
        return activeGame;
    }
    public boolean isTurn(){
        return playersTurn;
    }
    public boolean isReady(){
        return ready;
    }
    public String[][] getBoard(){
        return board;
    }


}
