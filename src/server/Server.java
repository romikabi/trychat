package server;

import chat.management.UserManagementSystem;
import chat.message.Message;
import chat.user.Password;
import chat.user.UserId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        //ServerSocket server = new ServerSocket(Integer.parseInt(args[1]));
        ServerSocket serverSocket = new ServerSocket(4900);//todo fix
        UserManagementSystem users = new UserManagementSystem();
        ExecutorService executors = Executors.newFixedThreadPool(5);

        ConcurrentMap<String, BlockingQueue<Message>> outgoing = new ConcurrentHashMap<>();
        List<UserSocketManager> managers = new ArrayList<>();

        System.out.println("Ready");
        while (true) {
            Socket connection = serverSocket.accept();
            System.out.println("accepted");
            // todo decompose
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = reader.readLine();
            System.out.println(content + " got");
            String[] parts = content.split(" ");
            UserSocketManager man;
            switch (parts[0]) {
                // register
                case "0":
                    users.register(new UserId(parts[1]), new Password(Integer.parseInt(parts[2])));

                    man = new UserSocketManager(
                            users.login(
                                    new UserId(parts[1]),
                                    new Password(Integer.parseInt(parts[2]))),
                            connection,
                            outgoing);
                    managers.add(man);
                    outgoing.put(parts[1], new SynchronousQueue<>());
                    man.start();
                    System.out.println(parts[1] + " registered and log on");
                    break;
                //login
                case "1":
                    man = new UserSocketManager(
                            users.login(
                                    new UserId(parts[1]),
                                    new Password(Integer.parseInt(parts[2]))),
                            connection,
                            outgoing);
                    managers.add(man);
                    outgoing.put(parts[1], new SynchronousQueue<>());
                    man.start();
                    System.out.println(parts[1] + " login");
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
