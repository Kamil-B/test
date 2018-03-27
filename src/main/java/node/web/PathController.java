package node.web;

import lombok.extern.slf4j.Slf4j;
import node.model.EventType;
import node.model.PathActionResult;
import node.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(value = "/path")
public class PathController {

    @Autowired
    private FileWatcherService fileWatcherService;

    @GetMapping("/{action}")
    public ResponseEntity<PathActionResult> createFile(@PathVariable String action, @RequestParam("path") String path) {
        try {
            return ResponseEntity.ok().body(fileWatcherService.performAction(action, path));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
