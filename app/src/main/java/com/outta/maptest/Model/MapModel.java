package com.outta.maptest.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapModel {

    @SerializedName("updateTime")
    @Expose
    private String updateTime;
    @SerializedName("receivedDate")
    @Expose
    private Object receivedDate;
    @SerializedName("speed")
    @Expose
    private String speed;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("parking")
    @Expose
    private Boolean parking;
    @SerializedName("parkingTime")
    @Expose
    private Object parkingTime;
    @SerializedName("offlineFrom")
    @Expose
    private String offlineFrom;
    @SerializedName("online")
    @Expose
    private Boolean online;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("engine")
    @Expose
    private Object engine;
    @SerializedName("battery")
    @Expose
    private Object battery;
    @SerializedName("defense")
    @Expose
    private Object defense;
    @SerializedName("exBattery")
    @Expose
    private Object exBattery;

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Object getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Object receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public Object getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(Object parkingTime) {
        this.parkingTime = parkingTime;
    }

    public String getOfflineFrom() {
        return offlineFrom;
    }

    public void setOfflineFrom(String offlineFrom) {
        this.offlineFrom = offlineFrom;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getEngine() {
        return engine;
    }

    public void setEngine(Object engine) {
        this.engine = engine;
    }

    public Object getBattery() {
        return battery;
    }

    public void setBattery(Object battery) {
        this.battery = battery;
    }

    public Object getDefense() {
        return defense;
    }

    public void setDefense(Object defense) {
        this.defense = defense;
    }

    public Object getExBattery() {
        return exBattery;
    }

    public void setExBattery(Object exBattery) {
        this.exBattery = exBattery;
    }

}
