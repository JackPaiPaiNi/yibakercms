/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:ColumnRecipe.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月11日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;


import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.resp.RecipeListResp;

/**
 * @Description: 栏目菜谱Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月11日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface ColumnRecipeDao
{
	/**
	 * 查询首页幻灯片菜谱
	 * @return
	 */
	RecipeListResp getSlideAll(@Param("limit")int count);
	
	/**
	 * 查询最新食谱栏目菜谱
	 * @return
	 */
	RecipeListResp getLatestAll(@Param("limit")int count);
	
	/**
	 * 查询热度榜栏目菜谱
	 * @return
	 */
	RecipeListResp getHotAll(@Param("limit")int count);
	
	/**
	 * 查询本周TOP10栏目菜谱
	 * @return
	 */
	RecipeListResp getTopAll(@Param("limit")int count);

}

