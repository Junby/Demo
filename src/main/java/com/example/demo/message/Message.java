package com.example.demo.message;

import org.json.simple.JSONObject;

import java.util.Map;

public class Message extends JSONObject
{
    public Message()
    {
        super();
    }

    public Message(Map<String, ?> map)
    {
        super(map);
    }

    public String toJSONString()
    {
        JSONObject cloned = (JSONObject) super.clone();
        cloned.put("_style_", getClass().getSimpleName());
        return JSONObject.toJSONString(cloned);
    }
}
