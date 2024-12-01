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
public class Connection {

    /** Instância da classe JSch para gerenciar a conexão SSH. */
    static JSch jsch = new JSch();

    /** Sessão SSH para estabelecer a conexão. */
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
        // Iniciando uma sessão SSH:
        try {
            session = jsch.getSession(user, host, port); // Cria uma sessão SSH.
            session.setPassword(password); // Insere a senha do servidor SSH.

            // Configurando para ignorar verificações de chave de host.
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect(); // Conectando ao servidor.

            System.out.println("Conexão estabelecida com o host.");
        } catch (JSchException e) {
            System.out.println("Não foi possível estabelecer uma conexão com o host: " + e.getMessage() + "\n");
            e.printStackTrace(); // Pilha de execução.
        }
    }

    /**
     * Executa um comando remoto via SSH e exibe a saída.
     *
     * @param command O comando a ser executado.
     * @throws JSchException Se ocorrer erro ao executar o comando.
     */
    public static void command(String command) {
        try {
            // Criando o canal para execução de comandos e definindo o comando a ser
            // executado
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command); // Definindo o comando a ser executado pelo channel.

            channel.connect(); // Se conectando ao canal para receber respostas.

            System.out.println(captureCommandOutput(channel)); // Capturando e exibindo saída do comando.

            channel.disconnect(); // Fechando o canal após a execução.
        } catch (JSchException e) {
            System.out.println("Não foi possível executar o comando: " + e.getMessage() + "\n");
            e.printStackTrace(); // Pilha de execução.
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

    /**
     * Método principal para testar a conexão SSH e execução de comandos.
     */
    public static void main(String[] args) {
        connect("ubuntu", "localhost", "ubuntu", 2222);
        command("ifconfig");
    }
}
