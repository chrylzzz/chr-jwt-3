package com.chryl.bean;

import java.io.Serializable;

/**
 * Created by Chryl on 2019/7/20.
 */
public class UserModel implements Serializable {

    private static final long serialVersionUID = 8967540087812202111L;

    private String id;

    private String userName;

    public UserModel() {
    }

    public UserModel(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
