/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:MissionAdvice.java   Package name:com.xinlianfeng.yibaker.provider.aop
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2015年12月30日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.aop;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xinlianfeng.yibaker.common.entity.MissionPerformRecord;
import com.xinlianfeng.yibaker.common.entity.RecipeWork;
import com.xinlianfeng.yibaker.common.entity.ShareLog;
import com.xinlianfeng.yibaker.common.entity.User;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.MissionService;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年12月30日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Component
@Aspect
public class MissionAdvice
{

	@Autowired
	private MissionService missionService;
    
    @Pointcut("(execution(* com.xinlianfeng.yibaker.provider.service.impl.UserServiceImpl.registerEx(..))) "
    		+ " || (execution(* com.xinlianfeng.yibaker.provider.service.impl.UserServiceImpl.tpLoginEx(..)))")
    private void registerUser(){};
   
    @Pointcut("execution(* com.xinlianfeng.yibaker.provider.service.impl.UserServiceImpl.updateUserInfoEx(..))")
    private void updateUserInfoEx(){};
    
    @Pointcut("execution(* com.xinlianfeng.yibaker.provider.service.impl.RecipeServiceImpl.createRecipeWorkEx(..))")
    private void createRecipeWorkEx(){};
    
    @Pointcut("execution(* com.xinlianfeng.yibaker.provider.service.impl.SNSServiceImpl.createShareLogEx(..))")
    private void createShareLogEx(){};
    
    @Pointcut("(execution(* com.xinlianfeng.yibaker.provider.service.impl.RecipeServiceImpl.createRecipeReplyEx(..))) "
    		+ " || (execution(* com.xinlianfeng.yibaker.provider.service.impl.RecipeServiceImpl.createWorkReplyEx(..))) "
    		+ " || (execution(* com.xinlianfeng.yibaker.provider.service.impl.SubjectServiceImpl.createSubjectReplyEx(..)))")
    private void createReplyEx(){};
    
    
    
    /**  
     * AfterReturning  
     * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice 
     * @param joinPoint 
     * @throws Exception 
     */   
    @AfterReturning(value = "registerUser()", returning = "retVal")    
    public void afterRegister(JoinPoint joinPoint, Object retVal) throws Exception  {    
    	
    	Object[] args = joinPoint.getArgs();
    	User user = (User)args[0];
    	
    	List<MissionPerformRecord> mprs = missionService.createMissionPerformRecord(user);
    	if(null != mprs&&!mprs.isEmpty()){
    		YibakerResp resp = (YibakerResp)retVal;
        	resp.setExtra(filter(mprs));
    	}
    }  
    
    @AfterReturning(value = "updateUserInfoEx()", returning = "retVal")    
    public void afterUpdateUserInfo(JoinPoint joinPoint, Object retVal) throws Exception  {    
    	
    	Object[] args = joinPoint.getArgs();
    	UserInfo userInfo = (UserInfo)args[0];
    	
    	List<MissionPerformRecord> mprs = missionService.createMissionPerformRecord(userInfo);
    	if(null != mprs&&!mprs.isEmpty()){
    		YibakerResp resp = (YibakerResp)retVal;
        	resp.setExtra(filter(mprs));
    	}
    }  

    @AfterReturning(value = "createRecipeWorkEx()", returning = "retVal")    
    public void afterCreateRecipeWorkEx(JoinPoint joinPoint, Object retVal) throws Exception  {    
    	
    	Object[] args = joinPoint.getArgs();
    	RecipeWork recipeWork = (RecipeWork)args[0];
    	
    	List<MissionPerformRecord> mprs =  missionService.createMissionPerformRecord(recipeWork);
    	if(null != mprs&&!mprs.isEmpty()){
    		YibakerResp resp = (YibakerResp)retVal;
        	resp.setExtra(filter(mprs));
    	}
    }  
    
    @AfterReturning(value = "createShareLogEx()", returning = "retVal")    
    public void afterCreateShareLogEx(JoinPoint joinPoint, Object retVal) throws Exception  {    
    	
    	Object[] args = joinPoint.getArgs();
    	ShareLog shareLog = (ShareLog)args[0];
    	
    	List<MissionPerformRecord> mprs =  missionService.createMissionPerformRecord(shareLog);
    	if(null != mprs&&!mprs.isEmpty()){
    		YibakerResp resp = (YibakerResp)retVal;
        	resp.setExtra(filter(mprs));
    	}
    }  
    
    @AfterReturning(value = "createReplyEx()", returning = "retVal")    
    public void afterCreateReplyEx(JoinPoint joinPoint, Object retVal) throws Exception  {    
    	
    	Object[] args = joinPoint.getArgs();
    	
    	List<MissionPerformRecord> mprs =  missionService.createReplyPerformRecord(args[0]);
    	if(null != mprs&&!mprs.isEmpty()){
    		YibakerResp resp = (YibakerResp)retVal;
        	resp.setExtra(filter(mprs));
    	}
    }  
    
    
    private List<MissionPerformRecord> filter(List<MissionPerformRecord> mprs){
    	List<MissionPerformRecord> destMprs = new ArrayList<MissionPerformRecord>();
    	for(MissionPerformRecord mpr:mprs){
    		MissionPerformRecord destMpr = new MissionPerformRecord();
    		BeanUtils.copyProperties(mpr, destMpr, "mpr_id", "award_maxtimes", "perform_user_id", "perform_user_name","perform_mission_name","perform_time","perform_result");
    		destMprs.add(destMpr);
    	}
    	return destMprs;
    }
}
