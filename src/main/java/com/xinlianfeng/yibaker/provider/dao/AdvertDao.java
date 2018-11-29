/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:AdvertDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月13日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.Advert;

/**
 * @Description: 广告信息Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月13日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface AdvertDao
{

	/**
	 * 根据类型查询广告信息
	 * @param ad_type
	 * @return
	 */
	Advert getInfo(@Param("ad_type")int ad_type);
	
	/**
	 * 根据类型查询广告列表
	 * @param ad_type
	 * @return
	 */
	List<Advert> getList(@Param("ad_type")int ad_type,  @Param("limit")int count);
	
}

