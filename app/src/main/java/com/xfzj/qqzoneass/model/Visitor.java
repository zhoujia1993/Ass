package com.xfzj.qqzoneass.model;

import android.graphics.Bitmap;

public class Visitor implements Comparable<Visitor> {
    public String uin;
    public String time;
    public String name;
    public Bitmap bitmap;

    /**
     * @param uin
     * @param time
     * @param name
     */
    public Visitor(String uin, String time, String name) {
        this.uin = uin;
        this.time = time;
        this.name = name;
    }

    public Visitor() {

    }

    @Override
    public String toString() {
        return "Visitor [uin=" + uin + ", time=" + time + ", name=" + name
                + "]";
    }

    public int compareTo(Visitor o) {
        if (null == o) {
            return -1;
        }
        if (Integer.valueOf(this.time) < Integer.valueOf(o.time)) {
            return 1;
        } else if (Integer.valueOf(this.time) == Integer.valueOf(o.time)) {
            return 0;
        } else {
            return -1;
        }
    }
}
