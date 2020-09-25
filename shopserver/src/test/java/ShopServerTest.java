import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import refactoring.servlet.AddProductServlet;
import refactoring.servlet.GetProductsServlet;
import refactoring.servlet.QueryServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class ShopServerTest {

    private static Server server;

    private void dbInit() throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:test.db")) {
            c.createStatement().executeUpdate("DROP TABLE IF EXISTS PRODUCT");
            String sql = "CREATE TABLE PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        }

    }

    @BeforeAll
    public static void init() throws Exception {

        server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet()), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet()),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet()),"/query");

        server.start();
    }

    @Nested
    public class TestServlets {

        @Test
        public void TestAddProduct() throws Exception {
            dbInit();

            HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8081/add-product?name=iphone6&price=300").openConnection();
            http.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            assertEquals("OK", in.readLine());
        }

        @Test
        public void TestGetProducts() throws Exception {
            dbInit();

            HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8081/add-product?name=iphone6&price=300").openConnection();
            http.connect();
            http.getInputStream();
            http = (HttpURLConnection)new URL("http://localhost:8081/get-products").openConnection();
            http.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            assertEquals("<html><body>", in.readLine());
            assertEquals("iphone6\t300</br>", in.readLine());
            assertEquals("</body></html>", in.readLine());
        }

        @Test
        public void TestQuery() throws Exception {
            dbInit();

            HttpURLConnection http = (HttpURLConnection)new URL("http://localhost:8081/add-product?name=iphone6&price=300").openConnection();
            http.connect();
            http.getInputStream();
            http = (HttpURLConnection)new URL("http://localhost:8081/query?command=sum").openConnection();
            http.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            assertEquals("<html><body>", in.readLine());
            assertEquals("Summary price: ", in.readLine());
            assertEquals("300", in.readLine());
            assertEquals("</body></html>", in.readLine());
        }
    }

    @AfterAll
    public static void cleanUp() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
