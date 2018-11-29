/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.component
 * File name:RedisBeanDao.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月28日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.component;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Description: 通用redis数据bean操作类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月28日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Component
public class RedisBeanDao
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JedisPool jedisPool;

	/**
	 * 将 bean保存到redis
	 * @param t
	 * @param key
	 * @param field
	 */
	public <T> String saveBean(final T bean, final String key, final String field)
	{
		Gson g = new GsonBuilder().disableHtmlEscaping().create();
		String jstr = g.toJson(bean);
		Jedis jedis = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			return jedis.hmset(key, ImmutableMap.of(field, jstr));
		} catch (Exception e) 
		{  
			log.error("Failed to saveBean to redis ", e);
			//释放redis对象   
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}
	
	/**
	 * 保存字符串到redis
	 * @param key
	 * @param value
	 * @return
	 */
	public  String saveString(final String key, final String value)
	{
		Jedis jedis = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			return jedis.set(key, value);
		} catch (Exception e) 
		{  
			log.error("Failed to saveString to redis ", e);
			//释放redis对象   
			if (null != jedis) 
			{
				this.jedisPool.returnBrokenResource(jedis);  
				jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}
	
	/**
	 * 设置缓存时间
	 * @param key
	 * @param seconds
	 * @return
	 */
	public  Long setExpire(final String key, final int seconds)
	{
		Jedis jedis = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			return jedis.expire(key, seconds);
		} catch (Exception e) 
		{  
			log.error("Failed to saveString to redis ", e);
			//释放redis对象   
			if (null != jedis) 
			{
				this.jedisPool.returnBrokenResource(jedis);  
				jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}

	/**
	 * 从redis 取出 bean
	 * @param key
	 * @param filed
	 * @param t
	 * @return
	 */
	public  String getString(final String key) 
	{
		Jedis jedis = null;
		String result = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			result = jedis.get(key);
			
			return result;
		} catch (Exception e) 
		{  
			log.error("Failed to getString from redis ", e);
			//释放redis对象   
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}
	
	
	public <T> T getBean(final String key, final String filed, final Class<T> clazz) 
	{
		Jedis jedis = null;
		List<String> result = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			result = jedis.hmget(key, filed);
			if (null != result && (!result.isEmpty())) 
			{
				String jstr = null;
				jstr = result.get(0);
				Gson g = new GsonBuilder().disableHtmlEscaping().create();
				T t = g.fromJson(jstr, clazz);
				return t;
			}
			return null;
		} catch (Exception e) 
		{  
			log.error("Failed to getBean from redis ", e);
			//释放redis对象   
			if (null != jedis) 
			{
				this.jedisPool.returnBrokenResource(jedis);  
				jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}

	/**
	 * 删除对应的bean
	 * @param key
	 * @param field
	 * @return
	 */
	public Long delBean(final String key, final String field) 
	{
		Jedis jedis = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			return jedis.hdel(key, field);
		} catch (Exception e) 
		{  
			e.printStackTrace();
			//释放redis对象   
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}
	
	/**
	 * 对应的bean是否已存在
	 * @param key
	 * @param filed
	 * @return
	 */
	public boolean isBeanExist(final String key, final String filed) 
	{
		Jedis jedis = null;
		try 
		{
			jedis = this.jedisPool.getResource();
			return jedis.hexists(key, filed);
		} catch (Exception e) 
		{  
			e.printStackTrace();
			//释放redis对象   
			if (null != jedis) 
			{
			   	this.jedisPool.returnBrokenResource(jedis);  
			   	jedis = null;
			}
			throw new JedisConnectionException(e);					
		} finally 
		{  
			//返还到连接池   
			if (null != jedis) 
			{
				this.jedisPool.returnResource(jedis);
			}
		}  
	}
	
	
	
}

