package ru.toxing.sd.TaskManager.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import ru.toxing.sd.TaskManager.model.Task;
import ru.toxing.sd.TaskManager.model.TaskList;

import javax.sql.DataSource;
import java.util.List;

public class TaskJdbcDao extends JdbcDaoSupport implements TaskDao {

    public TaskJdbcDao(DataSource dataSource) {
        super();
        setDataSource(dataSource);
    }

    @Override
    public List<TaskList> getAllLists(){
        String sql = "SELECT * FROM lists ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(TaskList.class));
    }

    @Override
    public List<Task> getTasksByList(int listId) {
        String sql = "SELECT * FROM tasks WHERE list=" + listId + " ORDER BY id";
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<>(Task.class));
    }

    @Override
    public int addList(TaskList taskList) {
        String sql = "INSERT INTO lists (name) VALUES (?)";
        return getJdbcTemplate().update(sql, taskList.getName());
    }

    @Override
    public int deleteList(int listId) {
        return getJdbcTemplate().update("DELETE FROM lists WHERE id = " + listId + "; DELETE FROM tasks WHERE list = " + listId);
    }

    @Override
    public int addTask(Task task) {
        String sql = "INSERT INTO tasks (list, description, status) VALUES (?, ?, ?)";
        return getJdbcTemplate().update(sql, task.getList(), task.getDescription(), 0);
    }

    @Override
    public int deleteTask(int taskId) {
        return getJdbcTemplate().update("DELETE FROM tasks WHERE id = "+ taskId);
    }

    @Override
    public int markAsDone(int taskId) {
        return getJdbcTemplate().update("UPDATE tasks SET status = 1 WHERE id = " + taskId);
    }

//    Connection c;
//    Statement stmt;
//
//    public TaskJdbcDao() throws SQLException {
//        c = DriverManager.getConnection("jdbc:sqlite:taskmanager.db");
//        stmt = c.createStatement();
//    }
//
//    public ResultSet execute(String query) throws SQLException {
//        return stmt.executeQuery(query);
//    }
//
//    public void update(String query) throws SQLException {
//        stmt.executeUpdate(query);
//    }
//
//    @Override
//    public void close () throws Exception {
//        if (this.stmt != null) {
//            this.stmt.close ();
//        }
//    }
}
