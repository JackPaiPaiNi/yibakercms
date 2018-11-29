/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:CookieAuthServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月2日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xinlianfeng.yibaker.common.constant.CookieOption;
import com.xinlianfeng.yibaker.common.constant.RedisUserAuth;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.constant.UserType;
import com.xinlianfeng.yibaker.common.emum.CookieAccessType;
import com.xinlianfeng.yibaker.common.entity.UserCookieInfo;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.service.CookieAuthService;

/**
 * @Description: TODO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月2日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("cookieAuthService")
public class CookieAuthServiceImpl implements CookieAuthService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JedisPool jedisPool;

	/**
	 * 保存登录用户
	 * 
	 * @param ucinf
	 */
	public void saveLoginUser(UserCookieInfo ucinf)
	{
		Jedis jedis = null;
		try
		{
			jedis = this.jedisPool.getResource();
			Gson g = new GsonBuilder().disableHtmlEscaping().create();
			String json = g.toJson(ucinf);
			synchronized (this)
			{
				List<String> guestInfo = jedis.hmget(RedisUserAuth.GUEST_USER,
						ucinf.getCookie());
				// 删除访客信息
				if (null != guestInfo && (!guestInfo.isEmpty())
						&& null != guestInfo.get(0))
				{
					jedis.hdel(RedisUserAuth.GUEST_USER, ucinf.getCookie());
					jedis.hdel(RedisUserAuth.ACCESS_SESSION, ucinf.getCookie());
				}
				// 如果存在已经登录的记录,就删除
				List<String> loginedUser = jedis.hmget(
						RedisUserAuth.LOGIN_USER, ucinf.getUserId());
				if (null != loginedUser && (!loginedUser.isEmpty())
						&& null != loginedUser.get(0))
				{
					String savedJson = loginedUser.get(0);
					UserCookieInfo savedUserCookieInfo = g.fromJson(savedJson,
							UserCookieInfo.class);
					jedis.hdel(RedisUserAuth.ACCESS_SESSION,
							savedUserCookieInfo.getCookie());
					jedis.hdel(RedisUserAuth.LOGIN_USER, ucinf.getUserId());
				}

				jedis.hmset(RedisUserAuth.LOGIN_USER,
						ImmutableMap.of(ucinf.getUserId(), json));
				jedis.hmset(RedisUserAuth.ACCESS_SESSION,
						ImmutableMap.of(ucinf.getCookie(), ucinf.getUserId()));
			}

		} catch (Exception e)
		{
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			throw new YiBakerException(RetEnum.USER_SAVE_COOKIE_FAIL);
		} finally
		{
			if (null != jedis)
			{
				this.jedisPool.returnResource(jedis);
			}
		}

	}

	/**
	 * 根据cookie获取用户信息
	 * 
	 * @param cookie
	 * @return
	 * @throws Exception
	 */
	public UserCookieInfo getUserInfoByCookie(String cookie) throws Exception
	{
		Jedis jedis = null;
		try
		{
			jedis = this.jedisPool.getResource();
			List<String> jsonUserInfos = null;
			if (this.checkCookieType(cookie) == CookieAccessType.Guest)
			{
				jsonUserInfos = jedis.hmget(RedisUserAuth.GUEST_USER, cookie);

			} else if (this.checkCookieType(cookie) == CookieAccessType.User)
			{
				List<String> users = jedis.hmget(RedisUserAuth.ACCESS_SESSION,
						cookie);
				String userId = null;
				if (null != users && (!users.isEmpty()))
				{
					userId = users.get(0);
					jsonUserInfos = jedis.hmget(RedisUserAuth.LOGIN_USER,
							userId);
				}
			}
			if (null != jsonUserInfos && (!jsonUserInfos.isEmpty())
					&& (null != jsonUserInfos.get(0)))
			{
				Gson g = new GsonBuilder().disableHtmlEscaping().create();
				return g.fromJson(jsonUserInfos.get(0), UserCookieInfo.class);
			}
			throw new YiBakerException(RetEnum.USER_SAVED_COOKIE_FAIL);
		} catch (Exception e)
		{
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			log.debug("get user info by cookie error >>>>>>>>>>");
			throw e;
		} finally
		{
			if (null != jedis)
			{
				this.jedisPool.returnResource(jedis);
			}
		}
	}

	/**
	 * 检测cookie是什么类型
	 * 
	 * @param cookieStr
	 * @return
	 * @throws Exception 
	 */
	public CookieAccessType checkCookieType(String cookieStr) throws Exception
	{
		Jedis jedis = null;
		try
		{
			jedis = this.jedisPool.getResource();
			List<String> users = jedis.hmget(RedisUserAuth.ACCESS_SESSION,
					cookieStr);
			if (null != users && (!users.isEmpty()) && null != users.get(0))
			{
				if (users.contains(UserType.GUEST))
				{
					return CookieAccessType.Guest;
				} else
				{
					return CookieAccessType.User;
				}
			} else
			{
				throw new YiBakerException(RetEnum.USER_COOKIE_UNKNOW);
			}

		} catch (Exception e)
		{
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			log.debug("check cookie type error >>>>>>>>>>");
			throw e;

		} finally
		{
			if (null != jedis)
			{
				this.jedisPool.returnResource(jedis);
			}
		}

	}

	/**
	 * 保存访客信息
	 * 
	 * @param ucinf
	 */
	public void saveGuestUser(UserCookieInfo ucinf)
	{
		Jedis jedis = null;
		try
		{
			jedis = this.jedisPool.getResource();
			Gson g = new GsonBuilder().disableHtmlEscaping().create();
			String json = g.toJson(ucinf);
			synchronized (this)
			{
				jedis.hmset(RedisUserAuth.GUEST_USER,
						ImmutableMap.of(ucinf.getCookie(), json));
				jedis.hmset(RedisUserAuth.ACCESS_SESSION,
						ImmutableMap.of(ucinf.getCookie(), ucinf.getUserId()));
			}
		} catch (Exception e)
		{
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			throw new YiBakerException(RetEnum.GUEST_SAVE_COOKIE_FAIL);
		} finally
		{
			if (null != jedis)
			{
				this.jedisPool.returnResource(jedis);
			}
		}
	}
	
	/**
	 * 检查用户授权信息
	 * @param cookie
	 * @return
	 */
	public UserCookieInfo getLoginUserCookieInfoByCookie(String cookie) throws Exception
	{
		if (null == cookie)
		{
			throw new YiBakerException(RetEnum.AUTH_USER_LOGOUT);					
		}			
		
		Jedis jedis = null;
		try 
		{
			//这里有潜在的未知的cookie异常
//			if (CookieAccessType.Guest == this.checkCookieType(cookie)) 
//			{
//				throw new YiBakerException(RetEnum.AUTH_GUEST_FAIL);					
//			}
			jedis = this.jedisPool.getResource();
			List<String> rdsUserId = jedis.hmget(RedisUserAuth.ACCESS_SESSION, cookie);
			if (null != rdsUserId && (!rdsUserId.isEmpty()) && null != rdsUserId.get(0)) 
			{
				String userId = rdsUserId.get(0);
				List<String> rdsUserInfo = jedis.hmget(RedisUserAuth.LOGIN_USER, userId);
				
				if (null != rdsUserId && (!rdsUserInfo.isEmpty()) && null != rdsUserInfo.get(0)) 
				{
					String json = rdsUserInfo.get(0);
					Gson g = new GsonBuilder().disableHtmlEscaping().create();
					UserCookieInfo ucinfo = g.fromJson(json, UserCookieInfo.class);
					if(this.isCookieTimeOut(ucinfo))
					{
						log.debug(">>>>>>>>>>>>>>>>>>>>login is timeout. userId:" + ucinfo.getUserId());
						throw new YiBakerException(RetEnum.AUTH_TIMEOUT);					
					}
					return ucinfo;
				}
			}
			log.debug("redis data error RedisUserAuth.Access_Session and RedisUserAuth.Login_User data error");
			throw new YiBakerException(RetEnum.AUTH_USER_SSO_ERROR);		
			
		} catch (YiBakerException e)
		{
			throw e;		
		} catch (Exception e)
		{
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			e.printStackTrace();
			throw new YiBakerException(RetEnum.AUTH_FAIL);		
		}finally
		{
			if (null != jedis)
			{
				this.jedisPool.returnResource(jedis);
			}
		}
	}
	
	/**
	 * 判断用户cookie是否超时
	 * 
	 * @param uci
	 * @return
	 */
	private boolean isCookieTimeOut(final UserCookieInfo uci) 
	{
		if (null == uci) 
		{
			return true;
		}
		long time_out = System.currentTimeMillis() - uci.getSaveTime();
//		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>time_out:" + time_out);
//		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>CookieOption.MAX_TIME_OUT:" + CookieOption.MAX_TIME_OUT);
		if (time_out/1000 > CookieOption.MAX_TIME_OUT) {
			return true;
		}
		return false;
	}
	


}
