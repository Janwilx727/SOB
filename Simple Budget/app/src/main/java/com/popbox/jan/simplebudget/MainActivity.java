package com.popbox.jan.simplebudget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.popbox.jan.simplebudget.Models.BudgetModel;
import com.popbox.jan.simplebudget.Models.CategoryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView lvBudget;
    ArrayList<BudgetModel> budgetList = new ArrayList<>();
    ArrayAdapter<BudgetModel> adapter;
    SQLiteDatabase myDatabase;

    TextView tvTotalBudget;
    TextView tvTotalAvailable;
    ConstraintLayout clArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null)
        {
            String color = getResources().getString(R.string.yellow);
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Current Month Data</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
        }

        lvBudget = findViewById(R.id.lvBudget);

        myDatabase = this.openOrCreateDatabase(Global.budgetDatabaseName, MODE_PRIVATE, null);

        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvTotalAvailable = findViewById(R.id.tvTotalAvailable);
        clArrow = findViewById(R.id.clArrow);

        initTables();
    }

    public void initTables()
    {
        String CreateCategoryCommand = "CREATE TABLE IF NOT EXISTS " +
                ""+Global.categoryTable+" (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                "Name VARCHAR," +
                "Category VARCHAR," +
                "isDeleted BIT)";

        String CreateExpenseCommand = "CREATE TABLE IF NOT EXISTS " +
                ""+Global.expenseTable+" (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                "Cost VARCHAR," +
                "Description VARCHAR," +
                "FK_Category INT," +
                "Day INT," +
                "Month INT," +
                "Year INT)";

        String CreateBudgetCommand = "CREATE TABLE IF NOT EXISTS " +
                ""+Global.budgetTable+" (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                "BudgetAmount VARCHAR," +
                "FK_Category INT)";
        
        String CreateBudgetDate = "CREATE TABLE IF NOT EXISTS " +
                ""+Global.budgetDateTable+" (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                "BudgetMonth VARCHAR, " +
                "BudgetDay INT)";

        myDatabase.execSQL(CreateCategoryCommand);
        myDatabase.execSQL(CreateExpenseCommand);
        myDatabase.execSQL(CreateBudgetCommand);
        myDatabase.execSQL(CreateBudgetDate);

        getBudgetDate();
    }


    int budgetDay;
    public void getBudgetDate()
    {
        Calendar cal = Calendar.getInstance();
        String monthName = Global.monthArray[cal.get(Calendar.MONTH)];
        
        String dayFetch = "SELECT * FROM "+Global.budgetDateTable+" WHERE BudgetMonth = '"+monthName+"'";
        Cursor cursor = myDatabase.rawQuery(dayFetch, null);
        if (cursor.getCount() == 0)
        {
            insertAllMonths();
            budgetDay = Global.daysInMonth[cal.get(Calendar.MONTH)];
        }
        else
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                budgetDay = cursor.getInt(2);
            }
        }
        cursor.close();
        getBudgetCategories();
    }

    List<CategoryModel> catList = new ArrayList<>();
    public void getBudgetCategories()
    {
        String CatFetch = "SELECT * FROM "+Global.categoryTable+" WHERE isDeleted = 0 ORDER BY Name";

        Cursor cursor = myDatabase.rawQuery(CatFetch, null);
        
        if (cursor != null)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String categoryType = cursor.getString(2);

                catList.add(new CategoryModel(id,name,"0",0, categoryType));
            }

            fetchCategoryBudgets(catList);

            cursor.close();
        }

    }

    public void fetchCategoryBudgets(List<CategoryModel> catList)
    {
        for (CategoryModel cat : catList)
        {
            String fetchBudgetSQL = "SELECT * FROM "+Global.budgetTable+" WHERE FK_Category = "+cat.getID()+"";
            Cursor budgetCursor = myDatabase.rawQuery(fetchBudgetSQL, null);
            if (budgetCursor.getCount() <= 0)
            {
                cat.setBudgetTotal("0");
            }
            else
            {
                budgetCursor.moveToNext();
                cat.setBudgetTotal(budgetCursor.getString(1));
            }
            budgetCursor.close();
        }
//        fetchCategoryExpenses(catList);
    
    
        budgetList.add(0, new BudgetModel(99999, "", 0,0, true, "Multiple Expenses", ""));
        fetchCategoryExpenses(catList, Global.MULTIPLE);
        budgetList.add(budgetList.size(), new BudgetModel(99999, "", 0,0, true, "Once-Off Expenses",""));
        fetchCategoryExpenses(catList, Global.DEBIT_ORDER);
        
        if (budgetList.size() == 2)
        {
            clArrow.setVisibility(View.VISIBLE);
            lvBudget.setVisibility(View.GONE);
            return;
        }
        
        setupList();
    
    }
    
    

    
    public void fetchCategoryExpenses(List<CategoryModel> catList, String CategoryType)
    {
       Calendar cal = Calendar.getInstance();
       int year = cal.get(Calendar.YEAR);
       int currDay = cal.get(Calendar.DAY_OF_MONTH);
       
       
        
        for(CategoryModel cat : catList)
        {
            if (!cat.getCategoryType().equals(CategoryType))
                continue;
            
            String ExpFetch = "";
            
            if(cal.get(Calendar.MONTH) == 0)
            {
                //Accounts for Jan
                int monthEnd = currDay >= budgetDay ? 1 : 0;
                int monthStart = monthEnd == 0 ? 11 : 0;
                
                int yearEnd = year;
                int yearStart = monthStart == 11 ? (year-1) : year;
    
                ExpFetch =
                        "SELECT * FROM "+Global.expenseTable+" " +
                        "WHERE FK_Category = "+cat.getID()+" " +
                        "AND ((Day >= "+budgetDay+" AND Month = "+monthStart+" AND Year = "+yearStart+") " +
                        "OR (Day < "+budgetDay+" AND Month = "+monthEnd+" AND Year = "+yearEnd+"))";
    
            }
            else if (cal.get(Calendar.MONTH) == 11)
            {
                //Accounts for Dec
                int monthEnd = currDay >= budgetDay ? 0 : 11;
                int monthStart = monthEnd == 0 ? 11 : 10;
   
                int yearEnd = year;
                int yearStart = monthStart == 11 ? year : (year + 1);
                
                ExpFetch =
                        "SELECT * FROM "+Global.expenseTable+" " +
                        "WHERE FK_Category = "+cat.getID()+" " +
                        "AND ((Day < "+budgetDay+" AND Month = "+monthEnd+" AND Year = "+yearEnd+") " +
                        "OR (Day >= "+budgetDay+" AND Month = "+monthStart+" AND Year = "+yearStart+")";
            }
            else
            {
                //Accounts for FEB - NOV
                int monthEnd = currDay >= budgetDay ? (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH);
                int monthStart = monthEnd - 1;
                
                ExpFetch =
                        "SELECT * FROM "+Global.expenseTable+" " +
                        "WHERE FK_Category = "+cat.getID()+" " +
                        "AND ((Day < "+budgetDay+" AND Month = "+monthEnd+")" +
                        "OR (Day >= "+budgetDay+" AND Month = "+monthStart+")) " +
                        "AND Year = " + year;
            }
            
            Cursor expCursor = myDatabase.rawQuery(ExpFetch, null);

            if(expCursor != null)
            {
                double totalCost = 0;
                for (expCursor.moveToFirst(); !expCursor.isAfterLast(); expCursor.moveToNext())
                {
                    double cost = Double.parseDouble(expCursor.getString(1));
                    totalCost = totalCost + cost;
                    
                    int mo = expCursor.getInt(5);
                    int d = expCursor.getInt(4);
                    int yr = expCursor.getInt(6);
                }
                expCursor.close();

                if (cat.getCategoryType().equals(Global.DEBIT_ORDER))
                {
                    Double budgetTotal = Double.parseDouble(cat.getBudgetTotal());
                    budgetList.add(new BudgetModel(cat.getID(), cat.getName(), budgetTotal, budgetTotal, false, "", cat.getCategoryType()));
                }
                else
                {
                    budgetList.add(new BudgetModel(cat.getID(), cat.getName(), Double.parseDouble(cat.getBudgetTotal()), totalCost, false, "", cat.getCategoryType()));
                }
            }
            else
            {
                budgetList.add(new BudgetModel(cat.getID(), cat.getName(), 0, 0, false, "", cat.getCategoryType()));
            }
        }

    }



    public void setupList()
    {
        adapter = new BudgetAdapter();
        lvBudget.setAdapter(adapter);
        lvBudget.setDivider(null);

        calculateTotals();
    }

    public void calculateTotals()
    {
        AsyncTask.execute(new Runnable()
        {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void run()
            {
                double bud = 0.0;
                double cost = 0.0;
                for(BudgetModel model : budgetList)
                {
                    bud += model.getTotal();
                    cost += model.getTotalCost();
                }
                final String totBudget = String.valueOf(bud);
                final String totAvailable = String.valueOf(bud-cost);

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        tvTotalBudget.setText("Total Budgeted \n"+totBudget);
                        tvTotalAvailable.setText("Total Available \n"+totAvailable);
                    }
                });
            }
        });
    }




    public class BudgetAdapter extends ArrayAdapter<BudgetModel>
    {
        private BudgetAdapter()
        {
            super(MainActivity.this, R.layout.adapter_budget, budgetList);
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent)
        {
            View itemView = convertView;
            if (itemView == null)
            {
                itemView = getLayoutInflater().inflate(R.layout.adapter_budget, parent, false);
            }
            final BudgetModel itemInfo = budgetList.get(position);

            TextView tvName = itemView.findViewById(R.id.tvBudgetName);
            TextView tvTotal = itemView.findViewById(R.id.tvTotal);
            TextView tvAvailable = itemView.findViewById(R.id.tvAvailable);
            TextView tvPerDay = itemView.findViewById(R.id.tvPerDay);
            CardView cvCard = itemView.findViewById(R.id.cvCard);
            
            TextView tvHeader = itemView.findViewById(R.id.tvHeader);
            if (itemInfo.isHeader())
            {
                tvHeader.setVisibility(View.VISIBLE);
                cvCard.setVisibility(View.GONE);
                tvHeader.setText(itemInfo.getHeaderText());
            }
            else
            {
                tvHeader.setVisibility(View.GONE);
                cvCard.setVisibility(View.VISIBLE);
            }
            
            cvCard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(MainActivity.this, CategoryInfo.class);
                    i.putExtra("CatID",itemInfo.getCatID());
                    i.putExtra("CatName",itemInfo.getDescription());
                    i.putExtra("Budget",itemInfo.getTotal());
                    i.putExtra("CategoryType", itemInfo.getCategoryType());
                    startActivity(i);
                    finish();
                }
            });

            tvName.setText(itemInfo.getDescription());
            tvTotal.setText("Total Budgeted: \nR " + String.format("%.2f", itemInfo.getTotal()));

            Double available = itemInfo.getTotal() - itemInfo.getTotalCost();
            tvAvailable.setText("Total Available: \nR " + String.format("%.2f", available));

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            
//            int daysLeft = daysInMonth[month] - day;
            
            //TODO: Get days between budget dates
            int daysLeft = calculateDays() + 1;
            
            Double perDay = daysLeft == 0 ? available : available/daysLeft;

            tvPerDay.setText("Per day: \nR " + String.format("%.2f", perDay));

            return itemView;
        }
    }
    
    
    public int calculateDays()
    {
        //budgetDay
        Calendar cal = Calendar.getInstance();
        int currDay = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH);
    
        long startMilli = System.currentTimeMillis();
        
        String endDate;
        
        if(cal.get(Calendar.MONTH) == 0)
        {
            //Accounts for Jan
            int monthEnd = currDay >= budgetDay ? 1 : 0;
            String endDateMonth = Global.monthArray[monthEnd];
        
            endDate = budgetDay + "-" + endDateMonth + "-" + year;
        }
        else if (cal.get(Calendar.MONTH) == 11)
        {
            //Accounts for Dec
            int monthEnd = currDay >= budgetDay ? 0 : 11;
            
            String endDateMonth = Global.monthArray[monthEnd];
            int endDateYear = monthEnd == 0 ? (year + 1) : year;
            
            endDate = budgetDay+"-"+endDateMonth+"-"+endDateYear;
        }
        else
        {
            //Accounts for FEB - NOV
            int monthEnd = currDay >= budgetDay ? (cal.get(Calendar.MONTH) + 1) : cal.get(Calendar.MONTH);
            String endDateMonth = Global.monthArray[monthEnd];
            
            endDate = budgetDay+"-"+endDateMonth+"-"+year;
        }
    
    
        long endMilli = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try
        {
            Date date = format.parse(endDate);
            endMilli = date.getTime();
        }
        catch (Exception e)
        {
            Log.i("AppInfo",e.getMessage());
        }
        
        long timeDiff = Math.abs(startMilli - endMilli);
    
        float dayCount = (float) timeDiff / (24 * 60 * 60 * 1000);
    
        int days = Math.round(dayCount);
        return days;
    }
    
    public void insertAllMonths()
    {
        for (int i = 0; i < Global.monthArray.length; i++)
        {
            String SaveCommand = "INSERT INTO "+Global.budgetDateTable+" (BudgetMonth, BudgetDay) " +
                    "VALUES ('"+Global.monthArray[i]+"', "+Global.daysInMonth[i]+")";
            
            myDatabase.execSQL(SaveCommand);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
           case R.id.addCategory:
                startActivity(new Intent(MainActivity.this, AddCategory.class));
                finish();
                break;
           case R.id.mnuArchived:
                startActivity(new Intent(MainActivity.this, ArchiveActivity.class));
                finish();
                break;
           case R.id.mnuHistory:
                startActivity(new Intent(MainActivity.this,ExpenseHistory.class));
                finish();
              break;
            case R.id.mnuPaydate:
                startActivity(new Intent(MainActivity.this, SetBudgetDate.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
