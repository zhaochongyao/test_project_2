package cn.edu.test.mapper;

import cn.edu.test.model.TestUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestUserMapper extends BaseMapper<TestUser> {

}
