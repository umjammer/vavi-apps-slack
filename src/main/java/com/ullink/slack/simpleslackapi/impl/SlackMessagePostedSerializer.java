/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.ullink.slack.simpleslackapi.impl;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ullink.slack.simpleslackapi.SlackFile;


/**
 * SlackMessagePostedSerializer. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/07/26 umjammer initial version <br>
 */
public class SlackMessagePostedSerializer implements JsonSerializer<SlackMessagePostedImpl> {

    static class MySlackMessagePosted {
        String messageContent;
        String user;
        String timestamp;
        SlackFile slackFile;
        Map<String, Integer> reactions;
    }

    private Gson gson = new Gson();

    /* @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext) */
    @Override
    public JsonElement serialize(SlackMessagePostedImpl src, Type typeOfSrc, JsonSerializationContext context) {
        MySlackMessagePosted mySlackMessagePosted = new MySlackMessagePosted();
        mySlackMessagePosted.messageContent = src.getMessageContent();
        mySlackMessagePosted.timestamp = src.getTimeStamp();
        mySlackMessagePosted.user = src.getSender().getId();
        mySlackMessagePosted.reactions = src.getReactions();
        mySlackMessagePosted.slackFile = src.getSlackFile();
        return gson.toJsonTree(mySlackMessagePosted);
    }
}

/* */
