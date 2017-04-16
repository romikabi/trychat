package server;

import chat.management.UserManagementSystem;
import chat.message.Message;
import chat.user.Password;
import chat.user.UserId;

import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by romanabuzyarov on 10.04.17.
 */
public class Server extends Thread {
    private ServerSocket serverSocket = null;
    private UserManagementSystem users = null;
    private ConcurrentMap<String, BlockingQueue<Message>> outgoing = null;
    private List<UserSocketManager> managers = null;
    private Reporter reporter = null;
    private Properties codes;

    public void setupServer(Reporter reporter) throws Exception {
        //ServerSocket server = new ServerSocket(Integer.parseInt(args[1]));
        serverSocket = new ServerSocket(4900);//todo fix
        users = new UserManagementSystem();
        outgoing = new ConcurrentHashMap<>();
        managers = new ArrayList<>();

        this.reporter = reporter;
        codes = new Properties();
        codes.load(new FileInputStream(new File("data/codes.properties")));

        reporter.report("Setup is over.");
    }

    @Override
    public void run() {
        if (serverSocket == null || users == null || outgoing == null || managers == null || reporter == null) {
            reporter.report("Server wasn't set up.");
            return;
        }

        reporter.report("Server is up.");
        while (!Thread.currentThread().isInterrupted()) {
            Socket connection;
            try {
                connection = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            reporter.report("Connection accepted.");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ENTERING code;
                    do {
                        code = handleIncomingConnection(connection);
                    } while (code == ENTERING.REPEAT);
                }
            }).start();
        }
    }

    private enum ENTERING {
        SUCCESS,
        REPEAT,
        FAIL
    }

    private ENTERING handleIncomingConnection(Socket connection) {
        BufferedReader readerUser = null;
        try {
            readerUser = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            return ENTERING.FAIL;
        }

        PrintStream writerUser;
        try {
            writerUser = new PrintStream(connection.getOutputStream());
        } catch (IOException e) {
            return ENTERING.FAIL;
        }

        String content = null;
        try {
            content = readerUser.readLine();
        } catch (IOException e) {
            return ENTERING.FAIL;
        }
        reporter.report("Got: \"" + content + "\".");

        String[] parts = content.split(" ");
        UserSocketManager man;
        String code = parts[0];

        final String register = codes.getProperty("register_request");
        final String login = codes.getProperty("login_request");
        if (code.equals(register)) {
            try {
                users.register(new UserId(parts[1]), new Password(Integer.parseInt(parts[2])));
            } catch (IllegalArgumentException e) {
                writerUser.println(codes.getProperty("register_failure"));
                return ENTERING.REPEAT;
            }
            reporter.report(parts[1] + " registered successfully.");
            writerUser.println(codes.getProperty("register_success"));
            return ENTERING.REPEAT;
        } else if (code.equals(login)) {
            //login
            try {
                man = new UserSocketManager(
                        users.login(
                                new UserId(parts[1]),
                                new Password(Integer.parseInt(parts[2]))),
                        connection,
                        outgoing);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                writerUser.println(codes.getProperty("login_failure"));
                return ENTERING.REPEAT;
            }

            managers.add(man);
            outgoing.put(parts[1], new SynchronousQueue<>());
            man.start();
            reporter.report(parts[1] + " login successfully.");
            writerUser.println(codes.getProperty("login_success"));
            return ENTERING.SUCCESS;
        } else {
            // todo notify about senseless message
            return ENTERING.REPEAT;
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.setupServer(new Reporter() {
            @Override
            public void report(String message) {
                System.out.println(message);
            }
        });
        server.start();
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
