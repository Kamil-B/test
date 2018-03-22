package node.web;

import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(value = "/dispose")
public class DisposeController {

/*    @GetMapping("/all")
    public ResponseEntity disposeAll() {
        for (Disposable d : TopicController.disposables) {
            d.dispose();
            log.info(d.toString() + " disposed");
        }
        return ResponseEntity.noContent().build();
    }*/
}
