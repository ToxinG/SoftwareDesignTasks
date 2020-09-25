package refactoring.servlet;

import java.sql.ResultSet;
import java.util.Map;

public class GetProductsServlet extends AbstractServlet {

    @Override
    protected void handleRequest(Map<String, String[]> params, HTMLBuilder html) throws Exception {
        String query = DBLib.selectOrderedColumn("NAME", "ASC", false);
        ResultSet rs = dbm.execute(query);
        html.addHeader("Existing products:");
        html.addResultSet(rs, "name", "price");
    }
}
