/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:BindDeviceDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月6日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.BindDevice;

/**
 * @Description: TODO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月6日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface BindDeviceDao
{
	/**
	 * 插入设备绑定信息，如果存在就修改
	 * @param bindDevice
	 * @return
	 */
	int insertUpdateBind(BindDevice bindDevice);
	
	/**
	 * 解除用户绑定状态
	 * @param bindDevice
	 * @return
	 */
	int updateUnbind(BindDevice bindDevice);
	
	/**
	 * 解除所有用户绑定状态
	 * @param bindDevice
	 * @return
	 */
	int updateUnbindAll(BindDevice bindDevice);
	
	/**
	 * 检查绑定路由器名称是否匹配
	 * @param bindDevice
	 * @return
	 */
	int checkBindWifi(BindDevice bindDevice);
	
	/**
	 * 修改设备名称
	 * @param bindDevice
	 * @return
	 */
	int updateDeviceName(BindDevice bindDevice);
	
	/**
	 * 查询用户绑定设备列表
	 * @param yb_user_id
	 * @param last_bind_id
	 * @param count
	 * @return
	 */
	List<BindDevice> getList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("bind_id")long last_bind_id, 
			@Param("limit")int count);
	
	/**
	 * 查询用户绑定设备总数
	 * @param recipe_id
	 * @return
	 */
	int getTotal(@Param("yb_user_id")long yb_user_id);
	
}

