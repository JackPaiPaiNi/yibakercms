/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.component
 * File name:ColumnRedisList.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月12日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.component;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xinlianfeng.yibaker.common.constant.ColumnInfo;
import com.xinlianfeng.yibaker.common.constant.RedisKey;
import com.xinlianfeng.yibaker.common.resp.RecipeDetailResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;

/**
 * @Description: 栏目redis操作类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月12日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Component
public class ColumnRecipeRedisDao
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JedisPool jedisPool;

	/**
	 * 获取栏目对应的键值
	 * @param column_id
	 * @return
	 */
	private String getKey(int column_id)
	{
		String key = null;
		switch (column_id)
		{
			case ColumnInfo.HOMESLIDE:
				key = RedisKey.HOME_SLIDE;
				break;
			case ColumnInfo.LATEST:
				key = RedisKey.COLUMN_LATEST;
				break;
			case ColumnInfo.HOT:
				key = RedisKey.COLUMN_HOT;
				break;
			case ColumnInfo.TOP:
				key = RedisKey.COLUMN_TOP;
				break;
			default:
				key = RedisKey.HOME_SLIDE;
				break;
		}
		return key;
	}
	
	/**
	 * 根据key获得list的长度
	 * @param key
	 * @return
	 */
	public int getSize(final int column_id)
	{
		Jedis jedis = null;
		String key = this.getKey(column_id);
		
		try
		{
			jedis = this.jedisPool.getResource();
			return jedis.llen(key).intValue();
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
	 * 根据分页信息获取相应的菜谱列表
	 * @param business
	 * @param start
	 * @param end
	 * @return
	 */
	public RecipeListResp getList(final int column_id, final long last_id, final int count)
	{
		Jedis jedis = null;
		String key = this.getKey(column_id);
		
		try
		{
			jedis = this.jedisPool.getResource();
			long total = jedis.llen(key);
//			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>redis total:" + total + "count:" + count);
			
			//计算分页数据
//			long start = (last_id==0) ? 0 : last_id;
			long start = last_id;
			long end = start + count - 1;
			if(start >= total) return null;
			if (end >= total) end = total -1;
//			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>redis start:" + start + "end:" + end);
			
			//从redis里面取出数据并转换为对象
			List<String> jsonList = jedis.lrange(key, start,end);
			if (null == jsonList || jsonList.isEmpty())
			{
				return null;
			}
//			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>jsonList:" + jsonList.toString());
			
//			Gson g = new GsonBuilder().serializeNulls().create();
			Gson g= new GsonBuilder().disableHtmlEscaping().create();
			List<RecipeDetailResp> recipeList = new LinkedList<RecipeDetailResp>(); 
			RecipeDetailResp recipe = null;
			for (String string : jsonList)
			{
//				log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>jsonstring:" +string);
				recipe = g.fromJson(string, new TypeToken<RecipeDetailResp>(){}.getType());
//				recipe = g.fromJson(string, RecipeDetailResp.class);
				recipeList.add(recipe);
			}
			//组装返回列表
			RecipeListResp listResp = new RecipeListResp();
			listResp.setRecipelist(recipeList);
			listResp.setTotal((int)total);
			listResp.setCount(recipeList.size());
			 
			return listResp;
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
	 * 将菜谱列表插入到redis中
	 * @param busniess
	 * @param field
	 * @return
	 */
	public void insertList(int column_id, List<RecipeDetailResp> recipeList )
	{
		Jedis jedis = null;
		String key = this.getKey(column_id);
		
		try
		{
			synchronized(this)
			{
				jedis = this.jedisPool.getResource();
				jedis.del(key);
				
				String jsonStr = null;
				Gson g= new GsonBuilder().disableHtmlEscaping().create();
//				Gson g = new GsonBuilder().serializeNulls().create();
				for (RecipeDetailResp recipeDetailResp : recipeList)
				{
					jsonStr = g.toJson(recipeDetailResp);
					jedis.rpush(key, jsonStr);
				}
				jedis.expire(key, 5*60);//缓存超时时间 5分钟
			}
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

