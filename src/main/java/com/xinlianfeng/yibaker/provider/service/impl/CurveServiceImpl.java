/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:CurveServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月5日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.xinlianfeng.yibaker.common.constant.PageInfo;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.UserCurve;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.resp.UserCurveListResp;
import com.xinlianfeng.yibaker.common.service.CurveService;
import com.xinlianfeng.yibaker.provider.dao.UserCurveDao;

/**
 * @Description: 曲线服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("curveService")
public class CurveServiceImpl implements CurveService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserCurveDao userCurveDao;

	/**
	 * 上传用户曲线
	 * @param userCurve
	 * @return
	 * @throws Exception
	 */
	public long uploadUserCurve(UserCurve userCurve) throws Exception
	{
		if (null == userCurve || 0 == userCurve.getYb_user_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			userCurve.setCreate_time(System.currentTimeMillis());
			//如果不填device_type,则默认为1
			if(0==userCurve.getDevice_type())
			{
				userCurve.setDevice_type(1);
			}
			userCurveDao.create(userCurve);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CURVE_UPLOAD_FAIL);					
		}
		return userCurve.getCurve_id();
	}
	
	/**
	 * 修改用户曲线
	 * @param userCurve
	 * @return
	 * @throws Exception
	 */
	public int updateUserCurve(UserCurve userCurve) throws Exception
	{
		if (null == userCurve
				|| 0 == userCurve.getYb_user_id()
				|| 0 == userCurve.getCurve_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			userCurveDao.update(userCurve);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CURVE_UPDATE_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 删除用户曲线
	 * @param userCurve
	 * @return
	 * @throws Exception
	 */
	public int deleteUserCurve(long yb_user_id, long curve_id) throws Exception
	{
		if (0 ==yb_user_id || 0 == curve_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			userCurveDao.delete(yb_user_id, curve_id);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CURVE_DELETE_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 查询用户曲线详情
	 * @param yb_user_id
	 * @param curve_id
	 * @return
	 * @throws Exception
	 */
	public UserCurve getUserCurveInfo(long yb_user_id, long curve_id) throws Exception
	{
		if (0 ==yb_user_id || 0 == curve_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			UserCurve data = userCurveDao.getInfo(yb_user_id, curve_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CURVE_GETINFO_FAIL);					
		}
	}
	
	/**
	 * 查询曲线列表
	 * @param yb_user_id
	 * @param device_type
	 * @param last_reply_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public UserCurveListResp getUserCurveList(long yb_user_id, int device_type, long last_curve_id, int count) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<UserCurve> curvelist = userCurveDao.getList(yb_user_id, device_type, last_curve_id, count);
			
			if (null == curvelist || 0 == curvelist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			UserCurveListResp resp = new UserCurveListResp();
			resp.setCurvelist(curvelist);
			int size = curvelist.size();
			int total = userCurveDao.getTotal(yb_user_id,device_type,last_curve_id);
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CURVE_GETLIST_FAIL);					
		}
	}

	/**
	 * 查询曲线列表
	 * @param yb_user_id
	 * @param manu_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public UserCurveListResp getUserCurveListByMaunuId(long yb_user_id, int manu_id, long last_curve_id, int count)
			throws Exception {
		// TODO Auto-generated method stub
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		if(0==count)
		{
			count=PageInfo.DEFAULT_COUNT;
		}	
		try{
			UserCurveListResp resp = new UserCurveListResp();
			List<UserCurve> curvelist=null;
			int size,total=0;
			curvelist=userCurveDao.getListByManuId(yb_user_id, manu_id, last_curve_id, count);
			if (null == curvelist || 0 == curvelist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			resp.setCurvelist(curvelist);
			size=curvelist.size();
			total=userCurveDao.getTotalByManuId(yb_user_id, manu_id, last_curve_id);
			resp.setCount(size);
			resp.setTotal(total);
			return resp;
		}
		catch(DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CURVE_GETLIST_FAIL);
		}
	}
		
}

