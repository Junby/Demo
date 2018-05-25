package com.example.demo.worker;

import com.example.demo.message.Topic;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TopicBuilder
{
    private EventType eventType;
    private String username;
    private String resourceId;
    private Map<String,String> customContent = new HashMap<>();
    private String url;

    public static TopicBuilder eventType(EventType eventType)
    {
        TopicBuilder builder = new TopicBuilder();
        builder.eventType = eventType;
        return builder;
    }

    public TopicBuilder username(String username)
    {
        this.username = username;
        return this;
    }

    public TopicBuilder putContent(String key, String value)
    {
        this.customContent.put(key, value);
        return this;
    }

    public TopicBuilder operation(HttpServletRequest request)
    {
        this.url = request.getRequestURI();
        return this;
    }

    public static class BaseTopic extends Topic {
        Map<String, Object> map;
        private String url;

        private BaseTopic(TopicBuilder builder) {
            builder.resourceId = UUID.randomUUID().toString();
            setAuthor(builder.username);
            setTitle(builder.eventType + " " + builder.resourceId);
            setCreatedAt(new Date());
            setChannel("/" + builder.eventType.getChannel() + "/" + builder.eventType + "/" + builder.resourceId);
            setId(builder.resourceId);
            content.putAll(builder.customContent);
            content.put("status", MessageState.DOING.getText());
            content.put("eventType", builder.eventType + "");
            this.url = builder.url;
            map = super.toMap();
            map.put("_type_", builder.eventType.toString());
        }

        @Override
        public Map<String, Object> toMap()
        {
            return map;
        }

        public MessageState getStatus()
        {
            return MessageState.from(content.get("status") + "");
        }

        public String getRequestUrl()
        {
            return url;
        }

        public void setMessage(String key, String value)
        {
            content.put(key, value);
        }
    }

    public BaseTopic build()
    {
        return new BaseTopic(this);
    }
}
