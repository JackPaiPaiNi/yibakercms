/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:SNSServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月5日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.constant.MsgGroup;
import com.xinlianfeng.yibaker.common.constant.MsgSendType;
import com.xinlianfeng.yibaker.common.constant.MsgType;
import com.xinlianfeng.yibaker.common.constant.PageInfo;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.constant.SNSOp;
import com.xinlianfeng.yibaker.common.entity.SNSMsgCount;
import com.xinlianfeng.yibaker.common.entity.SNSMsgInfo;
import com.xinlianfeng.yibaker.common.entity.ShareLog;
import com.xinlianfeng.yibaker.common.entity.TopicShareLog;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.req.CollectRecipeReq;
import com.xinlianfeng.yibaker.common.req.CollectTopicReq;
import com.xinlianfeng.yibaker.common.req.DownloadRecipeReq;
import com.xinlianfeng.yibaker.common.req.FollowUserReq;
import com.xinlianfeng.yibaker.common.req.LikeRecipeReplyReq;
import com.xinlianfeng.yibaker.common.req.LikeRecipeReq;
import com.xinlianfeng.yibaker.common.req.LikeSubjectReq;
import com.xinlianfeng.yibaker.common.req.LikeTopicReq;
import com.xinlianfeng.yibaker.common.req.LikeWorkReq;
import com.xinlianfeng.yibaker.common.resp.OpDetailResp;
import com.xinlianfeng.yibaker.common.resp.OpListResp;
import com.xinlianfeng.yibaker.common.resp.SNSMsgListResp;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.SNSService;
import com.xinlianfeng.yibaker.provider.component.SNSMsgSender;
import com.xinlianfeng.yibaker.provider.dao.SNSMsgDao;
import com.xinlianfeng.yibaker.provider.dao.SNSMsgInfoDao;
import com.xinlianfeng.yibaker.provider.dao.SNSRecipeDao;
import com.xinlianfeng.yibaker.provider.dao.SNSRecipeReplyDao;
import com.xinlianfeng.yibaker.provider.dao.SNSSubjectDao;
import com.xinlianfeng.yibaker.provider.dao.SNSTopicDao;
import com.xinlianfeng.yibaker.provider.dao.SNSUserDao;
import com.xinlianfeng.yibaker.provider.dao.SNSWorkDao;
import com.xinlianfeng.yibaker.provider.dao.ShareLogDao;
import com.xinlianfeng.yibaker.provider.dao.TopicShareLogDao;

/**
 * @Description: SNS服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月5日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("snsService")
public class SNSServiceImpl implements SNSService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SNSUserDao snsUserDao;
	
	@Autowired
	private SNSRecipeDao snsRecipeDao;
	
	@Autowired
	private SNSRecipeReplyDao snsRecipeReplyDao;
	
	@Autowired
	private SNSWorkDao snsWorkDao;
	
	@Autowired
	private SNSSubjectDao snsSubjectDao;
	
	@Autowired
	private SNSTopicDao snsTopicDao;

	@Autowired
	private SNSMsgSender snsMsgSender;
	
	@Autowired
	private SNSMsgDao snsMsgDao;
	
	@Autowired
	private SNSMsgInfoDao snsMsgInfoDao;
	
	@Autowired
	private ShareLogDao recipeShareLogDao;
	
	@Autowired
	private TopicShareLogDao topicShareLogDao;
	
	/**
	 * 关注用户
	 * @param followUserReq
	 * @return
	 * @throws Exception
	 */
	public int followUser(FollowUserReq followUserReq) throws Exception
	{
		if (null == followUserReq
				|| 0 == followUserReq.getFrom_user_id()
				|| 0 == followUserReq.getTo_user_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			followUserReq.setFollow_time(System.currentTimeMillis());
			snsUserDao.insertUpdateFollow(followUserReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_USER_FOLLOW_FAIL);					
		}
		
		//发送站内消息，目前只有关注发送消息
		if (1 == followUserReq.getIs_follow())
		{
			try
			{
				SNSMsgInfo msgInfo = new SNSMsgInfo();
				msgInfo.setFrom_user_id(followUserReq.getFrom_user_id());
				msgInfo.setMsg_type(MsgType.MSG_401);
				msgInfo.setSend_type(MsgSendType.P2P);
				snsMsgSender.sendSNSMsg(followUserReq.getTo_user_id(), MsgGroup.FOLLOW, msgInfo);
			} catch (Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
			}
		}

		return 0;
	}
	
	/**
	 * 菜谱点赞
	 * @param likeRecipeReq
	 * @return
	 * @throws Exception
	 */
	public int likeRecipe(LikeRecipeReq likeRecipeReq) throws Exception
	{
		if (null == likeRecipeReq
				|| 0 == likeRecipeReq.getYb_user_id()
				|| 0 == likeRecipeReq.getRecipe_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			likeRecipeReq.setLike_time(System.currentTimeMillis());
			snsRecipeDao.insertUpdateLike(likeRecipeReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_RECIPE_LIKE_FAIL);					
		}
		
		//发送站内消息,取消点赞不发送消息
		if (SNSOp.LIKED == likeRecipeReq.getIs_like())
		{
			try
			{
				SNSMsgInfo msgInfo = new SNSMsgInfo();
				msgInfo.setFrom_user_id(likeRecipeReq.getYb_user_id());
				msgInfo.setMsg_type(MsgType.MSG_301);
				msgInfo.setSend_type(MsgSendType.P2P);
				msgInfo.setSrc_id(likeRecipeReq.getRecipe_id());
				snsMsgSender.sendSNSMsg(0, MsgGroup.LIKE, msgInfo);
			} catch (Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
			}
		}
		
		return 0;
	}

	/**
	 * 收藏菜谱
	 * @param collectRecipeReq
	 * @return
	 * @throws Exception
	 */
	public int collectRecipe(CollectRecipeReq collectRecipeReq) throws Exception
	{
		if (null == collectRecipeReq
				|| 0 == collectRecipeReq.getYb_user_id()
				|| 0 == collectRecipeReq.getRecipe_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			collectRecipeReq.setCollect_time(System.currentTimeMillis());
			snsRecipeDao.insertUpdateCollect(collectRecipeReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_RECIPE_COLLECT_FAIL);					
		}
		
		//发送站内消息，取消收藏不发送消息
		if (SNSOp.COLLECTED == collectRecipeReq.getIs_collect())
		{
			try
			{
				SNSMsgInfo msgInfo = new SNSMsgInfo();
				msgInfo.setFrom_user_id(collectRecipeReq.getYb_user_id());
				msgInfo.setMsg_type(MsgType.MSG_502);
				msgInfo.setSend_type(MsgSendType.P2P);
				msgInfo.setSrc_id(collectRecipeReq.getRecipe_id());
				snsMsgSender.sendSNSMsg(0, MsgGroup.DOWNLOAD, msgInfo);
			} catch (Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
			}
		}
		
		return 0;
	}
	
	/**
	 * 下载菜谱操作（ 强制此接口只作为用户取消菜谱下载关系使用）
	 * @param downloadRecipeReq
	 * @return
	 * @throws Exception
	 */
	public int downloadRecipe(DownloadRecipeReq downloadRecipeReq) throws Exception
	{
		if (null == downloadRecipeReq
				|| 0 == downloadRecipeReq.getYb_user_id()
				|| 0 == downloadRecipeReq.getRecipe_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			downloadRecipeReq.setIs_download(0);//TODO 强制此接口只作为用户取消菜谱下载关系使用
			downloadRecipeReq.setDownload_time(System.currentTimeMillis());
			snsRecipeDao.insertUpdateDownload(downloadRecipeReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_RECIPE_DOWNLOAD_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 菜谱评论点赞
	 * @param likeRecipeReplyReq
	 * @return
	 */
	public int likeRecipeReply(LikeRecipeReplyReq likeRecipeReplyReq) throws Exception
	{
		if (null == likeRecipeReplyReq
				|| 0 == likeRecipeReplyReq.getYb_user_id()
				|| 0 == likeRecipeReplyReq.getR_reply_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			likeRecipeReplyReq.setLike_time(System.currentTimeMillis());
			snsRecipeReplyDao.insertUpdateLike(likeRecipeReplyReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_R_REPLY_LIKE_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 作品点赞
	 * @param likeWorkReq
	 * @return
	 * @throws Exception
	 */
	public int likeWork(LikeWorkReq likeWorkReq) throws Exception
	{
		if (null == likeWorkReq
				|| 0 == likeWorkReq.getYb_user_id()
				|| 0 == likeWorkReq.getWork_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			likeWorkReq.setLike_time(System.currentTimeMillis());
			snsWorkDao.insertUpdateLike(likeWorkReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_WORK_LIKE_FAIL);					
		}
		
		//发送站内消息
		if (SNSOp.LIKED == likeWorkReq.getIs_like())
		{
			try
			{
				SNSMsgInfo msgInfo = new SNSMsgInfo();
				msgInfo.setFrom_user_id(likeWorkReq.getYb_user_id());
				msgInfo.setMsg_type(MsgType.MSG_302);
				msgInfo.setSend_type(MsgSendType.P2P);
				msgInfo.setSrc_id(likeWorkReq.getWork_id());
				snsMsgSender.sendSNSMsg(0, MsgGroup.LIKE, msgInfo);
			} catch (Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
			}
		}
		
		return 0;
	}

	/**
	 * 查询作品点赞列表
	 * @param work_id
	 * @param last_time
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public OpListResp getWorkLikeList(long work_id,  long last_time, int count) throws Exception
	{
		if (0 ==work_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<OpDetailResp> oplist = snsWorkDao.getLikeList(work_id, last_time, count);
			
			if (null == oplist || 0 == oplist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			OpListResp resp = new OpListResp();
			resp.setOplist(oplist);
			int size = oplist.size();
			int total = snsWorkDao.getLikeTotal(work_id);
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_WORK_GETLIKELIST_FAIL);					
		}
	}
	
	/**
	 * 社区话题点赞
	 * @param likeSubjectReq
	 * @return
	 * @throws Exception
	 */
	public int likeSubject(LikeSubjectReq likeSubjectReq) throws Exception
	{
		if (null == likeSubjectReq
				|| 0 == likeSubjectReq.getYb_user_id()
				|| 0 == likeSubjectReq.getSubject_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			likeSubjectReq.setLike_time(System.currentTimeMillis());
			snsSubjectDao.insertUpdateLike(likeSubjectReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_SUBJECT_LIKE_FAIL);					
		}
		
		//发送站内消息
		if (SNSOp.LIKED == likeSubjectReq.getIs_like())
		{
			try
			{
				SNSMsgInfo msgInfo = new SNSMsgInfo();
				msgInfo.setFrom_user_id(likeSubjectReq.getYb_user_id());
				msgInfo.setMsg_type(MsgType.MSG_304);
				msgInfo.setSend_type(MsgSendType.P2P);
				msgInfo.setSrc_id(likeSubjectReq.getSubject_id());
				snsMsgSender.sendSNSMsg(0, MsgGroup.LIKE, msgInfo);
			} catch (Exception e)
			{
				log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
			}
		}
		
		return 0;
	}
	
	/**
	 * 查询社区话题点赞列表
	 * 
	 * @param subject_id
	 * @param last_time
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public OpListResp getSubjectLikeList(long subject_id,  long last_time, int count) throws Exception
	{
		if (0 ==subject_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<OpDetailResp> oplist = snsSubjectDao.getLikeList(subject_id, last_time, count);
			
			if (null == oplist || 0 == oplist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			OpListResp resp = new OpListResp();
			resp.setOplist(oplist);
			int size = oplist.size();
			int total = snsSubjectDao.getLikeTotal(subject_id);
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_SUBJECT_GETLIKELIST_FAIL);					
		}
	}
	
	/**
	 * 收藏专题
	 * @param collectTopicReq
	 * @return
	 * @throws Exception
	 */
	public int collectTopic(CollectTopicReq collectTopicReq) throws Exception
	{
		if (null == collectTopicReq
				|| 0 == collectTopicReq.getYb_user_id()
				|| 0 == collectTopicReq.getTopic_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			collectTopicReq.setCollect_time(System.currentTimeMillis());
			snsTopicDao.insertUpdateCollect(collectTopicReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_TOPIC_COLLECT_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 点赞专题
	 * @param likeTopicReq
	 * @return
	 * @throws Exception
	 */
	public int likeTopic(LikeTopicReq likeTopicReq) throws Exception
	{
		if (null == likeTopicReq
				|| 0 == likeTopicReq.getYb_user_id()
				|| 0 == likeTopicReq.getTopic_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			likeTopicReq.setLike_time(System.currentTimeMillis());
			snsTopicDao.insertUpdateLike(likeTopicReq);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_TOPIC_LIKE_FAIL);					
		}
		return 0;
	}
	
	
	/**
	 * 查询站内未读通知总数
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public SNSMsgCount getSNSMsgTotal(long yb_user_id) throws Exception
	{
		if (0 == yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			//查询是否有新的广播消息
			List<SNSMsgInfo> msglist = snsMsgInfoDao.getNewBroadcastMsgList(yb_user_id, 20);//查询最近的20条系统广播消息
			if (null != msglist && 0 != msglist.size())
			{
				//将新的广播消息插入本用户的点对点消息中
				for (SNSMsgInfo snsMsgInfo : msglist)
				{
					snsMsgDao.create(yb_user_id, snsMsgInfo.getMsg_info_id(), MsgGroup.SYSTEM);
				}
			}
			
			//获取未读消息总数
			int total = snsMsgDao.getUnReadTotal(yb_user_id);
			SNSMsgCount data = new SNSMsgCount();
			data.setTotal_count(total);
			
			return data;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_GETMSGTOTAL_FAIL);					
		}
	}
	
	/**
	 * 查询站内未读分类通知数
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public SNSMsgCount getSNSMsgTotalAll(long yb_user_id) throws Exception
	{
		if (0 == yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			//查询是否有新的广播消息
			List<SNSMsgInfo> msglist = snsMsgInfoDao.getNewBroadcastMsgList(yb_user_id, 20);//查询最近的20条系统广播消息
			if (null != msglist && 0 != msglist.size())
			{
				//将新的广播消息插入本用户的点对点消息中
				for (SNSMsgInfo snsMsgInfo : msglist)
				{
					snsMsgDao.create(yb_user_id, snsMsgInfo.getMsg_info_id(), MsgGroup.SYSTEM);
				}
			}
			
			//获取未读消息总数
			SNSMsgCount data = new SNSMsgCount();
			data = snsMsgDao.getUnReadTotalByType(yb_user_id);
			
			return data;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_GETMSGTOTAL_FAIL);					
		}
	}

	/**
	 * 查询站内通知列表
	 * @param yb_user_id
	 * @param group_id
	 * @param last_msg_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public SNSMsgListResp getSNSMsgList(long yb_user_id, int group_id,  long last_msg_id, int count) throws Exception
	{
		if (0 == yb_user_id || 0 == group_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			//获取分类消息列表
			List<SNSMsgInfo> msglist = snsMsgInfoDao.getList(yb_user_id, group_id, last_msg_id, count);
			if (null == msglist || 0 == msglist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			//将未读消息设为已读
			long begin_info_id = msglist.get(0).getMsg_info_id();
			long end_info_id = msglist.get(msglist.size() -1).getMsg_info_id();
			snsMsgDao.updateReadList(yb_user_id, group_id, begin_info_id, end_info_id);
			
			int size = msglist.size();
			int total = snsMsgInfoDao.getTotal(yb_user_id, group_id);
			SNSMsgListResp resp = new SNSMsgListResp();
			resp.setCount(size);
			resp.setTotal(total);
			resp.setMsglist(msglist);
			
			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_GETMSGLIST_FAIL, e);					
		}
	}
	
	/**
	 * 删除站内通知
	 * @param yb_user_id
	 * @param msg_info_id 为0删除所有通知
	 * @return
	 * @throws Exception
	 */
	public int deleteSNSMsg(long yb_user_id, int group_id, long msg_info_id) throws Exception
	{
		if (0 ==yb_user_id
				|| (0==group_id && 0==msg_info_id)
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			snsMsgDao.delete(yb_user_id, group_id, msg_info_id);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_DELETEMSG_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 创建菜谱分享日志
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public int createShareLog(ShareLog shareLog) throws Exception
	{
		if (null == log 
				|| 0 == shareLog.getSrc_type()
//				|| 0 == shareLog.getSrc_id()
				|| 0 == shareLog.getShare_tp_type()
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			shareLog.setShare_time(System.currentTimeMillis());
			recipeShareLogDao.create(shareLog);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_CREATERECIPESHARELOG_FAIL);					
		}
		return 0;
	}
	
	/**
	 * 创建专题分享日志
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public int createTopicShareLog(TopicShareLog log) throws Exception
	{
		if (null == log 
				|| 0 == log.getTopic_id()
				|| 0 == log.getShare_tp_type()
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			log.setShare_time(System.currentTimeMillis());
			topicShareLogDao.create(log);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SNS_CREATETOPICSHARELOG_FAIL);					
		}
		return 0;
	}

	@Transactional
	@Override
	public YibakerResp createShareLogEx(ShareLog shareLog) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode = this.createShareLog(shareLog);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}

}

