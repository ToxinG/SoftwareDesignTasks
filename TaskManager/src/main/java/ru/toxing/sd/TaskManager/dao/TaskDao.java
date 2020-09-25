package ru.toxing.sd.TaskManager.dao;

import ru.toxing.sd.TaskManager.model.Task;
import ru.toxing.sd.TaskManager.model.TaskList;

import java.util.List;

public interface TaskDao {
    List<TaskList> getAllLists();

    List<Task> getTasksByList(int listId);

    int addList(TaskList list);

    int deleteList(int listId);

    int addTask(Task task);

    int deleteTask(int taskId);

    int markAsDone(int taskId);
}
