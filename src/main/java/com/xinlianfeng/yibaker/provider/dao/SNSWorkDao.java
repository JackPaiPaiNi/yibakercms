/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSWorkDao.java
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

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.SNSMyTopic;
import com.xinlianfeng.yibaker.common.entity.SNSMyWork;
import com.xinlianfeng.yibaker.common.req.LikeWorkReq;
import com.xinlianfeng.yibaker.common.resp.OpDetailResp;

/**
 * @Description: SNS作品操作表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSWorkDao
{
	/**
	 * 插入SNS菜谱评论点赞信息，如果存在就修改
	 * @param likeRecipeReplyReq
	 * @return
	 */
	int insertUpdateLike(LikeWorkReq likeWorkReq);
	
	/**
	 * 查询作品点赞列表
	 * @param work_id
	 * @param last_time
	 * @param count
	 * @return
	 */
	List<OpDetailResp> getLikeList(@Param("work_id")long work_id, @Param("like_time")long last_time, @Param("limit")int count);
	
	/**
	 * 查询作品点赞总数
	 * @param work_id
	 * @return
	 */
	int getLikeTotal(@Param("work_id")long work_id);

	/**
	 * 用户对作品的SNS状态
	 * @param yb_user_id
	 * @return
	 */
	SNSMyWork getSNSMyWork(@Param("yb_user_id")long yb_user_id, @Param("work_id")long work_id);

}

