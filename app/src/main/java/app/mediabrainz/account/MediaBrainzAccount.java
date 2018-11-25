package app.mediabrainz.account;

import android.accounts.Account;
import android.os.Parcel;


public class MediaBrainzAccount extends Account {

    public static final String ACCOUNT_TYPE = "app.mediabrainz";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRE_IN = "expire_in";

    public MediaBrainzAccount(Parcel in) {
        super(in);
    }

    public MediaBrainzAccount(String name) {
        super(name, ACCOUNT_TYPE);
    }

}
