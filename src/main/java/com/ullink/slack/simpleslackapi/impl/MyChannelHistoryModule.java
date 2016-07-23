/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.ullink.slack.simpleslackapi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.replies.GenericSlackReply;


/**
 * MyChannelHistoryModule.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/07/23 umjammer initial version <br>
 */
public class MyChannelHistoryModule {

    private final SlackSession session;

    private static final String FETCH_CHANNEL_HISTORY_COMMAND = "channels.history";
    private static final String FETCH_GROUP_HISTORY_COMMAND = "groups.history";
    private static final String FETCH_IM_HISTORY_COMMAND = "im.history";

    private static final int DEFAULT_HISTORY_FETCH_SIZE = 1000;

    /**
     * @param session
     */
    public MyChannelHistoryModule(SlackSession session) {
        this.session = session;
    }

    public List<SlackMessagePosted> fetchHistoryOfChannel(String channelId, String from, String to, int numberOfMessages) {
        Map<String, String> params = new HashMap<>();
        params.put("channel", channelId);
        if (from != null) {
            params.put("latest", from);
        }
        if (to != null) {
            params.put("oldest", to);
        }
        if (numberOfMessages > -1) {
            params.put("count", String.valueOf(numberOfMessages));
        } else {
            params.put("count", String.valueOf(DEFAULT_HISTORY_FETCH_SIZE));
        }
        SlackChannel channel = session.findChannelById(channelId);
        switch (channel.getType()) {
        case INSTANT_MESSAGING:
            return fetchHistoryOfChannel(params, FETCH_IM_HISTORY_COMMAND);
        case PRIVATE_GROUP:
            return fetchHistoryOfChannel(params, FETCH_GROUP_HISTORY_COMMAND);
        default:
            return fetchHistoryOfChannel(params, FETCH_CHANNEL_HISTORY_COMMAND);
        }
    }

    private List<SlackMessagePosted> fetchHistoryOfChannel(Map<String, String> params, String command) {
        SlackMessageHandle<GenericSlackReply> handle = session.postGenericSlackCommand(params, command);
        GenericSlackReply replyEv = handle.getReply();
        JSONObject answer = replyEv.getPlainAnswer();
        JSONArray events = (JSONArray) answer.get("messages");
        List<SlackMessagePosted> messages = new ArrayList<>();
        if (events != null) {
            for (Object event : events) {
                if ((((JSONObject) event).get("subtype") == null)) {
                    messages.add((SlackMessagePosted) SlackJSONMessageParser.decode(session, (JSONObject) event));
                }
            }
        }
        return messages;
    }

    public static List<SlackMessagePosted> toList(SlackSession session, SlackChannel channel, String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray events = (JSONArray) parser.parse(json);
            List<SlackMessagePosted> messages = new ArrayList<>();
            if (events != null) {
                for (Object event : events) {
                    JSONObject obj = (JSONObject) event; 
                    String ts = (String) obj.get("timestamp");
                    String text = (String) obj.get("messageContent");
                    String userId = (String) ((JSONObject) obj.get("user")).get("id");
                    SlackUser user = session.findUserById(userId);
                    Map<String, Integer> reacs = extractReactionsFromMessageJSON(obj);
                    SlackMessagePostedImpl message = new SlackMessagePostedImpl(text, null, user, channel, ts);
                    message.setReactions(reacs);
                    messages.add(message);
                }
            }
            return messages;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Map<String, Integer> extractReactionsFromMessageJSON(JSONObject obj) {
        Map<String, Integer> reacs = new HashMap<>();
        JSONObject rawReactions = (JSONObject) obj.get("reactions");
        if (rawReactions != null) {
            for (Object e : rawReactions.entrySet()) {
                @SuppressWarnings("unchecked")
                String emojiCode = ((Map.Entry<String, String>) e).getKey();
                @SuppressWarnings("unchecked")
                Integer count = ((Map.Entry<String, Long>) e).getValue().intValue();
                reacs.put(emojiCode, count);
            }
        }
        return reacs;
    }
}

/* */
