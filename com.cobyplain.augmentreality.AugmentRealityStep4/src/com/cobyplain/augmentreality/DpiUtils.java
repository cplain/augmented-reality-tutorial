package com.cobyplain.augmentreality;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/*
 * Portions (c) 2009 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Coby Plain coby.plain@gmail.com, Ali Muzaffar ali@muzaffar.me
 */

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
