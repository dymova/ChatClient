package ru.nsu.ccfit.dymova.chatclient;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "NewMessages")
public class UpdateResponseMessage {
    @ElementList(inline = true, name = "Message")
    public final List<Message> messages;

    public UpdateResponseMessage(List<Message> messages) {
        this.messages = messages;
    }
}