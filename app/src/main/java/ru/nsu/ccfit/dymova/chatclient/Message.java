package ru.nsu.ccfit.dymova.chatclient;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="Message")
public class Message {
    private final String author;
    private final String text;
    private int id;

    public Message(String author, String text, int id) {
        this.author = author;
        this.text = text;
        this.id = id;
    }

    @Attribute(name="Author")
    public String getAuthor() {
        return author;
    }

    @Attribute(name="Text")
    public String getText() {
        return text;
    }

    @Attribute(name="Id")
    public int getId() {
        return id;
    }
}

