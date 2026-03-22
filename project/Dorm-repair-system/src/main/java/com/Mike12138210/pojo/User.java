package com.Mike12138210.pojo;

// Alt + 鼠标左键可整列编辑
public class User {
    // id,account,password,role,building,room
    private Integer id;
    private String account;      // 学号/工号
    private String password;
    private String role;         // "student" 或 "admin"
    private String building;
    private String room;

    public User() {}

    public User(String account, String password, String role) {
        this.account = account;
        this.password = password;
        this.role = role;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
}