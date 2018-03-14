package node.socket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendUpdateController {

    //https://medium.com/oril/spring-boot-websockets-angular-5-f2f4b1c14cee

    @MessageMapping("/user")
    @SendTo("/topic/user")
    public String send(){
        return "TEST";
    }
}
