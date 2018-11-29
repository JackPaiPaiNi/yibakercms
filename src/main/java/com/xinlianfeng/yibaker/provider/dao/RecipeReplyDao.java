/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeReplyDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月4日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.RecipeReply;
import com.xinlianfeng.yibaker.common.resp.RecipeReplyListResp;

/**
 * @Description: 菜谱评论表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface RecipeReplyDao
{
	/**
	 * 创建菜谱评论信息
	 * @param Recipereply
	 * @return
	 */
	int create(RecipeReply recipeReply);
	
	/**
	 * 查询菜谱评论列表
	 * @param recipe_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 */
	RecipeReplyListResp getList(@Param("recipe_id")long recipe_id, @Param("reply_id")long last_reply_id,  @Param("limit")int count);
	
	/**
	 * 查询菜谱评论总数
	 * @param recipe_id
	 * @return
	 */
	int getTotal(@Param("recipe_id")long recipe_id);

}

