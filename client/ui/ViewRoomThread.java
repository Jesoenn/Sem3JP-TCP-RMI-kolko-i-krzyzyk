package client.ui;

import client.ClientService;

import java.rmi.RemoteException;

public class ViewRoomThread extends Thread {
    private ClientService client;
    private UI.Display display;
    private UI ui;
    public ViewRoomThread(ClientService client, UI.Display display, UI ui) {
        this.client = client;
        this.display = display;
        this.ui = ui;
    }


    @Override
    public void run() {
        String username = client.getUsername();
        String opponentUsername;
        try{
            opponentUsername = client.getOpponentUsername();
        }catch (RemoteException e){
            System.out.println("Couldnt get opponent username");
            opponentUsername = "ERROR";
        }
        String roomId = client.getRoomId();
        boolean gameStarted = client.isActiveGame();
        boolean playersTurn = client.isTurn();
        boolean ready = client.isReady();
        String[][] board = client.getBoard();
        int gameResult = client.getGameResult();
        if(gameResult == 0)
            ui.errorStatus(UI.Display.DRAW);
        else if(gameResult == 1)
            ui.errorStatus(UI.Display.WINNER);
        else if(gameResult == 2)
            ui.errorStatus(UI.Display.LOSER);

        display=ui.viewRoomInfo(username,opponentUsername,roomId,gameStarted,ready,board,playersTurn);
    }

    public UI.Display getDisplay() {
        return display;
    }
}
