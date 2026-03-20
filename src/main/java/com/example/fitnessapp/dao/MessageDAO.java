package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Message;
import java.util.List;

public interface MessageDAO {
    List<Message> findConversation(int user1Id, int user2Id);
    List<Message> findReceivedMessages(int userId);
    void save(Message message);
    void delete(int id);
}