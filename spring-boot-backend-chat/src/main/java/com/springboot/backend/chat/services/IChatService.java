package com.springboot.backend.chat.services;

import com.springboot.backend.chat.models.documents.Message;

import java.util.List;

public interface IChatService {

    List<Message> getLast25Messages();

    Message save(Message message);
}
