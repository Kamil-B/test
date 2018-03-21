package node.web;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import node.model.Event;
import node.model.EventMessage;
import node.model.EventType;
import node.model.SubscriptionMessage;
import node.service.DisposablesService;
import node.service.FileWatcherService;
import node.utils.NodeTree;
import node.utils.NodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.nio.file.Paths;

@Controller
@Slf4j

public class TopicController {

    @Autowired
    private DisposablesService disposablesService;

    @Autowired
    private FileWatcherService fileWatcherService;
    private Observable<Event> observable;

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/path")
    @SendTo("/topic/file")
    public void startStreaming(SubscriptionMessage message, SimpMessageHeaderAccessor headerAccessor) {
        observable = fileWatcherService.startWatching(Paths.get(message.getPath()));

        Disposable subscriber = Observable.fromIterable(new NodeTree<>(NodeUtils.createNodeTree(Paths.get(message.getPath()))))
                .map(element -> new Event(element.getPayload(), EventType.CREATE))
                .mergeWith(observable)
                .subscribe(element -> template.convertAndSend("/topic/file", new EventMessage(element.getPath().toString(), element.getEvent())));

        disposablesService.put(headerAccessor.getSessionId(), subscriber);
    }
}