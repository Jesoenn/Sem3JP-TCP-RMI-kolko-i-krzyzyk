package client;

import shared.IGameServer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args){
        int port = parseArguments(args);
        String username = args[1];

        try{
            Registry registry = LocateRegistry.getRegistry(port);
            IGameServer server = (IGameServer) registry.lookup("GameServer");

            if(server.connect(username)){
                ClientService client = new ClientService(server, username);
            }
            else
                System.out.println("User is already logged in.");

        } catch (RemoteException | NotBoundException e) {
            System.out.println("Connection can not be established. Try different port.");
        }
    }

    public static int parseArguments(String[] args){
        //java -jar client.jar port username
        int port = -1;
        if(args.length < 2){
            System.out.println("Usage: java -jar server.jar <port> <username>");
            System.exit(1);
        }
        else{
            try{
                port = Integer.parseInt(args[0]);
                if(port<1 || port > 65535)
                    throw new NumberFormatException();
            }catch (NumberFormatException e){
                System.out.println("Wrong port number.");
                System.exit(2);
            }
        }
        return port;
    }
}
