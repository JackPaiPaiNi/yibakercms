/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name: TopicReplyDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2016年07月13日
 *   Author: xuqingdong(xuqingdong@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;


import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.TopicReply;
import com.xinlianfeng.yibaker.common.resp.TopicReplyListResp;

/**
 * @Description: 专题评论Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2016
 * @version: V2.0
 * @date: 2016年07月13日
 * @author xuqingdong (xuqingdong@topeastic.com)
 */
public interface TopicReplyDao
{
	/**
	 * 创建菜谱专题评论信息
	 * @param topicReply
	 * @return
	 */
	int create(TopicReply topicReply);

	/**
	 * 查询菜谱专题评论列表
	 * @param topic_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 */
	TopicReplyListResp getList(@Param("topic_id")long topic_id, @Param("reply_id")long last_reply_id,  @Param("limit")int count);
	
	/**
	 * 查询菜谱专题评论总数
	 * @param topic_id
	 * @return
	 */
	int getTotal(@Param("topic_id")long topic_id);

}

