/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSTopicDao.java
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

import com.xinlianfeng.yibaker.common.entity.SNSMyTopic;
import com.xinlianfeng.yibaker.common.req.CollectTopicReq;
import com.xinlianfeng.yibaker.common.req.LikeTopicReq;

/**
 * @Description: SNS专题操作表
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSTopicDao
{
	/**
	 * 插入SNS专题点赞信息，如果存在就修改
	 * @param LikeTopicReq
	 * @return
	 */
	int insertUpdateLike(LikeTopicReq likeTopicReq);
	
	/**
	 * 插入SNS专题收藏信息，如果存在就修改
	 * @param collectTopicReq
	 * @return
	 */
	int insertUpdateCollect(CollectTopicReq collectTopicReq);

	/**
	 * 用户对专题的SNS状态
	 * @param yb_user_id
	 * @return
	 */
	SNSMyTopic getSNSMyTopic(@Param("yb_user_id")long yb_user_id, @Param("topic_id")long topic_id);
	
}

