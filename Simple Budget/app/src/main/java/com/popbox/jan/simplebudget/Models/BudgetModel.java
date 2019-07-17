package com.popbox.jan.simplebudget.Models;

public class BudgetModel
{
    private int catID;
    private String description;
    private double total;
    private double totalCost;
    private boolean isHeader;
    private String headerText;
    private String categoryType;

    public BudgetModel(int catID, String description, double total, double totalCost, boolean isHeader, String headerText, String categoryType)
    {
        this.catID = catID;
        this.description = description;
        this.total = total;
        this.totalCost = totalCost;
        this.isHeader = isHeader;
        this.headerText = headerText;
        this.categoryType = categoryType;
    }

    public int getCatID() {
        return catID;
    }

    public String getDescription() {
        return description;
    }

    public double getTotal() {
        return total;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setTotalCost(double available) {
        this.totalCost = available;
    }
    
    public void setHeader(boolean header) {
        isHeader = header;
    }
    
    public boolean isHeader() {
        return isHeader;
    }
    
    
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }
    
    public String getHeaderText() {
        return headerText;
    }
    
    public String getCategoryType() {
        return categoryType;
    }
    
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
