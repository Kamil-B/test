package node.service;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.Node;
import node.utils.NodeHelperUtils;
import node.utils.NodeTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@EnableScheduling
@Slf4j
public class UpdateService {


    @Autowired
    private FileWatcherService fileWatcherService;

    @Scheduled(cron = "*/1 * * * * *")
    public void getUpdatedPaths() {
       // Observable<Event> events = fileWatcherService.update();
        //events.subscribe(event -> log.info(event.toString()));
    }

    public void subscribe(Path path) {
        NodeTree<Path> helper = new NodeTree<>(NodeHelperUtils.createPathTree(path));
        Observable<Node<Path>> snapshot = Observable.fromIterable(helper);
    }

}
