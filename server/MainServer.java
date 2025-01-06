package server;

import shared.IGameServer;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainServer {
    public static void main(String[] args) {
        //PODAWANE W LINII KOMEND JARU
        int port = 1099;

        try{
            IGameServer server = new GameServer();
            IGameServer stub = (IGameServer) UnicastRemoteObject.exportObject( server, 0);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("GameServer", stub);

        } catch (RemoteException e) {
            System.out.println("Error creating server on port "+port);
            e.printStackTrace();
        }
    }
}
