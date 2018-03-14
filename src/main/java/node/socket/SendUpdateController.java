package node.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@EnableScheduling
public class SendUpdateController {

    //https://medium.com/oril/spring-boot-websockets-angular-5-f2f4b1c14cee

    @Autowired
    private SimpMessagingTemplate template;


    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Message greeting(Message message){
        return  new Message("Welcome" + message);
    }

    @Scheduled(fixedDelay = 1000)
    public void sendAdhocMessages() {
        template.convertAndSend("/topic/greetings", new Message("Fixed Delay Scheduler"));
    }
}
