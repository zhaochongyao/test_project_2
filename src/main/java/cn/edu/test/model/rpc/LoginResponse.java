package cn.edu.test.model.rpc;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    public String status;
    public String token;
    public String role_key;
}
