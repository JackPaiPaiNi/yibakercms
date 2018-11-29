/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeTopicDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月9日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.RecipeTopic;
import com.xinlianfeng.yibaker.common.resp.TopicDetailResp;
import com.xinlianfeng.yibaker.common.resp.TopicListResp;

/**
 * @Description: 菜谱专题表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月9日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface RecipeTopicDao
{
	/**
	 * 查询专题详情
	 * @param topic_id
	 * @return
	 */
	TopicDetailResp getTopicDetail(long topic_id);

	/**
	 * 查询我收藏的专题列表（有授权）
	 * @param yb_user_id
	 * @param last_topic_id
	 * @param count
	 * @return
	 */
	TopicListResp getMyCollectList(@Param("yb_user_id")long yb_user_id, @Param("collect_time")long last_time, @Param("limit")int count);
	
	/**
	 * 查询我收藏的专题总数（有授权）
	 * @param yb_user_id
	 * @return
	 */
	int getMyCollectTotal(@Param("yb_user_id")long yb_user_id);
	
	/**
	 * 查询专题列表
	 * @param count
	 * @return
	 */
	List<RecipeTopic> getTopicList(@Param("topic_id")long last_topic_id, @Param("limit")int count);

	/**
	 * 查询专题总数
	 * @return
	 */
	int getTotal();
}

