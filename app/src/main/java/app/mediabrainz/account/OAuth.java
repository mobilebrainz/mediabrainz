package app.mediabrainz.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import app.mediabrainz.apihandler.ApiHandler;
import app.mediabrainz.api.Config;
import app.mediabrainz.api.oauth.OAuthCredential;
import app.mediabrainz.api.oauth.OAuthService;
import app.mediabrainz.functions.Action;
import app.mediabrainz.functions.Consumer;
import app.mediabrainz.functions.DisposableAction;
import app.mediabrainz.functions.ErrorHandler;
import app.mediabrainz.util.ShowUtil;

import static app.mediabrainz.account.MediaBrainzAccount.ACCESS_TOKEN;
import static app.mediabrainz.account.MediaBrainzAccount.ACCOUNT_TYPE;
import static app.mediabrainz.account.MediaBrainzAccount.EXPIRE_IN;
import static app.mediabrainz.account.MediaBrainzAccount.REFRESH_TOKEN;


public class OAuth {

    private static final String CLIENT_ID = "KoSg5TjyO7cMYXjt2kvz8g";
    private static final String CLIENT_SECRET = "XL84wz60G4tscOLZ4b2Leg";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    //TODO: do enum ScopeType and List<ScopeType>
    private static final String SCOPE = "profile email tag rating collection submit_isrc submit_barcode";

    private final long DELAY = 10000L;

    private Account account;
    private AccountManager accountManager;
    private OAuthService oauthService;

    public OAuth(@NonNull AccountManager accountManager) {
        oauthService = new OAuthService(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, SCOPE);
        this.accountManager = accountManager;
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length != 0) {
            account = accounts[0];
            setConfigs();
        }
    }

    public void authorize(@NonNull String username,
                          @NonNull String password,
                          Action action,
                          ErrorHandler errorHandler) {

        ApiHandler.subscribe503(
                oauthService.authorize(username, password),
                credential -> {
                    long expiresIn = System.currentTimeMillis() + credential.getExpiresIn() * 1000 - DELAY;
                    Bundle userdata = new Bundle();
                    userdata.putString(ACCESS_TOKEN, credential.getAccessToken());
                    userdata.putString(REFRESH_TOKEN, credential.getRefreshToken());
                    userdata.putString(EXPIRE_IN, expiresIn + "");
                    account = new MediaBrainzAccount(username);
                    accountManager.addAccountExplicitly(account, password, userdata);
                    setConfigs();
                    if (action != null) action.run();
                },
                errorHandler);
    }

    public void logOut() {
        if (hasAccount()) {
            accountManager.removeAccountExplicitly(account);
            account = null;
        }
    }

    public Disposable refreshToken(@NonNull Context context, DisposableAction action) {
        return refreshToken(action, t -> ShowUtil.showError(context, t));
    }

    public CompositeDisposable refreshToken(DisposableAction action, ErrorHandler errorHandler) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        if (account == null) {
            if (action != null) {
                Disposable disposable = action.run();
                if (disposable != null) compositeDisposable.add(disposable);
            }
            return compositeDisposable;
        }
        String refreshToken = accountManager.getUserData(account, REFRESH_TOKEN);
        if (System.currentTimeMillis() < Long.valueOf(accountManager.getUserData(account, EXPIRE_IN))) {
            if (action != null) {
                Disposable disposable = action.run();
                if (disposable != null) compositeDisposable.add(disposable);
            }
            return compositeDisposable;
        }

        compositeDisposable.add(ApiHandler.subscribe503(
                oauthService.refreshToken(refreshToken),
                credential -> {
                    long expiresIn = System.currentTimeMillis() + credential.getExpiresIn() * 1000 - DELAY;
                    accountManager.setUserData(account, EXPIRE_IN, expiresIn + "");
                    accountManager.setUserData(account, ACCESS_TOKEN, credential.getAccessToken());
                    Config.accessToken = credential.getAccessToken();
                    if (action != null) {
                        Disposable disposable = action.run();
                        if (disposable != null) compositeDisposable.add(disposable);
                    }
                },
                errorHandler));
        return compositeDisposable;
    }

    /*
    public Disposable refreshToken(Action action, ErrorHandler errorHandler) {
        String refreshToken = accountManager.getUserData(account, REFRESH_TOKEN);
        return ApiHandler.subscribe503(
                oauthService.refreshToken(refreshToken),
                credential -> {
                    long expiresIn = System.currentTimeMillis() + credential.getExpiresIn() * 1000 - DELAY;
                    accountManager.setUserData(account, EXPIRE_IN, expiresIn + "");
                    accountManager.setUserData(account, ACCESS_TOKEN, credential.getAccessToken());
                    Config.accessToken = credential.getAccessToken();
                    if (action != null) action.run();
                },
                errorHandler);
    }
    */

    public Disposable loadTokenInfo(@NonNull Context context, Consumer<OAuthCredential.TokenInfo> consumer) {
        if (account == null) throw new IllegalArgumentException("account is null");
        return ApiHandler.subscribe503(
                oauthService.getTokenInfo(accountManager.getUserData(account, ACCESS_TOKEN)),
                consumer,
                t -> ShowUtil.showError(context, t));
    }

    public Disposable loadUserInfo(@NonNull Context context, Consumer<OAuthCredential.UserInfo> consumer) {
        return refreshToken(context, () -> ApiHandler.subscribe503(
                oauthService.getUserInfo(accountManager.getUserData(account, ACCESS_TOKEN)),
                consumer,
                t -> ShowUtil.showError(context, t)));
    }

    private void setConfigs() {
        Config.accessToken = accountManager.getUserData(account, ACCESS_TOKEN);
        Config.setCredentials(account.name, accountManager.getPassword(account));
    }

    public boolean hasAccount() {
        return account != null;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getName() {
        return getAccount().name;
    }

    public String getPassword() {
        return accountManager.getPassword(getAccount());
    }

}
