package refactoring.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

public abstract class AbstractServlet extends HttpServlet {

    DBManager dbm;

    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HTMLBuilder html = new HTMLBuilder();
        try {
            dbm = new DBManager();
            Map<String, String[]> params = request.getParameterMap();
            handleRequest(params, html);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(html);

    }

    protected abstract void handleRequest(Map<String, String[]> params, HTMLBuilder html) throws Exception;
}
