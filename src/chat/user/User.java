package chat.user;

/**
 * Created by romanabuzyarov on 11.04.17.
 */
public class User {
    private UserId id;
    private Password pass;

    public User(String id, int hashpass) {
        this.id = new UserId(id);
        this.pass = new Password(hashpass);
    }

    public User(UserId id, Password pass) {
        this.id = new UserId(id.getNickname());
        this.pass = new Password(pass.getHash());
    }

    public boolean checkPass(Password hash) {
        return hash.getHash() == pass.getHash();
    }

    public UserId getId() {
        return id;
    }
}
