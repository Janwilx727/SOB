package com.popbox.jan.simplebudget;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCategory extends AppCompatActivity
{
    EditText etCategory;
    Spinner spMonths;
    String CategoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        if (getSupportActionBar() != null)
        {
            String color = getResources().getString(R.string.yellow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Add a Category</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
    
        }
        etCategory = findViewById(R.id.etCategory);
        CategoryType = "";
    }

    
    public void Click_SelectRadio(View view)
    {
        switch(view.getId())
        {
           case R.id.rdMultiple:
              CategoryType = Global.MULTIPLE;
              break;
           case R.id.rdOnce:
              CategoryType = Global.DEBIT_ORDER;
              break;
        }
    }
    

    public void click_save(View view)
    {
        if (!etCategory.getText().toString().isEmpty() && !CategoryType.isEmpty())
        {
            try
            {
                String strCategory = etCategory.getText().toString();
                //Is not empty. Therefore save
                SQLiteDatabase myDatabase = this.openOrCreateDatabase(Global.budgetDatabaseName, MODE_PRIVATE, null);

                String CreateCommand = "CREATE TABLE IF NOT EXISTS " +
                        "Categories (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                        "Name VARCHAR," +
                        "Category VARCHAR," +
                        "isDeleted BIT)";

                myDatabase.execSQL(CreateCommand);

                String SaveCommand = "INSERT INTO Categories (Name, Category, isDeleted) VALUES ('"+strCategory+"', '"+CategoryType+"', 0)";
                myDatabase.execSQL(SaveCommand);

                etCategory.setText("");

                Toast.makeText(getApplicationContext(),
                        strCategory+" has been added to your category",
                        Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                Log.i("AppInfo","Save error: "+e.getMessage());
            }
        }
        else
        {
           if (etCategory.getText().toString().isEmpty())
           {
              Toast.makeText(getApplicationContext(),
                      "Can't save empty text",
                      Toast.LENGTH_SHORT).show();
           }
           else
           {
              Toast.makeText(getApplicationContext(),
                      "Please select category type",
                      Toast.LENGTH_SHORT).show();
           }
        }
    }

    public void click_cancel(View view)
    {
        startActivity(new Intent(AddCategory.this, MainActivity.class));
        finish();
    }
    
    
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddCategory.this, MainActivity.class));
        finish();
    }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      
      if (item.getItemId() == android.R.id.home)
      {
         startActivity(new Intent(AddCategory.this, MainActivity.class));
         finish();
      }
      
      return super.onOptionsItemSelected(item);
   }
}
