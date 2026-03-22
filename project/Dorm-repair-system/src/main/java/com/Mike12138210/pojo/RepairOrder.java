package com.Mike12138210.pojo;

import java.time.LocalDateTime;

public class RepairOrder {
    private Integer id;
    private Integer studentId;
    private String deviceType;
    private String description;
    private String status;           // 等待中, 进行中, 已完成, 已取消
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public RepairOrder(){}

    public RepairOrder(Integer studentId, String deviceType, String description, String status) {
        this.studentId = studentId;
        this.deviceType = deviceType;
        this.description = description;
        this.status = status;
    }

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}

    public Integer getStudentId() {return studentId;}

    public void setStudentId(Integer studentId) {this.studentId = studentId;}

    public String getDeviceType() {return deviceType;}

    public void setDeviceType(String deviceType) {this.deviceType = deviceType;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public LocalDateTime getCreateTime() {return createTime;}

    public void setCreateTime(LocalDateTime createTime) {this.createTime = createTime;}

    public LocalDateTime getUpdateTime() {return updateTime;}

    public void setUpdateTime(LocalDateTime updateTime) {this.updateTime = updateTime;}
}