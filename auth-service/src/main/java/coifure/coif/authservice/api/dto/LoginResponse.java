package coifure.coif.authservice.api.dto;

public class LoginResponse {

    private String token;
    private String role;
    private String user;
    private String userName;
    private String id;

    public LoginResponse() {
    }

    public LoginResponse(String token, String role, String user, String userName, String id) {
        this.token = token;
        this.role = role;
        this.user = user;
        this.userName = userName;
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
