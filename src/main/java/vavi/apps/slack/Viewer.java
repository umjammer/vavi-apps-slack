package vavi.apps.slack;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;


public class Viewer {

    public static void main(String[] args) {

        get("/", (request, response) -> {
            try {
                Path resourceRoot = Paths.get("tmp");
                List<String> groups = Files.list(resourceRoot).map(p -> p.getFileName().toString()).collect(Collectors.toList());

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("groups", groups);
                return new ModelAndView(attributes, "groups.mustache");
            } catch (Exception e) {
                e.printStackTrace();
                halt(500, e.getMessage());
                return null;
            }
        }, new MustacheTemplateEngine());

        get("/categories/:group", (req, res) -> {
            try {
                Path resourceRoot = Paths.get("tmp", req.params("group"));
                List<String> categories = Files.list(resourceRoot).map(p -> p.getFileName().toString()).collect(Collectors.toList());

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("group", req.params("group"));
                attributes.put("categories", categories);
                return new ModelAndView(attributes, "categories.mustache");
            } catch (Exception e) {
                e.printStackTrace();
                halt(500, e.getMessage());
                return null;
            }
        }, new MustacheTemplateEngine());

        get("/logs/:group/:category", (req, res) -> {
            try {
                Path resourceRoot = Paths.get("tmp", req.params("group"), req.params("category"));
                List<String> logs = Files.list(resourceRoot).map(p -> p.getFileName().toString()).collect(Collectors.toList());

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("group", req.params("group"));
                attributes.put("category", req.params("category"));
                attributes.put("logs", logs);
                return new ModelAndView(attributes, "logs.mustache");
            } catch (Exception e) {
                e.printStackTrace();
                halt(500, e.getMessage());
                return null;
            }
        }, new MustacheTemplateEngine());

        get("/messages/:group/:category/:log", (req, res) -> {
            try {
                Path log = Paths.get("tmp", req.params("group"), req.params("category"), req.params("log"));
                Reader reader = new FileReader(log.toFile());

                Type type = new TypeToken<List<Message>>(){}.getType();
                List<Message> messages = gson.fromJson(reader, type);

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("group", req.params("group"));
                attributes.put("category", req.params("category"));
                attributes.put("log", req.params("log"));
                attributes.put("messages", messages);
                return new ModelAndView(attributes, "messages.mustache");
            } catch (Exception e) {
                e.printStackTrace();
                halt(500, e.getMessage());
                return null;
            }
        }, new MustacheTemplateEngine());

        //port(8080);
        System.err.println("welcome to slack viewer: " + port());
    }

    static Gson gson = new GsonBuilder().create();

    static class Message {
        String messageContent;
        String user;
        String timestamp;
        Map<String, Integer> reactions;
    }
}
