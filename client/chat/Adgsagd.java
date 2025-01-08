package client.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Adgsagd {
    String input = "";
    Thread roomThread;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public void createRoomInputThread(){
        roomThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while(!Thread.currentThread().isInterrupted() && input.isEmpty()) {
                        if(reader.ready()) {
                            input = reader.readLine();
                            System.out.println("INPUT: " + input);
                        }
                    }
                    System.out.println("KONIEC RUN");
                } catch (IOException e) {
                    System.out.println("EXCEPTION");
                }
            }
        });
        roomThread.start();
    }
    public void endRoomInputThread(){
        roomThread.interrupt();
        try {
            System.out.println("endRoomInputThread");
            reader.close();
        } catch (IOException e) {
            System.out.println("Error closing reader: ");
        }
    }

    public static void main(String[] args){
        Adgsagd ui = new Adgsagd();
        ui.createRoomInputThread();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("MINELY 3 SEKUNDY");
        ui.endRoomInputThread();
        System.out.println("Koniec wątku");
    }
}
