package com.example.demo.message;

import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TopicManager
{
    static TopicStorage storage = new MemoryTopicStorage();

    public TopicManager()
    {

    }

    public static class SingletonHolder
    {
        private static TopicManager instance = new TopicManager();
    }

    public static TopicManager getInstance()
    {
        return SingletonHolder.instance;
    }

    public Stream<Topic> all()
    {
        return storage.findAll().stream();
    }

    private void publish(Topic topic, Message message)
    {
        Messager.getInstance().privatePublish(topic.getAuthor(), "/topics", message);
    }

    private void publish(String username, Message message)
    {
        Messager.getInstance().privatePublish(username, "/topics", message);
    }

    public static Predicate<Topic> openned()
    {
        return (t) -> !t.isClosed();
    }

    public static Predicate<Topic> closed()
    {
        return (t) -> t.isClosed();
    }

    public static Predicate<Topic> ofAuthor(String author)
    {
        return ((t) -> t.getAuthor() != null ? t.getAuthor().equals(author) : author == null);
    }

    public static Predicate<Topic> titleContains(String keyword)
    {
        return ((t) -> t.getTitle() != null && t.getTitle().contains(keyword));
    }

    public static Predicate<Topic> isCreatedIn(int hours)
    {
        Date date = new Date();
        return ((t) -> t.getCreatedAt() != null ? DateUtils.addHours(t.getCreatedAt(), hours).after(date) : date == null);
    }

    public static Predicate<Topic> ofAuthorAndInLastTwoHour(String author)
    {
        return ofAuthor(author).and(isCreatedIn(2));
    }

    public void close(Topic topic)
    {
        topic.close();
        publish(topic, new Topic.TopicUpdate(topic));
        publish(topic, new Topic.TopicClose(topic));
    }

    public void delete(Topic topic)
    {
        topic.delete();
        publish(topic, new Topic.TopicDelete(topic));
    }

    public void update(Topic topic)
    {
        topic.update();
        publish(topic, new Topic.TopicUpdate(topic));
    }

    public void add(Topic topic)
    {
        topic.save();;
        publish(topic, new Topic.TopicAdd(topic));
    }

    public Topic find(String id)
    {
        return storage.find(id);
    }

    public void clean(String username)
    {
        TopicManager.getInstance().all().filter(ofAuthor(username).and(closed())).forEach(Topic::delete);
        publish(username, new Topic.TopicClean());
    }
}
