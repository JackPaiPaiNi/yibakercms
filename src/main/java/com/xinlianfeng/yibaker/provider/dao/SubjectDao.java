/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SubjectDao.java
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

import com.xinlianfeng.yibaker.common.entity.Subject;
import com.xinlianfeng.yibaker.common.entity.SubjectBriefInfo;
import com.xinlianfeng.yibaker.common.resp.SubjectDetailResp;
import com.xinlianfeng.yibaker.common.resp.SubjectListResp;

/**
 * @Description: 话题信息表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月16日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SubjectDao
{
	/**
	 * 创建话题
	 * @param subject
	 * @return
	 */
	int create(Subject subject);
	
	/**
	 * 查询话题详情
	 * @param subject_id
	 * @return
	 */
	SubjectDetailResp getInfo(@Param("subject_id")long subject_id);
	
	/**
	 * 查询话题详情
	 * @param subject_id
	 * @param yb_user_id
	 * @return
	 */
	SubjectDetailResp getSubjectInfo(@Param("subject_id") long subject_id, @Param("yb_user_id") long yb_user_id);
	
	/**
	 * 查询话题简介
	 * @param subject_id
	 * @return
	 */
	SubjectBriefInfo getBriefInfo(@Param("subject_id")long subject_id);
	
	/**
	 * 查询话题列表
	 * @param board_id
	 * @param last_subject_id
	 * @param count
	 * @return
	 */
	SubjectListResp getList(@Param("board_id")int board_id, @Param("subject_id")long last_subject_id, @Param("limit")int count);
	
	/**
	 * 查询话题列表
	 * @param board_id
	 * @param yb_user_id
	 * @param last_subject_id
	 * @param count
	 * @return
	 */
	SubjectListResp getSubjectList(@Param("board_id")int board_id, @Param("subject_id")long last_subject_id, @Param("yb_user_id") long yb_user_id, @Param("limit")int count);
	
	/**
	 * 查询话题列表,按最新回复时间倒序
	 * @param board_id
	 * @param last_reply_time
	 * @param count
	 * @return
	 */
	SubjectListResp findSubjects(@Param("board_id")int board_id, @Param("last_reply_time")long last_reply_time, @Param("limit")int count);
	
	/**
	 * 查询话题总数
	 * @param board_id
	 * @return
	 */
	int getTotal(@Param("board_id")int board_id);

	/**
	 * 查询所有分类话题总数
	 * @param board_id
	 * @return
	 */
	int getAllTotal(@Param("board_id")int board_id);

	/**
	 * 查询话题置顶列表
	 * @param board_id
	 * @return
	 */
	SubjectListResp getTopList(@Param("board_id")int board_id, @Param("limit")int count);
	
	/**
	 * 查询话题置顶列表
	 * @param board_id
	 * @return
	 */
	SubjectListResp getTopSubjectList(@Param("board_id") int board_id, @Param("yb_user_id") long yb_user_id, @Param("limit")int count);
	
	/**
	 * 查询我的话题列表
	 * @param yb_user_id
	 * @param last_subject_id
	 * @param count
	 * @return
	 */
	SubjectListResp getMytList(@Param("yb_user_id")long yb_user_id, @Param("subject_id")long last_subject_id, @Param("limit")int count);
	
	/**
	 * 查询我的话题总数
	 * @param board_id
	 * @return
	 */
	int getMyTotal(@Param("yb_user_id")long yb_user_id);

}

