package server;

import chat.management.UserManagementSystem;
import chat.user.Password;
import chat.user.UserId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        //ServerSocket server = new ServerSocket(Integer.parseInt(args[1]));
        ServerSocket serverSocket = new ServerSocket(8080);//todo fix
        UserManagementSystem users = new UserManagementSystem();
        ExecutorService executors = Executors.newFixedThreadPool(5);
        //ConcurrentHashMap<User,> todo todo

        System.out.println("Ready");
        while (true) {
            Socket connection = serverSocket.accept();
            // todo decompose
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = reader.readLine();
            String[] parts = content.split(" ");
            switch (parts[0]) {
                // register
                case "0":
                    users.register(new UserId(parts[1]), new Password(Integer.parseInt(parts[2])));
                    break;
                //login
                case "1":

                    break;
                //message
                case "2":

                    break;
                default:
                    break;
            }
        }
    }

    private static String myPublicIP() {
        try {
            URL url = new URL("http://bot.whatismyipaddress.com");
            String res;
            try (InputStreamReader is = new InputStreamReader(url.openStream());
                 BufferedReader stream = new BufferedReader(is)) {
                res = stream.readLine().trim();
            }
            if (res.length() > 0)
                return res;
        } catch (MalformedURLException ex) {

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
