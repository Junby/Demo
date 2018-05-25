package com.example.demo.worker;

import com.example.demo.message.Messager;
import com.example.demo.message.Topic;
import com.example.demo.message.TopicManager;

public class TopicMessager
{
    public synchronized static void open(Topic topic)
    {
        TopicManager.getInstance().add(topic);
    }

    public synchronized static void close(Topic topic)
    {
        TopicManager.getInstance().close(topic);
    }

    public synchronized  static void publish(TopicMessage message)
    {
        Messager.getInstance().privatePublish(message.getAuthor(), message.getTopic(), message.getMessage());
        MessageState state = message.getTopic().getStatus();
        if(state.equals(MessageState.SUCCESS))
        {
            //TODO
        }
        else
        {
            //TODO
        }
    }
}
