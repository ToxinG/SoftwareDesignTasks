package refactoring.servlet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class HTMLBuilder {
    private final StringBuilder sb = new StringBuilder();

    public HTMLBuilder() {
        sb.append("<html><body>");
    }

    @Override
    public String toString() {
        return sb.append("</body></html>").toString();
    }

    public HTMLBuilder addLine(String str) {
        sb.append(str).append("<br>");
        return this;
    }

    public HTMLBuilder addHeader(String str) {
        sb.append("<h1>").append(str).append("</h1>");
        return this;
    }

    public HTMLBuilder addResultSet(ResultSet rs, String ... columns) throws SQLException {
        while (rs.next()) {
            Arrays.asList(columns).forEach(c -> {
                try {
                    sb.append(rs.getString(c)).append("\t");
                } catch (SQLException e) {
                    throw new IllegalStateException(e);
                }
            });
            sb.append("<br>");
        }
        return this;
    }
}
