package ru.toxing.sd.TaskManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.toxing.sd.TaskManager.dao.TaskDao;
import ru.toxing.sd.TaskManager.dao.TaskInMemoryDao;

@Configuration
public class InMemoryDaoContextConfiguration {
    @Bean
    public TaskInMemoryDao taskInMemoryDao() {
        return new TaskInMemoryDao();
    }
}
