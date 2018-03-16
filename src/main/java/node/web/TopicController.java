package node.web;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventMessage;
import node.model.EventType;
import node.model.SubscriptionMessage;
import node.service.FileWatcherService;
import node.utils.NodeUtils;
import node.utils.NodeTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.nio.file.Paths;

@Controller
@EnableScheduling
@Slf4j

public class TopicController {

    @Autowired
    private FileWatcherService fileWatcherService;
    private Observable<Event> observable = Observable.empty();

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/path")
    @SendTo("/topic/{file}")
    public void tree_generator(SubscriptionMessage message) {
        observable = fileWatcherService.startWatching(Paths.get(message.getPath()));

        Observable.fromIterable(new NodeTree<>(NodeUtils.createPathTree(Paths.get(message.getPath()))))
                .map(element -> new Event(element.getPayload(), EventType.CREATE))
                .mergeWith(observable)
                .subscribe(element -> template.convertAndSend("/topic/file", new EventMessage(element.getPath().toString(), element.getEvent())));

    }

    @Scheduled(fixedDelay = 1000)
    public void update(){
        observable.subscribe(element -> template.convertAndSend("/topic/file", new EventMessage(element.getPath().toString(), element.getEvent())));
    }
}