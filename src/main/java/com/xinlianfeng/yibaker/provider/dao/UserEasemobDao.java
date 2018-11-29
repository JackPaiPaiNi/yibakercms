/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:UserEasemob.java   Package name:com.xinlianfeng.yibaker.provider.dao
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年7月12日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.UserEasemob;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年7月12日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface UserEasemobDao
{

	/**
	 * 创建环信用户
	 * @param userEasemob
	 * @return
	 */
	int createUserEasemob(UserEasemob userEasemob);
	
	/**
	 * 删除单个环信用户
	 * @param yb_user_id
	 * @return
	 */
	int deleteUserEasemob(@Param("yb_user_id") Long yb_user_id);
	
	
	/**
	 * 查找单个环信用户
	 * @param yb_user_id
	 * @return
	 */
	UserEasemob findUserEasemob(@Param("yb_user_id") Long yb_user_id);
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	int deleteUserEasemobByUsername(@Param("username") String username);
	
	/**
	 * 删除所有环信用户
	 * @return
	 */
	int deleteUserEasemobAll();
	
	/**
	 * 分页查询环信用户
	 * @param yb_user_id
	 * @param row_cnt
	 * @return
	 */
	List<UserEasemob> findUserEasemobs(@Param("yb_user_id") Long yb_user_id, @Param("row_cnt") Integer row_cnt);
}
