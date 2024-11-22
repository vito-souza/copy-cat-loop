package vitor.dev.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnection {

    static JSch jsch = new JSch();
    static Session session;

    /**
     * Estabelece uma conexão SSH com o servidor especificado usando o usuário,
     * senha e porta fornecidos.
     * 
     * <p>
     * Este método cria uma sessão SSH, configura a senha e a opção para ignorar a
     * verificação da chave de host,
     * e, em seguida, tenta conectar ao servidor. Se a conexão não for estabelecida,
     * uma mensagem de erro será exibida.
     * </p>
     * 
     * @param user     O nome de usuário utilizado para autenticação no servidor
     *                 SSH.
     * @param host     O endereço IP ou nome de domínio do servidor SSH.
     * @param password A senha do usuário no servidor SSH.
     * @param port     A porta na qual o servidor SSH está escutando. O padrão
     *                 geralmente é 22.
     */
    public static void connect(String user, String host, String password, int port) {
        // Iniciando uma sessão SSH:
        try {
            session = jsch.getSession(user, host, port); // Cria uma sessão SSH.
            session.setPassword(password); // Insere a senha do servidor SSH.

            // Configurando para ignorar verificações de chave de host.
            session.setConfig("StrictHostKeyChecking", "no");

            // Conectando ao servidor
            session.connect();

            System.out.println("Conexão estabelecida com o host.");
        } catch (JSchException e) {
            System.out.println("Não foi possível estabelecer uma conexão com o host.");
            e.printStackTrace(); // Pilha de execução.
        }
    }

    /**
     * Executa um comando em um servidor remoto via SSH e exibe a saída.
     * 
     * Este método cria um canal SSH para executar o comando fornecido no servidor
     * remoto. Após a execução do comando, ele captura e exibe a saída gerada.
     *
     * @param command O comando a ser executado no servidor remoto.
     * @throws JSchException Se ocorrer um erro ao tentar estabelecer o canal SSH
     *                       ou ao executar o comando.
     */
    public static void command(String command) {
        try {
            // Criando o canal para execução de comandos:
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command); // Definindo o comando a ser executado pelo channel.

            // Se conectando ao canal para receber respostas.
            channel.connect();

            // Capturando e exibindo saída do comando.
            System.out.println(captureCommandOutput(channel));

            // Fechando o canal após a execução.
            channel.disconnect();
        } catch (JSchException e) {
            System.out.println("Não foi executar o comando.");
            e.printStackTrace(); // Pilha de execução.
        }
    }

    /**
     * Captura a saída de um comando executado em um canal SSH.
     * 
     * Este método lê a saída do comando enviado ao canal SSH, armazenando-a em um
     * `StringBuilder`, e a retorna como uma string.
     *
     * @param channel O canal SSH (`Channel`) onde o comando foi executado.
     * @return A saída do comando como uma string.
     * @throws IOException Se ocorrer um erro ao tentar ler a saída do comando.
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
            System.out.println("Erro ao ler a saída do comando.");
            e.printStackTrace(); // Pilha de execução.
        }

        return output.toString(); // Retornando a saída do comando.
    }

    /**
     * Método principal que executa a aplicação.
     * 
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        connect("ubuntu", "localhost", "ubuntu", 2222);
        command("pwd");
    }
}
