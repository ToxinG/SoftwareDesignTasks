package refactoring.servlet;

import java.util.Map;

public class AddProductServlet extends AbstractServlet {

    @Override
    public void handleRequest(Map<String, String[]> params, HTMLBuilder html) throws Exception {
        String name = params.get("name")[0];
        long price = Long.parseLong(params.get("price")[0]);
        String query = DBLib.addProduct(name, price);
        dbm.update(query);
        html.addLine("Product \"" + name + " " + price + "\" added successfully.");
    }
}