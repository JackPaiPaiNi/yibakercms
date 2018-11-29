/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * Project:yibaker-provider
 * Package name:com.xinlianfeng.yibaker.provider.service.impl
 * File name:UserCenterServiceImpl.java
 *Version:2.0
 *
 * Description:
 *    TODO
 *
 * History:
 * 1.Date: 2015年10月8日
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

import com.xinlianfeng.yibaker.common.constant.MyRecipeType;
import com.xinlianfeng.yibaker.common.constant.PageInfo;
import com.xinlianfeng.yibaker.common.constant.RankEnum;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.entity.BakerAccount;
import com.xinlianfeng.yibaker.common.entity.Mission;
import com.xinlianfeng.yibaker.common.entity.RecipeBriefInfo;
import com.xinlianfeng.yibaker.common.entity.SNSUser;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.UserLevelInfo;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.resp.FollowDetailResp;
import com.xinlianfeng.yibaker.common.resp.FollowListResp;
import com.xinlianfeng.yibaker.common.resp.RecipeBriefListResp;
import com.xinlianfeng.yibaker.common.resp.RecipeDataResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;
import com.xinlianfeng.yibaker.common.resp.TopicListResp;
import com.xinlianfeng.yibaker.common.resp.UserDetailResp;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.UserCenterService;
import com.xinlianfeng.yibaker.provider.dao.BakerAccountDao;
import com.xinlianfeng.yibaker.provider.dao.MissionDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeTopicDao;
import com.xinlianfeng.yibaker.provider.dao.SNSUserDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;

/**
 * @Description: 用户中心服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年10月8日
 * @author mozheyuan (mozheyuan@topeastic.com)
 */
@Service("userCenterService")
public class UserCenterServiceImpl implements UserCenterService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private SNSUserDao snsUserDao;

	@Autowired
	private RecipeDao recipeDao;

	@Autowired
	private RecipeTopicDao recipeTopicDao;
	
	@Autowired
	private BakerAccountDao bakerAccountDao;
	
	@Autowired
	private MissionDao missionDao;

	/**
	 * 查询我的信息（有授权）
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public UserDetailResp getMyInfo(long yb_user_id) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			UserDetailResp data = userInfoDao.getMyInfo(yb_user_id);
			if(null != data){
				BakerAccount bakerAccount = bakerAccountDao.findOne(yb_user_id);
				if (null != bakerAccount){
					data.getSnscount().setCoin_count(bakerAccount.getBalance());
				}
				data.getSnscount().setMission_count(this.findMissionCount(yb_user_id));
			}
			
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_GETMYINFO_FAIL);					
		}
	}
	
	private int findMissionCount(long yb_user_id){
		List<Mission> onceMissions = missionDao.findOnceMissions(yb_user_id);
		List<Mission> dailyMissions = missionDao.findDailyMissions(yb_user_id);
		int unfullfilCount = 0;
		for(Mission mission:onceMissions){
			if(mission.getFullfil_status()==(byte)0){
				unfullfilCount++;
			}
		}
		for(Mission mission:dailyMissions){
			if(mission.getFullfil_status()==(byte)0){
				unfullfilCount++;
			}
		}
		return unfullfilCount;
	}
	
	/**
	 * 查询她的信息（无授权）
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public UserDetailResp getOtherInfo(long yb_user_id, long to_user_id) throws Exception
	{
		if (0 ==to_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			UserDetailResp data = userInfoDao.getOtherInfo(to_user_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			//是否关注
			int is_follow = snsUserDao.isFollow(yb_user_id, to_user_id);
			SNSUser mysns = new SNSUser();
			mysns.setIs_follow(is_follow);
			data.setMysns(mysns);
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_GETOTHERINFO_FAIL);					
		}
	}
	
	/**
	 * 查询好友列表
	 * @param yb_user_id
	 * @param fried_type
	 * @param last_time
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public FollowListResp getFriendList(long yb_user_id, long user_id, int friend_type, long last_time, int count) throws Exception
	{
		if (0 == user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			List<FollowDetailResp> followlist = userInfoDao.getFriendList(yb_user_id, user_id, friend_type, last_time, count);
			if (null == followlist || 0 == followlist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			if (0 != yb_user_id)
			{
				for(FollowDetailResp followDetailResp:followlist)
				{
					followDetailResp.setIs_myfollow(snsUserDao.isFollow(yb_user_id, followDetailResp.getYb_user_id()));
				}
			}
			
			FollowListResp resp = new FollowListResp();
			int size = followlist.size();
			int total = userInfoDao.getFriendTotal(user_id, friend_type);
			resp.setCount(size);
			resp.setTotal(total);
			resp.setFollowlist(followlist);
		
			return resp;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_GETFOLLOWLIST_FAIL);					
		}
	}
	
	/**
	 * 查询我发布的菜谱列表（有授权）
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public RecipeListResp getMyRecipe(long yb_user_id, long last_time, int count) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			RecipeListResp recipelist = recipeDao.getMyList(yb_user_id, last_time, count);//我的发布
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = recipelist.getRecipelist().size();
			int total = recipeDao.getMyTotal(yb_user_id);//我的发布总数
			recipelist.setCount(size);
			recipelist.setTotal(total);
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETMYLIST_FAIL);					
		}
	}
	
	/**
	 * 查询我认证失败的菜谱列表(有授权)
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public List<RecipeBriefInfo> getMyFailRecipeList(long yb_user_id) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			List<RecipeBriefInfo> recipelist = recipeDao.getMyFailList(yb_user_id, PageInfo.DEFAULT_COUNT);//默认只保留最近10个菜谱
			
			if (null == recipelist || 0 == recipelist.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETMYLIST_FAIL);					
		}
	}
	
	/**
	 * 下载我认证失败的菜谱数据
	 * @param yb_user_id
	 * @param recipe_id
	 * @return
	 * @throws Exception
	 */
	public RecipeDataResp getFailRecipeData(long yb_user_id, long recipe_id) throws Exception
	{
		if (0 ==recipe_id || 0 == yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			//下载菜谱数据
			RecipeDataResp data = recipeDao.getMyFailData(yb_user_id, recipe_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETDATA_FAIL);					
		}
	}

	/**
	 * 删除我认证失败的菜谱
	 * @param yb_user_id
	 * @param recipe_id
	 * @return
	 * @throws Exception
	 */
	public int deleteFailRecipe(long yb_user_id, long recipe_id) throws Exception
	{
		if (0 ==yb_user_id || 0 == recipe_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			recipeDao.deleteMyFailRecipe(yb_user_id, recipe_id);
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_DELETE_FAIL);					
		}
		return 0;
	}

	/**
	 * 查询我收藏/下载的菜谱列表（有授权）
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public RecipeBriefListResp getMySNSRecipe(int type, long yb_user_id, long last_time, int count) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			RecipeBriefListResp recipelist = null;
			int size, total = 0;
			switch (type)
			{
				case MyRecipeType.COLLECT:
					recipelist = recipeDao.getMyCollectList(yb_user_id, last_time, count);//我的收藏
					if (null != recipelist)
					{
						total = recipeDao.getMyCollectTotal(yb_user_id);//我的收藏总数
					}
					break;
	
				case MyRecipeType.DOWNLOAD:
					recipelist = recipeDao.getMyDownLoadList(yb_user_id, last_time, count);//我的下载
					if (null != recipelist)
					{
						total = recipeDao.getMyDownLoadTotal(yb_user_id);//我的下载总数
					}
					break;
	
				default:
					break;
			}
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			size = recipelist.getRecipelist().size();
			recipelist.setCount(size);
			recipelist.setTotal(total);
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETMYLIST_FAIL);					
		}
	}
	
	/**
	 * 查询她的发布列表（无授权）
	 * @param yb_user_id
	 * @param last_recipe_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public RecipeListResp getOtherRecipe(long yb_user_id, long last_time, int count) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			RecipeListResp recipelist = recipeDao.getOtherList(yb_user_id, last_time, count);
			if (null == recipelist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = recipelist.getRecipelist().size();
			int total = recipeDao.getOtherListTotal(yb_user_id);
			recipelist.setCount(size);
			recipelist.setTotal(total);
			
			return recipelist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETOTHERLIST_FAIL);					
		}
	}
	
	/**
	 * 查询我收藏的专题列表（有授权）
	 * @param yb_user_id
	 * @param last_topic_id
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public TopicListResp getMyTopic(long yb_user_id, long last_time, int count) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		if (0 == count)
		{
			count = PageInfo.DEFAULT_COUNT;//默认分页数为10
		}
		
		try
		{
			TopicListResp topiclist = recipeTopicDao.getMyCollectList(yb_user_id, last_time, count);
			if (null == topiclist)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			int size = topiclist.getTopiclist().size();
			int total = recipeTopicDao.getMyCollectTotal(yb_user_id);
			topiclist.setCount(size);
			topiclist.setTotal(total);
			
			return topiclist;
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETOTHERLIST_FAIL);					
		}
	}

	@Override
	public YibakerResp findUserLevelInfo(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		if(null == yb_user_id){
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);	
		}
		UserLevelInfo userLevelInfo = this.userInfoDao.findUserLevelInfo(yb_user_id);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(userLevelInfo);
		return yibakerResp;
	}

	@Override
	public void updateUserLevelInfo(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		BakerAccount bakerAccount = bakerAccountDao.findOne(yb_user_id);
		UserInfo userInfo = this.userInfoDao.findOne(yb_user_id);
		int level_id = userInfo.getLevel_id();
		int new_level_id = RankEnum.gain_rank(bakerAccount.getAccum_income(), level_id);
		if(0==new_level_id){//没有升级
			return;
		}
		userInfo.setLevel_id(new_level_id);
		userInfoDao.updateUserLevel(userInfo);
	}

	@Override
	public YibakerResp findUserBakerBalance(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		BakerAccount  bakerAccount = bakerAccountDao.findOne(yb_user_id);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(bakerAccount.getBalance());
		return yibakerResp;
	}

	@Override
	public YibakerResp findUserInfoByHxUserAcccount(String hx_user_account)
	{
		long hx_id = Long.parseLong(hx_user_account);
		UserInfo  userInfo = userInfoDao.findUserInfoByHxUserAcccount(hx_id);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(userInfo);
		
		return yibakerResp;
	}

	
	@Override
	public RecipeBriefListResp getOptionalRecipe(int type, long yb_user_id, int device_type, long last_time, int count)
			throws Exception {
		// TODO Auto-generated method stub
		if(0==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);
		}
		
		if(0==count)
		{
			count=PageInfo.DEFAULT_COUNT;
		}
		
		/*type默认为1,用于区分下载还是收藏*/
		if(type<=0)
		{
			type=1;
		}
		
		/*device_type默认为1*/
		if(device_type<=0)
		{
			device_type=1;
		}		
		try{
			RecipeBriefListResp recipeList=null;
			int size,total=0;
			recipeList=recipeDao.getOptionalList(yb_user_id, last_time, device_type, count);
			if(null==recipeList)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);
			}
		    size=recipeList.getRecipelist().size();
		    total=recipeDao.getOptionalTotal(yb_user_id, device_type);
		    recipeList.setCount(size);
		    recipeList.setTotal(total);
		return recipeList;
		}catch(DataAccessException e)
		{
			throw new YiBakerException(RetEnum.RECIPE_GETMYLIST_FAIL);
		}
	}

}

