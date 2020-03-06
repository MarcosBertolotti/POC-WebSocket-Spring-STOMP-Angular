package com.springboot.backend.chat.services;

import com.springboot.backend.chat.models.documents.Message;
import com.springboot.backend.chat.repository.IChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements IChatService {

    @Autowired
    private IChatRepository chatRepository;

    @Override
    public List<Message> getLast25Messages() {

        return chatRepository.findFirst25ByOrderByDateDesc();
    }

    @Override
    public Message save(Message message) {

        return chatRepository.save(message);
    }
}
