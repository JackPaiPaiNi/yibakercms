/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:BakerAccountDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2015年12月30日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.BakerAccount;
import com.xinlianfeng.yibaker.common.entity.IncomeState;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年12月30日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface BakerAccountDao
{

	/**
	 * 创建焙壳账户
	 * @param bakerAccount
	 * @return
	 */
	int createBakerAccount(BakerAccount bakerAccount);
	
	/**
	 * 更新焙壳账户
	 * @param incomeState
	 * @return
	 */
	int updateBakerAccount(IncomeState incomeState);
	
	/**
	 * 
	 * @param yb_user_id
	 * @return
	 */
	BakerAccount findOne(@Param("yb_user_id") Long yb_user_id);
	
	/**
	 * 
	 * @param ba_id
	 * @return
	 */
	BakerAccount findOneByPKForUpdate(@Param("ba_id") Long ba_id);
}
