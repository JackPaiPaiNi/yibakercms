/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSRecipeDao.java
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

import com.xinlianfeng.yibaker.common.entity.SNSMyRecipe;
import com.xinlianfeng.yibaker.common.req.CollectRecipeReq;
import com.xinlianfeng.yibaker.common.req.DownloadRecipeReq;
import com.xinlianfeng.yibaker.common.req.LikeRecipeReq;

/**
 * @Description: SNS菜谱操作表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSRecipeDao
{
	/**
	 * 插入SNS菜谱点赞信息，如果存在就修改
	 * @param likeRecipeReq
	 * @return
	 */
	int insertUpdateLike(LikeRecipeReq likeRecipeReq);
	
	/**
	 * 插入SNS菜谱收藏信息，如果存在就修改
	 * @param collectRecipeReq
	 * @return
	 */
	int insertUpdateCollect(CollectRecipeReq collectRecipeReq);
	
	/**
	 * 插入SNS菜谱下载操作信息，如果存在就修改
	 * @param downloadRecipeReq
	 * @return
	 */
	int insertUpdateDownload(DownloadRecipeReq downloadRecipeReq);
	
	/**
	 * 用户是否已购买下载此菜谱
	 * @param yb_user_id
	 * @param recipe_id
	 * @return
	 */
	int isDownload(@Param("yb_user_id")long yb_user_id, @Param("recipe_id")long recipe_id);
	
	/**
	 * 用户对菜谱的SNS状态
	 * @param yb_user_id
	 * @return
	 */
	SNSMyRecipe getSNSMyRecipe(@Param("yb_user_id")long yb_user_id, @Param("recipe_id")long recipe_id);
	
}

