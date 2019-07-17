package com.popbox.jan.simplebudget;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.popbox.jan.simplebudget.Helpers.DateHelper;
import com.popbox.jan.simplebudget.Helpers.StringHelper;

import java.util.Arrays;

public class SetBudgetDate extends AppCompatActivity
{
   
   Button spMonths;
   Button etSpecificDay;
   EditText etAllDay;
   DateHelper _dateHelper;
   
   ConstraintLayout clAll;
   ConstraintLayout clSpecific;
   LinearLayout llMenu;
   
   SQLiteDatabase myDatabase;
   
   StringHelper helper;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_set_budget_date);
      
      if (getSupportActionBar() != null)
      {
         String color = getResources().getString(R.string.yellow);
         getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Set your budget day</font>"));
         getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }
   
      myDatabase = this.openOrCreateDatabase(Global.budgetDatabaseName, MODE_PRIVATE, null);
      helper = new StringHelper();
      _dateHelper = new DateHelper();
      
      spMonths = findViewById(R.id.spMonths);
      etSpecificDay = findViewById(R.id.etSpecificDay);
      etAllDay = findViewById(R.id.etAllDay);
      
      SetupInitialDates();
      
      clAll = findViewById(R.id.clAll);
      clSpecific = findViewById(R.id.clSpecific);
      llMenu = findViewById(R.id.llMenu);
   }
   
   //region Page Setup
   public void SetupInitialDates()
   {
      spMonths.setText(_dateHelper.GetMonthString());
      etSpecificDay.setText(String.valueOf(_dateHelper.GetDay()));
   }
   //endregion
   
   //Saving a day to a specific month
   public void Click_SelectDay(View view)
   {
      int daysInMonth = _dateHelper.GetDaysInMonthWithString(spMonths.getText().toString());
   
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
            Button btnDay = findViewById(R.id.etSpecificDay);
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
            Button btnMonth = findViewById(R.id.spMonths);
            btnMonth.setText(item.getTitle());
            checkLastDayOfMonth(String.valueOf(item.getTitle()));
            return false;
         }
      });
   
      inflater.inflate(R.menu.year_menu, popup.getMenu());
   
      popup.show();
   }
   
   private void checkLastDayOfMonth(String month)
   {
      int totalDays = _dateHelper.GetDaysInMonthWithString(String.valueOf(spMonths.getText()));
      int btnDays = Integer.parseInt(String.valueOf(etSpecificDay.getText()));
      
      if (btnDays > totalDays)
      {
         etSpecificDay.setText(String.valueOf(_dateHelper.GetDaysInMonthWithString(month)));
      }
   }
   
   
   
   
   
   public void click_saveSpecific(View view)
   {
      if (etSpecificDay.getText().length() > 0)
      {
         String dayString = etSpecificDay.getText().toString().replaceAll("\\s","");
         int dayInt = Integer.parseInt(dayString);
         saveLastSpecificDay(dayInt);
      }
   }
   
   public void saveLastSpecificDay(int dayInt)
   {
      String month = String.valueOf(spMonths.getText());
      int maxDays = Arrays.asList(_dateHelper.GetMonthArray()).indexOf(month);
      if (dayInt > maxDays)
      {
         dayInt = Integer.parseInt(String.valueOf(etSpecificDay.getText()));
      }
      String SQLSpecificUpdate = "UPDATE " + Global.budgetDateTable + " SET BudgetDay = " + dayInt + " WHERE BudgetMonth = '" + month + "'";
      myDatabase.execSQL(SQLSpecificUpdate);
      
      helper.showToast(getApplicationContext(), 0, month + " pay date saved successfully");
      resetLayout();
   }
   
   //Saving 1 date to all months
   public void click_saveAll(View view)
   {
      if (etAllDay.getText().length() > 0)
      {
         String dayString = etAllDay.getText().toString().replaceAll("\\s","");
         int dayInt = Integer.parseInt(dayString);
         if (dayInt == 31)
         {
            saveLastdays();
         }
         else
         {
            saveAllDays(dayInt);
         }
         resetLayout();
         helper.showToast(getApplicationContext(), 0, "All pay dates saved successfully");
      }
   }
   
   public void saveAllDays(int dayInt)
   {
      for (String month : Global.monthArray)
      {
         String SQLAllUpdate;
         if ((month.equals("Feb") && dayInt > 28))
         {
            SQLAllUpdate = "UPDATE " + Global.budgetDateTable + " SET BudgetDay = 28 WHERE BudgetMonth = 'FEB'";
         }
         else
         {
            SQLAllUpdate = "UPDATE " + Global.budgetDateTable + " SET BudgetDay = " + dayInt + " WHERE BudgetMonth = '" + month + "'";
         }
         myDatabase.execSQL(SQLAllUpdate);
      }
   }
   
   public void saveLastdays()
   {
      for (int i = 0; i < Global.monthArray.length; i++)
      {
         String mon = Global.monthArray[i];
         int day = Global.daysInMonth[i];
   
         String SQLAllUpdate = "UPDATE " + Global.budgetDateTable + " SET BudgetDay = " +day + " WHERE BudgetMonth = '" + mon + "'";
         myDatabase.execSQL(SQLAllUpdate);
      }
   }
   
   
   public void click_cancel(View view)
   {
      llMenu.setVisibility(View.VISIBLE);
      clAll.setVisibility(View.GONE);
      clSpecific.setVisibility(View.GONE);
   }
   
   public void click_menu(View view)
   {
      switch (view.getId())
      {
         case R.id.btnAll:
            llMenu.setVisibility(View.GONE);
            clAll.setVisibility(View.VISIBLE);
            clSpecific.setVisibility(View.GONE);
            break;
         case R.id.btnSpecific:
            llMenu.setVisibility(View.GONE);
            clAll.setVisibility(View.GONE);
            clSpecific.setVisibility(View.VISIBLE);
            break;
      }
   }
   
   //Reset to default
   public void resetLayout()
   {
      etSpecificDay.setText("");
      etAllDay.setText("");
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      if (item.getItemId() == android.R.id.home)
      {
         if (clSpecific.getVisibility() == View.VISIBLE || clAll.getVisibility() == View.VISIBLE)
         {
            clSpecific.setVisibility(View.GONE);
            clAll.setVisibility(View.GONE);
            llMenu.setVisibility(View.VISIBLE);
            return super.onOptionsItemSelected(item);
         }
   
         startActivity(new Intent(SetBudgetDate.this, MainActivity.class));
         finish();
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   @Override
   public void onBackPressed()
   {
      if (clSpecific.getVisibility() == View.VISIBLE || clAll.getVisibility() == View.VISIBLE)
      {
         clSpecific.setVisibility(View.GONE);
         clAll.setVisibility(View.GONE);
         llMenu.setVisibility(View.VISIBLE);
         return;
      }
   
      startActivity(new Intent(SetBudgetDate.this, MainActivity.class));
      finish();
   }
}
