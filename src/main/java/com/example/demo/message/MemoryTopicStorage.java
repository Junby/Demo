package com.example.demo.message;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryTopicStorage implements TopicStorage
{
    private ConcurrentHashMap<String, Topic> ALL = new ConcurrentHashMap<>();

    public MemoryTopicStorage()
    {

    }

    @Override
    public String save(Topic topic)
    {
        if(topic.getId() != null)
        {
            update(topic);
        }
        else
        {
            topic.setId(UUID.randomUUID().toString());
        }
        ALL.put(topic.getId(), topic);
        return topic.getId();
    }

    @Override
    public void update(Topic topic)
    {
        if(topic.getId() != null)
        {
            ALL.put(topic.getId(), topic);
        }
    }

    @Override
    public void delete(Topic topic)
    {
        if(topic.getId() != null)
        {
            ALL.remove(topic.getId());
        }
    }

    @Override
    public Topic find(String id)
    {
        return ALL.get(id);
    }

    public Collection<Topic> findAll()
    {
        return ALL.values();
    }
}
