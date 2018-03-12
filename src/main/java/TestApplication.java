import node.service.FileWatcherService;
import node.service.FileWatcherServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.nio.file.Paths;

@SpringBootApplication
@ComponentScan("node.*")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    @Primary
    public FileWatcherService createFileWatcher() {
        FileWatcherService watcher = new FileWatcherServiceImpl();
        try {
            watcher.registerPath(Paths.get("C:\\git\\tree_test"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return watcher;
    }

}
