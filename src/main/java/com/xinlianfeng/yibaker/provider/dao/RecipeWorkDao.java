/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeWorkDao.java
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

import com.xinlianfeng.yibaker.common.entity.RecipeWork;
import com.xinlianfeng.yibaker.common.resp.WorkDetailResp;
import com.xinlianfeng.yibaker.common.resp.WorkListResp;

/**
 * @Description: 菜谱作品表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface RecipeWorkDao
{
	/**
	 * 创建菜谱作品信息
	 * @param recipeWork
	 * @return
	 */
	int create(RecipeWork recipeWork);
	
	/**
	 * 查询作品详情
	 * @param recipe_id
	 * @return
	 */
	WorkDetailResp getWorkDetail(@Param("work_id")long work_id);

	/**
	 * 查询作品信息
	 * @param recipe_id
	 * @return
	 */
	RecipeWork getWorkInfo(@Param("work_id")long work_id);

	/**
	 * 查询菜谱作品列表
	 * @param recipe_id
	 * @param last_work_id
	 * @param count
	 * @return
	 */
	WorkListResp getList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("to_user_id")long to_user_id, 
			@Param("recipe_id")long recipe_id, 
			@Param("work_id")long last_work_id,  
			@Param("limit")int count);

	/**
	 * 查询菜谱作品总数
	 * @param recipe_id
	 * @return
	 */
	int getTotal(@Param("yb_user_id")long yb_user_id, @Param("recipe_id")long recipe_id);

}

