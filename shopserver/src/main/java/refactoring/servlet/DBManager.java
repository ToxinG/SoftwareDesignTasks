package refactoring.servlet;

import java.sql.*;

public class DBManager implements AutoCloseable{
    Connection c;
    Statement stmt;

    public DBManager() throws SQLException {
        c = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = c.createStatement();
    }

    public ResultSet execute(String query) throws SQLException {
        return stmt.executeQuery(query);
    }

    public void update(String query) throws SQLException {
        stmt.executeUpdate(query);
    }

    @Override
    public void close () throws Exception {
        if (this.stmt != null) {
            this.stmt.close ();
        }
    }
}
