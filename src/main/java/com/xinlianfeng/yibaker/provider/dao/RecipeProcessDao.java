/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:RecipeProcessDao.java
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

import com.xinlianfeng.yibaker.common.entity.RecipeProcess;

/**
 * @Description: 菜谱制作过程表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface RecipeProcessDao
{
	/**
	 * 创建菜谱制作过程
	 * @param recipeProcess
	 * @return
	 */
	int createRecipeProcess(RecipeProcess recipeProcess);

}

