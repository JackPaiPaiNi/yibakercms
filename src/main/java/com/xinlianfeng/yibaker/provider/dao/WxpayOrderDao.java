/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:WxpayOrderDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
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

import java.util.List;

import com.xinlianfeng.yibaker.common.entity.WxpayOrder;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年3月23日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface WxpayOrderDao
{

	int createWxpayOrder(WxpayOrder wxpayOrder);
	
	
	int updateWxpayOrder(WxpayOrder wxpayOrder);
	
	WxpayOrder findOne(WxpayOrder wxpayOrder);
	
	WxpayOrder findOneByPrepayid(WxpayOrder wxpayOrder);
	
	List<WxpayOrder>  findWxpayOrdersWithoutResult();
}
