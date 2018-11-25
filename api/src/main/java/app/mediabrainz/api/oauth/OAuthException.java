package app.mediabrainz.api.oauth;


public class OAuthException extends Exception {

    public final static OAuthException INVALID_AUTENTICATION_ERROR = new OAuthException("Incorrect username or password");

    public OAuthException() {
    }

    public OAuthException(String var1) {
        super(var1);
    }

    public OAuthException(String var1, Throwable var2) {
        super(var1, var2);
    }

    public OAuthException(Throwable var1) {
        super(var1);
    }
}
