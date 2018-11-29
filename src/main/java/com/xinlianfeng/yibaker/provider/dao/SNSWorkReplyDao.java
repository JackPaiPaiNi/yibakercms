/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSRecipeReply.java
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

import com.xinlianfeng.yibaker.common.req.LikeWorkReplyReq;

/**
 * @Description: SNS作品评论操作表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSWorkReplyDao
{
	/**
	 * 插入SNS作品评论点赞信息，如果存在就修改
	 * @param likeRecipeReplyReq
	 * @return
	 */
	int insertUpdateLike(LikeWorkReplyReq likeRecipeReplyReq);

}

