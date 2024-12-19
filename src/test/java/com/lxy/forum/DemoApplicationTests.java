package com.lxy.forum;

import com.lxy.forum.dao.UserMapper;
import com.lxy.forum.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserMapper userMapper;


	@Test
	void select(){
		User user = userMapper.selectByPrimaryKey(1l);
		System.out.println(user);

	}

}
