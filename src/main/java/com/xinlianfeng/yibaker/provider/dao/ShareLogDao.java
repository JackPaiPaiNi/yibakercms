/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeShareLogDao.java
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

import com.xinlianfeng.yibaker.common.entity.ShareLog;

/**
 * @Description: 菜谱分享日志表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月27日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface ShareLogDao
{
	/**
	 * 创建菜谱分享日志
	 * @param log
	 * @return
	 */
	int create(ShareLog shareLog);
	
}

