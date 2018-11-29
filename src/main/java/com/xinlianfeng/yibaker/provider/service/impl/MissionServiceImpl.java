/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:MissionServiceImpl.java   Package name:com.xinlianfeng.yibaker.provider.service.impl
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2015年12月28日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.constant.BakerConsts;
import com.xinlianfeng.yibaker.common.constant.MissionConsts;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.BakerAccount;
import com.xinlianfeng.yibaker.common.entity.IncomeState;
import com.xinlianfeng.yibaker.common.entity.Mission;
import com.xinlianfeng.yibaker.common.entity.MissionPerformRecord;
import com.xinlianfeng.yibaker.common.entity.RecipeBriefInfo;
import com.xinlianfeng.yibaker.common.entity.RecipeReply;
import com.xinlianfeng.yibaker.common.entity.RecipeWork;
import com.xinlianfeng.yibaker.common.entity.ShareLog;
import com.xinlianfeng.yibaker.common.entity.User;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.WorkReply;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.req.CreateSubjectReplyReq;
import com.xinlianfeng.yibaker.common.resp.DailySigninStatusResp;
import com.xinlianfeng.yibaker.common.resp.UserMissionResp;
import com.xinlianfeng.yibaker.common.service.BakerService;
import com.xinlianfeng.yibaker.common.service.MissionService;
import com.xinlianfeng.yibaker.provider.dao.BakerAccountDao;
import com.xinlianfeng.yibaker.provider.dao.IncomeStateDao;
import com.xinlianfeng.yibaker.provider.dao.MissionDao;
import com.xinlianfeng.yibaker.provider.dao.MissionPerformRecordDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2015
 * @version: POSS_2.0
 * @date: 2015年12月28日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Service("missionService")
public class MissionServiceImpl implements MissionService
{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MissionDao missionDao;
	
	@Autowired
	private MissionPerformRecordDao missionPerformRecordDao;
	
	@Autowired
	private IncomeStateDao incomeStateDao;
	
	@Autowired
	private BakerAccountDao bakerAccountDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private RecipeDao recipeDao;
	
	@Autowired
	private BakerService bakerService;

	/* (non-Javadoc)
	 * @see com.xinlianfeng.yibaker.common.service.MissionService#findMyMissions(java.lang.Long)
	 */
	@Override
	public UserMissionResp findMyMissions(Long yb_user_id) throws Exception
	{
		// TODO Auto-generated method stub
		List<Mission> onceMissions = missionDao.findOnceMissions(yb_user_id);
		List<Mission> dailyMissions = missionDao.findDailyMissions(yb_user_id);
		
		UserMissionResp userMissionResp = new UserMissionResp();
		userMissionResp.setOnceMissions(onceMissions);
		userMissionResp.setDailyMissions(dailyMissions);
		
		return userMissionResp;
	}

	/* (non-Javadoc)
	 * @see com.xinlianfeng.yibaker.common.service.MissionService#findMissionDetail(java.lang.Long, java.lang.Long)
	 */
	@Override
	public Mission findMissionDetail(Long yb_user_id, Long mission_id) throws Exception
	{
		// TODO Auto-generated method stub
		return missionDao.findMissionDetail(yb_user_id, mission_id);
	}

	@Override
	@Transactional
	public int createMissionPerformRecord(MissionPerformRecord missionPerformRecord) throws Exception
	{
		// TODO Auto-generated method stub
		missionPerformRecordDao.createMissionPerformRecord(missionPerformRecord);
		if(missionPerformRecord.getEarn_count()>0){//赚取焙壳
			BakerAccount bakerAccount = bakerAccountDao.findOne(missionPerformRecord.getPerform_user_id());
			if(null == bakerAccount){
				long currtime = System.currentTimeMillis();
				bakerAccount = new BakerAccount();
				bakerAccount.setYb_user_id(missionPerformRecord.getPerform_user_id());
				bakerAccount.setAccum_income(0);
				bakerAccount.setAccum_outlay(0);
				bakerAccount.setBalance(0);
				bakerAccount.setCreate_time(currtime);
				bakerAccount.setUpdate_time(currtime);
				bakerAccount.setFrozen(BakerConsts.FROZEN_NO);
				bakerAccountDao.createBakerAccount(bakerAccount);
			}
			
			bakerService.updateBakerAccount(missionPerformRecord.getPerform_user_id(), BakerConsts.INST_TYPE_IN, missionPerformRecord.getEarn_count(),
					BakerConsts.INST_REASON_MISSION, missionPerformRecord.getMpr_id(), missionPerformRecord.getPerform_time());
		}
		
		return 0;
	}

	@Override
	public List<MissionPerformRecord> createMissionPerformRecord(User user)
	{
		// TODO Auto-generated method stub
		List<MissionPerformRecord> mprList = new ArrayList<MissionPerformRecord>();
		UserInfo userInfo = user.getUserinfo();
		if(null == userInfo||0==userInfo.getYb_user_id()){
			return mprList;
		}
		List<MissionPerformRecord> mprs1 = this.createMpr(MissionConsts.MISSIONID_REGISTER, userInfo.getYb_user_id(), userInfo.getNickname(), null);
		mprList.addAll(mprs1);
		List<MissionPerformRecord> mprs2 =this.createMissionPerformRecord(userInfo);
		mprList.addAll(mprs2);
		return mprList;
	}

	@Override
	public List<MissionPerformRecord> createMissionPerformRecord(UserInfo userInfo)
	{
		// TODO Auto-generated method stub
		List<MissionPerformRecord> mprs = new ArrayList<MissionPerformRecord>();
		if (!StringUtils.isEmpty(userInfo.getPhoto())){
			MissionPerformRecord missionPerformRecord = missionPerformRecordDao.findOnceMissionPerformRecord(userInfo.getYb_user_id(), MissionConsts.MISSIONID_UPLOAD_PHOTO);
			if (null == missionPerformRecord){
				List<MissionPerformRecord> mprs1 = this.createMpr(MissionConsts.MISSIONID_UPLOAD_PHOTO, userInfo.getYb_user_id(), userInfo.getNickname(), null);
				mprs.addAll(mprs1);
			}
		}
		if (!StringUtils.isEmpty(userInfo.getArea())
				&&!StringUtils.isEmpty(userInfo.getBirth())
				&&!StringUtils.isEmpty(userInfo.getSignature())){
			MissionPerformRecord missionPerformRecord = missionPerformRecordDao.findOnceMissionPerformRecord(userInfo.getYb_user_id(), MissionConsts.MISSIONID_FULLFIL_INFO);
			if (null == missionPerformRecord){
				List<MissionPerformRecord> mprs2 = this.createMpr(MissionConsts.MISSIONID_FULLFIL_INFO, userInfo.getYb_user_id(), userInfo.getNickname(), null);
					mprs.addAll(mprs2);
			}
		}
		return mprs;
	}

	@Override
	public List<MissionPerformRecord> createMissionPerformRecord(RecipeWork recipeWork)
	{
		// TODO Auto-generated method stub
		RecipeBriefInfo rbi = recipeDao.getRecipeBriefInfo(recipeWork.getRecipe_id());
		
		return this.createMpr(MissionConsts.MISSIONID_PUBLISH_WORK, recipeWork.getYb_user_id(), rbi.getRecipe_name());
	}

	@Override
	public List<MissionPerformRecord> createMissionPerformRecord(ShareLog shareLog)
	{
		// TODO Auto-generated method stub
		return this.createMpr(MissionConsts.MISSIONID_TRANS, shareLog.getYb_user_id(), null);
	}

	@Override
	public List<MissionPerformRecord> createReplyPerformRecord(Object reply)
	{
		// TODO Auto-generated method stub
		long yb_user_id = 0;
		if(reply instanceof RecipeReply){
			 yb_user_id = ((RecipeReply) reply).getYb_user_id();
		} else if (reply instanceof WorkReply){
			 yb_user_id = ((WorkReply) reply).getYb_user_id();
		} else if (reply instanceof CreateSubjectReplyReq){
			 yb_user_id = ((CreateSubjectReplyReq) reply).getYb_user_id();
		}
		return this.createMpr(MissionConsts.MISSIONID_REPLY, yb_user_id, null);
	}
	
	
	private void createMpr(MissionPerformRecord missionPerformRecord){
		try
		{
			if (null == missionPerformRecord){
				return ;
			}
			this.createMissionPerformRecord(missionPerformRecord);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	private List<MissionPerformRecord> createMpr(Long mission_id, Long yb_user_id, String nickname, String srcname){
		List<MissionPerformRecord> mprList = new ArrayList<MissionPerformRecord>();
		
		MissionPerformRecord missionPerformRecord = new MissionPerformRecord();
		Mission mission = missionDao.findOne(mission_id);
		int earn_count = 0;
		if (MissionConsts.MISSIONID_PUBLISH_RECIPE != mission_id && MissionConsts.MISSIONID_DAILY_SIGNIN != mission_id){
			earn_count = Integer.parseInt(mission.getOnce_award());
		}
		missionPerformRecord.setAward_maxtimes(mission.getAward_maxtimes());
		missionPerformRecord.setEarn_count(earn_count);
		missionPerformRecord.setPerform_mission_id(mission.getMission_id());
		missionPerformRecord.setPerform_mission_name(mission.getMission_name());
		missionPerformRecord.setPerform_result((byte)0);
		missionPerformRecord.setPerform_time(System.currentTimeMillis());
		
		missionPerformRecord.setPerform_user_id(yb_user_id);
		missionPerformRecord.setPerform_user_name(nickname);
		
		if(mission_id>3){
			int performedCnt = missionPerformRecordDao.findDailyMissionPerformRecordCnt(yb_user_id, mission_id);
			if(performedCnt>=missionPerformRecord.getAward_maxtimes()){//超过最大任务数，不再记录
//				missionPerformRecord.setEarn_count(0);
				return mprList;
			}
		}
		if(MissionConsts.MISSIONID_PUBLISH_WORK==mission_id&&!StringUtils.isEmpty(srcname)){
			missionPerformRecord.setPerform_mission_name(mission.getMission_name()+" "+srcname);
		}
		this.createMpr(missionPerformRecord);
		
		if(null!=missionPerformRecord&&missionPerformRecord.getEarn_count()>0){
			mprList.add(missionPerformRecord);
		}
		
		return mprList;
	}
	
	private List<MissionPerformRecord> createMpr(Long mission_id, Long yb_user_id,  String srcname){
		if(0==yb_user_id.longValue()){//用户未登录，不作任务记录
			return null;
		}
		UserInfo userInfo = this.userInfoDao.findOne(yb_user_id);
		if(null == userInfo){
			return null;
		}
		
		return this.createMpr(mission_id, yb_user_id, userInfo.getNickname(), srcname);
	}

	@Override
	public List<MissionPerformRecord> findDailySigninRecord(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		return this.findDailySigninRecord(yb_user_id, 0);
	}

	@Override
	public List<MissionPerformRecord> createDailySigninRecord(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		MissionPerformRecord oldMissionPerformRecord = this.missionPerformRecordDao.findDailySigninRecord(yb_user_id, 0);
		if (null != oldMissionPerformRecord){
			throw new YiBakerException(RetEnum.MISSION_PERFORMED);		
		}
		List<MissionPerformRecord> mprList = new ArrayList<MissionPerformRecord>();
		Mission mission = missionDao.findOne(MissionConsts.MISSIONID_DAILY_SIGNIN);
		MissionPerformRecord missionPerformRecord = new MissionPerformRecord();
		int earn_count = 0;
		 List<MissionPerformRecord> signinedRecords = this.findDailySigninRecord(yb_user_id, 1);
		 int signinedDays = signinedRecords.size();
		 if(signinedDays>=MissionConsts.DAILY_SIGNIN_DAYRANGE){//signinedDays作为索引取每天的奖励焙壳数，要限制它的值不超过签到范围天数
			 signinedDays = MissionConsts.DAILY_SIGNIN_DAYRANGE - 1;
		 }
		 String[] strs = mission.getOnce_award().split(",");
		 earn_count = Integer.parseInt(strs[signinedDays]);
		 
		missionPerformRecord.setAward_maxtimes(mission.getAward_maxtimes());
		missionPerformRecord.setEarn_count(earn_count);
		missionPerformRecord.setPerform_mission_id(mission.getMission_id());
		missionPerformRecord.setPerform_mission_name(mission.getMission_name());
		missionPerformRecord.setPerform_result((byte)0);
		missionPerformRecord.setPerform_time(System.currentTimeMillis());
		
		UserInfo userInfo = this.userInfoDao.findOne(yb_user_id);
		missionPerformRecord.setPerform_user_id(yb_user_id);
		missionPerformRecord.setPerform_user_name(userInfo.getNickname());
		
		this.createMpr(missionPerformRecord);
		
		mprList.add(missionPerformRecord);
		
		return mprList;
	}
	
	/**
	 * 查询从今天或昨天开始的接续签到天数
	 * @param yb_user_id
	 * @param beforedays 0-今天;1-昨天
	 * @return
	 */
	public List<MissionPerformRecord> findDailySigninRecord(Long yb_user_id, int beforedays)
	{
		// TODO Auto-generated method stub
		List<MissionPerformRecord> mprList = new ArrayList<MissionPerformRecord>();
		do {
			MissionPerformRecord missionPerformRecord = this.missionPerformRecordDao.findDailySigninRecord(yb_user_id, beforedays);
			if (null != missionPerformRecord){
				mprList.add(missionPerformRecord);
			} else if (beforedays>0){
				break;
			}
			beforedays++;
		} while (beforedays<=MissionConsts.DAILY_SIGNIN_DAYRANGE);
		
		return mprList;
	}

	@Override
	public DailySigninStatusResp findDailySigninStatus(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		DailySigninStatusResp dailySigninStatusResp = new DailySigninStatusResp();
		int siz = this.findDailySigninRecord(yb_user_id).size();
		if (siz>MissionConsts.DAILY_SIGNIN_DAYRANGE){
			siz = MissionConsts.DAILY_SIGNIN_DAYRANGE;
		}
		dailySigninStatusResp.setSigninDays(siz);
		MissionPerformRecord missionPerformRecord = this.missionPerformRecordDao.findDailySigninRecord(yb_user_id, 0);
		dailySigninStatusResp.setTodaySignined(null != missionPerformRecord);
		return dailySigninStatusResp;
	}

}
