package com.tessellateapp.tessellate;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //callbacks interface for communication with service clients!
    public interface Callbacks{
        public void updateClient(long data);
    }
}
