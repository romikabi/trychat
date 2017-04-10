package client;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Main {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket("94.25.177.242", 8080);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write("Hello world!");
        socket.close();
    }
}
