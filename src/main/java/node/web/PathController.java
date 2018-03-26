package node.web;

import lombok.extern.slf4j.Slf4j;
import node.model.PathActionResult;
import node.service.FileWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(value = "/path")
public class PathController {

    @Autowired
    private FileWatcherService fileWatcherService;

    @GetMapping("/{action}")
    public ResponseEntity<PathActionResult> createFile(@PathVariable String action, @RequestParam String path) {
       return ResponseEntity.ok().body(fileWatcherService.performAction(action, path));
    }
}
