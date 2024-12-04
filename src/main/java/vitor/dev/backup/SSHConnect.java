package vitor.dev.backup;

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

public class SSHConnect implements Runnable {
    private String user;
    private String host;
    private String password;
    private int port;

    private JSch jsch = new JSch();
    private Session session;

    public SSHConnect(String user, String host, String password, int port) {
        this.user = user;
        this.host = host;
        this.password = password;
        this.port = port;
    }

    public void connect() {
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();

            System.out.println("Conexão estabelecida com o host: \"" + user + "@" + host + "\"");
        } catch (JSchException e) {
            System.out.println("Não foi possível estabelecer uma conexão com o host: " + e.getMessage() + "\n");
            e.printStackTrace(); // Pilha de execução.
        }
    }

    public void command(String command) {
        executeCommand(command, false);
    }

    public void sendSudoCommand(String command) {
        executeCommand(command, true);
    }

    private void executeCommand(String command, boolean isSudo) {
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand(command);
            channel.setErrStream(channel.getOutputStream());
            channel.connect();

            if (isSudo) {
                channel.getOutputStream().write((password + "\n").getBytes());
                channel.getOutputStream().flush();
            }

            System.out.println(captureCommandOutput(channel));
        } catch (JSchException | IOException e) {
            System.out.println("Não foi possível executar o comando: \"" + command + "\"" + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void transferFile(String fromPath, String toPath) {
        try {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");

            channel.connect();
            channel.put(fromPath, toPath);

            System.out.println("Arquivo \"" + fromPath + "\" transferido com sucesso para \"" + toPath + "\"");

            channel.disconnect();
        } catch (JSchException | SftpException e) {
            System.out.println("Não foi possível executar a transferência: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private String captureCommandOutput(Channel channel) {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler a saída do comando: " + e.getMessage() + "\n");
            e.printStackTrace();
        }

        return output.toString();
    }

    @Override
    public void run() {
        connect();
    }
}