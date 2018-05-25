package com.example.demo.worker;

import com.example.demo.message.Message;

public class OperationSuccess extends Message
{
    public OperationSuccess(String eventType, String name, String displayName)
    {
        put("type", eventType);
        put("name", name);
        put("displayName", displayName);
    }
}
