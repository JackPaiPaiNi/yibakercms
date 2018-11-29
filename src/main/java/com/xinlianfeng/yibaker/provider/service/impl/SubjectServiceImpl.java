/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:SubjectServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月16日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.constant.ADType;
import com.xinlianfeng.yibaker.common.constant.MsgGroup;
import com.xinlianfeng.yibaker.common.constant.MsgSendType;
import com.xinlianfeng.yibaker.common.constant.MsgType;
import com.xinlianfeng.yibaker.common.constant.PageInfo;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.Activity;
import com.xinlianfeng.yibaker.common.entity.Advert;
import com.xinlianfeng.yibaker.common.entity.ClassRoom;
import com.xinlianfeng.yibaker.common.entity.CommunitySNSCount;
import com.xinlianfeng.yibaker.common.entity.SNSMsgInfo;
import com.xinlianfeng.yibaker.common.entity.Subject;
import com.xinlianfeng.yibaker.common.entity.SubjectReply;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.req.CreateSubjectReplyReq;
import com.xinlianfeng.yibaker.common.req.CreateSubjectReq;
import com.xinlianfeng.yibaker.common.resp.ActivityListResp;
import com.xinlianfeng.yibaker.common.resp.ClassListResp;
import com.xinlianfeng.yibaker.common.resp.CommunityPageResp;
import com.xinlianfeng.yibaker.common.resp.SubjectDetailResp;
import com.xinlianfeng.yibaker.common.resp.SubjectListResp;
import com.xinlianfeng.yibaker.common.resp.SubjectReplyListResp;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.SubjectService;
import com.xinlianfeng.yibaker.provider.component.SNSMsgSender;
import com.xinlianfeng.yibaker.provider.dao.ActivityDao;
import com.xinlianfeng.yibaker.provider.dao.AdvertDao;
import com.xinlianfeng.yibaker.provider.dao.ClassRoomDao;
import com.xinlianfeng.yibaker.provider.dao.SubjectDao;
import com.xinlianfeng.yibaker.provider.dao.SubjectReplyDao;

/**
 * @Description: 话题服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月16日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("subjectService")
public class SubjectServiceImpl implements SubjectService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SubjectDao subjectDao;

	@Autowired
	private SubjectReplyDao subjectReplyDao;

	@Autowired
	private AdvertDao advertDao;

	@Autowired
	private ClassRoomDao classRoomDao;
	
	@Autowired
	private ActivityDao activityDao;
	
	@Autowired
	private SNSMsgSender snsMsgSender;

	/**
	 * 创建话题
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public long createSubject(CreateSubjectReq req) throws Exception
	{
		if (null == req 
//				|| StringUtils.isBlank(req.getSubject_name())
				|| StringUtils.isBlank(req.getSubject_content())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			Subject subject = new Subject();
			BeanUtils.copyProperties(subject,req);
			
			String content = subject.getSubject_content();
			int length  = 50;
			if (content.length() > length) content = content.substring(0, length);
			subject.setSubject_brief(content);
			if (StringUtils.isBlank(subject.getSubject_name()))
			{
				subject.setSubject_name(content);
			}
			
			long now = System.currentTimeMillis();
			subject.setCreate_time(now);
			subject.setUpdate_time(now);
			subjectDao.create(subject);
			return subject.getSubject_id();
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_CREATE_FAIL, e);					
		}
	}
	
	/**
	 * 查询话题详细信息
	 * @param subject_id
	 * @return
	 * @throws Exception
	 */
	public SubjectDetailResp getSubjectInfo(long subject_id) throws Exception
	{
		if (0 ==subject_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			SubjectDetailResp data = subjectDao.getInfo(subject_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_GETINFO_FAIL,e);					
		}
	}
	
	/**
	 * 查询话题详细信息
	 * @param subject_id
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public SubjectDetailResp getSubjectInfo(long subject_id, long yb_user_id) throws Exception
	{
		if (0 ==subject_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			SubjectDetailResp data = subjectDao.getSubjectInfo(subject_id, yb_user_id);
			
			if(null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_GETINFO_FAIL,e);					
		}
	}
	
	/**
	 * 查询话题列表
	 * @param board_id
	 * @param last_subject_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public SubjectListResp getSubjectList(int board_id, long last_subject_id, int count) throws Exception
	{
		if (0 ==board_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			SubjectListResp subjectlist = subjectDao.getList(board_id, last_subject_id, count);
			if (null == subjectlist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = subjectlist.getSubjectlist().size();
			int total = subjectDao.getTotal(board_id);
			subjectlist.setCount(size);
			subjectlist.setTotal(total);
			
			return subjectlist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 查询话题列表
	 * @param board_id
	 * @param yb_user_id
	 * @param last_subject_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public SubjectListResp getSubjectList(int board_id, long yb_user_id, long last_subject_id, int count) throws Exception
	{
		if (0 ==board_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			SubjectListResp subjectlist = subjectDao.getSubjectList(board_id, last_subject_id, yb_user_id, count);
			if (null == subjectlist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = subjectlist.getSubjectlist().size();
			int total = subjectDao.getTotal(board_id);
			subjectlist.setCount(size);
			subjectlist.setTotal(total);
			
			return subjectlist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 查询话题置顶列表
	 * @param board_id
	 * @return
	 * @throws Exception
	 */
	public SubjectListResp getSubjectTopList(int board_id) throws Exception
	{
		if (0 ==board_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			SubjectListResp subjectlist = subjectDao.getTopList(board_id, PageInfo.DEFAULT_COUNT);
			if (null == subjectlist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			return subjectlist;
		} catch (DataAccessException e)
		{
			log.error(RetEnum.SUBJECT_GETTOPLIST_FAIL.name(), e);
			throw new YiBakerException(RetEnum.SUBJECT_GETTOPLIST_FAIL);					
		}
	}
	
	/**
	 * 查询话题置顶列表
	 * @param board_id
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public SubjectListResp getSubjectTopList(int board_id, long yb_user_id) throws Exception
	{
		if (0 ==board_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			SubjectListResp subjectlist = subjectDao.getTopSubjectList(board_id, yb_user_id, PageInfo.DEFAULT_COUNT);
			if (null == subjectlist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			return subjectlist;
		} catch (DataAccessException e)
		{
			log.error(RetEnum.SUBJECT_GETTOPLIST_FAIL.name(), e);
			throw new YiBakerException(RetEnum.SUBJECT_GETTOPLIST_FAIL);					
		}
	}
	
	/**
	 * 查询我的话题列表
	 * @param yb_user_id
	 * @param last_subject_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public SubjectListResp getMySubjectList(long yb_user_id, long last_subject_id, int count) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			SubjectListResp subjectlist = subjectDao.getMytList(yb_user_id, last_subject_id, count);
			if (null == subjectlist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = subjectlist.getSubjectlist().size();
			int total = subjectDao.getMyTotal(yb_user_id);
			subjectlist.setCount(size);
			subjectlist.setTotal(total);
			
			return subjectlist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 创建话题评论
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public int createSubjectReply(CreateSubjectReplyReq req) throws Exception
	{
		if (null == req 
				|| 0 == req.getSubject_id()
				|| (StringUtils.isBlank(req.getReply_content()) && StringUtils.isBlank(req.getReply_image()))
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			SubjectReply reply = new SubjectReply();
			BeanUtils.copyProperties(reply,req);
			reply.setReply_time(System.currentTimeMillis());
			subjectReplyDao.create(reply);
			req.setReply_id(reply.getReply_id());
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_REPLY_CREATE_FAIL);					
		}
		
		//发送站内消息
		try
		{
			SNSMsgInfo msgInfo = new SNSMsgInfo();
			msgInfo.setFrom_user_id(req.getYb_user_id());
			msgInfo.setSend_type(MsgSendType.P2P);
			msgInfo.setSrc_id(req.getSubject_id());
			msgInfo.setMsg_title(req.getAdd_reply_content());//原评论内容
			msgInfo.setMsg_content(req.getReply_content());
			long to_user_id = req.getAdd_user_id();
			if (0 == to_user_id)
			{
				msgInfo.setMsg_type(MsgType.MSG_203);
			}else
			{
				msgInfo.setMsg_type(MsgType.MSG_206);
			}
			snsMsgSender.sendSNSMsg(to_user_id, MsgGroup.REPLY, msgInfo);
		} catch (Exception e)
		{
			log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
		}
		
		return 0;
	}
	
	/**
	 * 查询话题评论列表
	 * @param subject_id
	 * @return
	 * @throws Exception
	 */
	public SubjectReplyListResp getSubjectReplyList(long yb_user_id, long subject_id, long last_reply_id, int count) throws Exception
	{
		if (0 ==subject_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			SubjectReplyListResp replylist = subjectReplyDao.getList(yb_user_id, subject_id, last_reply_id, count);
			if (null == replylist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = replylist.getReplylist().size();
			int total = subjectReplyDao.getTotal(yb_user_id, subject_id);
			replylist.setCount(size);
			replylist.setTotal(total);
			
			return replylist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_REPLY_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 查询社区首页信息
	 * @return
	 * @throws Exception
	 */
	public CommunityPageResp getCommunityPageInfo() throws Exception
	{
		try
		{
			CommunityPageResp resp = new CommunityPageResp();
			CommunitySNSCount snscount = new CommunitySNSCount();
			
			//查询社区首页广告栏信息
			List<Advert> adlist = advertDao.getList(ADType.COMMUNITYPAGE, PageInfo.DEFAULT_COUNT);
			
			//查询课堂总数
			snscount.setClass_count(classRoomDao.getTotal(0));
			
			//查询话题总数
			snscount.setSubject_count(subjectDao.getAllTotal(0));
			
			//查询活动总数
			snscount.setActivity_count(activityDao.getTotal());
			
			if(null != adlist && adlist.size() > 0) resp.setAdlist(adlist);
			resp.setSnscount(snscount);
			
			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.COMMUNITY_GETPAGE_FAIL);					
		}
	}

	/**
	 * 查询烘焙课堂列表
	 * @param last_class_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public ClassListResp getClassList(long last_id, int count, int class_type) throws Exception
	{
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<ClassRoom> classlist = classRoomDao.getList(last_id, count, class_type);
			
			if (null == classlist || 0 == classlist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			ClassListResp resp = new ClassListResp();
			resp.setClasslist(classlist);
			int size = classlist.size();
			int total = classRoomDao.getTotal(class_type);
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CLASS_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 查询活动列表
	 * @param last_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public ActivityListResp getActivityList(long last_id, int count) throws Exception
	{
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<Activity> activitylist = activityDao.getList(last_id, count);
			
			if (null == activitylist || 0 == activitylist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			ActivityListResp resp = new ActivityListResp();
			resp.setActivitylist(activitylist);
			int size = activitylist.size();
			int total = activityDao.getTotal();
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.ACTIVITY_GETLIST_FAIL);					
		}
	}

	@Transactional
	@Override
	public YibakerResp createSubjectReplyEx(CreateSubjectReplyReq req) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode = this.createSubjectReply(req);
		YibakerResp yibakerResp = new YibakerResp();
		SubjectReply sr = new SubjectReply();
		BeanUtils.copyProperty(sr, "reply_id", req.getReply_id());
		yibakerResp.setData(sr);
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}

	@Override
	public SubjectListResp findSubjects(int board_id, long last_reply_time, int count) throws Exception
	{
		// TODO Auto-generated method stub
		if (0 ==board_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			SubjectListResp subjectlist = subjectDao.findSubjects(board_id, last_reply_time, count);
			if (null == subjectlist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = subjectlist.getSubjectlist().size();
			int total = subjectDao.getTotal(board_id);
			subjectlist.setCount(size);
			subjectlist.setTotal(total);
			
			return subjectlist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SUBJECT_GETLIST_FAIL, e);					
		}
	}
	
}

