package com.example.demo.message;

public interface Receiver
{
    public String getId();

    public void receive(Message message);
}
