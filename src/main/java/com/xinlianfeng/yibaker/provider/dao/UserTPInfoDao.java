/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:UserTPDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月1日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.UserTPInfo;

/**
 * @Description: TODO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月1日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Repository
public interface UserTPInfoDao
{
	/**
	 * 查询单个第三方关联用户信息
	 * @return
	 */
	Long isExist(UserTPInfo userTPInfo);

	/**
	 * 创建第三方关联用户信息
	 * @param user
	 * @return
	 */
	int createTPUser(UserTPInfo userTPInfo);
	
	/**
	 * 查询第三方关联用户列表
	 * @param yb_user_id
	 * @return
	 */
	List<UserTPInfo> findByUser(long yb_user_id);

}

