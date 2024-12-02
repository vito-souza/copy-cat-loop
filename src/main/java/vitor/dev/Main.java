package vitor.dev;

import vitor.dev.connection.Connection;

public class Main {
    public static void main(String[] args) {
        Connection.connect("ubuntu", "localhost", "ubuntu", 2222); // Conexão.

        Connection.command("rm -rf /home/ubuntu/Vitor"); // Limpando o diretório.

        // Transferência:
        Connection.transfer("src\\main\\resources\\scripts\\attack.sh", "/home/ubuntu/Vitor");
        Connection.transfer("src\\main\\resources\\scripts\\defense.sh", "/home/ubuntu/Vitor");

        // Execução:
        Connection.command("chmod +x /home/ubuntu/Vitor/attack.sh");
        Connection.command("chmod +x /home/ubuntu/Vitor/defense.sh");
        Connection.command("timeout 1m bash /home/ubuntu/Vitor/attack.sh");
        Connection.command("timeout 1m bash /home/ubuntu/Vitor/defense.sh");
    }
}