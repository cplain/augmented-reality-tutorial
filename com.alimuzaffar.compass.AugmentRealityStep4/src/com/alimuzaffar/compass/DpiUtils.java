package com.alimuzaffar.compass;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DpiUtils {
	
   public static int getPxFromDpi(Context _context, int _px){
      int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
            (float) _px, _context.getResources().getDisplayMetrics());
      return value;

   }
   
   public static int getDpiFromPx(Context context, int dp) {
	   DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	   float logicalDensity = metrics.density;
	   return (int) (dp * logicalDensity + 0.5);
   }
   
   public static DisplayMetrics getDisplayMetrics(Context context) { 
	   DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	  // activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	   return metrics;
   }
   
}
