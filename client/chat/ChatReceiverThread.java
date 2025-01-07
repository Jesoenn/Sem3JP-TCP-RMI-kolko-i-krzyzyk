package client.chat;

public class ChatReceiverThread extends Thread {
    private ChatConnection chatConnection;
    private int roomId;

    public ChatReceiverThread(ChatConnection chatConnection, int roomId) {
        this.chatConnection = chatConnection;
        this.roomId = roomId;
    }

    @Override
    public void run() {
        chatConnection.startReceiving(roomId);
        System.out.println("KONIEC WATKU");
    }
}
