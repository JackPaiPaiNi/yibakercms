/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:ClassRoomDao.java
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

import com.xinlianfeng.yibaker.common.entity.ClassRoom;

/**
 * @Description: 烘焙课堂信息表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月26日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface ClassRoomDao
{
	/**
	 * 查询烘焙课堂列表
	 * @param last_id
	 * @param count
	 * @return
	 */
	List<ClassRoom> getList(@Param("class_id")long last_id, @Param("limit")int count, @Param("class_type")int class_type);
	
	/**
	 * 查询烘焙课堂总数
	 * @return
	 */
	int getTotal(@Param("class_type")int class_type);
	
}

