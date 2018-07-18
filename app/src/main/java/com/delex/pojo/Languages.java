package com.delex.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by DELL on 21-12-2017.
 */

public class Languages implements Serializable {


    @SerializedName("lan_id")
    private String id;

    @SerializedName("lan_name")
    private String name;

    private String code;

    private boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
