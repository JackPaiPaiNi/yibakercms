/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:FeedbackDao.java
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

import com.xinlianfeng.yibaker.common.entity.Feedback;

/**
 * @Description: 用户反馈表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月6日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface FeedbackDao
{
	/**
	 * 增加意见反馈信息
	 * @param feedback
	 * @return
	 */
	int create(Feedback feedback);
}

