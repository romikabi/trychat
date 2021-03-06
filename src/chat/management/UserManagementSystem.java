package chat.management;

import chat.user.Password;
import chat.user.User;
import chat.user.UserId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by romanabuzyarov on 11.04.17.
 */
public class UserManagementSystem {
    private Map<String, User> users;

    public UserManagementSystem() {
        users = new TreeMap<>();
        try {
            Scanner scan = new Scanner(new File("data/users.txt"));
            while (scan.hasNext()) {
                String name = scan.next();
                int pass = scan.nextInt();

                users.put(name, new User(name, pass));
            }
        } catch (FileNotFoundException e) {
            // empty user database is creating
        }
    }

    public synchronized void register(UserId id, Password pass) throws IllegalArgumentException {
        if (users.containsKey(id.getNickname()))
            throw new IllegalArgumentException("Such id was already used!");

        users.put(id.getNickname(), new User(id, pass));
        PrintStream writer;
        try {
            writer = new PrintStream(new File("data/users.txt"));
            writer.println(id.getNickname() + " " + String.valueOf(pass.getHash()));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized User login(UserId id, Password hash) throws IllegalAccessException, IllegalArgumentException {
        if (!users.containsKey(id.getNickname()))
            throw new IllegalArgumentException("No such id!");

        if (users.get(id.getNickname()).checkPass(hash))
            return users.get(id.getNickname());

        throw new IllegalAccessException("Wrong password!");
    }
}
