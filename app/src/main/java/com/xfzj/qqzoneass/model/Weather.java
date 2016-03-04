package com.xfzj.qqzoneass.model;

/**
 * Created by zj on 2015/7/21.
 */
public class Weather {
    public String city;
    public String date;
    public String weather;
    public String temp;
    public String name;

    public Weather(String city, String date, String weather, String temp, String name) {

        this.city = city;
        this.date = date;
        this.weather = weather;
        this.temp = temp;
        this.name = name;
    }

    public Weather() {
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", weather='" + weather + '\'' +
                ", temp='" + temp + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
