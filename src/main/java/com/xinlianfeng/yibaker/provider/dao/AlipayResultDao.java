/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:AlipayResultDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年3月23日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.AlipayResult;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年3月23日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface AlipayResultDao
{

	int createAlipayResult(AlipayResult alipayResult);
	
	AlipayResult findOneByOrderno(@Param("out_trade_no") String orderno );
	
	int updateAlipayResultStatus(AlipayResult alipayResult);
}
