/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:PersonDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
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
package com.xinlianfeng.yibaker.provider.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xinlianfeng.yibaker.common.entity.UserRegInfo;
import com.xinlianfeng.yibaker.common.req.ResetPasswdReq;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年9月22日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Repository
public interface UserRegInfoDao
{
	/**
	 * 密码校验
	 * @return
	 */
	UserRegInfo checkPasswd(UserRegInfo userRegInfo);
	
	/**
	 * 创建用户注册信息
	 * @param user
	 * @return
	 */
	int createUser(UserRegInfo userRegInfo);
	
	/**
	 * 查询用户密码
	 * @param user
	 * @return
	 */
	UserRegInfo findPasswd(long yb_user_id);
	
	/**
	 * 修改用户密码
	 * @param userRegInfo
	 * @return
	 */
	int changePasswd(UserRegInfo userRegInfo);
	
	/**
	 * 重设用户密码
	 * @param resetPasswdReq
	 * @return
	 */
	int resetPasswd(ResetPasswdReq resetPasswdReq);
	
	/**
	 * 手机号码是否已注册
	 * @param mobile
	 * @return
	 */
	int checkMobile(@Param("mobile")String mobile);
	
	/**
	 * 查询绑定的手机
	 * @param yb_user_id
	 * @return
	 */
	UserRegInfo getMobile(@Param("yb_user_id")long yb_user_id);
	
	/**
	 * 绑定手机
	 * @param yb_user_id
	 * @param mobile
	 * @return
	 */
	int addMobile(UserRegInfo userRegInfo);
	
	/**
	 * 修改用户注册信息
	 * @param userRegInfo
	 * @return
	 */
	int updateUserRegInfo(UserRegInfo userRegInfo);
	
	/**
	 * 查找注册用户
	 * @param yb_user_id
	 * @param row_cnt
	 * @return
	 */
	List<UserRegInfo> findUserRegInfos(@Param("yb_user_id") Long yb_user_id,  @Param("row_cnt") Integer row_cnt);
	
}
