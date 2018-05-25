package com.example.demo.worker;

import com.google.common.base.CaseFormat;
import org.hibernate.sql.CaseFragment;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum EventType
{
    TEST("test"),
    UNKNOWN("unknown");

    private String channel;

    EventType(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public static final Map<String, EventType> map = Stream.of(EventType.values())
            .collect(Collectors.toMap(EventType::getChannel, Function.identity(), (x, y) -> x));


    public static String toUpperCamel(String string)
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
    }

    public static EventType from(String text)
    {
        return map.getOrDefault(text, UNKNOWN);
    }

    public String toString()
    {
        return toUpperCamel(name());
    }
}
