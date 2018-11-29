/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeIngredientDao.java
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
import com.xinlianfeng.yibaker.common.entity.RecipeIngredient;

/**
 * @Description: 菜谱食材表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface RecipeIngredientDao
{
	/**
	 * 创建菜谱食材
	 * @param recipeIngredient
	 * @return
	 */
	int createRecipeIngredient(RecipeIngredient recipeIngredient);

	/**
	 * 获取所有菜谱食材
	 * 
	 * @return List<RecipeIngredient>
	 */
	List<RecipeIngredient> getRecipeIngredientAll();
	
	
	/**
	 * 3.0 搜索获取菜谱食材
	 * 
	 * @return List<RecipeIngredient>
	 */
	List<RecipeIngredient> getRecipeIngredient();
	
}

