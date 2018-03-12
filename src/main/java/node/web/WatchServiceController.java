package node.web;

import lombok.extern.slf4j.Slf4j;
import node.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Slf4j
@RequestMapping(value = "/watch")
public class WatchServiceController {

    @Autowired
    FileWatcherService fileWatcherService;

    @PostMapping(value = "/{path}")
    public ResponseEntity addPathToWatchService(@RequestParam(value = "path") Path path) {
        log.info("Received path request: " + path);
        return ResponseEntity.ok(fileWatcherService.watch(path));
    }
}
