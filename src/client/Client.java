package client;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by romanabuzyarov on 10.04.17.
 */

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), 4900);
        ClientSocketManager manager = new ClientSocketManager(socket);
        manager.start();
    }
}
