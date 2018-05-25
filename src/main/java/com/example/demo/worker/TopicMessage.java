package com.example.demo.worker;

import com.example.demo.message.Message;

import java.util.Optional;

public class TopicMessage
{
    private TopicBuilder.BaseTopic topic;

    private Message message;

    public TopicMessage(TopicBuilder.BaseTopic topic, Message message)
    {
        this.topic = topic;
        this.message = message;
    }

    public TopicBuilder.BaseTopic getTopic()
    {
        return topic;
    }

    public Message getMessage()
    {
        return message;
    }

    public String getAuthor()
    {
        return topic.getAuthor();
    }

    public static class Builder
    {
        private TopicBuilder.BaseTopic topic;

        private MessageState state;

        private String displayName;

        private String resourceId;

        public static Builder success(TopicBuilder.BaseTopic topic)
        {
            Builder builder = new Builder();
            builder.topic = topic;
            builder.state = MessageState.SUCCESS;
            builder.resourceId = topic.getId();
            builder.displayName = (String) topic.getContent().getOrDefault("displayName", "");
            return builder;
        }

        public static Builder failure(TopicBuilder.BaseTopic topic)
        {
            return failure(topic, null);
        }

        public static Builder failure(TopicBuilder.BaseTopic topic, String errorCode)
        {
            Builder builder = new Builder();
            builder.topic = topic;
            builder.state = MessageState.FAILURE;
            builder.resourceId = topic.getId();
            builder.displayName = (String) topic.getContent().getOrDefault("displayName", "");
            builder.topic.getContent().put("error", Optional.ofNullable(errorCode).orElse("unknown"));
            return builder;
        }

        public static Builder topic(TopicBuilder.BaseTopic topic)
        {
            Builder builder = new Builder();
            builder.topic = topic;
            builder.resourceId = topic.getId();
            builder.displayName = (String) topic.getContent().getOrDefault("displayName", "");
            return builder;
        }

        public Builder state(MessageState state)
        {
            this.state = state;
            return this;
        }

        public Builder displayName(String displayName)
        {
            this.displayName = displayName;
            return this;
        }

        public Builder resourceId(String resourceId)
        {
            this.resourceId = resourceId;
            return this;
        }

        public TopicMessage build()
        {
            EventType eventType = EventType.from((String)topic.getContent().get("eventType"));
            Message message = null;
            switch (state)
            {
                case FAILURE:
                    message = new OperationFailure(eventType.toString(), resourceId, displayName);
                    message.put("error", topic.getContent().get("error"));
                    break;
                case SUCCESS:
                    message = new OperationSuccess(eventType.toString(), resourceId, displayName);
                    break;
                case DOING:
                    default:
                        break;
            }
            topic.getContent().put("status", state.getText());
            if(message != null)
            {
                message.put("content", topic.getContent());
            }
            return new TopicMessage(topic, message);
        }
    }
}
