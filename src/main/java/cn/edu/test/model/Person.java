package cn.edu.test.model;

import java.util.Objects;

public class Person {
    public static final String defaultAccount = "xiaoming";
    public static final String defaultPassword = "123456";
    private String account;     //账号
    private String password;    // 密码
    private String name;        // 姓名
    private String role_key;    // 角色
// 主键
    public Person(String account, String password, String name) {
        this.account = account;
        this.password = password;
        this.name = name;
    }

    public String getRole_key() {
        return role_key;
    }

    public void setRole_key(String role_key) {
        this.role_key = role_key;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean same(String account, String password) {
        if (this.getAccount().equals(account) && this.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

}
