/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSUserDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月5日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.req.FollowUserReq;
import com.xinlianfeng.yibaker.common.req.LikeRecipeReq;

/**
 * @Description: TODO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSUserDao
{
	
	/**
	 * 关注用户
	 * @param followUserReq
	 * @return
	 */
	int insertUpdateFollow(FollowUserReq followUserReq);

	/**
	 * 是否关注
	 * @param followUserReq
	 * @return
	 */
	int isFollow(@Param("yb_user_id")long yb_user_id, @Param("user_id")long user_id);

}

