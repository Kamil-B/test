package node.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class UpdateService {

    @Autowired
    private FileWatcherService fileWatcherService;

    public void getUpdatedPaths(Path path){
        //fileWatcherService.getWatcher(path);
    }



}
