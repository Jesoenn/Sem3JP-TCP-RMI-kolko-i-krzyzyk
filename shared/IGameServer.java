package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
}
