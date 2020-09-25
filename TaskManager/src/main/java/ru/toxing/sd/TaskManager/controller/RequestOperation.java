//package ru.toxing.sd.TaskManager.controller;
//
//import static ru.toxing.sd.TaskManager.controller.Response.*;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.function.BiFunction;
//
//import org.json.JSONObject;
//
//import ru.toxing.sd.TaskManager.dao.DBAccess;
//import ru.toxing.sd.TaskManager.model.Task;
//
//public enum RequestOperation {
//
//    ADD_LIST((db, r) -> {
//        String name = r.getString("name").trim();
//
//        if (name.length() == 0) {
//            return error("Title cannot be empty");
//        }
//
//        try {
//            db.addList(name);
//        } catch (SQLException sqle) {
//            return error(sqle.toString());
//        }
//
//        return success();
//    }, "name"),
//
//    DELETE_LIST((db, r) -> {
//        long listID = r.getLong("list");
//
//        try {
//            db.deleteList(listID);
//        } catch (SQLException sqle) {
//            return error(sqle.toString());
//        }
//
//        return success();
//    }, "list"),
//
//    ADD_TASK((db, r) -> {
//        long listID = r.getLong("list");
//
//        String description = r.getString("description").trim();
//
//        if (description.length() == 0) {
//            return error("Description cannot be empty");
//        }
//
//        try {
//            db.addTask(listID, description);
//        } catch (SQLException sqle) {
//            return error(sqle.toString());
//        }
//
//        return success();
//    }, "list", "description"),
//
//    DELETE_TASK((db, r) -> {
//        long taskID = r.getLong("task");
//
//        try {
//            db.deleteTask(taskID);
//        } catch (SQLException sqle) {
//            return error(sqle.toString());
//        }
//
//        return success();
//    }, "task"),
//
//    MARK_TASK((db, r) -> {
//        long taskID = r.getLong("task");
//        Task task = db.getTask(taskID);
//
//        if (task == null) {
//            return error("Task not found");
//        }
//
//        try {
//            db.markAsDone(taskID);
//        } catch (SQLException sqle) {
//            return error(sqle.toString());
//        }
//
//        return success();
//    }, "task");
//
//    private final BiFunction<DBAccess, JSONObject, JSONObject> handler;
//    private final List<String> fields;
//
//    private RequestOperation(BiFunction<DBAccess, JSONObject, JSONObject> handler, String ... fields) {
//        this.fields = Collections.unmodifiableList(Arrays.asList(fields));
//        this.handler = handler;
//    }
//
//    public JSONObject handleRequest(DBAccess db, JSONObject input) {
//        return handler.apply(db, input);
//    }
//
//}
