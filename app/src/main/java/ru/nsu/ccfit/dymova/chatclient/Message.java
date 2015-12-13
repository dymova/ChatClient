package ru.nsu.ccfit.dymova.chatclient;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Message")
public class Message {
    @Element(name = "Author")
    private String author;

    @Element(name = "Text")
    private String text;

    @Element(name = "Id")
    private int id;

    public Message(String author, String text, int id) {
        this.author = author;
        this.text = text;
        this.id = id;
    }

    public Message() {
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
