package com.cobyplain.augmentreality;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

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

public class LocationUtils {
	/** this criteria will settle for less accuracy, high power, and cost */
	public static Criteria createCoarseCriteria() {
	 
	  Criteria c = new Criteria();
	  c.setAccuracy(Criteria.ACCURACY_COARSE);
	  c.setAltitudeRequired(false);
	  c.setBearingRequired(false);
	  c.setSpeedRequired(false);
	  c.setCostAllowed(true);
	  c.setPowerRequirement(Criteria.POWER_HIGH);
	  return c;
	 
	}
	 
	/** this criteria needs high accuracy, high power, and cost */
	public static Criteria createFineCriteria() {
	 
	  Criteria c = new Criteria();
	  c.setAccuracy(Criteria.ACCURACY_FINE);
	  c.setAltitudeRequired(false);
	  c.setBearingRequired(false);
	  c.setSpeedRequired(false);
	  c.setCostAllowed(true);
	  c.setPowerRequirement(Criteria.POWER_HIGH);
	  return c;
	 
	}
	 
	/** 
	  make sure to call this in the main thread, not a background thread
	  make sure to call locMgr.removeUpdates(...) when you are done
	*/
	public static void init(Context ctx, LocationManager locMgr){
	 
	  //LocationManager locMgr = LocationUtils.getLocationManager(ctx.getMyContext());
	 
	  // get low accuracy provider
	  LocationProvider low=
	    locMgr.getProvider(locMgr.getBestProvider(createCoarseCriteria(),true));
	 
	  // get high accuracy provider
	  LocationProvider high=
	    locMgr.getProvider(locMgr.getBestProvider(createFineCriteria(), true));
	 
	  // using low accuracy provider... to listen for updates
	  locMgr.requestLocationUpdates(low.getName(), 0, 0f,
	        new LocationListener() {
	        public void onLocationChanged(Location location) {
	          // do something here to save this new location
	        }
	        public void onStatusChanged(String s, int i, Bundle bundle) {
	 
	        }
	        public void onProviderEnabled(String s) {
	           // try switching to a different provider
	        }
	        public void onProviderDisabled(String s) {
	           // try switching to a different provider
	        }
	      });
	 
	  // using high accuracy provider... to listen for updates
	  locMgr.requestLocationUpdates(high.getName(), 0, 0f,
	        new LocationListener() {
	        public void onLocationChanged(Location location) {
	          // do something here to save this new location
	        }
	        public void onStatusChanged(String s, int i, Bundle bundle) {
	 
	        }
	        public void onProviderEnabled(String s) {
	          // try switching to a different provider
	        }
	        public void onProviderDisabled(String s) {
	          // try switching to a different provider
	        }
	      });
	}
}
