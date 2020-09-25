package ru.toxing.sd.TaskManager.dao;

import ru.toxing.sd.TaskManager.model.Task;
import ru.toxing.sd.TaskManager.model.TaskList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// class is never used so something may be implemented incorrectly
public class TaskInMemoryDao implements TaskDao {
    private final AtomicInteger lastListId = new AtomicInteger(0);
    private final AtomicInteger lastTaskId = new AtomicInteger(0);

    private final HashMap<Integer, TaskList> taskLists = new HashMap<>();
    private final List<Task> tasks = new CopyOnWriteArrayList<>();

    @Override
    public List<TaskList> getAllLists() {
        return new ArrayList<TaskList>(taskLists.values());
    }

    @Override
    public List<Task> getTasksByList(int listId) {
        return taskLists.get(listId).getTasks();
    }

    @Override
    public int addList(TaskList list) {
        int id = lastListId.incrementAndGet();
        list.setId(id);
        taskLists.put(list.getId(), list);
        return id;
    }

    @Override
    public int deleteList(int listId) {
        taskLists.remove(listId);
        return listId;
    }

    @Override
    public int addTask(Task task) {
        int id = lastTaskId.incrementAndGet();
        task.setId(id);
        tasks.add(task);
        TaskList list = taskLists.remove(task.getList());
        list.addTask(task);
        taskLists.put(list.getId(), list);
        return id;
    }

    @Override
    public int deleteTask(int taskId) {
        tasks.remove(taskId);
        return taskId;
    }

    @Override
    public int markAsDone(int taskId) {
        return taskId;
    }
}
