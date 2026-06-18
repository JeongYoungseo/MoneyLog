package kr.ac.kopo.moneylog.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String type;
    private int amount;
    private String category;
    private String memo;
    private String date;

    public Transaction() {
    }
    public Transaction(String type, int amount, String category, String memo, String date){
        this.type = type;
        this.amount = amount;
        this. category = category;
        this.memo = memo;
        this. date = date;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
