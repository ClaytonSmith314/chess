package model;

public record UserData(
        String username,
        String password,
        String email
) {
    public LoginData getLoginData() {
        return new LoginData(username, password);
    }
}
