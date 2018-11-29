/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSSubjectDao.java
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

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.SNSMySubject;
import com.xinlianfeng.yibaker.common.req.LikeSubjectReq;
import com.xinlianfeng.yibaker.common.resp.OpDetailResp;


/**
 * @Description: 社区话题SNS统计Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月16日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSSubjectDao
{

	/**
	 * 插入SNS菜谱评论点赞信息，如果存在就修改
	 * @param likeRecipeReplyReq
	 * @return
	 */
	int insertUpdateLike(LikeSubjectReq likeWorkReq);
	
	/**
	 * 查询社区话题点赞列表
	 * @param subject_id
	 * @param last_time
	 * @param count
	 * @return
	 */
	List<OpDetailResp> getLikeList(@Param("subject_id")long subject_id, @Param("like_time")long last_time, @Param("limit")int count);
	
	/**
	 * 查询社区话题点赞总数
	 * @param subject_id
	 * @return
	 */
	int getLikeTotal(@Param("subject_id")long subject_id);

	/**
	 * 用户对社区话题的SNS状态
	 * @param yb_user_id
	 * @param subject_id
	 * @return
	 */
	SNSMySubject getSNSMySubject(@Param("yb_user_id")long yb_user_id, @Param("subject_id")long subject_id);


}

