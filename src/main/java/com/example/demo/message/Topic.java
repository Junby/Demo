package com.example.demo.message;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Topic implements Serializable
{
    private String id;

    protected Date createdAt;

    protected Date closedAt;

    protected boolean closed;

    protected String channel;

    protected String url;

    protected String title;

    protected String author;

    protected Map<String, Object> content = new HashMap<>();

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt)
    {
        this.closedAt = closedAt;
    }

    public boolean isClosed()
    {
        return closed;
    }

    public void setClosed(boolean closed)
    {
        this.closed = closed;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Map<String, Object> getContent()
    {
        return content;
    }

    public void setContent(Map<String, Object> content)
    {
        this.content = content;
    }

    public static class TopicAdd extends Message
    {
        public TopicAdd (Topic topic)
        {
            put("topic", topic.toMap());
        }
    }

    public static class TopicClose extends Message
    {
        public TopicClose (Topic topic)
        {
            put("topic", topic.toMap());
        }
    }

    public static class TopicDelete extends Message
    {
        public TopicDelete (Topic topic)
        {
            put("topic", topic.toMap());
        }
    }


    public static class TopicUpdate extends Message
    {
        public TopicUpdate (Topic topic)
        {
            put("topic", topic.toMap());
        }
    }


    public static class TopicClean extends Message
    {
        public TopicClean ()
        {

        }
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("_type_", getClass().getSimpleName());
        map.put("id", id);
        map.put("createdAt", getTime(createdAt));
        map.put("closedAt", getTime(closedAt));
        map.put("closed", closed);
        map.put("channel", channel);
        map.put("url", url);
        map.put("title", title);
        map.put("author", author);
        map.put("content", content);
        return map;
    }

    public Long getTime(Date date)
    {
        return date == null ? null : date.getTime();
    }

    @Override
    public String toString()
    {
        return "Topic{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", closedAt=" + closedAt +
                ", closed=" + closed +
                ", channel='" + channel + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content=" + content +
                '}';
    }

    public void delete()
    {
        TopicManager.storage.delete(this);
    }
    public void update()
    {
        TopicManager.storage.update(this);
    }

    public void save()
    {
        TopicManager.storage.save(this);
    }

    public void close()
    {
        closed = true;
        closedAt = new Date();
        update();
    }

}
