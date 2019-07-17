package com.popbox.jan.simplebudget.Models;


public class ExpenseModel
{
   private int cost;
   private String category;
   private int day;
   private String description;
   
   public ExpenseModel(int cost, String category, int day, String description)
   {
      this.cost = cost;
      this.category = category;
      this.day = day;
      this.description = description;
   }
   
   public int getCost() {
      return cost;
   }
   
   public String getCategory() {
      return category;
   }
   
   public int getDay() {
      return day;
   }
   
   public String getDescription() {
      return description;
   }
}
