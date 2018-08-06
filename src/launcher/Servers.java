package launcher;

/**
 * Created by mkope on 03-Aug-18.
 */
public class Servers {
    private String name;
    private String server;

    public Servers(String name, String server) {
        this.name = name;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }
}
