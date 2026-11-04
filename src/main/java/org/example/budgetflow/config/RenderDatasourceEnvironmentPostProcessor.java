package org.example.budgetflow.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RenderDatasourceEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String raw = firstNonBlank(
                System.getenv("DATABASE_URL"),
                System.getenv("SPRING_DATASOURCE_URL"),
                environment.getProperty("SPRING_DATASOURCE_URL"),
                environment.getProperty("DATABASE_URL")
        );
        if (raw == null || raw.startsWith("jdbc:")) {
            return;
        }
        if (!raw.startsWith("postgres")) {
            return;
        }

        URI uri = URI.create(raw);
        String user = null;
        String password = "";
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            int colon = userInfo.indexOf(':');
            if (colon >= 0) {
                user = decode(userInfo.substring(0, colon));
                password = decode(userInfo.substring(colon + 1));
            } else {
                user = decode(userInfo);
            }
        }

        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = uri.getPath() == null ? "" : uri.getPath();
        String database = path.startsWith("/") ? path.substring(1) : path;
        String query = uri.getQuery();
        String jdbc = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        if (query == null || query.isBlank()) {
            jdbc += "?sslmode=require";
        } else {
            jdbc += "?" + query;
            if (!query.contains("sslmode")) {
                jdbc += "&sslmode=require";
            }
        }

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbc);
        if (user != null) {
            props.put("spring.datasource.username", user);
        }
        props.put("spring.datasource.password", password);
        environment.getPropertySources().addFirst(new MapPropertySource("render-datasource", props));
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
