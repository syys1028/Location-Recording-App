package com.example.b2203098_1030;

public class ListViewItem {
    private Integer numID, timeSpent;
    private Double latitude, longitude;
    private String locDate, locTime, place;

    public Integer getNum() { return numID; }
    public Integer getTimeSpent() { return timeSpent; }

    public Double getLatitude() { return latitude; }

    public Double getLongitude() { return longitude; }

    public String getLocDate() { return locDate; }

    public String getLocTime() { return locTime; }

    public String getPlace() { return place; }

    public void setNum(Integer numID) { this.numID = numID; }
    public void setTimeSpent(Integer timeSpent) { this.timeSpent = timeSpent; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public void setLocDate(String locDate) { this.locDate = locDate; }

    public void setLocTime(String locTime) { this.locTime = locTime; }

    public void setPlace(String place) { this.place = place; }
}
