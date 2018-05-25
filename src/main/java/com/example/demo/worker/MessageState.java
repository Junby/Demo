package com.example.demo.worker;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MessageState
{
    DOING("doing"),
    SUCCESS("success"),
    FAILURE("failure"),
    UNKNOWN("unknown");

    private String text;

    MessageState(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public static final Map<String, MessageState> map = Stream.of(MessageState.values())
            .collect(Collectors.toMap(MessageState::getText, Function.identity(), (x,y) -> x));


    public static MessageState from(String text)
    {
        return map.getOrDefault(text, UNKNOWN);
    }
}
