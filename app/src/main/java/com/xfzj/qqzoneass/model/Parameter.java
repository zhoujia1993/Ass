package com.xfzj.qqzoneass.model;

/**
 * Created by zj on 2015/6/27.
 */
public class Parameter {
    public String name;
    public String value;


    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Parameter() {
    
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
