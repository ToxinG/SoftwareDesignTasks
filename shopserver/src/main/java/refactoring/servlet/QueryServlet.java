package refactoring.servlet;

import java.sql.ResultSet;
import java.util.Map;

/**
 * @author akirakozov
 */
public class QueryServlet extends AbstractServlet {

    @Override
    protected void handleRequest(Map<String, String[]> params, HTMLBuilder html) throws Exception {
        String command = params.get("command")[0];
        if ("max".equals(command)) {
            String query = DBLib.selectOrderedColumn("price", "DESC", true);
            ResultSet rs = dbm.execute(query);
            html.addHeader("Product with max price: ");
            html.addResultSet(rs, "name", "price");
        } else if ("min".equals(command)) {
            String query = DBLib.selectOrderedColumn("price", "ASC", true);
            ResultSet rs = dbm.execute(query);
            html.addHeader("Product with min price: ");
            html.addResultSet(rs, "name", "price");
        } else if ("sum".equals(command)) {
            String query = DBLib.selectAggregation("SUM", "price");
            ResultSet rs = dbm.execute(query);
            html.addHeader("Summary price: ");
            html.addLine(Integer.toString(rs.getInt(1)));
        } else if ("count".equals(command)) {
            String query = DBLib.selectAggregation("COUNT", "*");
            ResultSet rs = dbm.execute(query);
            html.addHeader("Number of products: ");
            html.addLine(Integer.toString(rs.getInt(1)));
        }
    }
}