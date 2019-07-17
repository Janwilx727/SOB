package com.popbox.jan.simplebudget;

import android.app.Application;

public class Global extends Application
{
    public static String expenseTable = "Expenses";
    public static String categoryTable = "Categories";
    public static String budgetTable = "Budgets";
    public static String budgetDateTable = "BudgetDate";
    public static String budgetDatabaseName = "db_Budget";
    
    // Category Table
    public static final String DEBIT_ORDER = "Debit_Order";
    public static final String MULTIPLE = "Multiple";

    //These Integers are for the isDeleted Column.
    public static int nonDeletedInt = 0;
    public static int archiveInt = 1;
    public static int deleteInt = 2;
    

    //Column Names
    //Categories
    public static int cat_ID_col = 0;
    public static int cat_NAME_col = 1;
    public static int cat_DELETED_col = 1;
    
    public static int[] daysInMonth
            = new int[]
                {
                    31,
                    28,
                    31,
                    30,
                    31,
                    30,
                    31,
                    31,
                    30,
                    31,
                    30,
                    31
                };
    
    public static String[] monthArray = new String[]
   {
      "Jan",
              "Feb",
              "Mar",
              "Apr",
              "May",
              "Jun",
              "Jul",
              "Aug",
              "Sep",
              "Oct",
              "Nov",
              "Dec"
   };
}
