/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:MissionPerformRecordDao.java   Package name:com.xinlianfeng.yibaker.provider.dao.mysql
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

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.MissionPerformRecord;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年12月30日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface MissionPerformRecordDao
{

	/**
	 * 创建任务执行记录
	 * @param missionPerformRecord
	 * @return
	 */
	int createMissionPerformRecord(MissionPerformRecord missionPerformRecord);
	
	/**
	 * 查找用户的一次性任务执行记录
	 * @param perform_user_id
	 * @param perform_mission_id
	 * @return
	 */
	MissionPerformRecord findOnceMissionPerformRecord(@Param("perform_user_id") Long perform_user_id, @Param("perform_mission_id")  Long perform_mission_id);
	
	/**
	 * 获取每日任务执行记录
	 * @param perform_user_id
	 * @param perform_mission_id
	 * @return
	 */
	List<MissionPerformRecord> findDailyMissionPerformRecord(@Param("perform_user_id") Long perform_user_id, @Param("perform_mission_id")  Long perform_mission_id);
	
	/**
	 * 获取每日任务执行记录数
	 * @param perform_user_id
	 * @param perform_mission_id
	 * @return
	 */
	int findDailyMissionPerformRecordCnt(@Param("perform_user_id") Long perform_user_id, @Param("perform_mission_id")  Long perform_mission_id);
	
	/**
	 * 获取用户每日签到记录
	 * @param perform_user_id
	 * @param beforedays 前几天,0-今天，1-昨天，以此类推
	 * @return
	 */
	MissionPerformRecord findDailySigninRecord(@Param("perform_user_id") Long perform_user_id, @Param("beforedays")  Integer beforedays);
}
