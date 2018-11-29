/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SubjectReplyDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月16日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.SubjectReply;
import com.xinlianfeng.yibaker.common.resp.SubjectReplyListResp;

/**
 * @Description: 话题评论表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月16日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SubjectReplyDao
{
	/**
	 * 创建话题评论
	 * @param subjectReply
	 * @return
	 */
	int create(SubjectReply subjectReply);
	
	/**
	 * 查询话题评论列表
	 * @param subject_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 */
	SubjectReplyListResp getList(@Param("yb_user_id")long yb_user_id, @Param("subject_id")long subject_id, @Param("reply_id")long last_reply_id, @Param("limit")int count);
	
	/**
	 * 查询话题评论总数
	 * @param board_id
	 * @return
	 */
	int getTotal(@Param("yb_user_id")long yb_user_id, @Param("subject_id")long subject_id);
}

