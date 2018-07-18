package com.delex.pojo;

import java.io.Serializable;

/**
 * Created by Admin on 8/3/2017.
 */

public class SupportSubCat implements Serializable {
    private String Name;

    private String desc;

    private String link;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
