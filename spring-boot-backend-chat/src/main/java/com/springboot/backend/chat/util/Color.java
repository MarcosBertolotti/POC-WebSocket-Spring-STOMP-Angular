package com.springboot.backend.chat.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Color {

    RED,
    GREEN,
    BLUE,
    MAGENTA,
    PURPLE,
    ORANGE;

    public static List<String> getColors(){

        return Arrays.stream(Color.values())
                .map(color -> color.name().toLowerCase())
                .collect(Collectors.toList());
    }

}
