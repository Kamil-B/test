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
import node.utils.NodePathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.nio.file.Paths;
import java.util.stream.Stream;

@Controller
@Slf4j
public class TopicController {

    @Autowired
    private DisposablesService disposablesService;
    @Autowired
    private FileWatcherService fileWatcherService;
    @Autowired
    private SimpMessagingTemplate template;

    private Observable<Event> observable;

    @MessageMapping("/path")
    public void sendUpdates(SubscriptionMessage message, SimpMessageHeaderAccessor header) {

        observable = fileWatcherService.startWatching(Paths.get(message.getPath()));
        Disposable subscriber = observable.subscribe(element ->
                template.convertAndSend("/topic/file", new EventMessage(element.getPath().toString(), element.getEvent())));
        disposablesService.put(header.getSessionId(), subscriber);
    }

    @SubscribeMapping("/tree/{path}")
    public Stream<EventMessage> sendActualPaths(@DestinationVariable String path) {
        return NodePathUtils.getNodeTreePaths(Paths.get(path))
                .map(element -> new EventMessage(element.toString(), EventType.CREATE));
    }
}