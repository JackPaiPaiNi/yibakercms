/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:IncomeStateDao.java   Package name:com.xinlianfeng.yibaker.provider.dao
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

import com.xinlianfeng.yibaker.common.entity.IncomeState;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年12月30日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public interface IncomeStateDao
{

	/**
	 * 创建收支记录
	 * @param incomeState
	 * @return
	 */
	int createIncomeState( IncomeState  incomeState);
	
	/**
	 * 查找收支记录
	 * @param yb_user_id
	 * @return
	 */
	List<IncomeState> findIncomeStates( Long yb_user_id);
	
	/**
	 * 查找收支记录总数
	 * @param yb_user_id
	 * @return
	 */
	int findIncomeStatesCnt( Long yb_user_id);
	
	/**
	 * 分页查找收支记录
	 * @param yb_user_id
	 * @param last_time
	 * @param count
	 * @return
	 */
	List<IncomeState> findIncomeStatesByPage( @Param("yb_user_id") Long yb_user_id,  @Param("last_time") Long last_time, @Param("count") Integer count);
	
	/**
	 * 查找收支记录总数
	 * @param yb_user_id
	 * @return
	 */
	int findIncomeStatesCntForReason( @Param("yb_user_id") Long yb_user_id, @Param("inst_reason")  Byte inst_reason);
	
	/**
	 * 分页查找收支记录
	 * @param yb_user_id
	 * @param last_time
	 * @param count
	 * @return
	 */
	List<IncomeState> findIncomeStatesByPageForReason( @Param("yb_user_id") Long yb_user_id,  @Param("last_time") Long last_time, @Param("count") Integer count,  @Param("inst_reason") Byte inst_reason);
	
	/**
	 * 查看收支详情
	 * @param yb_user_id
	 * @param inst_id
	 * @param inst_reason
	 * @return
	 */
	IncomeState findIncomeStateDetail( @Param("yb_user_id") Long yb_user_id,  @Param("inst_id") Long inst_id,   @Param("inst_reason") Byte inst_reason);
	
	/**
	 * 查找收支记录
	 * @param inst_id
	 * @return
	 */
	IncomeState findOne(@Param("inst_id") Long inst_id);
	
}
