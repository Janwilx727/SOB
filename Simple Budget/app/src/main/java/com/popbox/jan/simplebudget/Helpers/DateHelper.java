package com.popbox.jan.simplebudget.Helpers;

import java.util.Arrays;
import java.util.Calendar;

public class DateHelper
{
   public int GetDay()
   {
      return GetCalendarInstance().get(Calendar.DAY_OF_MONTH);
   }
   
   public int GetMonth()
   {
      return GetCalendarInstance().get(Calendar.MONTH);
   }
   
   public int GetYear()
   {
      return GetCalendarInstance().get(Calendar.YEAR);
   }
   
   public String[] GetMonthArray()
   {
      return new String[]
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
   
   public String GetMonthString()
   {
      return GetMonthArray()[GetMonth()];
   }
   
   public int GetDaysInMonthWithString(String month)
   {
      int index = Arrays.asList(GetMonthArray()).indexOf(month);
      return GetDaysArray(index);
   }
   
   private int GetDaysArray(int index)
   {
      int[] daysInMonth
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
      
      return daysInMonth[index];
   }
   
   private Calendar GetCalendarInstance()
   {
      return Calendar.getInstance();
   }
}
