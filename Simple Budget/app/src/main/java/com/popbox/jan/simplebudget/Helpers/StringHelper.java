package com.popbox.jan.simplebudget.Helpers;

import android.content.Context;
import android.widget.Toast;

public class StringHelper
{
   
   // 0 - Short ; 1 - Long; >1 - Time specified
   public void showToast(Context context,int Length, String message)
   {
      if (Length == 0)
      {
         Toast.makeText(context,
                 message,
                 Toast.LENGTH_SHORT).show();
      }
      else if (Length == 1)
      {
         Toast.makeText(context,
                 message,
                 Toast.LENGTH_LONG).show();
      }
      else
      {
         Toast.makeText(context,
                 message,
                 Length).show();
      }
   }
   
}
