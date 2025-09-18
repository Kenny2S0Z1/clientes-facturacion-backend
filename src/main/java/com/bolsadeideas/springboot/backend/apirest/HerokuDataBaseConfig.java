package com.bolsadeideas.springboot.backend.apirest;

import java.net.URI;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("heroku")
public class HerokuDataBaseConfig {

    @Value("${JAWSDB_URL}")
    private String jawsdbUrl;

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        if (jawsdbUrl != null && !jawsdbUrl.isEmpty()) {

            URI dbUri = new URI(jawsdbUrl);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String jdbcUrl = "jdbc:mysql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() +
                    "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            return DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        }
        return null;
    }
}
