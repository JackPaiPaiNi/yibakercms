/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:SNSMsgInfoDao.java
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

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.SNSMsgInfo;

/**
 * @Description: SNS消息内容表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月20日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface SNSMsgInfoDao
{
	/**
	 * 创建通知内容
	 * @param info
	 * @return
	 */
	int create(SNSMsgInfo info);
	
	/**
	 * 查询分类站内通知列表
	 * @param yb_user_id
	 * @param group_id
	 * @param last_msg_id
	 * @param count
	 * @return
	 */
	List<SNSMsgInfo> getList(
			@Param("to_user_id")long yb_user_id, 
			@Param("group_id")int group_id,  
			@Param("msg_info_id")long last_msg_id, 
			@Param("limit")int count);
	
	/**
	 * 查询分类站内通知总数
	 * @param yb_user_id
	 * @param group_id
	 * @return
	 */
	int getTotal(@Param("to_user_id")long yb_user_id, @Param("group_id")long group_id);
	
	/**
	 * 查询新的广播消息
	 * @param yb_user_id
	 * @return
	 */
	List<SNSMsgInfo> getNewBroadcastMsgList(@Param("to_user_id")long yb_user_id, @Param("limit")int count);
}

