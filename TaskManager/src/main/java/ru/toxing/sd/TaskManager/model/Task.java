package ru.toxing.sd.TaskManager.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Task {
    public static enum TaskStatus {
        ACTIVE,
        DONE,
    }

    private int id;
    private int list;
    private String description;
    private int status;

    public Task() {}

    public Task(int id, int list, String description) {
        this.id = id;
        this.list = list;
        this.description = description;
        this.status = TaskStatus.ACTIVE.ordinal();
    }

    public Task(int id, int list, String description, int status) {
        this.id = id;
        this.list = list;
        this.description = description;
        this.status = status;
    }

    public void markAsDone() {
        if (status == TaskStatus.ACTIVE.ordinal()) {
            status = TaskStatus.DONE.ordinal();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task '").append(description).append("', status: ").append(TaskStatus.values()[status]);
        return sb.toString();
    }

    public static Task valueFrom(ResultSet rs, int listId) throws SQLException {
        String description = rs.getString("description");
        int status = rs.getInt("status");
        int id = rs.getInt("id");

        return new Task(id, listId, description, status);

    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getList() {
        return list;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setList(int list) {
        this.list = list;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
