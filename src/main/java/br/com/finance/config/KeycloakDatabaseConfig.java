package br.com.finance.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class KeycloakDatabaseConfig {

    @Bean
    @ConfigurationProperties("keycloak.datasource")
    public DataSourceProperties keycloakDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "keycloakDataSource")
    public DataSource keycloakDataSource(
            @Qualifier("keycloakDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "keycloakJdbcTemplate")
    public JdbcTemplate keycloakJdbcTemplate(@Qualifier("keycloakDataSource") DataSource keycloakDataSource) {
        return new JdbcTemplate(keycloakDataSource);
    }
}