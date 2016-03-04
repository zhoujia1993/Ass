package com.xfzj.qqzoneass.model;

/**
 * Created by zj on 2015/7/10.
 */
public class PicInfo {
    public String albumid;
    public String lloc;
    public String sloc;
    public String type;
    public String height;
    public String width;
    public String extra=",,,";

    public PicInfo() {
    }

    public PicInfo(String albumid, String lloc, String sloc, String type, String height, String width) {
        this.albumid = albumid;
        this.lloc = lloc;
        this.sloc = sloc;
        this.type = type;
        this.height = height;
        this.width = width;
     
    }

    @Override
    public String toString() {
        return albumid + "," + lloc + "," + sloc + "," + type + "," + height + "," + width + extra;
    }
    
}
