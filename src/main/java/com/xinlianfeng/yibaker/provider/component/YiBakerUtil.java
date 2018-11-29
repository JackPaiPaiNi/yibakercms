/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.component
 * File name:YiBakerUtil.java
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Random;

import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月28日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Component
public class YiBakerUtil
{
	  static String[] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};

	  /**
	 * 随机数
	 * @param strlen
	 * @return
	 */
	public String getRandom(int strlen)
	{
		Random rc = new Random();
		int i = 0;
		int start = 48;
		int len = 10;
		StringBuilder sb = new StringBuilder();
		while (i < strlen) 
		{
			char c = (char) (start + rc.nextInt(len));
			sb.append(c);
			i++;
		}
		return sb.toString();
	}

	/**
	 * 标签转换
	 * @param aText
	 * @return
	 */
	public String HTMLEncode(String aText) 
	{
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(
				aText);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) 
		{
			if (character == '<') 
			{
				result.append("&amp;lt;");
			} else if (character == '>') 
			{
				result.append("&amp;gt;");
			} else if (character == '&') 
			{
				result.append("&amp;amp;");
			} else if (character == '\"') 
			{
				result.append("&amp;quot;");
			} else 
			{
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}

	public String encodeByMD5(String password) throws NoSuchAlgorithmException 
	{
		    MessageDigest digest = MessageDigest.getInstance("md5");
		    byte[] results = digest.digest(password.getBytes());
		    return byteArrayToString(results);
	}
	private String byteArrayToString(byte[] results) 
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<results.length;i++) 
		{
			//per byte to string
			sb.append(byteToString(results[i]));
		}
		return sb.toString();
	}

	//per byte to String (Algorithms)
	private Object byteToString(byte b) 
	{
	    int n = b;
	    if(n<0) 
	    {
	      n = 256+n;
	    }
	    int d1 = n/16;
	    int d2 = n%16;		
	    return hex[d1]+hex[d2];
	}
}

