package cn.edu.test.model.rpc;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    public String account;
    public String password;
    public String name;
}
