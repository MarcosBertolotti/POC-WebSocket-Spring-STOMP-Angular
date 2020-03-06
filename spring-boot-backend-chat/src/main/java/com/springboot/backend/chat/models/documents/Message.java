package com.springboot.backend.chat.models.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message implements Serializable {

    @Id
    private String id;

    private String text;
    private Long date;
    private String username;
    private String type;
    private String color;

    private static final long serialVersionUID = 1L;
}
