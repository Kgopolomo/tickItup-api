package za.co.tickItup.api.response;


import org.springframework.security.core.userdetails.UserDetails;


public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType;
    private UserDetails userDetails;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String accessToken, String tokenType, UserDetails userDetails) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.userDetails = userDetails;
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

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
