package com.xfzj.qqzoneass.model;

import java.io.Serializable;

/**
 * Created by zj on 2015/7/6.
 */
public class Location  implements Serializable {
    public String address;
    public String lat;
    public String lon;

    public Location(String address, String lat, String lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public Location() {
    }

    @Override
    public String toString() {
        return address;
    }
    
    
}
