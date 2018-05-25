package com.example.demo.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Messager
{
    private static final Log logger = LogFactory.getLog(Messager.class);

    private volatile BroadcasterFactory broadcasterFactory = null;

    private final AtomicBoolean broadcasterFactoryInitialized = new AtomicBoolean(false);

    private volatile Map<String, Map<String, Receiver>> channelPatterns = new ConcurrentHashMap<>();

    public synchronized void addReceiver(String channelPattern, Receiver receiver)
    {
        Map<String, Receiver> receivers = channelPatterns.get(channelPattern);
        if(receivers == null)
        {
            receivers = new ConcurrentHashMap<>();
            channelPatterns.put(channelPattern, receivers);
        }
        receivers.put(receiver.getId(), receiver);
        logger.info("channel pattern [" +channelPattern + "] added receiver [" + receiver.getId() + "]");
    }

    public synchronized void removeReceiver(String channelPattern, String receiverId)
    {
        Map<String, Receiver> receivers = channelPatterns.get(channelPattern);
        if(receivers != null)
        {
            receivers.remove(receiverId);
            if(receivers.isEmpty())
            {
                channelPatterns.remove(channelPattern);
            }
        }
    }

    private static String toRegexp(String channelPattern)
    {
        channelPattern = "^/" + channelPattern.replaceAll("^\\/ + ","");
        return channelPattern.replaceAll("\\*", ".*").replaceAll("\\/", "\\\\/");
    }

    private boolean matches(String channel, String channelPattern)
    {
        return channel.matches(toRegexp(channelPattern));
    }

    private void publishToServerReceivers(String channel, Message message)
    {
        String[] keys = channelPatterns.keySet().toArray(new String[0]);
        for (String channelPattern : keys)
        {
            Map<String, Receiver> receivers = null;
            if(matches(channel, channelPattern) && (receivers = channelPatterns.get(channelPattern)) != null)
            {
                for (Receiver r : receivers.values())
                {
                    r.receive(message);
                }
            }
        }
    }

    private void publishToBrowsers(String channel, Message message)
    {
        if(broadcasterFactory == null)
        {
            return;
        }

        Collection<Broadcaster> broadcasters = broadcasterFactory.lookupAll();
        for (Broadcaster b : broadcasters)
        {
            String channelPattern = b.getID().replaceFirst("^\\/[^\\/]\\/", "/"); // delete the username
            if (matches(channel, channelPattern))
            {
                b.broadcast(message);
            }
        }
    }

    public void publish(String channel, Message message)
    {
        message = (Message) message.clone();
        message.put("_channel_", channel);
        publishToServerReceivers(channel, message);
        publishToBrowsers(channel, message);
    }

    public void publish(Topic topic, Message message)
    {
        publish(topic.getChannel(), message);
    }

    private void privatePublishToBrowsers(String user, String channel, Message message)
    {
        if(broadcasterFactory == null)
        {
            return;
        }

        Collection<Broadcaster> broadcasters = broadcasterFactory.lookupAll();
        for (Broadcaster b : broadcasters)
        {
            if (!b.getID().startsWith("/" + user + "/"))
            {
                continue;
            }

            String channelPattern = b.getID().replaceFirst("^\\/[^\\/] + \\/", "/"); // delete the username
            if (matches(channel, channelPattern))
            {
                b.broadcast(message);
            }
        }
    }

    public void privatePublish(String user, String channel, Message message)
    {
        message = (Message) message.clone();
        message.put("_channel_", channel);
        logger.info("privatePublic user: " + user + " channel: " + channel + " message:" + message.toJSONString());

        publishToServerReceivers(channel, message);
        privatePublishToBrowsers(user, channel, message);
    }

    public void privatePublish(String user, Topic topic, Message message)
    {
        privatePublish(user, topic.getChannel(), message);
    }

    private Messager()
    {
    }

    public static Messager getInstance()
    {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder
    {
        private static Messager instance = new Messager();
    }

    boolean trySetBroadcasterFactory(BroadcasterFactory broadcasterFactory)
    {
        if(!broadcasterFactoryInitialized.getAndSet(true))
        {
            this.broadcasterFactory = broadcasterFactory;
            return true;
        }
        else
        {
            return false;
        }
    }
}
