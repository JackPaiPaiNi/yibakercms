/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:UserDetailDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年9月29日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xinlianfeng.yibaker.common.entity.User;
import com.xinlianfeng.yibaker.common.entity.UserBriefInfo;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.UserLevelInfo;
import com.xinlianfeng.yibaker.common.entity.UserRegInfo;
import com.xinlianfeng.yibaker.common.resp.FollowDetailResp;
import com.xinlianfeng.yibaker.common.resp.FollowListResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;
import com.xinlianfeng.yibaker.common.resp.UserDetailResp;

/**
 * @Description: 用户详细信息表DAO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年9月29日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Repository
public interface UserInfoDao
{
	/**
	 * 查询单个用户
	 * @return
	 */
	UserInfo findOne(Long yb_user_id);

	/**
	 * 创建用户详细信息
	 * @param user
	 * @return
	 */
	int createUserDetail(UserInfo userInfo);
	
	/**
	 * 修改个人资料
	 * @param userInfo
	 * @return
	 */
	int updateUserInfo(UserInfo userInfo);
	
	/**
	 * 查询用户简介
	 * @param yb_user_id
	 * @return
	 */
	UserBriefInfo getUserBriefInfo(@Param("yb_user_id")long yb_user_id);
	
	/**
	 * 查询我的信息
	 * @param yb_user_id
	 * @return
	 */
	UserDetailResp getMyInfo(@Param("yb_user_id")long yb_user_id);

	/**
	 * 查询她的信息
	 * @param yb_user_id
	 * @return
	 */
	UserDetailResp getOtherInfo(@Param("yb_user_id")long yb_user_id);
	
	/**
	 * 查询好友列表
	 * @param yb_user_id
	 * @param last_time
	 * @param count
	 * @return
	 */
	List<FollowDetailResp> getFriendList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("user_id")long user_id, 
			@Param("friend_type")long friend_type, 
			@Param("follow_time")long last_time, 
			@Param("limit")int count);

	/**
	 * 查询好友总数
	 * @param yb_user_id
	 * @return
	 */
	int getFriendTotal(@Param("user_id")long user_id, @Param("friend_type")long friend_type);

	/**
	 * 检查昵称是否存在
	 * @param nickname
	 * @return
	 */
	int checkNickName(@Param("nickname")String nickname);
	
	/**
	 * 查询用户等级信息
	 * @param yb_user_id
	 * @return
	 */
	UserLevelInfo findUserLevelInfo(Long yb_user_id);

	/**
	 * 更新用户等级
	 * @param userInfo
	 * @return
	 */
	int updateUserLevel(UserInfo userInfo);
	
	/**
	 * 根据环信用户账号查找用户信息
	 * @param hx_user_account 环信用户账号
	 * @return UserInfo
	 */
	UserInfo findUserInfoByHxUserAcccount(Long hx_user_account);
}

