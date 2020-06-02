package model;

public class Stock {
    public int amount;
    public double price;

    public Stock(int amount, double price) {
        this.amount = amount;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "amount=" + amount +
                ", price=" + price +
                '}';
    }
}