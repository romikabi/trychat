package client;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

/**
 * Created by romanabuzyarov on 11.04.17.
 */
public class ClientSocketManager extends Thread implements Closeable {
    private final Socket socket;
    private SendThread sendThread;
    private ReceiveThread receiveThread;

    public ClientSocketManager(Socket socket) {
        this.socket = socket;

        sendThread = new SendThread();
        receiveThread = new ReceiveThread();
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
        sendThread.interrupt();
        receiveThread.interrupt();
        socket.close();
    }

    private class SendThread extends Thread {
        @Override
        public void run() {
            //todo
            System.out.println("run send");

            PrintStream writeToServer = null;
            System.out.println("Output stream is setting up.");
            try {
                writeToServer = new PrintStream((socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Output stream set up.");
            while (!Thread.currentThread().isInterrupted()) {
                String mes = "";
                System.out.println("write message");
                mes = new Scanner(System.in).nextLine();
                if (Objects.equals(mes, ""))
                    continue;
                synchronized (writeToServer) {
                    writeToServer.println(mes);
                    //todo
                    System.out.println("sent");
                }
            }
        }
    }

    private class ReceiveThread extends Thread {

        @Override
        public void run() {
            //todo
            System.out.println("run receive");

            BufferedReader readFromServer;
            try {
                readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (readFromServer) {
                    String mes = "";
                    try {
                        mes = readFromServer.readLine();
                    } catch (IOException e) {
                    }
                    if (Objects.equals(mes, ""))
                        continue;

                    //todo
                    if (mes == null) {
                        return;
                    }

                    //todo
                    System.out.println(mes);
                }
            }
        }
    }
}