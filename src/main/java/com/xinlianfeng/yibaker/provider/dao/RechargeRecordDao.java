/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:RechargeRecordDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
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

import com.xinlianfeng.yibaker.common.entity.RechargeRecord;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年3月23日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface RechargeRecordDao
{

	/**
	 * 创建充值记录
	 * @param rechargeRecord
	 * @return
	 */
	int createRechargeRecord(RechargeRecord rechargeRecord);
	
	/**
	 * 更新充值订单号
	 * @param rechargeRecord
	 * @return
	 */
	int updateChargeOrderno(RechargeRecord rechargeRecord);
	
	/**
	 * 更新充值记录
	 * @param rechargeRecord
	 * @return
	 */
	int updateRechargeRecord(RechargeRecord rechargeRecord);
	
	/**
	 * 按订单查找充值记录
	 * @param rechargeRecord
	 * @return
	 */
	RechargeRecord findOneByOrderno(RechargeRecord rechargeRecord);
	
	/**
	 * 获取今天的累计充值金额
	 * @param rechargeRecord
	 * @return
	 */
	int findChargeAmountToday(RechargeRecord rechargeRecord);
}
