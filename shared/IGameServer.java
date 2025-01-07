package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface IGameServer extends Remote {
    boolean connect(String username) throws RemoteException;
    int createRoom(String username) throws RemoteException;
    boolean joinRoom(String username, int roomId) throws RemoteException;
    String getOpponentUsername(String username) throws RemoteException;
    boolean isRoomGameActive(String username) throws RemoteException;
    void leaveRoom(String username) throws RemoteException;
    void setReady(String username) throws RemoteException;
    boolean isReady(String username) throws RemoteException;
    String[][] getBoard(String username) throws RemoteException;
    int makeMove(String username, int row, int column) throws RemoteException;
    boolean isPlayersTurn(String username) throws RemoteException;
    void leave(String username) throws RemoteException;
    ArrayList<String> getAvailableRoomList() throws RemoteException;
    HashMap<String, ArrayList<Integer>> getPlayerStats() throws RemoteException;
    void addUserIP(String username, String ip) throws RemoteException;
    String getOpponentIP(String username) throws RemoteException;
    boolean isRoomFull(String username) throws RemoteException;
}
