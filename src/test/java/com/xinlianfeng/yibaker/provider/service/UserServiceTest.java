/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:PersonServiceTest.java   Package name:com.xinlianfeng.yibaker.provider.service
 * Project:YiBaker-Provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2015年9月22日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.xinlianfeng.yibaker.common.entity.User;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.UserRegInfo;
import com.xinlianfeng.yibaker.common.service.TestService;
import com.xinlianfeng.yibaker.common.service.UserService;
import com.xinlianfeng.yibaker.provider.service.impl.TestServiceImpl;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年9月22日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
//@Transactional
@Component
public class UserServiceTest
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private TestService testService;
	
	@Test
	public void findAll(){
//		List<UserInfo> userList = userService.findAll();
//		Assert.assertNotNull(userList );
//		Assert.assertTrue(userList .size()>0);
//		for(UserInfo user:userList){
//			System.out.println("user="+user.getNickname());
//		}
	}
	
	@Test
	public void createUser(){
//		User user = new User();
//		user.setUser_name("sept1");
//		user.setUser_email("sept1@163.com");
//		user.setUser_mobile("13011112222");
//		user.setUser_pwd("111111");
//		user.setUser_remark("sldk");
//		int cnt = userService.createUser(user);
//		Assert.assertEquals(1, cnt);
	}
	
	@Test
	public void createBoth(){
		try
		{
			testService.createBoth();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public void createUserEx(){
		try
		{
			User user = new User();
			user.setCheck_code("111000");
			UserRegInfo userRegInfo = new UserRegInfo();
			userRegInfo.setEmail("a@a.com");
			userRegInfo.setMobile("18600000001");
			userRegInfo.setPasswd(DigestUtils.md5DigestAsHex("123456".getBytes()));
			user.setUserreginfo(userRegInfo);
			
			UserInfo userInfo = new UserInfo();
			userInfo.setNickname("xxx");
			userInfo.setBirth("19900811");
			userInfo.setArea("");
			userInfo.setSex(2);
			userInfo.setPhoto("");
			user.setUserinfo(userInfo);
			//testService.createUser(user);
			userService.registerEx(user);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			System.err.println(e);
			e.printStackTrace();
		}
	}

}
