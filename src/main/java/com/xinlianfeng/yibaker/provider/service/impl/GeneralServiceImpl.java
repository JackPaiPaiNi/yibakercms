/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:GeneralServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月6日
 *   Author: mozheyuan(mozheyuan@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.csource.common.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.xinlianfeng.server.common.fdfs.FDFSOperateProxy;
import com.xinlianfeng.server.common.fdfs.FDFSStorageCallback;
import com.xinlianfeng.server.common.fdfs.FDFSStorageClient;
import com.xinlianfeng.yibaker.common.constant.ADType;
import com.xinlianfeng.yibaker.common.constant.ColumnInfo;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.Advert;
import com.xinlianfeng.yibaker.common.entity.ClassRoom;
import com.xinlianfeng.yibaker.common.entity.Feedback;
import com.xinlianfeng.yibaker.common.entity.RecipeChannel;
import com.xinlianfeng.yibaker.common.entity.RecipeTopic;
import com.xinlianfeng.yibaker.common.entity.Report;
import com.xinlianfeng.yibaker.common.entity.UserBriefInfo;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.req.FileUploadReq;
import com.xinlianfeng.yibaker.common.resp.FileUploadResp;
import com.xinlianfeng.yibaker.common.resp.HomePageResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;
import com.xinlianfeng.yibaker.common.service.GeneralService;
import com.xinlianfeng.yibaker.provider.component.ColumnRecipeRedisDao;
import com.xinlianfeng.yibaker.provider.dao.AdvertDao;
import com.xinlianfeng.yibaker.provider.dao.ChannelRecipeDao;
import com.xinlianfeng.yibaker.provider.dao.ClassRoomDao;
import com.xinlianfeng.yibaker.provider.dao.ColumnRecipeDao;
import com.xinlianfeng.yibaker.provider.dao.FeedbackDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeTopicDao;
import com.xinlianfeng.yibaker.provider.dao.ReportDao;
import com.xinlianfeng.yibaker.provider.dao.UserExpertDao;

/**
 * @Description: 基础服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月6日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("generalService")
public class GeneralServiceImpl implements GeneralService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("#{configProperties['system.file.host']}")
	private String fileHost;

	@Value("#{configProperties['system.file.port']}")
	private int filePort;

	@Value("#{configProperties['system.file.group']}")
	private String fileGroup;

	@Autowired
	private AdvertDao advertDao;

	@Autowired
	private FeedbackDao feedbackDao;

	@Autowired
	private ReportDao reportDao;

	@Autowired
	private ColumnRecipeDao columnRecipeDao;

	@Autowired
	private ColumnRecipeRedisDao columnRecipeRedisDao;

	@Autowired
	private RecipeTopicDao recipeTopicDao;

	@Autowired
	private UserExpertDao userExpertDao;
	
	@Autowired
	private ClassRoomDao classRoomDao;

	@Autowired
	private ChannelRecipeDao channelRecipeDao;

	/**
	 * 查询首页信息
	 * @return
	 * @throws Exception
	 */
	public HomePageResp getHomePageInfo() throws Exception
	{
		HomePageResp homepage = new HomePageResp();
		
		//查询redis中首页幻灯片信息
		RecipeListResp recipeList = null;
		RecipeChannel tzchannel = null;
		boolean noRedis = false;
		try
		{
			int total = (int)columnRecipeRedisDao.getSize(ColumnInfo.HOMESLIDE);
			recipeList = columnRecipeRedisDao.getList(ColumnInfo.HOMESLIDE, 0, total);
			
		} catch (Exception e)
		{
			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>no redis, getlist from mysql");
			noRedis = true;
		}
		
		try
		{
			//redis中没有数据，查询mysql中首页幻灯片信息
			if (null == recipeList)
			{
				recipeList = columnRecipeDao.getSlideAll(ColumnInfo.HOMESLIDE_TOTAL);
				
				if (null == recipeList)
				{
					throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
				}else
				{
					if (!noRedis)
					{
						columnRecipeRedisDao.insertList(ColumnInfo.HOMESLIDE, recipeList.getRecipelist());//保存栏目数据到redis中
					}
				}
			}
			homepage.setSlide(recipeList.getRecipelist());
		
			//查询广告信息
			Advert ad = advertDao.getInfo(ADType.HOMEPAGE);
			if(null != ad)
				homepage.setAd(ad);
			
			//查询首页烘焙课堂信息
			List<ClassRoom>classroomlist = classRoomDao.getList(0, 1, 0);
			if(null != classroomlist && !classroomlist.isEmpty()){
				homepage.setClassroom(classroomlist.get(0));
				int crTotal = classRoomDao.getTotal(0);
				homepage.getClassroom().setNote(crTotal+"");
			}

			//查询首页专题信息
			List<RecipeTopic>topiclist = recipeTopicDao.getTopicList(0, 2);
			if(null != topiclist && !topiclist.isEmpty())
				homepage.setTopiclist(topiclist);
			
			//查询烘焙达人信息
			List<UserBriefInfo> expertlist = userExpertDao.getList(10);
			if(null != expertlist && !expertlist.isEmpty())
				homepage.setExpertlist(expertlist);
			
			// 查询天倬发帅食谱
			tzchannel = channelRecipeDao.findBrandChannel(202);
			if (null != tzchannel)
			{
				homepage.setTzchannel(tzchannel);
			}

		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.GENERAL_HOMEPAGE_FAIL, e);					
		}

		return homepage;
	}
	
	/**
	 * 意见反馈
	 * @param feedback
	 * @return
	 * @throws Exception
	 */
	public int feedback(Feedback feedback) throws Exception
	{
		if (null == feedback || StringUtils.isBlank(feedback.getFb_content()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			feedback.setFb_time(System.currentTimeMillis());
			feedbackDao.create(feedback);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.GENERAL_FEEDBACK_FAIL);					
		}
		return 0;
	}

	/**
	 * 上传文件
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public FileUploadResp uploadFile(final FileUploadReq file) throws Exception
	{
		if (null == file)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			String fileId = FDFSOperateProxy.invokeStorage(new FDFSStorageCallback<String>()
					{
						public String execute(FDFSStorageClient client) throws Exception
						{
				            //设置元信息   
				            NameValuePair[] metaList = new NameValuePair[3];   
				            metaList[0] = new NameValuePair("fileName", file.getFile_name());   
				            metaList[1] = new NameValuePair("fileExtName", file.getExt_name());   
				            metaList[2] = new NameValuePair("fileLength", file.getFile_size());   
				            //上传文件
							return  client.upload_file1(fileGroup, file.getFile_buffer(), file.getExt_name(), metaList);
						}
					});
			
			FileUploadResp resp = new FileUploadResp();
//			String fileUrl =  FDFSOperateProxy.getHttpUrl(fileHost, filePort, fileId);
//			log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>file url:" + fileUrl);
//			resp.setFile_path(fileUrl);
			resp.setFile_path("/" + fileId);//兼容四期路径
			return resp;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new YiBakerException(RetEnum.GENERAL_FILEUPLOAD_FAIL);					
		}
	}
	
	/**
	 * 举报
	 * @param report
	 * @return
	 * @throws Exception
	 */
	public int report(Report report) throws Exception
	{
		if (null == report 
				|| 0 == report.getSrc_id()
				|| 0 == report.getSrc_type()
				|| StringUtils.isBlank(report.getFrom_ip_addr())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			report.setReport_time(System.currentTimeMillis());
			reportDao.create(report);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.GENERAL_REPORT_FAIL);					
		}
		return 0;
		
	}
	/***
	 * 获取广告
	 */
	@Override
	public List<Advert> getAd(int type, int count) throws Exception {
		// TODO Auto-generated method stub
		return advertDao.getList(type, count);
	}

}

