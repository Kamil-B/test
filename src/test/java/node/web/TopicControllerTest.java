package node.web;

import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;
import node.model.EventMessage;
import node.model.EventType;
import node.model.SubscriptionMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopicControllerTest {

    @Value("${local.server.port}")
    private int port;

    private String WEBSOCKET_URI;
    private final String WEBSOCKET_TOPIC = "/topic/file";
    private final String WEBSOCKET_APP = "/app/path";
    private final String WEBSOCKET_TREE = "/app/tree/";
    private BlockingQueue<EventMessage> events;
    private FrameHandler frameHandler = new FrameHandler();

    @Before
    public void setup() {
        WEBSOCKET_URI = "ws://localhost:" + String.valueOf(port) + "/websocket";
        events = new LinkedBlockingDeque<>();
    }

    @After
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get("src\\test\\resources\\test.txt"));
        Files.deleteIfExists(Paths.get("src\\test\\resources\\existed.txt"));
    }

    @Test
    public void subscribeToTopic_receiveMessageFromServer() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        EventMessage expectedMessage = new EventMessage("src\\test\\resources\\test.txt", EventType.CREATE);
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        WebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new TextWebSocketHandler(), WEBSOCKET_URI);

        StompSession stompSession = stompClient.connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {
        }).get(5, SECONDS);

        stompSession.send(WEBSOCKET_APP, new SubscriptionMessage("src\\test\\resources", "test"));
        stompSession.subscribe(WEBSOCKET_TREE + "C:\\\\git\\\\tree_test2", frameHandler);
        stompSession.subscribe(WEBSOCKET_TOPIC, frameHandler);
        Thread.sleep(100);
        Files.createFile(Paths.get("src\\test\\resources\\test.txt"));

        EventMessage actualMessage = events.poll(100, MILLISECONDS);
        stompSession.disconnect();

        assertThat(actualMessage).isNotNull();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class FrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return EventMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            events.offer((EventMessage) o);
        }
    }
}