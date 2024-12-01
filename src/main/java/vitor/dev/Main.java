package vitor.dev;

import vitor.dev.connection.Connection;

public class Main {
    public static void main(String[] args) {
        Connection.connect("ubuntu", "localhost", "ubuntu", 2222); // Conexão.
        Connection.transfer("src\\main\\resources\\scripts\\script.sh", "/home/ubuntu/Vitor");
        Connection.command("chmod +x /home/ubuntu/Vitor/script.sh");
        Connection.command("timeout 1m bash /home/ubuntu/Vitor/script.sh");
    }
}