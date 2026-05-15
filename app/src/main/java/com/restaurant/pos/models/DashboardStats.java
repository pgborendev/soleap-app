package com.restaurant.pos.models;

public class DashboardStats {
    private double todayRevenue;
    private int activeOrders;
    private int occupiedTables;
    private int totalTables;
    private double avgOrderValue;
    private int pendingKitchenOrders;

    public double getTodayRevenue() { return todayRevenue; }
    public int getActiveOrders() { return activeOrders; }
    public int getOccupiedTables() { return occupiedTables; }
    public int getTotalTables() { return totalTables; }
    public double getAvgOrderValue() { return avgOrderValue; }
    public int getPendingKitchenOrders() { return pendingKitchenOrders; }

    public void setTodayRevenue(double v) { this.todayRevenue = v; }
    public void setActiveOrders(int v) { this.activeOrders = v; }
    public void setOccupiedTables(int v) { this.occupiedTables = v; }
    public void setTotalTables(int v) { this.totalTables = v; }
    public void setAvgOrderValue(double v) { this.avgOrderValue = v; }
    public void setPendingKitchenOrders(int v) { this.pendingKitchenOrders = v; }
}
