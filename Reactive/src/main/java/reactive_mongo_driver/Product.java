package reactive_mongo_driver;

import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class Product {
    private static final String EUR = "eur";
    private static final String RUB = "rub";
    private static final String USD = "usd";
    private int id;
    private String name;
    private Map<String, String> priceByCurrency;

    public Product(Document doc) {
        this(doc.getInteger("id"), doc.getString("name"), doc.getString(EUR),
                doc.getString(RUB), doc.getString(USD));
    }

    public Product(int id, String name, String eur, String rub, String usd) {
        this.id = id;
        this.name = name;
        this.priceByCurrency = new HashMap<>();
        priceByCurrency.put(EUR, eur);
        priceByCurrency.put(RUB, rub);
        priceByCurrency.put(USD, usd);
    }

    public Document getDocument() {
        return new Document("id", id)
                .append("name", name)
                .append(EUR, priceByCurrency.get(EUR))
                .append(RUB, priceByCurrency.get(RUB))
                .append(USD, priceByCurrency.get(USD));
    }

    public String priceString(String currency) {
        return priceByCurrency.get(currency);
    }

    public String priceString() {
        StringBuilder pricesBuilder = new StringBuilder();
        for (Map.Entry<String, String> price : priceByCurrency.entrySet()) {
            pricesBuilder.append(price.getKey()).append(": ").append(price.getValue()).append(", ");
        }
        String prices = pricesBuilder.toString();
        return prices.substring(0, prices.length() - 2);
    }

    public String genericString(String priceString) {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + priceString +
                '}';
    }

    public String toString(String currency) {
        return genericString(priceString(currency));
    }

    @Override
    public String toString() {
        return genericString(priceString());
    }
}