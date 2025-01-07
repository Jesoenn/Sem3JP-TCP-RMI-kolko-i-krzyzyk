package client;

import server.models.Board;

import java.nio.file.LinkPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class UI {
    private Scanner sc;
    private int roomId;
    private int[] move;

    public enum Display{
        MAIN_MENU, CREATE_ROOM, LEAVE_ROOM, ROOM, JOIN_ROOM, MARK_READY, WRONG_TURN, MAKE_MOVE,
        TILE_TAKEN, WINNER, LOSER, ROOM_LIST, PLAYER_STATS
    }

    public UI(){
        sc = new Scanner(System.in);
    }

    public Display viewMainMenu(){
        System.out.println("Available commands:\n" +
                " create -> creates room\n" +
                " join 'id' -> joins room under given id\n" +
                " rooms -> list of available rooms\n" +
                " stats -> view players statistics");
        String input = sc.nextLine();
        if (input.equals("create"))
            return Display.CREATE_ROOM;
        else if (input.startsWith("join")) {
            checkJoinCommand(input);
            return Display.JOIN_ROOM;
        }
        else if(input.equals("rooms"))
            return Display.ROOM_LIST;
        else if(input.equals("stats"))
            return Display.PLAYER_STATS;

        System.out.println("Wrong command.");
        sleep(500);
        return Display.MAIN_MENU;
    }


    // TU ZROBIC ROZGRYWKE. JAK NIKT NIE JEST POLACZONY TO CZEKAM.
    // if player 2 to null to czekam 1s i odswiezam. W clientService dac kolejna metode ktora wysweitla fragment gry
    public Display viewRoomInfo(String player1, String player2, String roomId, boolean gameStarted, boolean ready, String[][] board, boolean playersTurn){
        System.out.print("Room <"+roomId+">:\n" +
                "You: "+player1+" (");
        if(ready)
            System.out.print("Ready");
        else System.out.print("Not ready");
        System.out.println(")");
        if(player2!=null)
            System.out.println("Opponent: "+player2);

        if(gameStarted){
            System.out.println("=====================");
            for(int row=0; row<3; row++){
                System.out.println("       ");
                for(int column=0; column<3; column++){
                    System.out.print(board[row][column] + " ");
                }
                System.out.println();
            }
            System.out.println("=====================");

        }
        if(playersTurn)
            System.out.println("Your Turn\n=====================");


        if(!playersTurn && ready){
            sleep(1000);
            return Display.ROOM;
        }
        System.out.println("\nAvailable commands: " +
                "\nready | leave" +
                "\nmove \"row\" \"column\"");
        System.out.print("Command: ");
        String input = sc.nextLine();

        if(input.startsWith("move") && gameStarted){
            if(!checkMoveCommand(input.substring(5)))
                return Display.ROOM;
            input="move";
        }
        return switch(input){
            case "leave" -> Display.LEAVE_ROOM;
            case "ready" -> { if(!ready) yield Display.MARK_READY; yield Display.ROOM;}
            case "move" -> Display.MAKE_MOVE;
            default -> Display.ROOM;
        };
    }








    public Display errorStatus(Display display){
        if(display == Display.JOIN_ROOM){
            System.out.println("Invalid room number.");
            sleep(500);
            return Display.MAIN_MENU;
        }
        else if(display == Display.WRONG_TURN){
            System.out.println("Its not your turn.");
            sleep(500);
        }
        else if(display == Display.TILE_TAKEN){
            System.out.println("Tile is already taken.");
            sleep(500);
        }
        else if(display == Display.WINNER){
            System.out.println("You have won the game!");
            sleep(500);
        }
        else if(display == Display.LOSER){
            System.out.println("You have lost the game.");
            sleep(500);
        }
        return Display.MAIN_MENU;
    }
    public Display viewRooms(ArrayList<String> roomList){
        System.out.println("Room List:");
        if(roomList.isEmpty()){
            System.out.println("No available rooms.");
            sleep(1000);
            return Display.MAIN_MENU;
        }

        for(String room: roomList){
            System.out.println("-> room <"+room+">");
        }
        waitForInput();
        return Display.MAIN_MENU;
    }
    public Display viewPlayerStats(HashMap<String,ArrayList<String>> statsCombined){
        System.out.println("Player list with their stats:");
        for (String username: statsCombined.keySet()){
            String wins = statsCombined.get(username).getFirst();
            String draws = statsCombined.get(username).get(1);
            String loses = statsCombined.get(username).getLast();
            System.out.println(" -> "+username+" ( Wins: "+wins+" | Draws: "+
                    draws+" | Loses: "+loses+" )");
        }
        waitForInput();
        return Display.MAIN_MENU;
    }


    // Jakies komendy typu exit, idk
    public void globalCommands(){}




    private void waitForInput(){
        System.out.print("Press ENTER to return to main menu...");
        sc.nextLine();
    }
    private void checkJoinCommand(String text){
        String potentialId = text.substring(5);
        try{
            roomId = Integer.parseInt(potentialId);
        }catch(NumberFormatException e){
            roomId = -1;
        }
    }

    private boolean checkMoveCommand(String text){
        String[] splitText=text.split(" ");
        try{
            move = new int[2];
            for(int i=0; i<splitText.length; i++){
                move[i] = Integer.parseInt(splitText[i]);
                if(move[i] > 2 || move[i] < 0)
                    throw new NumberFormatException();
            }
            return true;
        }catch(NumberFormatException e){
            System.out.println("Invalid move.");
            sleep(500);
        }
        return false;
    }

    public void verticalSpacer(){
        System.out.println("\n\n\n\n\n\n\n\n");
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Sleep error");
        }
    }

    public int getRoomId(){
        return roomId;
    }
    public int[] getMove(){
        return move;
    }
}
