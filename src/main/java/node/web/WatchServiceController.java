package node.web;

import lombok.extern.slf4j.Slf4j;
import node.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@Slf4j
@RequestMapping(value = "/watcher")
public class WatchServiceController {

    @Autowired
    FileWatcherService fileWatcherService;

    @GetMapping(value = "/add")
    public ResponseEntity addPathToWatchService(@RequestParam(value = "path") Path path) {
        log.info("Received path request: " + path);
        fileWatcherService.startWatching(path);
        return ResponseEntity.noContent().build();
    }
}
