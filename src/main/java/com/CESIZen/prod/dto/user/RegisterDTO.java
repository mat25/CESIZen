package com.CESIZen.prod.dto.user;

public class RegisterDTO {
    private String name;
    private String firstName;
    private String username;
    private String email;
    private String password;

    public RegisterDTO() {
    }

    public RegisterDTO(String name, String firstName, String username, String email, String password) {
        this.name = name;
        this.firstName = firstName;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}