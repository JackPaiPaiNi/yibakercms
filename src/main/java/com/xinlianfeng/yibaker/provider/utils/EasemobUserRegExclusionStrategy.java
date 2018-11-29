/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:EasemobUserRegExclusionStrategy.java   Package name:com.xinlianfeng.yibaker.provider.utils
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年7月14日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年7月14日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public class EasemobUserRegExclusionStrategy implements ExclusionStrategy
{

	/**
	 * 
	 */
	public EasemobUserRegExclusionStrategy()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipClass(java.lang.Class)
	 */
	@Override
	public boolean shouldSkipClass(Class<?> arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.google.gson.ExclusionStrategy#shouldSkipField(com.google.gson.FieldAttributes)
	 */
	@Override
	public boolean shouldSkipField(FieldAttributes f)
	{
		// TODO Auto-generated method stub
		  if("yb_user_id".equals(f.getName())) return true;
		  if("ctime".equals(f.getName())) return true;
		  if("note".equals(f.getName())) return true;
		  
          return false;
	}

}
