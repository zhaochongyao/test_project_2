package cn.edu.test.api;

import cn.edu.test.model.rpc.LoginRequest;
import cn.edu.test.model.rpc.LoginResponse;
import cn.edu.test.model.rpc.RpcPerson;

import java.util.List;

public interface TestService {
    List<RpcPerson> SearchPerson(RpcPerson person);
    LoginResponse Login(LoginRequest request);
}
