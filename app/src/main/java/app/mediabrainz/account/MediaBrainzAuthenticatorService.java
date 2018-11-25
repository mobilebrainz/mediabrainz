package app.mediabrainz.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class MediaBrainzAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new MediaBrainzAuthenticator(this).getIBinder();
    }
}
