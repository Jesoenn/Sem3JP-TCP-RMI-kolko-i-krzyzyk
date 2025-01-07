package client.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatConnection {
    //Zbieram swoje ip i moge je dac dalej
    //Otwieram serwer jak dolacze do pokoju | socket: 2*id lub 2*id-1, zalezy ktore wolne
    //Jezeli ktos wlasnie dolaczyl to sie z nim lacze (szukam osoby do polaczenia do momentu az nie znajde)
    //Jezeli nikogo nie ma to koncze polaczenia wszystkie i serwer i klient
    //Zbieram historie wysylu i odbioru i to moge przekazac dalej. Czyszczone jest jak urywa sie polaczenie
    //WYSYLANA WIADOMOSC: NICK: WIADOMOSC


    //w ui dac sentMessage i pobierac je
    private String localIP;
    private boolean connectionEstablished;
    private int serverPort;
    private boolean serverWorking;
    private ArrayList<String> messageHistory;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Socket socket;
    private PrintWriter out;

    public ChatConnection(){
        messageHistory=new ArrayList<>();
        getMyIP();
    }
    private void getMyIP(){
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            localIP = localHost.getHostAddress();
            System.out.println("My local IP: " + localIP);
        } catch (UnknownHostException e) {
            System.out.println("Error finding local IP.");
            e.printStackTrace();
        }
    }

    public void startReceiving(int roomId){
        messageHistory=new ArrayList<>();
        int port=findAvailablePort(roomId);
        if(port==-1)
            return;
        BufferedReader in;
        try {
            serverPort=port;
            serverWorking=true;
            serverSocket = new ServerSocket(port);
            System.out.println("Created Server on port "+port);
            acceptNewClient();
        } catch (IOException e) {
            System.out.println("Error creating server socket.");
            e.printStackTrace();
        }
    }
    public void acceptNewClient(){
        try{
            clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message;
            while (clientSocket.isConnected()) {
                message = in.readLine();
                if (message != null)
                    messageHistory.add(message);
            }
        } catch (IOException e) {
            System.out.println("Error accepting client connection.");
        }
    }

    public void connectToOpponent(String ip, int roomId){
        int port;
        if(roomId*2 == serverPort)
            port = roomId*2-1;
        else
            port=roomId*2;

        try {
            socket = new Socket(ip, port);
            connectionEstablished=true;
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("\n\nConnection established.\n\n");
        } catch (IOException e) {
            System.out.println("Cannot connect to chat under port "+port);
        }
    }

    public void sendMessage(String message){
        if(connectionEstablished){
            messageHistory.add(message);
            out.println(message);
        }
    }

    public void endConnection(){
        try{
            if(connectionEstablished){
                messageHistory.clear();
                connectionEstablished=false;
                if(socket!=null)
                    socket.close();
                if(serverSocket!=null){
                    serverWorking=false;
                    serverSocket.close();
                }
            }
        }catch(IOException e){
            System.out.println("Error closing sockets.");
        }
    }

    private int findAvailablePort(int roomId){
        int port1=roomId*2;
        int port2=roomId*2-1;
        System.out.println("Port1 "+port1+" | Port 2 "+port2+")");
        if(isAvailable(port1))
            return port1;
        else if (isAvailable(port2))
            return port2;
        else{
            System.out.println("No available port found. ("+port1+" | "+port2+")");
            return -1;
        }
    }
    public boolean isAvailable(int port){
        try{
            ServerSocket testSocket = new ServerSocket(port);
            testSocket.close();
            return true;
        }catch(Exception e){
            return false;
        }
    }
    public String getLocalIP(){
        return localIP;
    }
    public boolean isConnectionEstablished(){
        return connectionEstablished;
    }
    public ArrayList<String> getMessages(){
        return messageHistory;
    }
    public boolean isServerWorking(){
        return serverWorking;
    }
}
