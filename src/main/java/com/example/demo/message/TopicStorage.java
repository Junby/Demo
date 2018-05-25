package com.example.demo.message;

import java.util.Collection;

public interface TopicStorage
{
    public String save(Topic topic);

    public void update(Topic topic);

    public void delete(Topic topic);

    public Topic find(String id);

    public Collection<Topic> findAll();
}
