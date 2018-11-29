/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:ActivityDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月26日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.Activity;

/**
 * @Description: 活动信息Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月26日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface ActivityDao
{
	/**
	 * 查询活动列表
	 * @param last_id
	 * @param count
	 * @return
	 */
	List<Activity> getList(@Param("activity_id")long last_id, @Param("limit")int count);

	/**
	 * 查询活动总数
	 * @return
	 */
	int getTotal();
}

