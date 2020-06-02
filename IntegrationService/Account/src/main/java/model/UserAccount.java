package model;

import java.util.HashMap;
import java.util.Map;

public class UserAccount {
    public double balance;
    public Map<Integer, Integer> stocks = new HashMap<>();

    public UserAccount(double balance) {
        this.balance = balance;
    }
}