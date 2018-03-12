import node.utils.FileWatcherService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.nio.file.Paths;

@SpringBootApplication
@ComponentScan("node.*")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public FileWatcherService createWatcherService() throws IOException {
        FileWatcherService fileWatcherService= new FileWatcherService();
        fileWatcherService.getWatcher(Paths.get("C:\\git\\tree_test"));
        return fileWatcherService;
    }

}
