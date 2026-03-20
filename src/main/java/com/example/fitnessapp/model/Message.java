package com.example.fitnessapp.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Message {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty senderId = new SimpleIntegerProperty();
    private final IntegerProperty receiverId = new SimpleIntegerProperty();
    private final StringProperty content = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> sentAt = new SimpleObjectProperty<>();

    public Message() {}

    public Message(int id, int senderId, int receiverId, String content, LocalDateTime sentAt) {
        this.id.set(id);
        this.senderId.set(senderId);
        this.receiverId.set(receiverId);
        this.content.set(content);
        this.sentAt.set(sentAt);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getSenderId() { return senderId.get(); }
    public IntegerProperty senderIdProperty() { return senderId; }
    public void setSenderId(int senderId) { this.senderId.set(senderId); }

    public int getReceiverId() { return receiverId.get(); }
    public IntegerProperty receiverIdProperty() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId.set(receiverId); }

    public String getContent() { return content.get(); }
    public StringProperty contentProperty() { return content; }
    public void setContent(String content) { this.content.set(content); }

    public LocalDateTime getSentAt() { return sentAt.get(); }
    public ObjectProperty<LocalDateTime> sentAtProperty() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt.set(sentAt); }
}