/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:ChannelRecipe.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月19日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.RecipeChannel;
import com.xinlianfeng.yibaker.common.entity.RecipeChannelEx;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;

/**
 * @Description: 类别菜谱Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月19日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface ChannelRecipeDao
{
	/**
	 * 创建菜谱分类关联信息
	 * @param channel_id
	 * @param recipe_id
	 * @return
	 */
	int create(@Param("channel_id")long channel_id, @Param("recipe_id")long recipe_id);
	
	/**
	 * 根据类别查询菜谱列表
	 * @param channel_id
	 * @param last_id
	 * @param count
	 * @return
	 */
	RecipeListResp getList(@Param("channel_id")long channel_id, @Param("recipe_id")long last_id, @Param("limit")int count);
	
	
	/**
	 * 根据类别查询菜谱列表(必须有曲线)
	 * @param channel_id
	 * @param last_id
	 * @param count
	 * @return
	 */
	RecipeListResp getListAndCurve(@Param("channel_id")long channel_id, @Param("recipe_id")long last_id,@Param("recipe_manuid")int recipe_manuid ,@Param("limit")int count);
	
	
	/**
	 * 查询类别菜谱总数
	 * @param channel_id
	 * @return
	 */
	int getTotal(@Param("channel_id")long channel_id);
	
	
	/**
	 * 查询类别菜谱总数 只查询一焙的
	 * @param channel_id
	 * @return
	 */
	int getTotalandCurve(@Param("channel_id")long channel_id);
	
	/**
	 * 查询食谱分类及其下面的食谱
	 * @param brand_channel_id
	 * @param bakemode_channel_pid
	 * @return
	 */
	List<RecipeChannelEx> findChannelsWithRecipes(@Param("brand_channel_id") int brand_channel_id,
			@Param("bakemode_channel_pid") int bakemode_channel_pid);
	
	/**
	 * 查询父类的食谱子分类
	 * 
	 * @param channel_pid
	 * @return
	 */
	List<RecipeChannel> findChannels(@Param("channel_pid") int channel_pid);

	/**
	 * 根据类别分类查询菜谱列表
	 * 
	 * @param brand_channel_id
	 * @param channel_id
	 * @param type
	 * @param last_id
	 * @param count
	 * @return
	 */
	List<RecipeChannelEx> getTypeList(@Param("brand_channel_id") int brand_channel_id, @Param("channel_id") long channel_id, @Param("type") int type, @Param("last_id") long last_id,
			@Param("limit") int count);

	/**
	 * 查询分类类别菜谱总数
	 * 
	 * @param brand_channel_id
	 * @param channel_id
	 * @param type
	 * @return
	 */
	int getTypeTotal(@Param("brand_channel_id") int brand_channel_id, @Param("channel_id") long channel_id, @Param("type") int type);

	/**
	 * 查询品牌的食谱分类信息
	 * 
	 * @param brand_channel_id
	 * @return
	 */
	RecipeChannel findBrandChannel(@Param("brand_channel_id") int brand_channel_id);
}

