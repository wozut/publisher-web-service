package tcla.contexts.realtimecollaboration.webapi.websocket

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.util.HtmlUtils

@Controller
class WebSocketController(

) {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    fun execute(message: HelloMessage): Greeting {
        Thread.sleep(1000)
        return Greeting(content = "Hello, " + HtmlUtils.htmlEscape(message.name) + "!")
    }
}
