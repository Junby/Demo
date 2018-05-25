package com.example.demo.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Message;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE;

@ManagedService(path = "/messages/{channel}", atmosphereConfig = MAX_INACTIVE + "=120000")
public class MessageService
{
    private final Log logger = LogFactory.getLog(getClass());

    @Message
    public String onMessage(String channel)
            throws IOException
    {
        return null;
    }

    @Ready
    public void onReady(final AtmosphereResource r)
            throws  IOException
    {
        String uri = r.getRequest().getRequestURI();
        String channel = uri.replaceFirst(".*\\/messages", "");
        logger.info("Browser " + r.uuid() + " connected,channel: " + channel);
        r.getResponse().setContentType("application/json;charset=UTF-8");

        //only login user can subscribe to message channels
        //TODO
        String sessionLoginUserKey = "xxxx";
        HttpSession session = r.getRequest().getSession(true);
        String userName = (String) session.getAttribute(sessionLoginUserKey);
        if(userName == null)
        {
            r.close();
            return;
        }

        //make a new broadcaster with Id associated with login user.
        String broadcasterId = "/" + userName + channel;

        // 1) destroy default broadcaster if id not match
        Broadcaster defaultBroadcaster = r.getBroadcaster();
        if(defaultBroadcaster != null && !defaultBroadcaster.getID().equals(broadcasterId))
        {
            defaultBroadcaster.destroy();
        }

        // 2) lookup or create a broadcaster with new Id, and associate it with the AtmosphereResource
        BroadcasterFactory broadcasterFactory = r.getAtmosphereConfig().getBroadcasterFactory();
        Broadcaster broadcaster = broadcasterFactory.lookup(broadcasterId, true);
        r.setBroadcaster(broadcaster);
        broadcaster.addAtmosphereResource(r);

        Messager.getInstance().trySetBroadcasterFactory(broadcasterFactory);
    }

    public static void main(String[] args)
    {
        System.out.println("/messages/*".replaceFirst(".*\\/messages", ""));
    }
}
