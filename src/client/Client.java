package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Client {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);
        PrintStream bw = new PrintStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bw.println("Hello world!");
        System.out.println(br.readLine());
        socket.close();
    }
}
