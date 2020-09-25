package ru.toxing.sd.TaskManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.toxing.sd.TaskManager.dao.TaskDao;
import ru.toxing.sd.TaskManager.dao.TaskJdbcDao;

import javax.sql.DataSource;

@Configuration
public class JdbcDaoContextConfiguration {
    @Bean
    public TaskDao taskDao(DataSource dataSource) {
        return new TaskJdbcDao(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:taskmanager.db");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}