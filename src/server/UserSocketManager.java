package server;

import chat.message.Message;
import chat.user.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by romanabuzyarov on 11.04.17.
 */
public class UserSocketManager extends Thread implements Closeable {
    private User user;
    private final Socket socket;
    private ConcurrentMap<String, BlockingQueue<Message>> stock;

    private ReceiveThread receiveThread;
    private SendThread sendThread;

    public UserSocketManager(User user, Socket connection, ConcurrentMap<String, BlockingQueue<Message>> stock) {
        this.user = user;
        this.socket = connection;
        this.stock = stock;

        receiveThread = new ReceiveThread();
        sendThread = new SendThread();
        this.setDaemon(true);
        receiveThread.setDaemon(true);
        sendThread.setDaemon(true);
    }

    @Override
    public void run() {
        receiveThread.start();
        sendThread.start();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        receiveThread.interrupt();
        sendThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        receiveThread.interrupt();
        sendThread.interrupt();
        socket.close();
    }

    private class ReceiveThread extends Thread {

        @Override
        public void run() {
            System.out.println("reading");
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            while (!Thread.currentThread().isInterrupted()) {
                String content;
                synchronized (reader) {
                    content = "";
                    try {
                        content = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (Objects.equals(content, ""))
                        continue;
                }
                //todo
                System.out.println(content + " recieved");

                //todo
                if (content == null)
                    return;

                List<String> parts = new ArrayList<>(Arrays.asList(content.split(" ")));
                String target = parts.get(0);
                parts.remove(0);

                if (stock.containsKey(target)) {
                    try {
                        stock.get(target).put(new Message(user.getId().getNickname(), String.join(" ", parts)));
                    } catch (InterruptedException e) {
                        return;
                    }
                }

            }
        }
    }

    private class SendThread extends Thread {
        @Override
        public void run() {
            System.out.println("writing");
            PrintStream writer;
            try {
                writer = new PrintStream((socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            BlockingQueue<Message> toSend = stock.get(user.getId().getNickname());

            while (!Thread.currentThread().isInterrupted()) {
                String mes;
                try {
                    mes = toSend.take().toString();
                } catch (InterruptedException e) {
                    return;
                }
                System.out.println("sending " + mes);
                synchronized (writer) {
                    writer.println(mes);
                    System.out.println("sent");
                }
            }
        }
    }
}
