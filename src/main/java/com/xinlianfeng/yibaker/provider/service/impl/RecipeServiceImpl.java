/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:RecipeServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月4日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import redis.clients.jedis.exceptions.JedisConnectionException;

import com.xinlianfeng.yibaker.common.constant.ColumnInfo;
import com.xinlianfeng.yibaker.common.constant.MsgGroup;
import com.xinlianfeng.yibaker.common.constant.MsgSendType;
import com.xinlianfeng.yibaker.common.constant.MsgType;
import com.xinlianfeng.yibaker.common.constant.PageInfo;
import com.xinlianfeng.yibaker.common.constant.RecipeStatus;
import com.xinlianfeng.yibaker.common.constant.RecipeType;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.constant.SNSOp;
import com.xinlianfeng.yibaker.common.constant.SystemParamEnum;
import com.xinlianfeng.yibaker.common.constant.VersionConsts;
import com.xinlianfeng.yibaker.common.entity.Recipe;
import com.xinlianfeng.yibaker.common.entity.RecipeChannel;
import com.xinlianfeng.yibaker.common.entity.RecipeChannelEx;
import com.xinlianfeng.yibaker.common.entity.RecipeCurve;
import com.xinlianfeng.yibaker.common.entity.RecipeIngredient;
import com.xinlianfeng.yibaker.common.entity.RecipeProcess;
import com.xinlianfeng.yibaker.common.entity.RecipeReply;
import com.xinlianfeng.yibaker.common.entity.RecipeSellInfo;
import com.xinlianfeng.yibaker.common.entity.RecipeTip;
import com.xinlianfeng.yibaker.common.entity.RecipeTool;
import com.xinlianfeng.yibaker.common.entity.RecipeTopic;
import com.xinlianfeng.yibaker.common.entity.RecipeWork;
import com.xinlianfeng.yibaker.common.entity.SNSMsgInfo;
import com.xinlianfeng.yibaker.common.entity.SNSMyRecipe;
import com.xinlianfeng.yibaker.common.entity.SNSMyTopic;
import com.xinlianfeng.yibaker.common.entity.SNSMyWork;
import com.xinlianfeng.yibaker.common.entity.SystemParam;
import com.xinlianfeng.yibaker.common.entity.TopicReply;
import com.xinlianfeng.yibaker.common.entity.UserCurve;
import com.xinlianfeng.yibaker.common.entity.WorkReply;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.req.CreateSubjectReq;
import com.xinlianfeng.yibaker.common.req.DownloadRecipeReq;
import com.xinlianfeng.yibaker.common.req.UploadRecipeReq;
import com.xinlianfeng.yibaker.common.resp.ChannelListResp;
import com.xinlianfeng.yibaker.common.resp.ChannelRecipeResp;
import com.xinlianfeng.yibaker.common.resp.RecipeDataResp;
import com.xinlianfeng.yibaker.common.resp.RecipeDetailResp;
import com.xinlianfeng.yibaker.common.resp.RecipeExListResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;
import com.xinlianfeng.yibaker.common.resp.RecipeReplyListResp;
import com.xinlianfeng.yibaker.common.resp.TopicDetailResp;
import com.xinlianfeng.yibaker.common.resp.TopicListResp;
import com.xinlianfeng.yibaker.common.resp.TopicReplyListResp;
import com.xinlianfeng.yibaker.common.resp.WorkDetailResp;
import com.xinlianfeng.yibaker.common.resp.WorkListResp;
import com.xinlianfeng.yibaker.common.resp.WorkReplyListResp;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.RecipeService;
import com.xinlianfeng.yibaker.common.service.SubjectService;
import com.xinlianfeng.yibaker.common.service.TradeRecordService;
import com.xinlianfeng.yibaker.common.util.VersionManager;
import com.xinlianfeng.yibaker.provider.component.ColumnRecipeRedisDao;
import com.xinlianfeng.yibaker.provider.component.SNSMsgSender;
import com.xinlianfeng.yibaker.provider.dao.ChannelRecipeDao;
import com.xinlianfeng.yibaker.provider.dao.ColumnRecipeDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeCurveDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeIngredientDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeProcessDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeReplyDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeTipDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeToolDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeTopicDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeWorkDao;
import com.xinlianfeng.yibaker.provider.dao.SNSRecipeDao;
import com.xinlianfeng.yibaker.provider.dao.SNSTopicDao;
import com.xinlianfeng.yibaker.provider.dao.SNSWorkDao;
import com.xinlianfeng.yibaker.provider.dao.SystemParamDao;
import com.xinlianfeng.yibaker.provider.dao.TopicReplyDao;
import com.xinlianfeng.yibaker.provider.dao.UserCurveDao;
import com.xinlianfeng.yibaker.provider.dao.WorkReplyDao;

/**
 * @Description: 菜谱服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月4日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("recipeService")
public class RecipeServiceImpl  implements RecipeService
{
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecipeDao recipeDao;

	@Autowired
	private RecipeCurveDao recipeCurveDao;

	@Autowired
	private RecipeIngredientDao recipeIngredientDao;

	@Autowired
	private RecipeProcessDao recipeProcessDao;

	@Autowired
	private RecipeTipDao recipeTipDao;

	@Autowired
	private RecipeToolDao recipeToolDao;

	@Autowired
	private UserCurveDao userCurveDao;

	@Autowired
	private RecipeReplyDao recipeReplyDao;

	@Autowired
	private RecipeWorkDao recipeWorkDao;

	@Autowired
	private WorkReplyDao workReplyDao;
	
	@Autowired
	private TopicReplyDao topicReplyDao;

	@Autowired
	private RecipeTopicDao recipeTopicDao;

	@Autowired
	private SNSRecipeDao snsRecipeDao;

	@Autowired
	private SNSTopicDao snsTopicDao;

	@Autowired
	private SNSWorkDao snsWorkDao;

	@Autowired
	private ColumnRecipeDao columnRecipeDao;

	@Autowired
	private ColumnRecipeRedisDao columnRecipeRedisDao;

	@Autowired
	private ChannelRecipeDao channelRecipeDao;

	@Autowired
	private SNSMsgSender snsMsgSender;

	@Autowired
	private SearcherService searcherService;
	
	@Autowired
	private SystemParamDao systemParamDao;
	
	@Autowired
	private TradeRecordService tradeRecordServcie;
	
	@Autowired
	private SubjectService subjectService;
	
	/**
	 * 发布菜谱
	 * @param uploadRecipeReq
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public int createRecipe(int channel_id, UploadRecipeReq uploadRecipeReq) throws Exception
	{
		if (null == uploadRecipeReq
				|| 0 == channel_id
				|| null == uploadRecipeReq.getRecipe()
				|| (null == uploadRecipeReq.getIngredientlist() && uploadRecipeReq.getIngredientlist().isEmpty())
				|| (null == uploadRecipeReq.getProcesslist() && !uploadRecipeReq.getProcesslist().isEmpty())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		Recipe recipe = uploadRecipeReq.getRecipe();
		long recipe_id = recipe.getRecipe_id();
		long yb_user_id = recipe.getYb_user_id();

		//认证失败后再次提交,逻辑删除之前认证失败的菜谱
		try
		{
			if (0 != recipe_id)
			{
				recipeDao.deleteMyFailRecipe(yb_user_id, recipe_id);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			log.debug(">>>>>>>>>>>>>>>>>>delete recipe fail. uid: " + yb_user_id + ", recipe_id:" + recipe_id);
		}
		
		try
		{
			long nowTime = System.currentTimeMillis();
			
			//创建菜谱
//			Recipe recipe = uploadRecipeReq.getRecipe();
			
			//判断是否包含曲线
			if (null == uploadRecipeReq.getCurve() 
					|| 0==uploadRecipeReq.getCurve().getCurve_id())
			{
					recipe.setRecipe_type(RecipeType.RECIPE_TYPE_NOCURVE);
			}else
			{
				recipe.setRecipe_type(RecipeType.RECIPE_TYPE_CURVE);
			}
			recipe.setCreate_time(nowTime);
			recipe.setUpdate_time(nowTime);
			recipe.setRecipe_status(RecipeStatus.RECIPE_STATUS_CERTIFYING);//默认为认证中
//			recipe.setDr(DR.DR_AVAILABLE);//默认可用
			recipeDao.createRecipe(recipe);
			
			recipe_id = recipe.getRecipe_id();
			//创建菜谱扩展信息,recipe_manuid不填默认为201,recipe_no不填则为空字符串
			if(null==recipe.getRecipe_manuid()||0==recipe.getRecipe_manuid())
			{
				recipe.setRecipe_manuid(201);
			}
			if(null==recipe.getRecipe_no()||recipe.getRecipe_no().isEmpty())
			{
				recipe.setRecipe_no("");
			}
			recipeDao.createRecipeExt(recipe_id,recipe.getRecipe_no(),recipe.getRecipe_manuid());
			//创建菜谱分类关联关系
			channelRecipeDao.create(channel_id, recipe_id);
			
			//查询用户曲线信息，将用户曲线复制到菜谱曲线表中
			if(null != uploadRecipeReq.getCurve()
					&& 0!=uploadRecipeReq.getCurve().getCurve_id())
			{
				long curve_id = uploadRecipeReq.getCurve().getCurve_id();
				UserCurve userCurve = userCurveDao.getInfo(recipe.getYb_user_id(), curve_id);
				
				RecipeCurve recipeCurve = new RecipeCurve();
				recipeCurve.setRecipe_id(recipe_id);
				recipeCurve.setDevice_id(userCurve.getDevice_id());
				//不填device_type，则默认为1
				if(0==userCurve.getDevice_type())
				{
					userCurve.setDevice_type(1);
				}
				recipeCurve.setDevice_type(userCurve.getDevice_type());
				recipeCurve.setCurve_content(userCurve.getCurve_content());
				recipeCurveDao.createRecipeCurve(recipeCurve);
			}
			
			//创建菜谱食材
			List<RecipeIngredient> ingredientList = uploadRecipeReq.getIngredientlist();
			for(RecipeIngredient recipeIngredient:ingredientList)			
			{
				recipeIngredient.setRecipe_id(recipe_id);
				recipeIngredientDao.createRecipeIngredient(recipeIngredient);
			}
			
			//创建菜谱工具
			List<RecipeTool>toolList = uploadRecipeReq.getToollist();
			if(null != toolList && !toolList.isEmpty())
			{
				for(RecipeTool recipeTool:toolList)			
				{
					recipeTool.setRecipe_id(recipe_id);
					recipeToolDao.createRecipeTool(recipeTool);
				}
			}
			
			//创建菜谱制作过程
			List<RecipeProcess>processList = uploadRecipeReq.getProcesslist();
			for(RecipeProcess recipeProcess:processList)			
			{
				recipeProcess.setRecipe_id(recipe_id);
				recipeProcessDao.createRecipeProcess(recipeProcess);
			}
			
			//创建菜谱小贴士
			RecipeTip recipeTip = uploadRecipeReq.getTip();
			if(null != recipeTip)
			{
				recipeTip.setRecipe_id(recipe_id);
				recipeTipDao.createRecipeTip(recipeTip);
			}
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_UPLOAD_FAIL);					
		}
		
		return 0;
	}

	/**
	 * 查询菜谱详情
	 * @param recipe_id
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeDetailResp getRecipeDetail(long yb_user_id, long recipe_id) throws Exception
	{
		if (0 ==recipe_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			//查询菜谱详情
			RecipeDetailResp data = recipeDao.getRecipeDetail(recipe_id);
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			//查询菜谱我的社交信息
			boolean buyed = tradeRecordServcie.findRecipeBuyStatus(yb_user_id, recipe_id);
			SNSMyRecipe mysns = snsRecipeDao.getSNSMyRecipe(yb_user_id, recipe_id);
			if (null == mysns){
				mysns = new SNSMyRecipe();
			}
			mysns.setIs_buy(buyed?1:0);
			data.setMysns(mysns);
			
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETDETAIL_FAIL);					
		}
	}
	
	/**
	 * 下载菜谱数据
	 * @param recipe_id
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeDataResp getRecipeData(long yb_user_id, long recipe_id, String clientVersion) throws Exception
	{
		if (0 ==recipe_id || 0 == yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			int isDownload = snsRecipeDao.isDownload(yb_user_id, recipe_id);
			
			if (0==isDownload){//用户没下载过食谱
				//检查用户是否已购买此菜谱
				RecipeSellInfo recipeSellInfo = recipeDao.findRecipeSellInfo(recipe_id);
				if (VersionManager.greaterEquals(clientVersion, VersionConsts.V6_1)||recipeSellInfo.getRecipe_price()>0){//新版本6-1之后判断是否购买,之前版本价格大于0的也要判断
					if (yb_user_id != recipeSellInfo.getYb_user_id()){//如果当前用户不是作者本人，判断是否购买
						boolean buyed = tradeRecordServcie.findRecipeBuyStatus(yb_user_id, recipe_id);
						if (!buyed)
						{
								throw new YiBakerException(RetEnum.RECIPE_NOTPAY_FAIL);					
						}
					}
				} 
			}
			
			//下载菜谱数据
			RecipeDataResp data = recipeDao.getRecipeData(recipe_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			if(0==isDownload){
				//记录下载状态
				DownloadRecipeReq downloadRecipeReq = new DownloadRecipeReq();
				downloadRecipeReq.setRecipe_id(recipe_id);
				downloadRecipeReq.setYb_user_id(yb_user_id);
				downloadRecipeReq.setIs_download(SNSOp.DOWNLOADED);
				downloadRecipeReq.setDownload_time(System.currentTimeMillis());
				snsRecipeDao.insertUpdateDownload(downloadRecipeReq);
				
				//发送站内消息
				try
				{
					SNSMsgInfo msgInfo = new SNSMsgInfo();
					msgInfo.setFrom_user_id(yb_user_id);
					msgInfo.setMsg_type(MsgType.MSG_501);
					msgInfo.setSend_type(MsgSendType.P2P);
					msgInfo.setSrc_id(recipe_id);
					snsMsgSender.sendSNSMsg(0, MsgGroup.DOWNLOAD, msgInfo);
				} catch (Exception e)
				{
					log.debug(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg());	
					e.printStackTrace();
				}
			}
			
			
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETDATA_FAIL);					
		}
	}

	/**
	 * 购买并下载菜谱数据
	 * @param recipe_id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional
	public YibakerResp buyRecipeData(long yb_user_id, long recipe_id) throws Exception
	{
		if (0 ==recipe_id || 0 == yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		boolean buyed = tradeRecordServcie.findRecipeBuyStatus(yb_user_id, recipe_id);
		if(buyed){
			throw new YiBakerException(RetEnum.RECIPE_PAID_FAIL);		
		}
		
		try
		{
			//查询菜谱定价
			int recipe_price = recipeDao.getRecipePrice(recipe_id);
			
			
			//TODO 支付金币
			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>buy recipe price:" + recipe_price);
			
			//下载菜谱数据
			RecipeDataResp data = recipeDao.getRecipeData(recipe_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			//记录下载状态
			DownloadRecipeReq downloadRecipeReq = new DownloadRecipeReq();
			downloadRecipeReq.setRecipe_id(recipe_id);
			downloadRecipeReq.setYb_user_id(yb_user_id);
			downloadRecipeReq.setIs_download(SNSOp.DOWNLOADED);
			downloadRecipeReq.setDownload_time(System.currentTimeMillis());
			snsRecipeDao.insertUpdateDownload(downloadRecipeReq);
			
			//保存交易记录
		    tradeRecordServcie.createTradeRecord(yb_user_id, recipe_id);
			
			//发送站内消息
			try
			{
				SNSMsgInfo msgInfo = new SNSMsgInfo();
				msgInfo.setFrom_user_id(yb_user_id);
				msgInfo.setMsg_type(MsgType.MSG_501);
				msgInfo.setSend_type(MsgSendType.P2P);
				msgInfo.setSrc_id(recipe_id);
				snsMsgSender.sendSNSMsg(0, MsgGroup.DOWNLOAD, msgInfo);
			} catch (Exception e)
			{
				log.debug(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg());	
				e.printStackTrace();
			}
			YibakerResp yibakerResp = new YibakerResp();
			yibakerResp.setData(data);
			return yibakerResp;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETDATA_FAIL);					
		}
	}

	/**
	 * 发布菜谱评论
	 * @param recipeReply
	 * @return
	 * @throws Exception
	 */
	@Override
	public int createRecipeReply(RecipeReply recipeReply) throws Exception
	{
		if (null == recipeReply
				|| 0 == recipeReply.getYb_user_id()
				|| 0 == recipeReply.getRecipe_id()
				|| StringUtils.isBlank(recipeReply.getReply_content()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			recipeReply.setReply_time(System.currentTimeMillis());
			recipeReplyDao.create(recipeReply);
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_REPLY_FAIL);					
		}
		
		//发送站内消息
		try
		{
			SNSMsgInfo msgInfo = new SNSMsgInfo();
			msgInfo.setFrom_user_id(recipeReply.getYb_user_id());
			msgInfo.setSend_type(MsgSendType.P2P);
			msgInfo.setSrc_id(recipeReply.getRecipe_id());
			msgInfo.setMsg_title(recipeReply.getAdd_reply_content());//原评论内容
			msgInfo.setMsg_content(recipeReply.getReply_content());
			long to_user_id = recipeReply.getAdd_user_id();
			if (0 == to_user_id)
			{
				msgInfo.setMsg_type(MsgType.MSG_201);
			}else
			{
				msgInfo.setMsg_type(MsgType.MSG_204);
			}
			snsMsgSender.sendSNSMsg(to_user_id, MsgGroup.REPLY, msgInfo);
		} catch (Exception e)
		{
			log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
		}
		
		return 0;
	}

	/**
	 * 查询菜谱评论列表
	 * @param recipe_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeReplyListResp getRecipeReplyList(long recipe_id, long last_reply_id, int count) throws Exception
	{
		if (0 == recipe_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			RecipeReplyListResp replyList = recipeReplyDao.getList(recipe_id, last_reply_id, count);
			if (null == replyList)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = replyList.getReplylist().size();
			int total = recipeReplyDao.getTotal(recipe_id);
			replyList.setCount(size);
			replyList.setTotal(total);
			
			return replyList;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_REPLY_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 查询栏目菜谱列表
	 * @param column_id
	 * @param last_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeListResp getColumnRecipeList(int column_id, long last_id, int count) throws Exception
	{
		RecipeListResp recipeList = null;
		boolean noRedis = false;
		
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			//检查是否超出边界
			int total = columnRecipeRedisDao.getSize(column_id);
			if(0 != total && last_id >= total)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			recipeList = columnRecipeRedisDao.getList(column_id, last_id, count);
			
		} catch (JedisConnectionException e)
		{
			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>no redis, getlist from mysql");
			noRedis = true;
		}
		
		//redis中没有数据，查询mysql
		try
		{
			if (null == recipeList)
			{
				switch (column_id)
				{
					case ColumnInfo.HOMESLIDE:
						recipeList = columnRecipeDao.getSlideAll(ColumnInfo.HOMESLIDE_TOTAL);
						break;
	
					case ColumnInfo.LATEST:
						recipeList = columnRecipeDao.getLatestAll(ColumnInfo.HOT_TOTAL);
						break;
	
					case ColumnInfo.HOT:
						recipeList = columnRecipeDao.getHotAll(ColumnInfo.HOT_TOTAL);
						break;
	
					case ColumnInfo.TOP:
						recipeList = columnRecipeDao.getTopAll(ColumnInfo.TOP_TOTAL);
						break;
	
					default:
						break;
				}
				if (null == recipeList)
				{
					throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
				}else
				{
					if (!noRedis)
					{
						columnRecipeRedisDao.insertList(column_id, recipeList.getRecipelist());//保存栏目数据到redis中
					}
					int total = recipeList.getRecipelist().size();
					if(0 != total && last_id >= total)
					{
						throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
					}
					recipeList.setTotal(total);
					if((last_id + count) > total)
					{
						count = total - (int)last_id;
					}
					recipeList.setCount(count);
					recipeList.setRecipelist(recipeList.getRecipelist().subList((int)last_id, (int)last_id + count));
//					recipeList = columnRecipeRedisDao.getList(column_id, last_id, count);//TODO 再从redis中取出数据
				}
			}
			return recipeList;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.COLUMN_GETLIST_FAIL, e);					
		}
	}
	
	/**
	 * 查询分类菜谱列表
	 * @param channel_id
	 * @param last_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeListResp getChannelRecipeList(int channel_id, long last_id, int count) throws Exception
	{
		if (0 ==channel_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			RecipeListResp recipelist = channelRecipeDao.getList(channel_id, last_id, count);
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = recipelist.getRecipelist().size();
			int total = channelRecipeDao.getTotal(channel_id);
			recipelist.setCount(size);
			recipelist.setTotal(total);
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CHANNEL_GETLIST_FAIL, e);					
		}
	}
	/**
	 * 查询分类菜谱列表(必须有曲线)
	 * @param channel_id
	 * @param last_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeListResp getChannelRecipeListAndCurve(int channel_id, long last_id,int recipe_manuid, int count) throws Exception
	{
		if (0 ==channel_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			RecipeListResp recipelist = channelRecipeDao.getListAndCurve(channel_id, last_id,recipe_manuid, count);
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = recipelist.getRecipelist().size();
			int total = channelRecipeDao.getTotalandCurve((channel_id));
			recipelist.setCount(size);
			recipelist.setTotal(total);
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CHANNEL_GETLIST_FAIL, e);					
		}
	}
	
	/**
	 * 搜索菜谱(lucene搜索)
	 * @param keyword
	 * @param last_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeListResp searchRecipeList(String keyword, long last_id, int count) throws Exception
	{
		if (StringUtils.isBlank(keyword))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}

			List<Long> searcherIds = this.searcherService.searcherRecipeId(keyword);
			
			if (0 == searcherIds.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}
			List<Long> ids = recipeDao.filterIds(searcherIds);
//			log.debug(">>>>>>>>>>>>>>>>>>>ids:" + ids);
			if (null == ids || last_id >= ids.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}
			
			// 筛选id
			int end_index = (int)(last_id + count);
			List<Long> subList = ids.subList((int)last_id, end_index > ids.size() ? ids.size() : end_index);

			if (subList.size() == 0)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}

			RecipeListResp recipelist = recipeDao.searchList(subList);
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = recipelist.getRecipelist().size();
			
			log.debug(">>>>>>>>>>>>>>>>>>getSearchTotal:" + ids.size());
			recipelist.setCount(size);
			recipelist.setTotal(ids.size());
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SEARCH_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 发布作品
	 * @param recipeWork
	 * @return
	 * @throws Exception
	 */
	@Transactional
	@Override
	public long createRecipeWork(RecipeWork recipeWork) throws Exception
	{
		if (null == recipeWork
				|| 0 == recipeWork.getRecipe_id()
				|| 0 == recipeWork.getYb_user_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			//TODO 同一用户是否可以重复上传作品？
			
			recipeWork.setCreate_time(System.currentTimeMillis());
			recipeWorkDao.create(recipeWork);
			
			CreateSubjectReq req = new CreateSubjectReq();
			req.setYb_user_id(recipeWork.getYb_user_id());
			req.setBoard_id(1);
			req.setSubject_content(recipeWork.getWork_content());
			req.setSubject_image(recipeWork.getWork_image());
			req.setNote(recipeWork.getRecipe_id()+"");
			
			subjectService.createSubject(req);
			return recipeWork.getWork_id();
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_WORK_UPLOAD_FAIL, e);					
		}
	}

	/**
	 * 查询作品详情
	 * @param work_id
	 * @return
	 * @throws Exception
	 */
	@Override
	public WorkDetailResp getWorkDetail(long yb_user_id, long work_id) throws Exception
	{
		if (0 ==work_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			WorkDetailResp data = recipeWorkDao.getWorkDetail(work_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			//查询作品我的社交信息
			SNSMyWork mysns = snsWorkDao.getSNSMyWork(yb_user_id, work_id);
			if (null != mysns) data.setMysns(mysns);
			
			return data;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.WORK_GETDETAIL_FAIL);					
		}
	}

	/**
	 * 查询作品列表
	 * @param recipe_id
	 * @param last_work_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public WorkListResp getRecipeWorkList(long yb_user_id, long to_user_id, long recipe_id, long last_work_id, int count) throws Exception
	{
		if (0 == to_user_id && 0 == recipe_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			WorkListResp worklist = new WorkListResp();
			worklist = recipeWorkDao.getList(yb_user_id, to_user_id, recipe_id, last_work_id, count);
			if (null == worklist 
					|| null == worklist.getWorklist() 
					|| 0 == worklist.getWorklist().size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = worklist.getWorklist().size();
			int total = recipeWorkDao.getTotal(to_user_id, recipe_id);
			worklist.setCount(size);
			worklist.setTotal(total);
			
			return worklist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.WORK_GETLIST_FAIL);					
		}
	}

	/**
	 * 发布作品评论
	 * @param workReply
	 * @return
	 * @throws Exception
	 */
	@Override
	public int createWorkReply(WorkReply workReply) throws Exception
	{
		if (null == workReply
				|| 0 == workReply.getWork_id()
				|| 0 == workReply.getYb_user_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			workReply.setReply_time(System.currentTimeMillis());
			workReplyDao.create(workReply);
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_WORK_REPLY_FAIL);					
		}
		
		//发送站内消息
		try
		{
			SNSMsgInfo msgInfo = new SNSMsgInfo();
			msgInfo.setFrom_user_id(workReply.getYb_user_id());
			msgInfo.setSend_type(MsgSendType.P2P);
			msgInfo.setSrc_id(workReply.getWork_id());
			msgInfo.setMsg_title(workReply.getAdd_reply_content());//原评论内容
			msgInfo.setMsg_content(workReply.getReply_content());
			long to_user_id = workReply.getAdd_user_id();
			if (0 == to_user_id)
			{
				msgInfo.setMsg_type(MsgType.MSG_202);
			}else
			{
				msgInfo.setMsg_type(MsgType.MSG_205);
			}
			snsMsgSender.sendSNSMsg(to_user_id, MsgGroup.REPLY, msgInfo);
		} catch (Exception e)
		{
			log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
		}
		
		return 0;
	}

	/**
	 * 查询作品评论列表
	 * @param recipe_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public WorkReplyListResp getWorkReplyList(long work_id, long last_reply_id, int count) throws Exception
	{
		if (0 == work_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			WorkReplyListResp replyList = workReplyDao.getList(work_id, last_reply_id, count);
			if (null == replyList)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = replyList.getReplylist().size();
			int total = workReplyDao.getTotal(work_id);
			replyList.setCount(size);
			replyList.setTotal(total);
			
			return replyList;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.WORK_REPLY_GETLIST_FAIL);					
		}
	}
	
	
	/**
	 * 发布专题评论
	 * @param topicReply
	 * @return
	 * @throws Exception
	 */
	@Override
	public int createTopicReply(TopicReply topicReply) throws Exception
	{
		if (null == topicReply
				|| 0 == topicReply.getTopic_id()
				|| 0 == topicReply.getYb_user_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			topicReply.setReply_time(System.currentTimeMillis());
			topicReplyDao.create(topicReply);
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_TOPIC_REPLY_FAIL);					
		}
		
		//发送站内消息
		try
		{
			SNSMsgInfo msgInfo = new SNSMsgInfo();
			msgInfo.setFrom_user_id(topicReply.getYb_user_id());
			msgInfo.setSend_type(MsgSendType.P2P);
			msgInfo.setSrc_id(topicReply.getTopic_id());
			msgInfo.setMsg_title(topicReply.getAdd_reply_content());//原评论内容
			msgInfo.setMsg_content(topicReply.getReply_content());
			long to_user_id = topicReply.getAdd_user_id();
			if (0 == to_user_id)
			{
				msgInfo.setMsg_type(MsgType.MSG_208);
			}else
			{
				msgInfo.setMsg_type(MsgType.MSG_207);
			}
			snsMsgSender.sendSNSMsg(to_user_id, MsgGroup.REPLY, msgInfo);
		} catch (Exception e)
		{
			log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
		}
		
		return 0;
	}
	
	/**
	 * 查询作品评论列表
	 * @param recipe_id
	 * @param last_reply_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public TopicReplyListResp getTopicReplyList(long topic_id, long last_reply_id, int count) throws Exception
	{
		if (0 == topic_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}
			
			TopicReplyListResp replyList = topicReplyDao.getList(topic_id, last_reply_id, count);
			if (null == replyList)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = replyList.getReplylist().size();
			int total = topicReplyDao.getTotal(topic_id);
			replyList.setCount(size);
			replyList.setTotal(total);
			
			return replyList;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.TOPIC_REPLY_GETLIST_FAIL);					
		}
	}
	
	/**
	 * 查询专题列表
	 * @param last_topic_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public TopicListResp getTopicList(long last_topic_id, int count) throws Exception
	{
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			List<RecipeTopic> topiclist = recipeTopicDao.getTopicList(last_topic_id, count);
			if (null == topiclist || 0 == topiclist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			TopicListResp resp = new TopicListResp();
			resp.setTopiclist(topiclist);
			int size = topiclist.size();
			int total = recipeTopicDao.getTotal();
			resp.setCount(size);
			resp.setTotal(total);

			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.TOPIC_GETLIST_FAIL);					
		}
	}

	/**
	 * 查询专题详情
	 * @param topic_id
	 * @return
	 * @throws Exception
	 */
	@Override
	public TopicDetailResp getTopicDetail(long yb_user_id, long topic_id) throws Exception
	{
		if (0 ==topic_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			TopicDetailResp data = recipeTopicDao.getTopicDetail(topic_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			//查询专题我的社交信息
			SNSMyTopic mysns = snsTopicDao.getSNSMyTopic(yb_user_id, topic_id);
			if (null != mysns) data.setMysns(mysns);
			
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.TOPIC_GETDETAIL_FAIL);					
		}
	}

	/**
	 * 获取所有菜谱
	 * 
	 * @return RecipeListResp
	 */
	@Override
	public List<Recipe> getSearchAll()
	{
		return this.recipeDao.getSearchAll();
	}

	@Transactional
	@Override
	public YibakerResp createRecipeWorkEx(RecipeWork recipeWork) throws Exception
	{
		// TODO Auto-generated method stub
		RecipeWork data = new RecipeWork();
		data.setWork_id(this.createRecipeWork(recipeWork));
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(data);
		return yibakerResp;
	}

	@Transactional
	@Override
	public YibakerResp createWorkReplyEx(WorkReply workReply) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode = this.createWorkReply(workReply);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}
	
	@Transactional
	@Override
	public YibakerResp createTopicReplyEx(TopicReply topicReply) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode = this.createTopicReply(topicReply);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}
	

	@Transactional
	@Override
	public YibakerResp createRecipeReplyEx(RecipeReply recipeReply) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode = this.createRecipeReply(recipeReply);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}

	
	@Override
	public SystemParam findRecipeBakerValues() throws Exception
	{
		// TODO Auto-generated method stub
		SystemParam  systemParam  =  systemParamDao.findOneByKey(SystemParamEnum.RECIPE_BAKER_VALUES.name());
		if(null == systemParam){
			systemParam = new SystemParam();
			systemParam.setParam_value(SystemParamEnum.RECIPE_BAKER_VALUES.param_value());
		}
		
		return systemParam;
	}

	@Override
	public ChannelRecipeResp findChannelsWithRecipes(int brand_channel_id,
			int bakemode_channel_pid) {
		// TODO Auto-generated method stub
		if (0 == brand_channel_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		List<RecipeChannelEx> channels = channelRecipeDao.findChannelsWithRecipes(brand_channel_id, bakemode_channel_pid);
		ChannelRecipeResp channelRecipeResp = new ChannelRecipeResp();
		channelRecipeResp.setChannels(channels);
		return channelRecipeResp;
	}

	/**
	 * 查询父类对应的食谱子分类
	 * 
	 * @return
	 */
	@Override
	public ChannelListResp findChannels(int channel_pid)
	{
		if (0 > channel_pid)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);
		}
		List<RecipeChannel> channels = channelRecipeDao.findChannels(channel_pid);
		ChannelListResp channelListResp = new ChannelListResp();
		channelListResp.setChannellist(channels);
		if (null != channels && channels.size() > 0)
		{
			channelListResp.setCount(channels.size());
			channelListResp.setTotal(channels.size());
		}

		return channelListResp;
	}

	/**
	 * 按系统类型查询分类菜谱列表（可以指定系统默认食谱类型 type：1 系统默认；2非系统默认；3所有）
	 * 
	 * @param channel_id
	 * @param last_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	@Override
	public RecipeExListResp getCategoryChannelRecipeList(int brand_channel_id, int channel_id, int type, long last_id, int count)
	{
		if (0 == channel_id && 0 == brand_channel_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);
		}

		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;// 默认分页数为10
			}
			int size = 0;
			int total = 0;
			RecipeExListResp recipelist = new RecipeExListResp();
			List<RecipeChannelEx> list = channelRecipeDao.getTypeList(brand_channel_id, channel_id, type, last_id, count);
			if (null == list || list.size() == 0)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}
			if (list.size() > 0 && null != list.get(0) && null != list.get(0).getRecipes())
			{
				recipelist.setRecipelist(list.get(0).getRecipes());
				size = recipelist.getRecipelist().size();
				total = channelRecipeDao.getTypeTotal(brand_channel_id, channel_id, type);
			}


			recipelist.setCount(size);
			recipelist.setTotal(total);

			return recipelist;
		}
		catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.CHANNEL_GETLIST_FAIL, e);
		}
	}
	
	@Override
	public List<Recipe> getSearch() {
		return this.recipeDao.getSearch();
	}

	@Override
	public RecipeListResp ybsearchRecipeList(String keyword, long last_id,
			int count) throws Exception {
		
		if (StringUtils.isBlank(keyword))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = PageInfo.DEFAULT_COUNT;//默认分页数为10
			}

			List<Long> searcherIds = this.searcherService.searcherRecipeId3(keyword);
			
			if (0 == searcherIds.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}
			List<Long> ids = recipeDao.filterIds3(searcherIds);
//			log.debug(">>>>>>>>>>>>>>>>>>>ids:" + ids);
			if (null == ids || last_id >= ids.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}
			
			// 筛选id
			int end_index = (int)(last_id + count);
			List<Long> subList = ids.subList((int)last_id, end_index > ids.size() ? ids.size() : end_index);

			if (subList.size() == 0)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}

			RecipeListResp recipelist = recipeDao.searchList3(subList);
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = recipelist.getRecipelist().size();
			
			log.debug(">>>>>>>>>>>>>>>>>>getSearchTotal:" + ids.size());
			recipelist.setCount(size);
			recipelist.setTotal(ids.size());
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SEARCH_GETLIST_FAIL);					
		}
	}
}

