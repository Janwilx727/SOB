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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.popbox.jan.simplebudget.Models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity
{
     //variables
     ListView lvArchive;
     List<CategoryModel> archiveList = new ArrayList<>();
     ArrayAdapter<CategoryModel> adapter;
     SQLiteDatabase myDatabase;
     
     @Override
     protected void onCreate(@Nullable Bundle savedInstanceState)
     {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_archive);
          
          if (getSupportActionBar() != null)
          {
             String color = getResources().getString(R.string.yellow);
//             getSupportActionBar().setTitle("Archived Items");
             getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Archived Items</font>"));
             getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
             final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
             upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
             getSupportActionBar().setHomeAsUpIndicator(upArrow);
          }
  
          myDatabase = this.openOrCreateDatabase(Global.budgetDatabaseName, MODE_PRIVATE, null);
          
          lvArchive = findViewById(R.id.lvArchive);
          fetchArchiveData();
     }
     
     public void fetchArchiveData()
     {
          String fetchSQL = "SELECT * FROM "+Global.categoryTable+" WHERE isDeleted = "+Global.archiveInt+"";
          Cursor cursor = myDatabase.rawQuery(fetchSQL, null);
          
          for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
          {
               int id = cursor.getInt(Global.cat_ID_col);
               String name = cursor.getString(Global.cat_NAME_col);
               int deleted = cursor.getInt(Global.cat_DELETED_col);
               
               CategoryModel category =
                       new CategoryModel
                          (
                                  id,
                                  name,
                                  "0",
                                  deleted,
                                  ""
                          );
                       
               archiveList.add(category);
          }
          setUpList();
          cursor.close();
     }
     
     public void setUpList()
     {
        adapter = new ArchiveAdapter();
        lvArchive.setAdapter(adapter);
        lvArchive.setDivider(null);
     }
   
   
   
   public class ArchiveAdapter extends ArrayAdapter<CategoryModel>
   {
      private ArchiveAdapter()
      {
         super(ArchiveActivity.this, R.layout.adapter_category_archives, archiveList);
      }
      
      @NonNull
      @Override
      public View getView(final int position, View convertView, ViewGroup parent)
      {
         View itemView = convertView;
         if (itemView == null)
         {
            itemView = getLayoutInflater().inflate(R.layout.adapter_category_archives, parent, false);
         }
         final CategoryModel itemInfo = archiveList.get(position);
   
         TextView tvName = itemView.findViewById(R.id.tvDay);
         Button btnUnarchive = itemView.findViewById(R.id.btnUnarchive);
         Button btnDelete = itemView.findViewById(R.id.btnDelete);
         
         tvName.setText(itemInfo.getName());
         btnUnarchive.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View v)
            {
               showDeleteDialog(Global.nonDeletedInt, itemInfo.getID(), "Unarchive","Confirm Unarchive", position);
            }
         });
         
         btnDelete.setOnClickListener(new View.OnClickListener()
            {
            @Override
            public void onClick(View v)
            {
               showDeleteDialog(Global.deleteInt, itemInfo.getID(), "Delete","Confirm Deletion", position);
            }
         });
         
         return itemView;
      }
   }
   
   
   
   public void showDeleteDialog(final int isDeleted, final int catID, final String mess,final String title, final int position)
   {
      AlertDialog.Builder builder = new AlertDialog.Builder(ArchiveActivity.this);
      builder.setTitle(title);
      builder.setMessage("Are you sure you want to "+mess+" this category?");
      builder.setPositiveButton(mess, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            deleteCategory(isDeleted, catID, position);
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
   
   public void deleteCategory(int isDeleted, int catID, int position)
   {
      String deleteSQL = "UPDATE "+Global.categoryTable+" SET isDeleted = "+isDeleted+" where ID = "+catID+"";
      myDatabase.execSQL(deleteSQL);
      
      String message = isDeleted == 0 ? "Unarchived" : "Deleted";
      
      archiveList.remove(position);
      adapter.notifyDataSetChanged();
      
      Toast.makeText(getApplicationContext(), "Category "+message, Toast.LENGTH_SHORT).show();
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case android.R.id.home:
            startActivity(new Intent(ArchiveActivity.this, MainActivity.class));
            finish();
            break;
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   @Override
   public void onBackPressed() {
      startActivity(new Intent(ArchiveActivity.this, MainActivity.class));
      finish();
   }
}
