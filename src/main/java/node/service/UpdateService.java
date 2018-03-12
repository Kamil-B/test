package node.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@Slf4j
public class UpdateService {

    @Autowired
    private FileWatcherService fileWatcherService;

    @Scheduled(cron = "*/1 * * * * *")
    public void getUpdatedPaths() {

       fileWatcherService.update();
    }


}
