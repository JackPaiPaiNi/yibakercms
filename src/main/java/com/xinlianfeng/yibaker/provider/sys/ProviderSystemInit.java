/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:ProviderSystemInit.java   Package name:com.xinlianfeng.yibaker.provider.sys
 * Project:YiBaker-Provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2015年9月22日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.sys;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.JedisPool;

import com.xinlianfeng.server.common.fdfs.FDFSOperateProxy;
import com.xinlianfeng.yibaker.common.sys.SystemInit;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年9月22日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
public class ProviderSystemInit extends SystemInit
{
	ClassPathXmlApplicationContext application;

	/* (non-Javadoc)
	 * @see com.xinlianfeng.yibaker.common.sys.SystemInit#start()
	 */
	@Override
	public void start() throws Exception
	{
		//初始化fastfs
		String fdfsPath = this.getClass().getClassLoader().getResource("fdfs-pool.properties").getPath();
		System.out.println("FastFs目录:" + fdfsPath + "\r\n");
		FDFSOperateProxy.initFDFS(fdfsPath);
		

		application = new ClassPathXmlApplicationContext("classpath:application-context.xml");
		application.start();

	}

	/* (non-Javadoc)
	 * @see com.xinlianfeng.yibaker.common.sys.SystemInit#stop()
	 */
	@Override
	public void stop() throws Exception
	{
		JedisPool jedisPool = application.getBean(JedisPool.class);
		if (null != jedisPool)
		{
			jedisPool.destroy();
			System.out.println(">>>>>>>>>>>>>>>>>>JedisPool destroy");
		}
		System.out.println("ProviderSystemInit.stop()");
	}

}
