/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:MissionDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2015年12月28日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.Mission;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年12月28日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface MissionDao
{

	/**
	 * 按用户及任务类别查找任务
	 * @param yb_user_id
	 * @param mission_type
	 * @return
	 */
	List<Mission>findMissions(@Param("yb_user_id") Long yb_user_id, @Param("mission_type") Byte mission_type);
	
	/**
	 * 查询用户的一次性任务
	 * @param yb_user_id
	 * @return
	 */
	List<Mission>findOnceMissions(@Param("yb_user_id") Long yb_user_id);
	
	/**
	 * 查询用户的每日任务
	 * @param yb_user_id
	 * @return
	 */
	List<Mission>findDailyMissions(@Param("yb_user_id") Long yb_user_id);
	
	/**
	 * 任务详情
	 * @param yb_user_id
	 * @param mission_id
	 * @return
	 */
	Mission findMissionDetail(@Param("yb_user_id") Long yb_user_id,  @Param("mission_id") Long mission_id);
	
	
	
	/**
	 * 查找单个任务信息
	 * @param mission_id
	 * @return
	 */
	Mission findOne(@Param("mission_id") Long mission_id);
}
