package app.mediabrainz.api.oauth;

import com.squareup.moshi.Json;


public class OAuthCredential {

    @Json(name = "access_token")
    private String accessToken;

    @Json(name = "expires_in")
    private long expiresIn;

    @Json(name = "token_type")
    private String tokenType;

    @Json(name = "refresh_token")
    private String refreshToken;

    public static class TokenInfo {

        @Json(name = "error")
        private String error;

        @Json(name = "error_description")
        private String errorDescription;

        @Json(name = "access_type")
        private String accessType;

        @Json(name = "expires_in")
        private Long expiresIn;

        @Json(name = "audience")
        private String audience;

        @Json(name = "scope")
        private String scope;

        @Json(name = "issued_to")
        private String issuedTo;

        @Json(name = "token_type")
        private String tokenType;

        public TokenInfo() {
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
        }

        public String getAccessType() {
            return accessType;
        }

        public void setAccessType(String accessType) {
            this.accessType = accessType;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getIssuedTo() {
            return issuedTo;
        }

        public void setIssuedTo(String issuedTo) {
            this.issuedTo = issuedTo;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }
    }

    public static class UserInfo {

        @Json(name = "profile")
        private String profile;

        @Json(name = "sub")
        private String username;

        @Json(name = "email_verified")
        private Boolean emailVerified;

        @Json(name = "email")
        private String email;

        @Json(name = "metabrainz_user_id")
        private Integer userId;

        @Json(name = "zoneinfo")
        private String zoneinfo;

        @Json(name = "gender")
        private String gender;

        public UserInfo() {
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Boolean getEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getZoneinfo() {
            return zoneinfo;
        }

        public void setZoneinfo(String zoneinfo) {
            this.zoneinfo = zoneinfo;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }

    public OAuthCredential() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
