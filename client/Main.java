package client;

import shared.IGameServer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args){
        //PODAWANE W LINII KOMEND JARU -> parsowanie danych i weryfikacja
        String username = "user1";
        int port = 1099;
        String host = "localhost";


        try{
            Registry registry = LocateRegistry.getRegistry(port);
            IGameServer server = (IGameServer) registry.lookup("GameServer");

            if(server.connect(username)){
                ClientService client = new ClientService(server, username);
            }
            else
                System.out.println("User is already logged in.");

        } catch (RemoteException | NotBoundException e) {
            System.out.println("Connection can not be established.");
        }
    }

    public static void parseArguments(String[] args){}
}
