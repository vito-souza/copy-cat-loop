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

public class SSHConnection {

    private JSch jsch = new JSch();
    private Session session;

    public void connect(String user, String host, String password, int port) {
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            System.out.println("Conexão estabelecida com o host: \"" + user + "@" + host + "\"");
        } catch (JSchException e) {
            System.out.println("Não foi possível estabelecer uma conexão com o host: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void command(String command) {
        executeCommand(command, null);
    }

    public void command(String command, String sudoPasswd) {
        executeCommand(command, sudoPasswd);
    }

    private void executeCommand(String command, String sudoPasswd) {
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

    public void transfer(String fromPath, String toPath) {
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
}
