package refactoring.servlet;

public class DBLib {

    public static String createTable() {
        return new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS PRODUCT (")
                .append("ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
                .append(" NAME           TEXT    NOT NULL,")
                .append(" PRICE          INT     NOT NULL)")
                .toString();
    }

    public static String addProduct(String name, long price) {
        return new StringBuilder()
                .append("INSERT INTO PRODUCT ")
                .append("(NAME, PRICE) VALUES ")
                .append("(\"").append(name).append("\",").append(price).append(")")
                .toString();
    }

    public static String selectOrderedColumn(String column, String order, boolean limit) {
        return new StringBuilder()
                .append("SELECT * FROM PRODUCT ")
                .append("ORDER BY ").append(column).append(" ")
                .append(order).append(" ")
                .append(limit ? "LIMIT 1" : "")
                .toString();
    }

    public static String selectAggregation(String function, String column) {
        return new StringBuilder()
                .append("SELECT ")
                .append(function).append("(").append(column).append(") ")
                .append("FROM PRODUCT")
                .toString();
    }
}
