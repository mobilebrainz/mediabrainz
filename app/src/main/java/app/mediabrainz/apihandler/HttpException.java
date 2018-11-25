package app.mediabrainz.apihandler;

import java.io.IOException;

import retrofit2.Response;


public class HttpException extends Exception {

    private Response response;

    public HttpException(Response response) {
        super("HTTP Error. Status Code: " + response.code() + " " + response.message());
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public String getContent() {
        try {
            return response.errorBody().string();
        } catch (IOException e) {
            return "";
        }
    }
}
