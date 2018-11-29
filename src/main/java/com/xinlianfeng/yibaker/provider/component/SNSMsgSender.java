/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.component
 * File name:SNSMsg.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月20日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.component;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xinlianfeng.yibaker.common.constant.MsgType;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.Recipe;
import com.xinlianfeng.yibaker.common.entity.RecipeWork;
import com.xinlianfeng.yibaker.common.entity.RewardRecord;
import com.xinlianfeng.yibaker.common.entity.SNSMsgInfo;
import com.xinlianfeng.yibaker.common.entity.Subject;
import com.xinlianfeng.yibaker.common.entity.SubjectBriefInfo;
import com.xinlianfeng.yibaker.common.entity.UserBriefInfo;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.resp.SubjectDetailResp;
import com.xinlianfeng.yibaker.provider.dao.RecipeDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeWorkDao;
import com.xinlianfeng.yibaker.provider.dao.RewardRecordDao;
import com.xinlianfeng.yibaker.provider.dao.SNSMsgDao;
import com.xinlianfeng.yibaker.provider.dao.SNSMsgInfoDao;
import com.xinlianfeng.yibaker.provider.dao.SubjectDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;

/**
 * @Description: 发送站内通知组件
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月20日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Component
public class SNSMsgSender
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SNSMsgInfoDao snsMsgInfoDao;

	@Autowired
	private SNSMsgDao snsMsgDao;

	@Autowired
	private RecipeDao recipeDao;

	@Autowired
	private RecipeWorkDao recipeWorkDao;

	@Autowired
	private SubjectDao subjectDao;

	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private RewardRecordDao  rewardRecordDao; 

	/**
	 * 组装评论了你发布的食谱消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_201(SNSMsgInfo msgInfo)
	{
		Recipe recipe = recipeDao.getRecipeInfo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(recipe.getRecipe_name());
		return recipe.getYb_user_id();
	}
	
	/**
	 * 组装评论了你发布的作品消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_202(SNSMsgInfo msgInfo)
	{
		RecipeWork work = recipeWorkDao.getWorkInfo(msgInfo.getSrc_id());
		String work_content = work.getWork_content();
		int length = work_content.length() > 50 ? 50 : work_content.length();
		msgInfo.setMsg_title(work.getWork_content().substring(0, length));
		return work.getYb_user_id();
	}
	
	/**
	 * 组装评论了你发布的话题消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_203(SNSMsgInfo msgInfo)
	{
		SubjectBriefInfo subject = subjectDao.getBriefInfo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(subject.getSubject_name());
		return subject.getYb_user_id();
	}
	
	/**
	 * 组装在菜谱评论中@了你消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_204(SNSMsgInfo msgInfo)
	{
//		RecipeBriefInfo recipe = recipeDao.getRecipeBriefInfo(msgInfo.getSrc_id());
//		String msg_title = MessageFormat.format(MsgTemplate.TEMPLATE_204, recipe.getRecipe_name());
//		msgInfo.setMsg_title(recipe.getRecipe_name());
		String content = msgInfo.getMsg_title();
		int length = content.length() > 50 ? 50 : content.length();
		msgInfo.setMsg_title(content.substring(0, length));
		return 0;
	}
	
	/**
	 * 组装在作品评论中@了你消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_205(SNSMsgInfo msgInfo)
	{
//		RecipeWork work = recipeWorkDao.getWorkInfo(msgInfo.getSrc_id());
//		String work_content = work.getWork_content();
//		int length = work_content.length() > 50 ? 50 : work_content.length();
//		msgInfo.setMsg_title(work.getWork_content().substring(0, length));
		String content = msgInfo.getMsg_title();
		int length = content.length() > 50 ? 50 : content.length();
		msgInfo.setMsg_title(content.substring(0, length));
		return 0;
	}
	
	/**
	 * 组装在话题评论中@了你消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_206(SNSMsgInfo msgInfo)
	{
//		SubjectBriefInfo subject = subjectDao.getBriefInfo(msgInfo.getSrc_id());
//		msgInfo.setMsg_title(subject.getSubject_name());
		if (StringUtils.isNotBlank(msgInfo.getMsg_title()))
		{
			String content = msgInfo.getMsg_title();
			int length = content.length() > 50 ? 50 : content.length();
			msgInfo.setMsg_title(content.substring(0, length));
		}
		return 0;
	}
	
	/**
	 * 组装赞了你的菜谱消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_301(SNSMsgInfo msgInfo)
	{
		Recipe recipe = recipeDao.getRecipeInfo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(recipe.getRecipe_name());
		msgInfo.setSrc_image(recipe.getRecipe_image());
		return recipe.getYb_user_id();
	}
	
	/**
	 * 组装赞了你的作品消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_302(SNSMsgInfo msgInfo)
	{
		RecipeWork work = recipeWorkDao.getWorkInfo(msgInfo.getSrc_id());
		String work_content = work.getWork_content();
		int length = work_content.length() > 50 ? 50 : work_content.length();
		msgInfo.setMsg_title(work.getWork_content().substring(0, length));
		msgInfo.setSrc_image(work.getWork_image());
		return work.getYb_user_id();
	}
	
	/**
	 * 组装赞了你的话题评论消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_303(SNSMsgInfo msgInfo)
	{
		SubjectBriefInfo subject = subjectDao.getBriefInfo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(subject.getSubject_name());
		return subject.getYb_user_id();
	}
	
	/**
	 * 组装赞了你的话题点赞消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_304(SNSMsgInfo msgInfo)
	{
		SubjectDetailResp subjectDetailResp = subjectDao.getInfo(msgInfo.getSrc_id());
		Subject subject = subjectDetailResp.getSubject();
		msgInfo.setMsg_title(subject.getSubject_name());
		String img = "";
		String imgs = subject.getSubject_image();
		if(!StringUtils.isEmpty(imgs)){
			if(imgs.indexOf(";")!=-1){
				String[] imgArr = imgs.split(";");
				img = imgArr[0];
			} else {
				img = imgs;
			}
		}
		msgInfo.setSrc_image(img);
		return subject.getYb_user_id();
	}
	
	/**
	 * 组装下载了你的食谱消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_501(SNSMsgInfo msgInfo)
	{
		Recipe recipe = recipeDao.getRecipeInfo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(recipe.getRecipe_name());
		msgInfo.setSrc_image(recipe.getRecipe_image());
		return recipe.getYb_user_id();
	}
	
	/**
	 * 组装收藏了你的食谱消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_502(SNSMsgInfo msgInfo)
	{
		Recipe recipe = recipeDao.getRecipeInfo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(recipe.getRecipe_name());
		msgInfo.setSrc_image(recipe.getRecipe_image());
		return recipe.getYb_user_id();
	}
	
	/**
	 * 组装收藏了你的食谱消息
	 * @param msgInfo
	 * @return
	 */
	private long packMSG_601(SNSMsgInfo msgInfo)
	{
		RewardRecord rewardRecord = rewardRecordDao.findOneByRewardNo(msgInfo.getSrc_id());
		msgInfo.setMsg_title(rewardRecord.getReward_bakers().intValue()+"");
		return rewardRecord.getRewardee_user_id();
	}
	
	/**
	 * 组装消息内容
	 * @param msgInfo
	 * @return
	 */
	private long packMsgInfo(SNSMsgInfo msgInfo)
	{
		long to_user_id = 0;
		int msg_type = msgInfo.getMsg_type();
		switch (msg_type)
		{
			case MsgType.MSG_101://TODO 广播消息
				break;
	
			case MsgType.MSG_102://TODO 广播消息
				break;
	
			case MsgType.MSG_151://TODO 点对点消息
				break;
	
			case MsgType.MSG_201:
				to_user_id = packMSG_201(msgInfo);
				break;
	
			case MsgType.MSG_202:
				to_user_id = packMSG_202(msgInfo);
				break;
	
			case MsgType.MSG_203:
				to_user_id = packMSG_203(msgInfo);
				break;
	
			case MsgType.MSG_204:
				to_user_id = packMSG_204(msgInfo);
				break;
	
			case MsgType.MSG_205:
				to_user_id = packMSG_205(msgInfo);
				break;
	
			case MsgType.MSG_206:
				to_user_id = packMSG_206(msgInfo);
				break;
	
			case MsgType.MSG_301:
				to_user_id = packMSG_301(msgInfo);
				break;
	
			case MsgType.MSG_302:
				to_user_id = packMSG_302(msgInfo);
				break;
	
			case MsgType.MSG_303:
				to_user_id = packMSG_303(msgInfo);
				break;
				
			case MsgType.MSG_304:
				to_user_id = packMSG_304(msgInfo);
				break;
	
			case MsgType.MSG_401://不需要拼装数据
				break;
	
			case MsgType.MSG_501:
				to_user_id = packMSG_501(msgInfo);
				break;
	
			case MsgType.MSG_502:
				to_user_id = packMSG_502(msgInfo);
				break;
				
			case MsgType.MSG_601:
				to_user_id = packMSG_601(msgInfo);
				break;

			default:
				break;
		}
		
		return to_user_id;
	}
	
	/**
	 * 发送站内通知
	 * @param to_user_id
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public int sendSNSMsg(long to_user_id, int group_id, SNSMsgInfo msgInfo) throws Exception
	{
		if (null == msgInfo)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		//组装消息内容
		long user_id = packMsgInfo(msgInfo);
		if(0 != user_id)
		{
			to_user_id = user_id;//内容创建者编号
		}
		
		//组装消息发送者信息
		UserBriefInfo user = userInfoDao.getUserBriefInfo(msgInfo.getFrom_user_id());
		msgInfo.setFrom_user_name(user.getNickname());
		msgInfo.setFrom_user_photo(user.getPhoto());
		msgInfo.setSend_time(System.currentTimeMillis());

		//插入消息内容表
		snsMsgInfoDao.create(msgInfo);
		//插入消息表
		if (msgInfo.getMsg_type() >= MsgType.MSG_151)//广播消息不统一插入，用户查询时插入
		{
			snsMsgDao.create(to_user_id, msgInfo.getMsg_info_id(), group_id);
		}
		
		return 0;
	}
	
	
}

