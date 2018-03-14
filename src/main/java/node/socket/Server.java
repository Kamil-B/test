package node.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.nio.file.Path;

@Slf4j
@ServerEndpoint("/watcher")
public class Server {

    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathVariable("path") Path path) {
        this.session = session;
        log.info("endpoint opened!! " + path);
    }
}
