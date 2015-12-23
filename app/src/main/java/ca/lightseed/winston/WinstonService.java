package ca.lightseed.winston;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

import static android.widget.Toast.*;


public class WinstonService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        makeText(this, "Created Winston tracking service", LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        makeText(this, "Started Winston tracking service", LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        makeText(this, "Terminated Winston tracking service", LENGTH_LONG).show();
    }

}

