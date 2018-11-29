/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:BakerAdvice.java   Package name:com.xinlianfeng.yibaker.provider.aop
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年3月31日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xinlianfeng.yibaker.common.constant.BakerConsts;
import com.xinlianfeng.yibaker.common.service.UserCenterService;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年3月31日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Component
@Aspect
public class BakerAdvice
{

	@Autowired
	private UserCenterService  userCenterService;
    
    @Pointcut("execution(* com.xinlianfeng.yibaker.provider.service.impl.BakerServiceImpl.updateBakerAccount2(..))")
    private void updateBakerAccount(){};
    

    @AfterReturning(value = "updateBakerAccount()", returning = "retVal")    
    public void afterUpdateBakerAccount(JoinPoint joinPoint, Object retVal) throws Exception  {    
    	
    	Object[] args = joinPoint.getArgs();
    	Long yb_user_id = (Long)args[0];
    	Byte inst_type = (Byte)args[1];
    	
    	if(BakerConsts.INST_TYPE_IN.byteValue()==inst_type.byteValue()){
    		userCenterService.updateUserLevelInfo(yb_user_id);
    	}
    }  
}
