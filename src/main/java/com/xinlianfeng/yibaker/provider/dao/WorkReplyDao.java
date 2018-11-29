/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:WorkReplyDao.java
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

import com.xinlianfeng.yibaker.common.entity.WorkReply;
import com.xinlianfeng.yibaker.common.resp.WorkReplyListResp;

/**
 * @Description: TODO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface WorkReplyDao
{
	/**
	 * 创建菜谱作品评论信息
	 * @param workReply
	 * @return
	 */
	int create(WorkReply workReply);

	/**
	 * 查询菜谱作品评论列表
	 * @param work_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 */
	WorkReplyListResp getList(@Param("work_id")long work_id, @Param("reply_id")long last_reply_id,  @Param("limit")int count);
	
	/**
	 * 查询菜谱作品评论总数
	 * @param work_id
	 * @return
	 */
	int getTotal(@Param("work_id")long work_id);

}

