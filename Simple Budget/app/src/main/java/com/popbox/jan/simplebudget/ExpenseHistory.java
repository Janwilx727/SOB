package com.popbox.jan.simplebudget;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.popbox.jan.simplebudget.Helpers.DateHelper;
import com.popbox.jan.simplebudget.Models.ExpenseModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpenseHistory extends AppCompatActivity
{
   Button btnYear;
   Button btnMonth;
   ListView lvExpenses;
   SQLiteDatabase myDatabase;
   
   List<ExpenseModel> expenseList = new ArrayList<>();
   ArrayAdapter<ExpenseModel> expenseAdapter;
   List<String> monthList = new ArrayList<>();
   
   String[] months = Global.monthArray;
   
   DateHelper _dateHelper;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_expense_history);
   
      if (getSupportActionBar() != null)
      {
         String color = getResources().getString(R.string.yellow);
         getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Expense History</font>"));
         getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
         upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
         getSupportActionBar().setHomeAsUpIndicator(upArrow);
      }
      
      myDatabase = this.openOrCreateDatabase(Global.budgetDatabaseName, MODE_PRIVATE, null);
      monthList = Arrays.asList(months);
      
      
      _dateHelper = new DateHelper();
      btnMonth = findViewById(R.id.btnMonth);
      btnYear = findViewById(R.id.btnYear);
      lvExpenses = findViewById(R.id.lvExpenses);
   
      
      btnYear.setText(String.valueOf(_dateHelper.GetYear()));
      btnMonth.setText(String.valueOf(_dateHelper.GetMonthArray()[_dateHelper.GetMonth()]));
      
      filterList(_dateHelper.GetYear(), _dateHelper.GetMonth());
   }
   
   
   public void click_Filter(View view)
   {
      expenseList.clear();
      
      int year = Integer.parseInt(btnYear.getText().toString());
      int month = Arrays.asList(_dateHelper.GetMonthArray()).indexOf(String.valueOf(btnMonth.getText()));
      
      filterList(year, month);
   }
   
    public void filterList(int year, int month)
    {
        String fetchSQL = "SELECT ex.Cost, cat.Name, bud.BudgetAmount, ex.Day, ex.Description FROM "+Global.expenseTable+" ex " +
                "LEFT JOIN "+Global.categoryTable+" cat ON cat.ID = ex.FK_Category " +
                "LEFT JOIN "+Global.budgetTable+" bud ON cat.ID = bud.FK_Category " +
                "WHERE ex.Month = "+month+" " +
                "AND ex.Year = "+year+" " +
                "ORDER BY ex.Day DESC";

        Cursor c = myDatabase.rawQuery(fetchSQL, null);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
           int cost = c.getInt(0);
           String category = c.getString(1);
           int day = c.getInt(3);
           String description = c.getString(4);

           ExpenseModel model = new ExpenseModel(cost, category, day, description);
           expenseList.add(model);
        }
        c.close();
        
        if (expenseAdapter == null)
        {
           setUpList();
        }
        else
        {
           expenseAdapter.notifyDataSetChanged();
        }
    }
    
    public void setUpList()
    {
       expenseAdapter = new ExpenseAdapter();
       lvExpenses.setAdapter(expenseAdapter);
       lvExpenses.setDivider(null);
    }
    
    //region Popup Methods
    
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
            return false;
         }
      });
   
      inflater.inflate(R.menu.year_menu, popup.getMenu());
   
      popup.show();
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
   
   
   public class ExpenseAdapter extends ArrayAdapter<ExpenseModel>
   {
      private ExpenseAdapter()
      {
         super(ExpenseHistory.this, R.layout.adapter_expense_history, expenseList);
      }

      @NonNull
      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
      {
         View itemView = convertView;
         if (itemView == null)
         {
            itemView = getLayoutInflater().inflate(R.layout.adapter_expense_history, parent, false);
         }
         final ExpenseModel itemInfo = expenseList.get(position);

         TextView tvExpense = itemView.findViewById(R.id.tvExpense);
         TextView tvCategory = itemView.findViewById(R.id.tvCategory);
         TextView tvDay = itemView.findViewById(R.id.tvDay);
         TextView tvDescription = itemView.findViewById(R.id.tvDescription);

         tvExpense.setText("R " + String.valueOf(itemInfo.getCost()));
         tvCategory.setText(itemInfo.getCategory());
         tvDay.setText(String.valueOf(itemInfo.getDay()) + "th");
         tvDescription.setText(itemInfo.getDescription());

         return itemView;
      }
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case android.R.id.home:
            startActivity(new Intent(ExpenseHistory.this, MainActivity.class));
            finish();
            break;
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   @Override
   public void onBackPressed()
   {
      startActivity(new Intent(ExpenseHistory.this, MainActivity.class));
      finish();
   }
}
