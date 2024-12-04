package vitor.dev.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * A classe {@code SSHConnection} permite conectar-se a um servidor remoto via
 * SSH
 * e executar comandos. Utiliza a biblioteca JSch para estabelecer a conexão e
 * capturar a saída dos comandos executados.
 */
public class SSHConnection {

    static JSch jsch = new JSch();
    static Session session;

    /**
     * Estabelece uma conexão SSH com o servidor usando as credenciais fornecidas.
     *
     * @param user     O nome de usuário para autenticação no servidor SSH.
     * @param host     O endereço do servidor SSH.
     * @param password A senha do usuário.
     * @param port     A porta do servidor SSH (geralmente 22).
     */
    public static void connect(String user, String host, String password, int port) {
        try {
            session = jsch.getSession(user, host, port); // Cria uma sessão SSH.
            session.setPassword(password); // Insere a senha do servidor SSH.

            // Configurando para ignorar verificações de chave de host.
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect(); // Conectando ao servidor.

            System.out.println("Conexão estabelecida com o host: \"" + user + "@" + host + "\"");
        } catch (JSchException e) {
            System.out.println("Não foi possível estabelecer uma conexão com o host: " + e.getMessage() + "\n");
            e.printStackTrace(); // Pilha de execução.
        }
    }

    /**
     * Executa um comando SSH no servidor remoto.
     *
     * @param command O comando a ser executado.
     */
    public static void command(String command) {
        executeCommand(command, null);
    }

    /**
     * Executa um comando SSH com privilégios de superusuário no servidor remoto.
     *
     * @param command    O comando a ser executado.
     * @param sudoPasswd A senha do superusuário para autenticação.
     */
    public static void sudoCommand(String command, String sudoPasswd) {
        executeCommand(command, sudoPasswd);
    }

    /**
     * Executa um comando no servidor remoto, podendo incluir senha de superusuário.
     *
     * @param command    O comando a ser executado.
     * @param sudoPasswd A senha do superusuário (pode ser null se não necessário).
     */
    private static void executeCommand(String command, String sudoPasswd) {
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand(command);
            channel.setErrStream(channel.getOutputStream());
            channel.connect();

            if (sudoPasswd != null) {
                channel.getOutputStream().write((sudoPasswd + "\n").getBytes());
                channel.getOutputStream().flush();
            }

            System.out.println(captureCommandOutput(channel));
        } catch (JSchException | IOException e) {
            System.out.println("Não foi possível executar o comando: \"" + command + "\"" + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    /**
     * Transfere um arquivo de um caminho local para um caminho remoto usando SFTP.
     *
     * @param fromPath O caminho do arquivo local a ser transferido.
     * @param toPath   O caminho remoto para onde o arquivo será transferido.
     */
    public static void transfer(String fromPath, String toPath) {
        try {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp"); // Canal de transferência.
            channel.connect();

            channel.put(fromPath, toPath); // Transferir o arquivo de um local para o outro.
            System.out.println("Arquivo \"" + fromPath + "\" transferido com sucesso para \"" + toPath + "\"");

            channel.disconnect();
        } catch (JSchException | SftpException e) {
            System.out.println("Não foi possível executar a transferência: " + e.getMessage() + "\n");
            e.printStackTrace(); // Pilha de execução.
        }
    }

    /**
     * Captura e retorna a saída de um comando executado via SSH.
     *
     * @param channel O canal SSH onde o comando foi executado.
     * @return A saída do comando.
     * @throws IOException Se ocorrer erro ao ler a saída.
     */
    private static String captureCommandOutput(Channel channel) {
        StringBuilder output = new StringBuilder(); // Para armazenar a saída do comando.

        // Lendo a saída do comando:
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            String line;

            // Enquanto ainda receber respostas:
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n"); // Atribuíndo a respota ao objeto.
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler a saída do comando: " + e.getMessage() + "\n");
            e.printStackTrace(); // Pilha de execução.
        }

        return output.toString(); // Retornando a saída do comando.
    }
}
