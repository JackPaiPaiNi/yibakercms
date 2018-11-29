/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:DeviceOpLogDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月27日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.DeviceOpLog;

/**
 * @Description: 设备操作日志表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月27日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface DeviceOpLogDao
{
	/**
	 * 创建设备操作日志
	 * @param log
	 * @return
	 */
	int create(DeviceOpLog log);
	
	/**
	 * 查询设备操作日志列表
	 * @param yb_user_id
	 * @param device_id
	 * @param last_log_id
	 * @param count
	 * @return
	 */
	List<DeviceOpLog> getList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("device_id")String device_id, 
			@Param("log_id")long last_log_id, 
			@Param("limit")int count);
	
	/**
	 * 查询设备操作日志总数
	 * @param yb_user_id
	 * @param device_id
	 * @return
	 */
	int getTotal(@Param("yb_user_id")long yb_user_id, @Param("device_id")String device_id);

}

