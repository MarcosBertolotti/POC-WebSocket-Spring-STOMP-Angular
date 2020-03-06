package com.springboot.backend.chat.repository;

import com.springboot.backend.chat.models.documents.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IChatRepository extends MongoRepository<Message, String> {

    List<Message> findFirst25ByOrderByDateDesc();
}
