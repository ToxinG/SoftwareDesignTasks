package model;

public class Company {
    int id;
    public Stock stock;

    public Company(int id, double price, int amount) {
        this.id = id;
        this.stock = new Stock(amount, price);
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", stock=" + stock +
                '}';
    }
}