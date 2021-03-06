/*
 *    Copyright 2015 David Newell
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ca.lightseed.winston;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class WinstonService extends Service {

    private LocationManager locationManager;
    private PowerManager.WakeLock wakeLock;


    /**
     * Basic interface for synchronous API calls both in-process and cross-process
     * (Data is sent as a 'Parcel', which is a generic buffer plus metadata
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();
        return null;
    }

    /**
     * Creates the partial wake-lock which keeps CPU running, and allows this service
     * to continue passing location data even with screen off.
     * NOTE: WakeLocks consume battery very quickly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
        // TODO: Intrusively many toasts are generated. Fine for testing, not fine for release.
        Toast.makeText(this, "Created Winston tracking service", Toast.LENGTH_SHORT).show();
    }

    /**
     * onStart begins the visible lifetime for the activity.
     * This method performs a permissions check: the app requires either coarse or fine
     * location data permissions to have any function.
     * @param intent
     * @param startId
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 5, listener);
        }

        Toast.makeText(this, "Started Winston tracking service", Toast.LENGTH_SHORT).show();
    }

    /**
     * onDestroy is called when the activity finishes, or is destroyed by the system.
     * All cleanup functions should go here.
     * The wakelock is released here.
     */
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Terminated Winston tracking service", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        wakeLock.release();
    }

    /**
     * hasConnection method checks internet connectivity status
     * @param _context a context
     * @return true if device has network connection
     */
    public static boolean hasConnection(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    /**
     * LocationListener
     * TODO: Document this method
     */
    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            Log.e("Google", "Location Changed");

            if (location == null)
                return;
            //
            if (hasConnection(getApplicationContext())) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();

                try {
                    Log.e("latitude", location.getLatitude() + "");
                    Log.e("longitude", location.getLongitude() + "");

                    jsonObject.put("latitude", location.getLatitude());
                    jsonObject.put("longitude", location.getLongitude());

                    jsonArray.put(jsonObject);

                    Log.e("request", jsonArray.toString());

                    new LocationSendService().execute(new String[]{
                            Constants.TRACK_URL, jsonArray.toString()});
                } catch (Exception e) {
                    // TODO Deal with exceptions more specifically.
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


}




