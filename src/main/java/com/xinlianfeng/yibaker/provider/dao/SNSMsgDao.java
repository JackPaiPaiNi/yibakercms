/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSMsgDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月20日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;


import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.SNSMsgCount;

/**
 * @Description: SNS消息表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月20日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSMsgDao
{
	/**
	 * 创建用户消息
	 * @param to_user_id
	 * @param msg_info_id
	 * @return
	 */
	int create(@Param("to_user_id")long to_user_id, @Param("msg_info_id")long msg_info_id, @Param("group_id")long group_id);
	
	/**
	 * 查询用户未读站内消息总数
	 * @param to_user_id
	 * @return
	 */
	int getUnReadTotal(@Param("to_user_id")long to_user_id);
	
	/**
	 * 查询站内未读分类通知数
	 * @param to_user_id
	 * @return
	 */
	SNSMsgCount getUnReadTotalByType(@Param("to_user_id")long to_user_id);
	
	/**
	 * 批量设置已读
	 * @param begin_info_id
	 * @param end_info_id
	 * @return
	 */
	int updateReadList(
			@Param("to_user_id")long to_user_id, 
			@Param("group_id")long group_id,
			@Param("begin_info_id")long begin_info_id, 
			@Param("end_info_id")long end_info_id);
	
	/**
	 * 单条设置已读
	 * @param to_user_id
	 * @param msg_info_id
	 * @return
	 */
	int updateReadSingle(
			@Param("to_user_id")long to_user_id, 
			@Param("msg_info_id")long msg_info_id);
	
	/**
	 * 删除站内通知
	 * @param to_user_id
	 * @param msg_info_id
	 * @return
	 */
	int delete(@Param("to_user_id")long to_user_id, @Param("group_id")int group_id, @Param("msg_info_id")long msg_info_id);
	
}

