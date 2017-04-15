package chat.message;

/**
 * Created by romanabuzyarov on 11.04.17.
 */
public class Message {
    private String content;
    private String author;

    public Message(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String toString() {
        return "[" + author + "]:" + content;
    }
}
