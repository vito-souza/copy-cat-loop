package vitor.dev;

import vitor.dev.connection.Connection;

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
        // Corrigindo caminho para usar LIB (no Windows)
        Connection.transfer(LIB + "attack.sh", REMOTE_PATH);
        Connection.command("chmod +x " + REMOTE_PATH + "/attack.sh");
        Connection.sudoCommand("bash " + REMOTE_PATH + "/attack.sh", SUDO_PASSWD);
    }

    /**
     * Envia e executa o script de defesa no servidor remoto.
     */
    public static void defense() {
        // Corrigindo caminho para usar LIB (no Windows)
        Connection.transfer(LIB + "defense.sh", REMOTE_PATH);
        Connection.command("chmod +x " + REMOTE_PATH + "/defense.sh");
        Connection.sudoCommand("bash " + REMOTE_PATH + "/defense.sh", SUDO_PASSWD);
        // Connection.command("rm /defense.sh");
    }

    /**
     * Coneta e configura o ambiente no servidor remoto.
     */
    public static void setup() {
        Connection.connect(USER, HOST, PASSWD, PORT); // Se conectando ao host.
        Connection.command("rm -rf " + REMOTE_PATH);
        Connection.command("mkdir " + REMOTE_PATH);
    }

    public static void main(String[] args) {
        setup();
        // defense();
        // attack();
    }
}