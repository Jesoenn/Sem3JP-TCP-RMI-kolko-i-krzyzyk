package server;

import shared.IGameServer;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainServer {
    public static void main(String[] args) {
        //int port = 1099;
        int port = parseArguments(args);

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

    private static int parseArguments(String[] args) {
        int port=-1;
        if(args.length<1){
            System.out.println("Usage: java -jar server.jar <port>");
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
