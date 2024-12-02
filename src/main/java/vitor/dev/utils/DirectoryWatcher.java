package vitor.dev.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Classe utilitária para monitorar eventos em um diretório, como criação,
 * exclusão e modificação de arquivos.
 * Oferece funcionalidade para deletar automaticamente arquivos criados no
 * diretório monitorado.
 */
public class DirectoryWatcher {

    /**
     * Monitora um diretório para eventos de criação, exclusão e modificação.
     * Se um arquivo for criado, ele é imediatamente deletado.
     *
     * @param path O caminho do diretório a ser monitorado.
     */
    public static void watch(String path) {
        Path dir = Paths.get(path); // Caminho do diretório.

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Registro do diretório para eventos de criação, exclusão e modificação.
            dir.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Monitorando o diretório: \"" + dir + "\"");

            while (true) {
                WatchKey key = watchService.take(); // Espera por um evento.

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path fullPath = dir.resolve((Path) event.context()); // Caminho completo para o arquivo.
                        deleteFile(fullPath); // Deleta o arquivo imediatamente após a criação.
                    }
                }

                // Reseta a chave para continuar escutando eventos.
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Não foi possível monitorar o diretório.");
            e.printStackTrace();
        }
    }

    /**
     * Deleta o arquivo especificado, se ele existir.
     * 
     * @param filePath O caminho do arquivo a ser deletado.
     */
    public static void deleteFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath); // Deleta o arquivo se existir
            System.out.println("Arquivo removido: \"" + filePath + "\"");
        } catch (IOException e) {
            System.out.println("Erro ao remover arquivo: \"" + filePath + "\"\n");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        watch("C:\\Users\\AlmavivA\\Documents");
    }
}
