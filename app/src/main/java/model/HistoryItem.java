package model;

public class HistoryItem {
    private String orderName;
    private String orderDate;
    private double totalAmount;

    public HistoryItem(String orderName, String orderDate, double totalAmount) {
        this.orderName = orderName;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
