/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:RewardRecordDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
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

import com.xinlianfeng.yibaker.common.entity.RewardRecord;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年3月23日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface RewardRecordDao
{

	int createRewardRecord(RewardRecord rewardRecord);
	
	int updateRewardOrderno(RewardRecord rewardRecord);
	
	int findRewardRecordCntToday(RewardRecord rewardRecord);
	
	RewardRecord findOneByRewardNo(@Param("reward_no") Long reward_no);
}
