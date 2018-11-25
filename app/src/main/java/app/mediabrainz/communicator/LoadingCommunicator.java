package app.mediabrainz.communicator;


public interface LoadingCommunicator {

    void viewProgressLoading(boolean isView);

    void showConnectionWarning(Throwable t);
}
