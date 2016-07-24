package vavi.apps.slack;
/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import com.google.gson.Gson;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackChannel.SlackChannelType;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.MyChannelHistoryModule;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static java.io.File.separator; 


/**
 * Main. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/07/22 umjammer initial version <br>
 */
@PropsEntity(url = "file://${HOME}/.vavi_apps_slack.properties")
public class Main {

    @Property(name = "{0}.token")
    private String token;
    @Property(name = "{0}.user")
    private String username;
    private String group;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Main app = new Main();
        app.group = args[0];
        PropsEntity.Util.bind(app, app.group);
        app.proceed();
    }

    /** */
    void proceed() throws IOException {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(token);
        session.connect();

        SlackUser user = session.findUserByUserName(username);
        Collection<SlackChannel> channels = session.getChannels();

        channels.stream()
            .filter(channel -> channel.getMembers().contains(user))
            .forEach(channel -> saveHistory(session, channel, user));

        channels.stream()
            .filter(channel -> channel.isDirect())
            .forEach(channel -> saveHistory(session, channel, user));

        System.exit(0);
    }

    /** */
    private static final int NUMBER_OF_FETCH_MESSAGES = 1000;

    /** */
    private static final String DEFAULT_DATA_ROOT_DIRECTORY = "tmp";

    /** */
    private void saveHistory(SlackSession session, SlackChannel channel, SlackUser user) throws UncheckedIOException {
        try {
            Gson gson = new Gson();
            String directory = channel.isDirect() ? "direct" : (channel.getType().equals(SlackChannelType.PRIVATE_GROUP) ? "private" : "channel");
            String filename = channel.isDirect() ? channel.getMembers().iterator().next().getRealName() : channel.getName();
            Path path = Paths.get(DEFAULT_DATA_ROOT_DIRECTORY + separator + group + separator + directory + separator + filename + ".json");
            System.err.println("path: " + Files.exists(path) + ": " + path);

            String last = null;
            List<SlackMessagePosted> pasts = null;
            if (Files.exists(path)) {
                String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                pasts = MyChannelHistoryModule.toList(session, channel, json);
//                pasts.forEach(System.err::println);
                last = pasts.get(0).getTimestamp();
                System.err.println("last: " + (last != null ? toLocalDate(last) : null));
            }

            List<SlackMessagePosted> messages = getHistory(session, channel, user, last);
            System.err.println("news: " + messages.size());

            if (messages.size() > 0) {
                if (pasts != null) {
                    messages.addAll(pasts);
                }
                String json = gson.toJson(messages);
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                Files.write(path, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @param to get after the date, nullable. 
     */
    private List<SlackMessagePosted> getHistory(SlackSession session, SlackChannel channel, SlackUser user, String to) {
        System.err.println((channel.isDirect() ? "(+) " : (channel.getType().equals(SlackChannelType.PRIVATE_GROUP) ? "(@) " : "(*) ")) + channel.getId() + ": " + (channel.getName() != null ? channel.getName() : channel.getMembers().iterator().next().getRealName()));
        MyChannelHistoryModule channelHistoryModule = new MyChannelHistoryModule(session);
        List<SlackMessagePosted> result = new ArrayList<>();
        String from = null;
        List<SlackMessagePosted> messages;
        do {
            messages = channelHistoryModule.fetchHistoryOfChannel(channel.getId(), from, to, NUMBER_OF_FETCH_MESSAGES);
            if (messages.size() > 0) {
//                System.err.println("got: " + messages.size() + ", from: " + (date != null ? toLocalDate(date) : date));
//                System.err.println("first: " + toLocalDate(messages.get(0).getTimestamp()) + " : " + messages.get(0).toString());
//                System.err.println("last : " + toLocalDate(messages.get(messages.size() - 1).getTimestamp()) + " : " + messages.get(messages.size() - 1).toString());

                from = messages.get(messages.size() - 1).getTimestamp();

                result.addAll(messages);
            }
        } while (messages.size() > 0);
        System.err.println("--- TOTAL: " + result.size());
        return result;
    }

    /** */
    private LocalDate toLocalDate(String slackTimestamp) {
        long secondsSinceEpoch = Long.parseLong(slackTimestamp.substring(0, slackTimestamp.indexOf('.')));
        long nanoAdjustment = Long.parseLong(slackTimestamp.substring(slackTimestamp.indexOf('.') + 1));
        Instant instant = Instant.ofEpochSecond(secondsSinceEpoch, nanoAdjustment);
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        return zdt.toLocalDate();
    }
}

/* */
