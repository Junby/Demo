package com.example.demo.worker;

import com.example.demo.message.Message;

public class OperationFailure extends Message
{
    public OperationFailure (String eventType, String name, String displayName)
    {
        put("type", eventType);
        put("name", name);
        put("displayName", displayName);
    }
}
