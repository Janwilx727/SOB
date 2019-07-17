package com.popbox.jan.simplebudget;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.popbox.jan.simplebudget.Helpers.DateHelper;

import java.util.Arrays;

public class CategoryInfo extends AppCompatActivity
{

    //region Fields
      DateHelper _dateHelper;
    //endregion
   
    EditText etNewBudget;
    EditText etExpense;
    EditText etExpenseDescription;
    TextView tvCurrentBudget;
    TextView tvCatName;
    
    Button btnDay;
    Button btnMonth;
    Button btnYear;

    SQLiteDatabase myDatabase;
    int catID;
    boolean initLoad;
    String categoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);
        
        if (getSupportActionBar() != null)
        {
            String color = getResources().getString(R.string.yellow);
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Category Info</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        etNewBudget = findViewById(R.id.etNewBudget);
        etExpense = findViewById(R.id.etExpense);
        tvCurrentBudget = findViewById(R.id.tvCurrentBudget);
        etExpenseDescription = findViewById(R.id.etExpenseDescription);
        tvCatName = findViewById(R.id.tvCatName);
        
        
        
        
        _dateHelper = new DateHelper();
        
    
    
    
        btnDay = findViewById(R.id.btnDay);
        btnMonth = findViewById(R.id.btnMonth);
        btnYear = findViewById(R.id.btnYear);
        SetupDates();

        myDatabase = this.openOrCreateDatabase(Global.budgetDatabaseName, MODE_PRIVATE, null);

        Intent i = getIntent();
        catID = i.getIntExtra("CatID",0);
        String catName = i.getStringExtra("CatName");
        double budget = i.getDoubleExtra("Budget", 0);
        categoryType = i.getStringExtra("CategoryType");

        tvCatName.setText(catName);
        tvCurrentBudget.setText(String.valueOf(budget));
        
        initLoad = true;
    }
    
    public void SetupDates()
    {
        btnDay.setText(String.valueOf(_dateHelper.GetDay()));
        btnMonth.setText(_dateHelper.GetMonthString());
        btnYear.setText(String.valueOf(_dateHelper.GetYear()));
    }


    //region Budget
    public void click_updateBudget(View view)
    {
        if (!etNewBudget.getText().toString().isEmpty())
        {
            String newBudget = etNewBudget.getText().toString();
            if (!newBudget.equals("0"))
            {
                String checkSQL = "SELECT * FROM "+Global.budgetTable+" WHERE FK_Category = "+catID+"";

                Cursor checkCursor = myDatabase.rawQuery(checkSQL, null);
                if(checkCursor.getCount() == 0)
                {
                    //Row doesn't exits. Run Insert Statement
                    InsertIntoTable(newBudget);
                }
                else
                {
                    //Row does exist. Run Update Statement
                    UpdateBudgetTable(newBudget);
                }
                checkCursor.close();
            }
            tvCurrentBudget.setText(newBudget);
        }
    }

    public void InsertIntoTable(String budgetAmount)
    {
        String SaveCommand = "INSERT INTO "+Global.budgetTable+" (BudgetAmount, FK_Category) VALUES ('"+budgetAmount+"', "+catID+")";
        myDatabase.execSQL(SaveCommand);

        etNewBudget.setText("");
        showBudgetToast();
    }

    public void UpdateBudgetTable(String budgetAmount)
    {
        String UpdateCommand = "UPDATE "+Global.budgetTable+" SET BudgetAmount = "+budgetAmount+" WHERE FK_Category = "+catID+"";
        myDatabase.execSQL(UpdateCommand);

        etNewBudget.setText("");
        showBudgetToast();
    }

    public void showBudgetToast()
    {
        Toast.makeText(getApplicationContext(),
                "Budget has been updated",
                Toast.LENGTH_SHORT).show();
    }
    //endregion
    
    
    //region Popups
    
    public void Click_SelectDay(View view)
    {
        int daysInMonth = _dateHelper.GetDaysInMonthWithString(btnMonth.getText().toString());
    
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.year_menu, popup.getMenu());
    
        for (int i = 1; i <= daysInMonth; i++)
        {
            popup.getMenu().add(i-1, Menu.FIRST, Menu.NONE, String.valueOf(i));
        }
    
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Button btnDay = findViewById(R.id.btnDay);
                btnDay.setText(item.getTitle());
                return false;
            }
        });
    
        inflater.inflate(R.menu.year_menu, popup.getMenu());
    
        popup.show();
    }
    
    public void Click_SelectMonth(View view)
    {
        String[] monthArray = _dateHelper.GetMonthArray();
    
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.year_menu, popup.getMenu());
        
        for (String month : monthArray)
        {
            popup.getMenu().add(month);
        }
    
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Button btnMonth = findViewById(R.id.btnMonth);
                btnMonth.setText(item.getTitle());
                checkLastDayOfMonth(String.valueOf(btnMonth.getText()));
                return false;
            }
        });
    
        inflater.inflate(R.menu.year_menu, popup.getMenu());
    
        popup.show();
    }
    
    private void checkLastDayOfMonth(String month)
    {
        if (btnDay.getText().equals("31"))
        {
            btnDay.setText(String.valueOf(_dateHelper.GetDaysInMonthWithString(month)));
        }
    }
    
    public void Click_SelectYear(View view)
    {
       int year = _dateHelper.GetYear();
   
       PopupMenu popup = new PopupMenu(this, view);
       MenuInflater inflater = popup.getMenuInflater();
       inflater.inflate(R.menu.year_menu, popup.getMenu());
   
       for (int i = -2;i < 8; i++)
       {
          int place = i+2;
          popup.getMenu().add(place, Menu.FIRST, Menu.NONE, String.valueOf(year+i));
       }
   
       popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
       {
          @Override
          public boolean onMenuItemClick(MenuItem item)
          {
             Button btnYear = findViewById(R.id.btnYear);
             btnYear.setText(item.getTitle());
             return false;
          }
       });
   
       inflater.inflate(R.menu.year_menu, popup.getMenu());
   
       popup.show();
    }
    
    //endregion
    
    
    //region SaveExpenses
    public void click_saveExpense(View view)
    {
        if (!etExpenseDescription.getText().toString().isEmpty()
                && !etExpense.getText().toString().isEmpty()
                && !categoryType.equals(Global.DEBIT_ORDER))
        {
            String desc = etExpenseDescription.getText().toString();
            String cost = etExpense.getText().toString();

            int saveDay = Integer.parseInt(btnDay.getText().toString());
            int saveMonth = Arrays.asList(_dateHelper.GetMonthArray()).indexOf(String.valueOf(btnMonth.getText()));
            int saveYear = Integer.parseInt(btnYear.getText().toString());

            String ExpenseSQL = "INSERT INTO "+Global.expenseTable+" " +
                    "(Cost, Description, FK_Category, Day, Month, Year) " +
                    "VALUES " +
                    "('"+cost+"', '"+desc+"', "+catID+", "+saveDay+", "+saveMonth+", "+saveYear+")"
                    ;

            myDatabase.execSQL(ExpenseSQL);

            etExpense.setText("");
            etExpenseDescription.setText("");
            Toast.makeText(getApplicationContext(),
                    "Expense Logged",
                    Toast.LENGTH_SHORT).show();
        }
        else if(categoryType.equals(Global.DEBIT_ORDER))
        {
            Toast.makeText(getApplicationContext(),
                    "Can't add expense to Once-off Expenses",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Please fill in all fields\nAnd ensure year has 4 characters",
                    Toast.LENGTH_LONG).show();
        }
    }
    //endregion








    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_info, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.deleteCategory:
                showDeleteDialog(Global.deleteInt, "Delete");
                break;
            case R.id.archiveCategory:
                showDeleteDialog(Global.archiveInt, "Archive");
                break;
            case android.R.id.home:
                startActivity(new Intent(CategoryInfo.this, MainActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDeleteDialog(final int isDeleted, final String mess)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryInfo.this);
        builder.setTitle("Confirm deletion");
        builder.setMessage("Are you sure you want to "+mess+" this category?");
        builder.setPositiveButton(mess, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(isDeleted);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void deleteCategory(int isDeleted)
    {
        String deleteSQL = "UPDATE "+Global.categoryTable+" SET isDeleted = "+isDeleted+" where ID = "+catID+"";
        myDatabase.execSQL(deleteSQL);

        String message = isDeleted == 1 ? "Archived" : "Deleted";

        Toast.makeText(getApplicationContext(), "Category "+message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CategoryInfo.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CategoryInfo.this, MainActivity.class));
        finish();
    }
}











