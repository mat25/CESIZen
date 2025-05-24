package com.CESIZen.prod.dto.user;

import com.CESIZen.prod.entity.Role;
import com.CESIZen.prod.entity.User;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String name;
    private String firstName;
    private String username;
    private String email;
    private String status;
    private Role role;
    private LocalDateTime registrationDate;
    // En minute
    private Integer timeBan;
    private LocalDateTime banDate;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.firstName = user.getFirstName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.status = user.getStatus().name();
        this.role = user.getRole();
        this.registrationDate = user.getRegistrationDate();
        this.timeBan = user.getTimeBan();
        this.banDate = user.getBanDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Integer getTimeBan() {
        return timeBan;
    }

    public void setTimeBan(Integer timeBan) {
        this.timeBan = timeBan;
    }

    public LocalDateTime getBanDate() {
        return banDate;
    }

    public void setBanDate(LocalDateTime banDate) {
        this.banDate = banDate;
    }
}
