package ru.toxing.sd.TaskManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import ru.toxing.sd.TaskManager.dao.TaskDao;
import ru.toxing.sd.TaskManager.model.Task;
import ru.toxing.sd.TaskManager.model.TaskList;

import java.util.List;

@Controller
public class RequestController {

    private final TaskDao taskDao;

    public RequestController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @RequestMapping(value = "/lists", method = RequestMethod.GET)
    public String showTaskLists(ModelMap map) {
        List<TaskList> lists = taskDao.getAllLists();

        lists.forEach(l -> taskDao.getTasksByList(l.getId()).forEach(l::addTask));
        prepareModelMap(map, lists);
        return "index";
    }

    @RequestMapping(value = "/add-list", method = RequestMethod.POST)
    public String addList(@ModelAttribute("taskList") TaskList taskList) {
        taskDao.addList(taskList);
        return "redirect:/lists";
    }

    @RequestMapping(value = "/add-task", method = RequestMethod.POST)
    public String addTask(@ModelAttribute("task") Task task) {
        taskDao.addTask(task);
        return "redirect:/lists";
    }

    @RequestMapping(value = "/delete-list", method = RequestMethod.POST)
    public String deleteList(@RequestParam(name = "taskListId") int listId) {
        taskDao.deleteList(listId);
        return "redirect:/lists";
    }

    @RequestMapping(value = "/mark-task", method = RequestMethod.POST)
    public String markAsDone(@RequestParam(name = "taskId") int taskId) {
        taskDao.markAsDone(taskId);
        return "redirect:/lists";
    }

    @RequestMapping(value = "/delete-task", method = RequestMethod.POST)
    public String deleteTask(@RequestParam(name = "taskId") int taskId) {
        taskDao.deleteTask(taskId);
        return "redirect:/lists";
    }

    private void prepareModelMap(ModelMap map, List<TaskList> lists) {
        map.addAttribute("taskLists", lists);
        map.addAttribute("taskList", new TaskList());
        map.addAttribute("task", new Task());
    }
}
