/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.dao
 * File name:UserCurveDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月4日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.UserCurve;


/**
 * @Description: 用户曲线表Dao
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
public interface UserCurveDao
{
	/**
	 * 创建用户曲线信息
	 * @param userCurve
	 * @return
	 */
	int create(UserCurve userCurve);
	
	/**
	 * 修改用户曲线信息
	 * @param userCurve
	 * @return
	 */
	int update(UserCurve userCurve);
	
	/**
	 * 删除用户曲线信息
	 * @param yb_user_id
	 * @param curve_id
	 * @return
	 */
	int delete(@Param("yb_user_id")long yb_user_id,@Param("curve_id") long curve_id);
	
	/**
	 * 查找一条用户曲线信息
	 * @param curve_id
	 * @return
	 */
	UserCurve getInfo(@Param("yb_user_id")long yb_user_id,@Param("curve_id") long curve_id);
	
	/**
	 * 查询用户曲线列表
	 * @param yb_user_id
	 * @param device_type
	 * @param last_curve_id
	 * @param count
	 * @return
	 */
	List<UserCurve> getList(
			@Param("yb_user_id")long yb_user_id, 
			@Param("device_type")int device_type, 
			@Param("curve_id")long last_curve_id, 
			@Param("limit")int count);
	
	/**
	 * 查询用户曲线总数
	 * @param recipe_id
	 * @return
	 */
	int getTotal(@Param("yb_user_id")long yb_user_id,@Param("device_type")int device_type,
			@Param("curve_id")long last_curve_id);
	
	/**
	 * 查询用户曲线列表(依据厂商编号)
	 * @param yb_user_id
	 * @param manu_id
	 * @param last_curve_id
	 * @param count
	 * @return
	 */
    List<UserCurve> getListByManuId(
    		@Param("yb_user_id")long yb_user_id,
    		@Param("manu_id")int manu_id,
    		@Param("curve_id")long last_curve_id,
    		@Param("limit")int count);
    /**
     * 查询用户曲线总数(依据厂商编号)
     * @param yb_user_id
     * @param manu_id
     * @param last_curve_id
     * @return
     */
    int getTotalByManuId(@Param("yb_user_id")long yb_user_id,
    		             @Param("manu_id")int manu_id,
    		             @Param("curve_id")long last_curve_id); 		
}

