/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:EasemobService.java   Package name:com.xinlianfeng.yibaker.provider.component
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年7月13日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xinlianfeng.yibaker.common.entity.EasemobToken;
import com.xinlianfeng.yibaker.common.entity.UserEasemob;
import com.xinlianfeng.yibaker.common.resp.EasemobResp;
import com.xinlianfeng.yibaker.common.resp.EasemobUser;
import com.xinlianfeng.yibaker.provider.utils.EasemobUserRegExclusionStrategy;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年7月13日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Component
public class EasemobService
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final java.util.ResourceBundle BUNDLE = java.util.ResourceBundle.getBundle("system");
	private final String APP_URL = BUNDLE.getString("easemob.app_url");
	private final String CLIENT_ID = BUNDLE.getString("easemob.client_id");
	private final String CLIENT_SECRET = BUNDLE.getString("easemob.client_secret");
	private final String APIURL_TOKEN = APP_URL+"/token";
	private final String APIURL_REGUSERS = APP_URL+"/users";
	
	/**
	 * 删除单个环信用户接口地址
	 */
	private final String APIURL_DELUSER = APP_URL+"/users/{0}";
	
	/**
	 * 批量删除环信用户接口地址
	 */
	private final String APIURL_DELUSERS = APP_URL+"/users?limit={0}";
	
	/**
	 * 重试调用接口次数
	 */
	private final int TRY_MAX = 3;
	
	/**
	 * 批量删除环信用户最大数
	 */
	private final int DEL_MAX = 500;
	
	/**
	 * 环信用户令牌存储缓存键
	 */
	private final String EASEMOB_TOKEN = "EASEMOB_TOKEN";
	
	@Autowired
	private RedisBeanDao redisBeanDao;
	
	
	private Object locked = new Object();
	
	private  String getToken(boolean refetch) throws IOException{
		synchronized(locked){
			String token  = null;
			if(refetch||(token  = redisBeanDao.getString(EASEMOB_TOKEN))==null){
				EasemobToken easemobToken = this.fetchToken();
				if(null != easemobToken){
					token  = easemobToken.getAccess_token();
				} else {
					throw new RuntimeException("Failed to getToken!");
				}
			} 
			return token;
		}
	}
	
	public  String getAuthorization() throws IOException{
		return "Bearer ".concat(this.getToken(false));
	}
	
	private EasemobToken fetchToken() throws IOException{
		
		EasemobToken easemobToken = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(APIURL_TOKEN);
		httpPost.setHeader("Accept", "application/json");
		
		String respBody = "{\"grant_type\":\"client_credentials\",\"client_id\":\""+CLIENT_ID+"\",\"client_secret\":\""+CLIENT_SECRET+"\"}";
		StringEntity entity = new StringEntity(respBody, "UTF-8");
		httpPost.setEntity(entity);
		
		CloseableHttpResponse response2 = httpclient.execute(httpPost);

		try {
			StatusLine statusLine = response2.getStatusLine();
			 logger.info("StatusLine="+response2.getStatusLine().toString());
			if(HttpStatus.SC_OK == statusLine.getStatusCode()){
				HttpEntity entity2 = response2.getEntity();
			    String respStr = EntityUtils.toString(entity2, "UTF-8");
			    logger.info("entity2="+respStr);
			    Gson gson = new Gson();
			    easemobToken = gson.fromJson(respStr, EasemobToken.class);
			    if(null != easemobToken){
			    	redisBeanDao.saveString(EASEMOB_TOKEN, easemobToken.getAccess_token());
			    	redisBeanDao.setExpire(EASEMOB_TOKEN, easemobToken.getExpires_in().intValue());
			    }
			} else {
				HttpEntity entity2 = response2.getEntity();
				String respStr = "Failed to fetchToken =";
				if(null != entity2){
					respStr += EntityUtils.toString(entity2, "UTF-8");
				}
			    logger.error(respStr);
			}
			
		} finally {
		    response2.close();
		}
		return easemobToken;
	}
	
	public EasemobResp<EasemobUser> createEasemobUser(UserEasemob userEasemob) throws IOException{
		
		EasemobResp<EasemobUser> easemobResp = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(APIURL_REGUSERS);
		Gson gsonEasemob = new GsonBuilder()//
		    .setExclusionStrategies(new EasemobUserRegExclusionStrategy())//
		    .create();
		int tryTimes = 0;
		boolean needTry;
		
		do {
			needTry = false;
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Authorization", this.getAuthorization());
			
			
			 String userRegInfoStr = gsonEasemob.toJson(userEasemob);
			 
			 logger.info("userRegInfoStr="+userRegInfoStr);
			StringEntity entity = new StringEntity(userRegInfoStr, "UTF-8");
			httpPost.setEntity(entity);
			
			CloseableHttpResponse response2 = httpclient.execute(httpPost);

			try {
				StatusLine statusLine = response2.getStatusLine();
				 logger.info("StatusLine="+response2.getStatusLine().toString());
				if(HttpStatus.SC_OK == statusLine.getStatusCode()){
					HttpEntity entity2 = response2.getEntity();
				    String respStr = EntityUtils.toString(entity2, "UTF-8");
				    logger.info("entity2="+respStr);
				    easemobResp = gsonEasemob.fromJson(respStr, new TypeToken<EasemobResp<EasemobUser>>() {  
	                }.getType());
				} else if (HttpStatus.SC_UNAUTHORIZED== statusLine.getStatusCode()) {
					this.getToken(true);
					needTry = true;
					tryTimes++;
				} else {
					HttpEntity entity2 = response2.getEntity();
					String respStr = "Failed to createEasemobUser =";
					if(null != entity2){
						respStr += EntityUtils.toString(entity2, "UTF-8");
					}
				    logger.error(respStr);
				}
				
			} finally {
			    response2.close();
			}
		} while(needTry&&tryTimes<TRY_MAX);
		
		
		return easemobResp;
	}
	
	/**
	 * 调用批量创建环信用户接口
	 * @param userRegInfoList
	 * @return
	 * @throws IOException
	 */
	public EasemobResp<EasemobUser> createEasemobUsers(Collection<UserEasemob> userEasemobList) throws IOException{
		
		EasemobResp<EasemobUser> easemobResp = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		HttpPost httpPost = new HttpPost(APIURL_REGUSERS);
		Gson gsonEasemob = new GsonBuilder()//
	    .setExclusionStrategies(new EasemobUserRegExclusionStrategy())//
	    .create();
		int tryTimes = 0;
		boolean needTry;
		
		do {
			needTry = false;
			httpPost.setHeader("Authorization", this.getAuthorization());
			httpPost.setHeader("Accept", "application/json");
			
			String userEasemobListStr = gsonEasemob.toJson(userEasemobList);
			logger.info("userRegInfoListStr="+userEasemobListStr);
			StringEntity entity = new StringEntity(userEasemobListStr, "UTF-8");
			httpPost.setEntity(entity);
			
			CloseableHttpResponse response2 = httpclient.execute(httpPost);

			try {
				StatusLine statusLine = response2.getStatusLine();
				 logger.info("StatusLine="+response2.getStatusLine().toString());
				if(HttpStatus.SC_OK == statusLine.getStatusCode()){
					HttpEntity entity2 = response2.getEntity();
				    String respStr = EntityUtils.toString(entity2, "UTF-8");
				    logger.info("entity2="+respStr);
				    easemobResp = gsonEasemob.fromJson(respStr, new TypeToken<EasemobResp<EasemobUser>>() {  
	                }.getType());
				    
				} else if (HttpStatus.SC_UNAUTHORIZED== statusLine.getStatusCode()) {
					this.getToken(true);
					needTry = true;
					tryTimes++;
				} else {
					HttpEntity entity2 = response2.getEntity();
					String respStr = "Failed to createEasemobUsers =";
					if(null!=entity2){
						respStr += EntityUtils.toString(entity2, "UTF-8");
					}
				    logger.error(respStr);
				}
			} finally {
			    response2.close();
			}
		} while(needTry&&tryTimes<TRY_MAX);
		
		return easemobResp;
	}
	
   public EasemobResp<EasemobUser> deleteEasemobUsers(int limit) throws IOException{
	   
	   if (limit<=0||limit>DEL_MAX){
		   throw new RuntimeException("limit is not int range of [1,"+DEL_MAX+"] !");
	   }
		
		EasemobResp<EasemobUser> easemobResp = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		String apiurl = MessageFormat.format(APIURL_DELUSERS, limit);
		HttpDelete httpDelete = new HttpDelete(apiurl);
		Gson gson = new Gson();
		int tryTimes = 0;
		boolean needTry;
		
		do {
			needTry = false;
			httpDelete.setHeader("Authorization", this.getAuthorization());
			httpDelete.setHeader("Accept", "application/json");
			
			
			CloseableHttpResponse response2 = httpclient.execute(httpDelete);

			try {
				StatusLine statusLine = response2.getStatusLine();
				logger.info("StatusLine="+response2.getStatusLine().toString());
				if(HttpStatus.SC_OK == statusLine.getStatusCode()){
					HttpEntity entity2 = response2.getEntity();
				    String respStr = EntityUtils.toString(entity2, "UTF-8");
				    logger.info("entity2="+respStr);
				    easemobResp = gson.fromJson(respStr, new TypeToken<EasemobResp<EasemobUser>>() {  
	                }.getType());
				    
				} else if (HttpStatus.SC_UNAUTHORIZED== statusLine.getStatusCode()) {
					this.getToken(true);
					needTry = true;
					tryTimes++;
				} else {
					HttpEntity entity2 = response2.getEntity();
					String respStr = "Failed to deleteEasemobUsers =";
					if(null!=entity2){
						respStr += EntityUtils.toString(entity2, "UTF-8");
					}
				    logger.error(respStr);
				}
			} finally {
			    response2.close();
			}
		} while(needTry&&tryTimes<TRY_MAX);
		
		return easemobResp;
	}
   
   
   public EasemobResp<EasemobUser> deleteEasemobUsers(final String username) throws IOException{
	   
	   if (StringUtils.isEmpty(username)){
		   throw new RuntimeException("username is empty!");
	   }
	   
	   EasemobResp<EasemobUser> easemobResp = null;
	   CloseableHttpClient httpclient = HttpClients.createDefault();
	   
	   String apiurl = MessageFormat.format(APIURL_DELUSER, username);
	   HttpDelete httpDelete = new HttpDelete(apiurl);
	   Gson gson = new Gson();
	   int tryTimes = 0;
	   boolean needTry;
	   
	   do {
		   needTry = false;
		   httpDelete.setHeader("Authorization", this.getAuthorization());
		   httpDelete.setHeader("Accept", "application/json");
		   
		   CloseableHttpResponse response2 = httpclient.execute(httpDelete);
		   
		   try {
			   StatusLine statusLine = response2.getStatusLine();
			   logger.info("StatusLine="+response2.getStatusLine().toString());
			   if(HttpStatus.SC_OK == statusLine.getStatusCode()){
				   HttpEntity entity2 = response2.getEntity();
				   String respStr = EntityUtils.toString(entity2, "UTF-8");
				   logger.info("entity2="+respStr);
				   easemobResp = gson.fromJson(respStr, new TypeToken<EasemobResp<EasemobUser>>() {  
	                }.getType());
				   
			   } else if (HttpStatus.SC_UNAUTHORIZED== statusLine.getStatusCode()) {
				   this.getToken(true);
				   needTry = true;
				   tryTimes++;
			   } else {
				   HttpEntity entity2 = response2.getEntity();
					String respStr = "Failed to deleteEasemobUsers =";
					if(null!=entity2){
						respStr += EntityUtils.toString(entity2, "UTF-8");
					}
				    logger.error(respStr);
			   }
		   } finally {
			   response2.close();
		   }
	   } while(needTry&&tryTimes<TRY_MAX);
	   
	   return easemobResp;
   }

}
