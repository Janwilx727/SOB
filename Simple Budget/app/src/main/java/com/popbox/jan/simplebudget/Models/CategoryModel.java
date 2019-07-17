package com.popbox.jan.simplebudget.Models;

public class CategoryModel
{
    private int ID;
    private String Name;
    private String BudgetTotal;
    private  int isDeleted;
    private String categoryType;

    public CategoryModel(int ID, String Name,String BudgetTotal, int isDeleted, String categoryType)
    {
        this.ID = ID;
        this.Name = Name;
        this.BudgetTotal = BudgetTotal;
        this.isDeleted = isDeleted;
        this.categoryType = categoryType;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getBudgetTotal() {
        return BudgetTotal;
    }

    public int getIsDeleted() {
        return isDeleted;
    }
    
    public String getCategoryType() {
        return categoryType;
    }
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setBudgetTotal(String budgetTotal) {
        BudgetTotal = budgetTotal;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
