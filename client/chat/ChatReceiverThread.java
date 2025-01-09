package client.chat;

import java.util.concurrent.CountDownLatch;

public class ChatReceiverThread extends Thread {
    private ChatConnection chatConnection;
    private CountDownLatch latch;
    private int roomId;

    public ChatReceiverThread(ChatConnection chatConnection, int roomId, CountDownLatch latch) {
        this.chatConnection = chatConnection;
        this.roomId = roomId;
        this.latch = latch;
    }

    @Override
    public void run() {
        chatConnection.startReceiving(roomId, latch);
    }
}
