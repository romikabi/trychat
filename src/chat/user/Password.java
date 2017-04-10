package chat.user;

/**
 * Created by romanabuzyarov on 11.04.17.
 */
public class Password {
    private int hash;

    public Password(int hash) {
        this.hash = hash;
    }

    public int getHash() {
        return hash;
    }
}
