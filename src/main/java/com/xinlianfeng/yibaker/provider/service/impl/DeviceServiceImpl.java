/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:DeviceService.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月6日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.xinlianfeng.yibaker.common.constant.BindStatus;
import com.xinlianfeng.yibaker.common.constant.CDNName;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.BindDevice;
import com.xinlianfeng.yibaker.common.entity.CDN;
import com.xinlianfeng.yibaker.common.entity.DeviceOpLog;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.resp.DeviceListResp;
import com.xinlianfeng.yibaker.common.resp.DeviceOpLogListResp;
import com.xinlianfeng.yibaker.common.service.DeviceService;
import com.xinlianfeng.yibaker.provider.dao.BindDeviceDao;
import com.xinlianfeng.yibaker.provider.dao.CDNDao;
import com.xinlianfeng.yibaker.provider.dao.DeviceOpLogDao;

/**
 * @Description: 设备服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月6日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("deviceService")
public class DeviceServiceImpl implements DeviceService
{
//	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BindDeviceDao bindDeviceDao;

	@Autowired
	private CDNDao cdnDao;

	@Autowired
	private DeviceOpLogDao deviceOpLogDao;

	/**
	 * 绑定设备
	 * @param bindDevice
	 * @return
	 * @throws Exception
	 */
	public int bindDevice(BindDevice bindDevice) throws Exception
	{
		if (null == bindDevice 
				|| 0 == bindDevice.getYb_user_id()
				|| StringUtils.isBlank(bindDevice.getDevice_id()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			bindDevice.setBind_status(BindStatus.BIND);
			bindDevice.setBind_time(System.currentTimeMillis());
			bindDeviceDao.insertUpdateBind(bindDevice);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.DEVICE_BIND_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 解除设备绑定
	 * @param bindDevice
	 * @return
	 * @throws Exception
	 */
	public int unbindDevice(BindDevice bindDevice) throws Exception
	{
		if (null == bindDevice 
				|| 0 == bindDevice.getYb_user_id()
				|| StringUtils.isBlank(bindDevice.getDevice_id()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			bindDeviceDao.updateUnbind(bindDevice);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.DEVICE_UNBIND_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 解除所有人的绑定
	 * @param bindDevice
	 * @return
	 * @throws Exception
	 */
	public int unbindAllUser(BindDevice bindDevice) throws Exception
	{
		if (null == bindDevice 
				|| 0 == bindDevice.getYb_user_id()
				|| StringUtils.isBlank(bindDevice.getDevice_id()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == bindDeviceDao.checkBindWifi(bindDevice))
			{
				throw new YiBakerException(RetEnum.DEVICE_CHECKWIFI_FAIL);					
			}
			bindDeviceDao.updateUnbindAll(bindDevice);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.DEVICE_UNBINDALL_FAIL);					
		}
		return 0;
	}

	/**
	 * 修改设备名称
	 * @param bindDevice
	 * @return
	 * @throws Exception
	 */
	public int updateDeviceName(BindDevice bindDevice) throws Exception
	{
		if (null == bindDevice 
				|| 0 == bindDevice.getYb_user_id()
				|| StringUtils.isBlank(bindDevice.getDevice_id())
				|| StringUtils.isBlank(bindDevice.getDevice_name()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			bindDeviceDao.updateDeviceName(bindDevice);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.DEVICE_UPDATE_USERNAME_FAIL);					
		}
		return 0;
	}

	/**
	 * 查询绑定设备列表
	 * @param yb_user_id
	 * @param last_bind_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public DeviceListResp getBindDeviceList(long yb_user_id, long last_bind_id, int count) throws Exception
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
			
			List<BindDevice> devicelist = bindDeviceDao.getList(yb_user_id, last_bind_id, count);
			
			if (null == devicelist || 0 == devicelist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			DeviceListResp resp = new DeviceListResp();
			resp.setDevicelist(devicelist);
			int size = devicelist.size();
			int total = bindDeviceDao.getTotal(yb_user_id);
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.DEVICE_GETLIST_FAIL_FAIL);					
		}
	}

	/**
	 * 查询CDN信息
	 * @return
	 * @throws Exception
	 */
	public CDN getCDNInfo() throws Exception
	{
		try
		{
			CDN data = cdnDao.getInfo(CDNName.CDN_MAIN_ID);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.DEVICE_GETCDNINFO_FAIL);					
		}
	
	}

	/**
	 * 创建设备操作日志
	 * @param log
	 * @return
	 * @throws Exception
	 */
	 public int createDeviceOpLog(DeviceOpLog log) throws Exception
	 {
			if (null == log || 0 == log.getYb_user_id())
			{
				throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
			}
			
			try
			{
				log.setOp_time(System.currentTimeMillis());
				deviceOpLogDao.create(log);
				
			} catch (DataAccessException e)
			{
				throw new YiBakerException(RetEnum.DEVICE_CREATEOPLOG_FAIL);					
			}
			return 0;
	 }
	 
	 /**
	  * 查询设备操作日志列表
	  * @param yb_user_id
	  * @param device_id
	  * @param last_log_id
	  * @param count
	  * @return
	  * @throws Exception
	  */
	 public DeviceOpLogListResp getDeviceOpLogList(long yb_user_id, String device_id, long last_log_id, int count) throws Exception
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
				
				List<DeviceOpLog> loglist = deviceOpLogDao.getList(yb_user_id, device_id, last_log_id, count);
				
				if (null == loglist || 0 == loglist.size())
				{
					throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
				}
				
				DeviceOpLogListResp resp = new DeviceOpLogListResp();
				resp.setLoglist(loglist);
				int size = loglist.size();
				int total = deviceOpLogDao.getTotal(yb_user_id, device_id);
				resp.setCount(size);
				resp.setTotal(total);

				return resp;
			} catch (DataAccessException e)
			{
				throw new YiBakerException(RetEnum.DEVICE_GETOPLOGLIST_FAIL);					
			}
	 }

}

