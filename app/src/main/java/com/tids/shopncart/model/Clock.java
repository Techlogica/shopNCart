package com.tids.shopncart.model;

import com.google.gson.annotations.SerializedName;

public class Clock {


    @SerializedName("clock_id")
    private String clockId;

    @SerializedName("c_in_date")
    private String clockInDate;

    @SerializedName("value")
    private String value;

    @SerializedName("clock_time_in")
    private String clockTimeIn;

    @SerializedName("c_o_date")
    private String clockOutDate;

    @SerializedName("clock_time_out")
    private String clockTimeOut;

    @SerializedName("time")
    private String time;

    public String getClockId() {
        return clockId;
    }

    public void setClockId(String clockId) {
        this.clockId = clockId;
    }

    public String getClockInDate() {
        return clockInDate;
    }

    public void setClockInDate(String clockInDate) {
        this.clockInDate = clockInDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClockTimeIn() {
        return clockTimeIn;
    }

    public void setClockTimeIn(String clockTimeIn) {
        this.clockTimeIn = clockTimeIn;
    }

    public String getClockOutDate() {
        return clockOutDate;
    }

    public void setClockOutDate(String clockOutDate) {
        this.clockOutDate = clockOutDate;
    }

    public String getClockTimeOut() {
        return clockTimeOut;
    }

    public void setClockTimeOut(String clockTimeOut) {
        this.clockTimeOut = clockTimeOut;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}