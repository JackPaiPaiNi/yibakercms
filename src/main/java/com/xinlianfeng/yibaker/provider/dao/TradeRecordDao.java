/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:TradeRecordDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年1月11日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import com.xinlianfeng.yibaker.common.entity.TradeRecord;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年1月11日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface TradeRecordDao
{

	/**
	 * 创建交易记录
	 * @param tradeRecord
	 * @return
	 */
	int createTradeRecord(TradeRecord tradeRecord);
	
	/**
	 * 查找购买记录
	 * @param tradeRecord
	 * @return
	 */
	TradeRecord findBuyRecord(TradeRecord tradeRecord);
}
