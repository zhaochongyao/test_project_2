package cn.edu.test.controller;

import cn.edu.test.api.TestService;
import cn.edu.test.mapper.TestUserMapper;
import cn.edu.test.model.Person;
import cn.edu.test.model.TestUser;
import cn.edu.test.model.rpc.LoginRequest;
import cn.edu.test.model.rpc.LoginResponse;
import cn.edu.test.model.rpc.RpcPerson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("test/")
// 本地主机 127.0.0.1 or localhost
// http://10.222.122.111 -> 路由器 -> 运营商服务器 -> host缓存文件 -> 对应的机器
// http://198.0.0.2/test -> 路由器 -> 路由器下面的机器 -> 手机ip 198.0.0.2 -> 请求转发手机
// ip 公网ip 内网ip  10.222.122.111  198.127.0.1

// 注入
public class TestController {
    private HashMap<String, Person> loginMap = new HashMap<>();
    private TestUserMapper testUserMapper;
    @Autowired
    public void setTestUserMapper(TestUserMapper userMapper) {
        this.testUserMapper = userMapper;
    }
    @Reference
    TestService testService;

    private String Admin = "admin";
    private String CommonUser = "common_user";

    @RequestMapping(value = "login/", method = RequestMethod.POST)
    public @ResponseBody Object Login(@RequestBody Person person) {
        System.out.println("person的名字是:" + person.getName());
        LoginRequest request = new LoginRequest();
        request.account = person.getAccount();
        request.name = person.getName();
        request.password = person.getPassword();
        LoginResponse response = testService.Login(request);
        person.setRole_key(response.role_key);
        loginMap.put(response.token, person);
        return response.token;
    }

    @RequestMapping(value = "register/", method = RequestMethod.POST)
    public @ResponseBody Object Register(@RequestBody Person person) {
        // account 不能相同
        QueryWrapper<TestUser> userQuery = new QueryWrapper<>();
        userQuery.eq("account", person.getAccount());
        TestUser user = testUserMapper.selectOne(userQuery);
        if (user != null) {
            return "用户名重复";
        }
        TestUser testUser = new TestUser();
        testUser.name = person.getName();
        testUser.account = person.getAccount();
        testUser.password = person.getPassword();
        testUserMapper.insert(testUser);
        return "注册成功";
    }

    // key - value
    @RequestMapping(value = "search_person/", method = RequestMethod.GET)
    public @ResponseBody Object SearchPerson(@RequestParam String name, HttpServletRequest request) {
        String token = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
            }
        }
        System.out.println("token: " + token);
        if (!hasLogin(token)) {
            return "用户未登陆";
        }
        RpcPerson rpcPerson = new RpcPerson();
        rpcPerson.name = name;
        List<RpcPerson> resultList = testService.SearchPerson(rpcPerson);
        String result = "";
        for (RpcPerson user : resultList) {
            result += ("用户: " + user.name + "账号:" + user.account + " 角色:" + user.role_key +"\n");
        }
        System.out.println("符合"+name+"的是\n"+result);
        return "符合"+name+"的是\n"+result;
    }

    public boolean hasLogin(String token) {
        if (!loginMap.containsKey(token) || token == null) {
            return false;
        }
        return true;
    }

    String[] fileTypes = new String[] {"png", "jpg", "jpeg"};

    @PostMapping("/upload")
    @ResponseBody
    public String create(@RequestBody MultipartFile files, HttpServletRequest request) throws IOException {
        String token = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("token")) {
                token = cookie.getValue();
            }
        }
        if (!hasLogin(token)) {
            return "用户未登陆";
        }
        Person p = loginMap.get(token);
        if (p.getRole_key() == null || p.getRole_key().equals(CommonUser)) {
            return "无权限";
        }
        String fileName = files.getOriginalFilename();
        String[] result = fileName.split("\\.");
        String fileType = result[result.length - 1];
        for (String type : fileTypes) {
            if (fileType.equals(type)) {
                System.out.println(fileName);
                String filePath = "./" + (System.currentTimeMillis() + p.getName() + fileName);
                File dest = new File(filePath);
                Files.copy(files.getInputStream(), dest.toPath());
                return "Upload file success : " + dest.getAbsolutePath();
            }
        }
        return "文件格式错误";
    }

    @RequestMapping("/download")
    public void download(String path, HttpServletResponse response) {
        try {
            File file = new File(path);
            String filename = file.getName();
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


//    @RequestMapping(value = "login_v2/", method = RequestMethod.GET)
//    public @ResponseBody Object LoginV2(@RequestParam String name, @RequestParam String account, @RequestParam String password) {
//        Person person = new Person(account, password, name);
//        System.out.println("person的名字是:" + person.getName());
//        if (person.same(Person.defaultAccount, Person.defaultPassword)) {
//            return "登陆成功";
//        }
//        return "登陆失败";
//    }
}
