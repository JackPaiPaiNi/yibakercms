/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:TestServiceImpl.java   Package name:com.xinlianfeng.yibaker.provider.service.impl
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年1月14日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.entity.User;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.UserRegInfo;
import com.xinlianfeng.yibaker.common.service.TestService;
import com.xinlianfeng.yibaker.provider.dao.TestDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;
import com.xinlianfeng.yibaker.provider.dao.UserRegInfoDao;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年1月14日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Service("testService")
public class TestServiceImpl implements TestService
{

	@Autowired
	private TestDao testDao;
	
	@Autowired
	private UserRegInfoDao userRegInfoDao;

	@Autowired
	private UserInfoDao userInfoDao;
	
	@Transactional
	public void createBoth() throws Exception {
		
		System.out.println("entering method ...");
		Long last = testDao.findLastFirst();
		last ++;
		testDao.createFirst(last);
		testDao.createSecond(null);
		System.out.println("exiting method ...");
	}

	@Transactional
	@Override
	public void createUser(User user) throws Exception
	{
		// TODO Auto-generated method stub
		//创建用户注册信息
		UserRegInfo userRegInfo = user.getUserreginfo();
		userRegInfo.setReg_time(System.currentTimeMillis());
		userRegInfo.setStatus(1);//用户状态可用
		userRegInfoDao.createUser(userRegInfo);
		
		//创建用户基本信息
		UserInfo userInfo = user.getUserinfo();
		userInfo.setYb_user_id(userRegInfo.getYb_user_id());
		userInfo.setProto_tag((int)userRegInfo.getYb_user_id());//TODO 暂时操作员编号与一焙用户编号相同
		userInfo.setLevel_id(1);//TODO 需要根据金币数量计算本人等级
		//默认地区
		if (StringUtils.isBlank(userInfo.getArea()))
		{
			userInfo.setArea(StringUtils.EMPTY);
		}
		//默认生日
		if (StringUtils.isBlank(userInfo.getBirth()))
		{
			userInfo.setBirth(StringUtils.EMPTY);
		}
		//默认头像
		if (StringUtils.isBlank(userInfo.getPhoto()))
		{
			userInfo.setPhoto(StringUtils.EMPTY);
		}
		//默认签名
		if (StringUtils.isBlank(userInfo.getSignature()))
		{
			userInfo.setSignature(StringUtils.EMPTY);
		}

		userInfoDao.createUserDetail(userInfo);
	}

}
