package org.example.budgetflow.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class RenderDataSourceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RenderDataSourceConfiguration.class);

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String fallbackUrl,
            @Value("${spring.datasource.username}") String fallbackUser,
            @Value("${spring.datasource.password}") String fallbackPassword
    ) {
        String raw = firstNonBlank(System.getenv("DATABASE_URL"), System.getenv("SPRING_DATASOURCE_URL"));
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("org.postgresql.Driver");

        if (raw != null && raw.startsWith("postgres")) {
            Parsed parsed = parse(raw);
            ds.setJdbcUrl(parsed.jdbcUrl);
            ds.setUsername(parsed.username);
            ds.setPassword(parsed.password);
            log.info("Using Render DATABASE_URL host {}", parsed.host);
            return ds;
        }

        if (raw != null && raw.startsWith("jdbc:")) {
            ds.setJdbcUrl(raw);
            ds.setUsername(fallbackUser);
            ds.setPassword(fallbackPassword);
            log.info("Using SPRING_DATASOURCE_URL");
            return ds;
        }

        ds.setJdbcUrl(fallbackUrl);
        ds.setUsername(fallbackUser);
        ds.setPassword(fallbackPassword);
        log.info("Using fallback datasource {}", fallbackUrl.replaceAll(":[^:/]+@", ":****@"));
        return ds;
    }

    static Parsed parse(String raw) {
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
        return new Parsed(jdbc, user, password, host);
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

    static final class Parsed {
        final String jdbcUrl;
        final String username;
        final String password;
        final String host;

        Parsed(String jdbcUrl, String username, String password, String host) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
            this.host = host;
        }
    }
}
