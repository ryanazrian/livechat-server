package com.example.ryanazrian.livechat.controllers;

import com.example.ryanazrian.livechat.model.Message;
import com.example.ryanazrian.livechat.payload.chat.OutputMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage message(Message message) throws Exception {

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getName(), message.getMessage(), time);
    }

    @MessageMapping("/topic2/{room}")
    @SendTo("/topic2/{room}")
    public OutputMessage message2(@DestinationVariable String room, Message message) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getName(), message.getMessage(), time);
    }
}
