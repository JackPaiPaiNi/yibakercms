/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeDao.java
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

import com.xinlianfeng.yibaker.common.entity.Recipe;
import com.xinlianfeng.yibaker.common.entity.RecipeBriefInfo;
import com.xinlianfeng.yibaker.common.entity.RecipeSellInfo;
import com.xinlianfeng.yibaker.common.resp.RecipeBriefListResp;
import com.xinlianfeng.yibaker.common.resp.RecipeDataResp;
import com.xinlianfeng.yibaker.common.resp.RecipeDetailResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;

/**
 * @Description: 菜谱信息表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface RecipeDao
{
	/**
	 * 创建菜谱信息
	 * @param recipe
	 * @return
	 */
	int createRecipe(Recipe recipe);
	/**
	 * 创建菜谱扩展信息
	 * @param recipe_id
	 * @param recipe_no
	 * @param recipe_manuid
	 */
	void createRecipeExt(@Param("recipe_id")long recipe_id,@Param("recipe_no")String recipe_no,@Param("recipe_manuid")int recipe_manuid);
	/**
	 * 查询菜谱详情
	 * @param recipe_id
	 * @return
	 */
	RecipeDetailResp getRecipeDetail(@Param("recipe_id")long recipe_id);
	
	/**
	 * 获取菜谱执行数据
	 * @param recipe_id
	 * @return
	 */
	RecipeDataResp getRecipeData(@Param("recipe_id")long recipe_id);
	
	/**
	 * 获取我认证失败的菜谱数据
	 * @param recipe_id
	 * @return
	 */
	RecipeDataResp getMyFailData(@Param("yb_user_id")long yb_user_id, @Param("recipe_id")long recipe_id);
	
	/**
	 * 逻辑删除我认证失败的菜谱
	 * @param yb_user_id
	 * @param recipe_id
	 * @return
	 */
	int deleteMyFailRecipe(@Param("yb_user_id")long yb_user_id, @Param("recipe_id")long recipe_id);
	
	/**
	 * 查询菜谱定价
	 * @param recipe_id
	 * @return
	 */
	int getRecipePrice(@Param("recipe_id")long recipe_id);
	
	/**
	 * 查询菜谱简介
	 * @param recipe_id
	 * @return
	 */
	RecipeBriefInfo getRecipeBriefInfo(@Param("recipe_id")long recipe_id);

	/**
	 * 查询菜谱信息
	 * @param recipe_id
	 * @return
	 */
	Recipe getRecipeInfo(@Param("recipe_id")long recipe_id);

	/**
	 * 查询我发布的菜谱
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 */
	RecipeListResp getMyList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("create_time")long last_time, 
			@Param("limit")int count);
	
	/**
	 * 查询我发布的菜谱总数
	 * @param yb_user_id
	 * @return
	 */
	int getMyTotal(@Param("yb_user_id")long yb_user_id);

	/**
	 * 查询我认证失败的菜谱列表
	 * @param yb_user_id
	 * @param last_id
	 * @param count
	 * @return
	 */
	List<RecipeBriefInfo> getMyFailList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("limit")int count);
	
	/**
	 * 查询我收藏的菜谱
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 */
	RecipeBriefListResp getMyCollectList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("collect_time")long last_time, 
			@Param("limit")int count);

	/**
	 * 查询我收藏的菜谱总数
	 * @param yb_user_id
	 * @return
	 */
	int getMyCollectTotal(@Param("yb_user_id")long yb_user_id);

	/**
	 * 查询我下载的菜谱
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 */
	RecipeBriefListResp getMyDownLoadList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("download_time")long last_time, 
			@Param("limit")int count);

	/**
	 * 查询我下载的菜谱总数
	 * @param yb_user_id
	 * @return
	 */
	int getMyDownLoadTotal(@Param("yb_user_id")long yb_user_id);

	/**
	 * 查询她的发布列表（无授权）
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	RecipeListResp getOtherList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("create_time")long last_time, 
			@Param("limit")int count);
	
	/**
	 * 查询她发布的菜谱总数（有授权）
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	int getOtherListTotal(@Param("yb_user_id")long yb_user_id);
	
	/**
	 * 搜索菜谱（sql方式，暂时用来调试）
	 * @param keyword
	 * @param last_id
	 * @param count
	 * @return
	 */
	RecipeListResp searchList(@Param("ids") List<Long> ids);
	
	/**
	 * 搜索的菜谱总数（sql方式，暂时用来调试）
	 * @param keyword
	 * @return
	 */
	List<Long> filterIds(@Param("ids") List<Long> ids);
	/**
	 * 搜索菜谱（sql方式，暂时用来调试）3.0
	 * @param keyword
	 * @param last_id
	 * @param count
	 * @return
	 */
	RecipeListResp searchList3(@Param("ids") List<Long> ids);
	
	/**
	 * 搜索的菜谱总数（sql方式，暂时用来调试）3.0
	 * @param keyword
	 * @return
	 */
	List<Long> filterIds3(@Param("ids") List<Long> ids);

	/**
	 * 获取所有菜谱
	 * 
	 * @return RecipeListResp
	 */
	List<Recipe> getSearchAll();
	/**
	 * 3.0接口 获取菜谱相关
	 * 
	 * @return RecipeListResp
	 */
	List<Recipe> getSearch();
	/**
	 * 获取食谱出售信息
	 * @param recipe_id
	 * @return
	 */
	RecipeSellInfo findRecipeSellInfo(@Param("recipe_id")long recipe_id);
	
	/**
	 * 查询我的自选菜谱
	 * @param yb_user_id
	 * @param last_time
	 * @param count
	 * @return
	 */
	RecipeBriefListResp getOptionalList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("download_time")long last_time,
			@Param("device_type")int device_type,
			@Param("limit")int count);
	/**
	 * 查询我的自选菜谱总数
	 * @param yb_user_id
	 * @param device_type
	 * @return
	 */
	int getOptionalTotal(@Param("yb_user_id")long yb_user_id,@Param("device_type")int device_type);
			
}

