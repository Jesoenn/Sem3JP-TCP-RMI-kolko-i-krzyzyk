package client;

import server.GameServer;

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

    // i W serverze zapisuje dwukrotnie numer pokoju zmiany. (i usuwam raz jak pobrane) //jezeli juz serwer raz dodal update to nie daje kolejny raz
    //watek wyswietla pokoj i zbiera to co jest zwracane. Jezeli ROOM, to nic nie robie (odswiezam), jezeli
    //jak petla chce cos zaktualizowac to przerywam watek. Robie nowy.

    //funkcjacreatethread jako
    // arg konstruktora: server, gatherFrame, ui
    // co robi watek:
    // wyswietla ui pokoju
    // input w ui jako BufferedReader.
    // Jezeli UI cos zwraca to zwraca to do gatherFrame

    //W ui zrobic petle ktora zbiera do momentu az nie jest puste


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
        // 0 draw
        // 1 win
        // 2 lose
        System.out.println("\nViewRoomThread -> "+gameResult);
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
