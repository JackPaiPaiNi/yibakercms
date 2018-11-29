/*******************************************************************************
 * Copyright (c) 2003-2015,深圳市新联锋科技有限公司
 * File name:PersonServiceImpl.java   Package name:com.xinlianfeng.yibaker.provider.service.impl
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
package com.xinlianfeng.yibaker.provider.service.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.csource.common.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.xinlianfeng.server.common.fdfs.FDFSOperateProxy;
import com.xinlianfeng.server.common.fdfs.FDFSStorageCallback;
import com.xinlianfeng.server.common.fdfs.FDFSStorageClient;
import com.xinlianfeng.yibaker.common.constant.RedisUserAuth;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.constant.SMSOption;
import com.xinlianfeng.yibaker.common.entity.SMSCheckCode;
import com.xinlianfeng.yibaker.common.entity.User;
import com.xinlianfeng.yibaker.common.entity.UserEasemob;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.UserRegInfo;
import com.xinlianfeng.yibaker.common.entity.UserTPInfo;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.req.FileUploadReq;
import com.xinlianfeng.yibaker.common.req.ResetPasswdReq;
import com.xinlianfeng.yibaker.common.resp.EasemobResp;
import com.xinlianfeng.yibaker.common.resp.EasemobUser;
import com.xinlianfeng.yibaker.common.resp.FileUploadResp;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.MissionService;
import com.xinlianfeng.yibaker.common.service.UserService;
import com.xinlianfeng.yibaker.common.util.EasemobUtil;
import com.xinlianfeng.yibaker.provider.component.EasemobService;
import com.xinlianfeng.yibaker.provider.component.RedisBeanDao;
import com.xinlianfeng.yibaker.provider.component.YiBakerUtil;
import com.xinlianfeng.yibaker.provider.dao.UserEasemobDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;
import com.xinlianfeng.yibaker.provider.dao.UserRegInfoDao;
import com.xinlianfeng.yibaker.provider.dao.UserTPInfoDao;

/**
 * @Description:用户服务实现类
 * @Company: 深圳市新联锋科技有限公司
 * @Copyright: Copyright (c) 2003-2015
 * @version: V2.0
 * @date: 2015年9月22日
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Service("userService")
public class UserServiceImpl implements UserService
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("#{configProperties['system.file.group']}")
	private String fileGroup;

	@Autowired
	private UserRegInfoDao userRegInfoDao;

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private UserTPInfoDao userTPInfoDao;

	@Autowired
	private RedisBeanDao redisBeanDao;

	@Autowired
	private YiBakerUtil util;
	
	@Autowired
	private MissionService missionService;
	
	@Autowired
	private UserEasemobDao userEasemobDao;

	@Autowired
	private EasemobService  easemobService;
	
	private void checkSMSCode(String mobile, String reqCode, long timeout, String key)
	{
		boolean isBeanExist = redisBeanDao.isBeanExist(key, mobile);
		//校验码不存在
		if (!isBeanExist)
		{
			throw new YiBakerException(RetEnum.SMS_CHECK_FAIL);		
		}
		
		SMSCheckCode redisCode = redisBeanDao.getBean(key, mobile, SMSCheckCode.class);
		//校验码超时
		long nowTime = System.currentTimeMillis();
		if ((nowTime - redisCode.getCheck_time()) > timeout)
		{
//			redisBeanDao.delBean(RedisUserAuth.USER_MOBILE_CHECK, mobile);
			throw new YiBakerException(RetEnum.SMS_CHECK_TIMEOUT);		
		}
		
		//校验码错误
		if (!reqCode.equals(redisCode.getCheck_code()))
		{
			throw new YiBakerException(RetEnum.SMS_CHECK_FAIL);		
		}

		
	}
	
	/**
	 * 用户注册
	 * @param userInfo
	 * @return
	 * @throws Exception 
	 */
	@Transactional
	public int register(User user) throws Exception
	{
		if (null == user || StringUtils.isBlank(user.getCheck_code()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		String mobile = user.getUserreginfo().getMobile();
		String reqCode = user.getCheck_code();
		//判断校验码是否正确
		checkSMSCode(mobile, reqCode, SMSOption.USER_REG_TIME_OUT, RedisUserAuth.USER_MOBILE_CHECK);
		
		if (null == user.getUserinfo()||StringUtils.isEmpty(user.getUserinfo().getNickname())){
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);	
		}
		int cnt = userInfoDao.checkNickName(user.getUserinfo().getNickname());
		if (cnt>0){
			throw new YiBakerException(RetEnum.USER_REGISTER_NICKNAME_FAIL);
		}
		
		//增加用户
		try
		{
			//创建用户注册信息
			UserRegInfo userRegInfo = user.getUserreginfo();
			userRegInfo.setReg_time(System.currentTimeMillis());
			userRegInfo.setStatus(1);//用户状态可用
			userRegInfoDao.createUser(userRegInfo);
			
			//TODO 创建用户金币信息
			
			//创建用户基本信息
			UserInfo userInfo = user.getUserinfo();
			userInfo.setYb_user_id(userRegInfo.getYb_user_id());
			userInfo.setProto_tag((int)userRegInfo.getYb_user_id());//TODO 暂时操作员编号与一焙用户编号相同
			userInfo.setLevel_id(1);//TODO 需要根据金币数量计算本人等级
			//默认地区
			if (StringUtils.isBlank(userInfo.getArea()))
			{
				userInfo.setArea("广东省深圳市");
//				userInfo.setArea(StringUtils.EMPTY);
			}
			//默认生日
			if (StringUtils.isBlank(userInfo.getBirth()))
			{
				Date d=new Date();     
				SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");					
				userInfo.setBirth(f.format(d));
//				userInfo.setBirth(StringUtils.EMPTY);
			}
			//默认性别
//			if (0 == userInfo.getSex())
//			{
//				userInfo.setSex(2);
//			}
			//默认头像
			if (StringUtils.isBlank(userInfo.getPhoto()))
			{
				userInfo.setPhoto(StringUtils.EMPTY);
			}
			//默认签名
			if (StringUtils.isBlank(userInfo.getSignature()))
			{
//				userInfo.setSignature("正在烘焙的道路上努力奋进，没时间写签名...");
				userInfo.setSignature(StringUtils.EMPTY);
			}
			
			if(StringUtils.isEmpty(userInfo.getNickname())){
				userInfo.setNickname("无名氏");
			}

			userInfoDao.createUserDetail(userInfo);
			
			//创建环信帐号
			
			this.createUserEasemob(userRegInfo,  userInfo.getNickname());
			
		} 
		catch (DataAccessException e)
		{
			SQLException sqle = (SQLException) e.getCause(); 
			
			if(1062 == sqle.getErrorCode())
			{
				throw new YiBakerException(RetEnum.USER_REGISTER_NICKNAME_FAIL);//昵称已注册
			}else
			{
				throw new YiBakerException(RetEnum.USER_REGISTER_FAIL, e);		
			}
		}

		
		return 0;
	}
	
	/**
	 * 注册上传用户头像
	 * @param file
	 * @param checkCode
	 * @return
	 * @throws Exception
	 */
	public FileUploadResp uploadPhoto(final FileUploadReq file, String mobile, String checkCode) throws Exception
	{
		if (null == file)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		//判断校验码是否正确
		checkSMSCode(mobile, checkCode, SMSOption.USER_REG_TIME_OUT, RedisUserAuth.USER_MOBILE_CHECK);

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
			resp.setFile_path("/" + fileId);//兼容四期路径
			return resp;
		} catch (Exception e)
		{
			throw new YiBakerException(RetEnum.USER_UPLOAD_PHOTO_FAIL, e);					
		}
	}
	
	/**
	 * 检查昵称是否存在
	 * @param userInfo
	 * @return
	 * @throws Exception
	 */
	public int checkNickName(UserInfo userInfo) throws Exception
	{
		if (null == userInfo 
				|| StringUtils.isBlank(userInfo.getNickname()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			int isExist = userInfoDao.checkNickName(userInfo.getNickname());
			if (0 !=isExist)
			{
				throw new YiBakerException(RetEnum.USER_REGISTER_NICKNAME_FAIL);		
			}
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_CHECKNICKNAME_FAIL, e);					
		}
		
		return 0;
	}
	
	/**
	 * 用户登录
	 * @param userRegInfo
	 * @return
	 */
	public UserInfo login(UserRegInfo userRegInfo) throws Exception
	{
		if (null==userRegInfo
				|| (StringUtils.isBlank(userRegInfo.getMobile()) 
						&& StringUtils.isBlank(userRegInfo.getEmail())))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		//查询手机和密码是否匹配
		try
		{
			UserRegInfo retInfo = userRegInfoDao.checkPasswd(userRegInfo);
			
			if (null == retInfo)
			{
				throw new YiBakerException(RetEnum.LOGIN_MOBILE_PWD_ERROR);						
			}
			
			UserInfo userInfo = userInfoDao.findOne(retInfo.getYb_user_id());
			
			return userInfo;
			
		} catch (DataAccessException  e)
		{
			throw new YiBakerException(RetEnum.LOGIN_FAIL, e);		
		}
	}
	
	/**
	 * 第三方用户登录
	 * @param user
	 * @return
	 */
	@Transactional
	public User tpLogin(User user) throws Exception
	{
		if (null == user 
				|| null == user.getUsertpinfo() 
				|| null == user.getUserinfo()
				|| StringUtils.isBlank(user.getUsertpinfo().getOpen_id()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		UserInfo userInfo;
		UserRegInfo userreginfo;
		UserTPInfo usertpinfo;
		User data = new User();
		Long idObject;
		long yb_user_id;
		long nowTime = System.currentTimeMillis();
		int rnd = new java.util.Random().nextInt();//随机密码

		//TODO 向第三方平台，验证用户token
		
		//查询是否有关联的用户存在
		try
		{			
			idObject = userTPInfoDao.isExist(user.getUsertpinfo());
		}catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.LOGIN_FAIL,e);		
		}
		
		//第一次登录，创建一焙用户
		if (null == idObject)
		{
			//创建用户注册信息
			try
			{			
				userreginfo = new UserRegInfo() ;
				userreginfo.setPasswd(util.encodeByMD5(rnd + ""));
				userreginfo.setReg_time(nowTime);
				userreginfo.setStatus(1);//用户状态可用
				userRegInfoDao.createUser(userreginfo);
			}catch (DataAccessException e)
			{
				throw new YiBakerException(RetEnum.LOGIN_FAIL, e);		
			}
			
			yb_user_id = userreginfo.getYb_user_id();
			
			//TODO 创建用户金币信息
			
			//创建用户基本信息
			userInfo = user.getUserinfo();
			userInfo.setYb_user_id(yb_user_id);
			userInfo.setProto_tag((int)yb_user_id);//TODO 暂时操作员编号与一焙用户编号相同
			userInfo.setLevel_id(1);//TODO 需要根据金币数量计算本人等级
			//默认地区
			if (StringUtils.isBlank(userInfo.getArea()))
			{
				userInfo.setArea("广东省深圳市");
//				userInfo.setArea(StringUtils.EMPTY);
			}
			//默认生日
			if (StringUtils.isBlank(userInfo.getBirth()))
			{
				Date d=new Date();     
				SimpleDateFormat f=new SimpleDateFormat("yyyyMMdd");					
				userInfo.setBirth(f.format(d));
//				userInfo.setBirth(StringUtils.EMPTY);
			}
			//默认性别
//				if (0 == userInfo.getSex())
//				{
//					userInfo.setSex(2);
//				}
			//默认头像
			if (StringUtils.isBlank(userInfo.getPhoto()))
			{
				userInfo.setPhoto(StringUtils.EMPTY);
			}
			//默认签名
			if (StringUtils.isBlank(userInfo.getSignature()))
			{
//				userInfo.setSignature("正在烘焙的道路上努力奋进，没时间写签名...");
				userInfo.setSignature(StringUtils.EMPTY);
			}
			
			if(StringUtils.isEmpty(userInfo.getNickname())){
				userInfo.setNickname("无名氏");
			}

			try
			{			
				userInfoDao.createUserDetail(userInfo);
				//创建环信帐户
				this.createUserEasemob(userreginfo, userInfo.getNickname());
			}catch (DataAccessException e)
			{
				SQLException sqle = (SQLException) e.getCause(); 
				
				if(1062 == sqle.getErrorCode())
				{
//					throw new YiBakerException(RetEnum.USER_REGISTER_NICKNAME_FAIL);//昵称已注册
					//用户昵称冲突，则加随机后缀
					String nickName = userInfo.getNickname();
					userInfo.setNickname(nickName + "_" + util.getRandom(4)); 
					log.warn(">>>>>>>>>>>>>>>>nickname is confict, rename:" + userInfo.getNickname());
					userInfoDao.createUserDetail(userInfo);
					//创建环信帐户
					this.createUserEasemob(userreginfo, userInfo.getNickname());
				}else
				{
					throw new YiBakerException(RetEnum.LOGIN_FAIL, e);		
				}
			}
			
			//创建用户关联关系
			try
			{			
				usertpinfo = user.getUsertpinfo();
				usertpinfo.setYb_user_id(yb_user_id);
				usertpinfo.setStatus(1);
				usertpinfo.setLink_time(nowTime);
				userTPInfoDao.createTPUser(usertpinfo);
			}catch (DataAccessException e)
			{
				throw new YiBakerException(RetEnum.LOGIN_FAIL, e);		
			}
			
		}else
		{
			yb_user_id = idObject.longValue();
		}
		
		//已关联一焙用户，返回一焙用户信息
		try
		{			
			userInfo = userInfoDao.findOne(yb_user_id);
			data.setUserinfo(userInfo);
			userreginfo = userRegInfoDao.findPasswd(yb_user_id);
			data.setUserreginfo(userreginfo);			
		}catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.LOGIN_FAIL);		
		}
		return data;
	}
	
	/**
	 * 查询第三方关联账号列表
	 * @param open_id
	 * @return
	 * @throws Exception
	 */
	public List<UserTPInfo> findAllTPUser(long yb_user_id) throws Exception
	{
		if (0 == yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		List<UserTPInfo> data = null;
		try
		{
			data = userTPInfoDao.findByUser(yb_user_id);
			
			if (null == data || 0 == data.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
		} catch (DataAccessException e)
		{
			log.error(RetEnum.UPDATE_USER_INFO_FAIL.name(),  e);
			throw new YiBakerException(RetEnum.UPDATE_USER_INFO_FAIL, e);					
		}
		
		return data;
	}
	
	/**
	 * 关联第三方账号
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int bindTPUser(User user) throws Exception
	{
		return 0;
	}
	
	/**
	 * 解除第三方账号关联
	 * @param userTPInfo
	 * @return
	 * @throws Exception
	 */
	public int unbindTPUser(UserTPInfo userTPInfo) throws Exception
	{
		return 0;
	}

	/**
	 * 查询用户绑定的手机
	 * @param yb_user_id
	 * @return
	 * @throws Exception
	 */
	public UserRegInfo findBindMobile(long yb_user_id) throws Exception
	{
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			UserRegInfo data = userRegInfoDao.getMobile(yb_user_id);
			
			if (null == data)
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			return data;
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_GETMOBILE_FAIL);					
		}
	}

	/**
	 * 绑定手机
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int bindMobile(User user) throws Exception
	{
		if (null == user.getUserreginfo() 
				|| StringUtils.isBlank(user.getCheck_code())
				|| 0 == user.getUserreginfo().getYb_user_id()
				|| StringUtils.isBlank(user.getUserreginfo().getMobile())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		String mobile = user.getUserreginfo().getMobile();
		String reqCode = user.getCheck_code();
		//判断校验码是否正确
		checkSMSCode(mobile, reqCode, SMSOption.USER_REG_TIME_OUT, RedisUserAuth.USER_MOBILE_CHECK);
		
		try
		{
			userRegInfoDao.addMobile(user.getUserreginfo());
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_BINDMOBILE_FAIL, e);					
		}
		
		return 0;
	}

	/**
	 * 解除手机绑定
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int unbindMobile(User user) throws Exception
	{
		return 0;
	}

	/**
	 * 修改个人资料
	 * @param userInfo
	 * @return
	 */
	public int updateUserInfo(UserInfo userInfo) throws Exception
	{
		if (null == userInfo || 0 == userInfo.getYb_user_id())
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			userInfoDao.updateUserInfo(userInfo);			
		} 
		catch (DataAccessException e)
		{
			SQLException sqle = (SQLException) e.getCause(); 
			
			if(1062 == sqle.getErrorCode())
			{
				throw new YiBakerException(RetEnum.USER_REGISTER_NICKNAME_FAIL);//昵称已注册
			}else
			{
				throw new YiBakerException(RetEnum.UPDATE_USER_INFO_FAIL, e);		
			}
		}
		return 0;
	}
	
	/**
	 * 修改用户密码
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	public int changeUserPwd(UserRegInfo userRegInfo) throws Exception
	{
		if (null == userRegInfo 
				|| 0 == userRegInfo.getYb_user_id() 
				|| StringUtils.isBlank(userRegInfo.getPasswd()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			userRegInfoDao.changePasswd(userRegInfo);
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_CHANGEPASSWD_FAIL, e);					
		}
		
		return 0;
	}
	
	/**
	 * 重设用户密码
	 * @param mobile
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	public int resetUserPwd(ResetPasswdReq resetPasswdReq) throws Exception
	{
		if (null == resetPasswdReq 
				|| StringUtils.isBlank(resetPasswdReq.getCheck_code()) 
				|| StringUtils.isBlank(resetPasswdReq.getMobile()) 
				|| StringUtils.isBlank(resetPasswdReq.getNew_passwd())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		String mobile = resetPasswdReq.getMobile();
		String reqCode = resetPasswdReq.getCheck_code();
		//判断校验码是否正确
		checkSMSCode(mobile, reqCode, SMSOption.SMS_CHECK_TIME_OUT, RedisUserAuth.USER_MOBILE_PASSWD_RESET);

		try
		{
			if(0 == userRegInfoDao.resetPasswd(resetPasswdReq))
			{
				throw new YiBakerException(RetEnum.USER_RESETPASSWD_NOUSER);									
			}
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_RESETPASSWD_FAIL);					
		}
		
		return 0;
	}

	/**
	 * 发送短信验证码
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public String sendSMSCode(SMSCheckCode checkCode) throws Exception
	{
		if (null == checkCode 
				|| 0 == checkCode.getType()
				|| StringUtils.isBlank(checkCode.getMobile()))
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		String mobile = checkCode.getMobile();
		int type = checkCode.getType();
		long nowTime = System.currentTimeMillis();
		
		//检查手机是否已经注册
		try
		{
			int isMobileExist = userRegInfoDao.checkMobile(checkCode.getMobile());
			if(0 == isMobileExist)
			{
				if(SMSOption.SMS_RESET == type)
				{//手机未注册，不能发送密码重置验证码
					throw new YiBakerException(RetEnum.USER_NO_MOBILE_FAIL);									
				}
			}else
			{
				if(SMSOption.SMS_REG == type)
				{//手机已注册，不能发送注册验证码
					throw new YiBakerException(RetEnum.USER_REGISTER_MOBILE_EXIST);									
				}
			}
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.SMS_SEND_FAIL);					
		}

		//根据发送类型确认key
		String key = null;
		if (SMSOption.SMS_RESET == type)
		{
			key = RedisUserAuth.USER_MOBILE_PASSWD_RESET;
		}else
		{
			key = RedisUserAuth.USER_MOBILE_CHECK;
		}
		
		//1分钟内不能重复发送验证码
		boolean isBeanExist = redisBeanDao.isBeanExist(key, mobile);
		if(isBeanExist)
		{
			SMSCheckCode redisCode = redisBeanDao.getBean(key, mobile, SMSCheckCode.class);
			if((nowTime - redisCode.getCheck_time()) < SMSOption.SMS_SEND_TIME_OUT)
			{
				throw new YiBakerException(RetEnum.SMS_SEND_TOOFAST);					
			}else
			{
				redisBeanDao.delBean(key, mobile);//删除过期的验证码
			}
		}		
		
		//随机生成六位校验码
		String randomCode = util.getRandom(6);
//		String randomCode = "888888";//just for test
		
		//TODO 调用短信网关发送验证码
		
		//将验证码存入redis
		checkCode.setCheck_time(nowTime);
		checkCode.setCheck_code(randomCode);
		redisBeanDao.saveBean(checkCode, key, mobile);
		log.debug(">>>>>>>>>>>>>>>>>>>>key:" + key + "checkCode:"  + checkCode + " mobile:" + mobile);
		
		return randomCode;
	}
	
	/**
	 * 验证注册短信验证码
	 * @param checkCode
	 * @return
	 * @throws Exception
	 */
	public int checkSMSCode(SMSCheckCode checkCode) throws Exception
	{
		if (null == checkCode 
				|| StringUtils.isBlank(checkCode.getMobile())
				|| StringUtils.isBlank(checkCode.getCheck_code())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		String mobile = checkCode.getMobile();
		String reqCode = checkCode.getCheck_code();
		//判断校验码是否正确
		checkSMSCode(mobile, reqCode, SMSOption.SMS_CHECK_TIME_OUT, RedisUserAuth.USER_MOBILE_CHECK);
		
		return 0;
	}

	@Transactional
	@Override
	public YibakerResp registerEx(User user) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode =this.register(user);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}

	@Transactional
	@Override
	public YibakerResp updateUserInfoEx(UserInfo userInfo) throws Exception
	{
		// TODO Auto-generated method stub
		int retCode = this.updateUserInfo(userInfo);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setRetcode(retCode);
		return yibakerResp;
	}

	@Transactional
	@Override
	public YibakerResp tpLoginEx(User user) throws Exception
	{
		// TODO Auto-generated method stub
		User retUser = this.tpLogin(user);
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(retUser);
		return yibakerResp;
	}

	@Override
	public int firstBindMobile(User user) throws Exception
	{
		if (null == user.getUserreginfo() 
				|| StringUtils.isBlank(user.getCheck_code())
				|| 0 == user.getUserreginfo().getYb_user_id()
				|| StringUtils.isBlank(user.getUserreginfo().getMobile())
				|| StringUtils.isBlank(user.getUserreginfo().getPasswd())
				)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		String mobile = user.getUserreginfo().getMobile();
		String reqCode = user.getCheck_code();
		//判断校验码是否正确
		checkSMSCode(mobile, reqCode, SMSOption.USER_REG_TIME_OUT, RedisUserAuth.USER_MOBILE_CHECK);
		
		try
		{
			userRegInfoDao.updateUserRegInfo(user.getUserreginfo());
			
		} catch (DataAccessException e)
		{
			throw new YiBakerException(RetEnum.USER_BINDMOBILE_FAIL, e);					
		}
		
		return 0;
	}

	@Transactional
	@Override
	public YibakerResp createUserEasemobs() throws Exception
	{
		// TODO Auto-generated method stub
		YibakerResp yibakerResp = new YibakerResp();
		 final Integer row_cnt = 40;
		 Long yb_user_id = 0L;
		 List<UserRegInfo>  userRegInfoList = null; 
		 Map<String,UserEasemob> userEasemobMap = new HashMap<String, UserEasemob>();
		 int siz;
		 do {
			 siz = 0;
			 userRegInfoList = this.userRegInfoDao.findUserRegInfos(yb_user_id, row_cnt);
			 
			 if(null==userRegInfoList||userRegInfoList.isEmpty()){
				 break;
			 }
			 
			 for(UserRegInfo uri:userRegInfoList){
				 UserEasemob userEasemob = new UserEasemob();
				 userEasemob.setYb_user_id(uri.getYb_user_id());
				 userEasemob.setUsername(EasemobUtil.generateEasemobUsename(uri.getYb_user_id()));
				 userEasemob.setPassword(EasemobUtil.generateEasemobPassword());
				 userEasemob.setCtime(System.currentTimeMillis());
				 userEasemob.setNickname(uri.getEmail());
				 
				 userEasemobMap.put(userEasemob.getUsername(), userEasemob);
			 }
			 
			 EasemobResp<EasemobUser> easemobResp = easemobService.createEasemobUsers(userEasemobMap.values());
			 if(null == easemobResp||null == easemobResp.getEntities()||easemobResp.getEntities().isEmpty()){
				 throw new YiBakerException(RetEnum.USER_REG_EASEMOB);	
			 }
			 List<EasemobUser> entities = easemobResp.getEntities();
			 for(int i=0;i<entities.size();i++){
				 EasemobUser easemobUser = entities.get(i);
				 UserEasemob userEasemob = userEasemobMap.get( easemobUser.getUsername() );
				 userEasemobDao.createUserEasemob(userEasemob);
			 }
			 
			 siz = userRegInfoList.size();
			 if(siz<row_cnt){
				 break;
			 }
			 UserRegInfo userRegInfo = userRegInfoList.get(row_cnt-1);
			 yb_user_id = userRegInfo.getYb_user_id();
			 
			 userRegInfoList.clear();
			 userEasemobMap.clear();
		 } while(true);
		 
		return yibakerResp;
	}
	
	public void createUserEasemob(UserRegInfo userRegInfo, String nickname) throws Exception
	{
		// TODO Auto-generated method stub
		UserEasemob userEasemob = new UserEasemob();
		userEasemob.setYb_user_id(userRegInfo.getYb_user_id());
		userEasemob.setUsername(EasemobUtil.generateEasemobUsename(userRegInfo.getYb_user_id()));
		userEasemob.setPassword(EasemobUtil.generateEasemobPassword());
		userEasemob.setCtime(System.currentTimeMillis());
		userEasemob.setNickname(nickname);
		
		EasemobResp<EasemobUser> easemobResp = easemobService.createEasemobUser(userEasemob);
		
		 if(null == easemobResp||null == easemobResp.getEntities()||easemobResp.getEntities().isEmpty()){
			 throw new YiBakerException(RetEnum.USER_REG_EASEMOB);	
		 }
		
		 List<EasemobUser> entities = easemobResp.getEntities();
		 if(entities.size()>0){
			 EasemobUser easemobUser = entities.get(0);
			 if(userEasemob.getUsername().equals(easemobUser.getUsername())){
				 userEasemobDao.createUserEasemob(userEasemob);
			 } else {
				 throw new YiBakerException(RetEnum.USER_REG_EASEMOB);	
			 }
		 }
	}

	@Transactional
	@Override
	public YibakerResp deleteUserEasemobs() throws Exception
	{
		// TODO Auto-generated method stub
		YibakerResp yibakerResp = new YibakerResp();
			
		Integer row_cnt = 50;
		String cursor = null;
		do {
			EasemobResp<EasemobUser> easemobResp = easemobService.deleteEasemobUsers(row_cnt);
			 cursor = easemobResp.getCursor();
			List<EasemobUser> easemobUserList =  easemobResp.getEntities();
			if(null == easemobUserList || easemobUserList.isEmpty()){
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);			
			}
			for(int i=0;i<easemobUserList.size();i++){
				EasemobUser easemobUser = easemobUserList.get(i);
				if(null == easemobUser || StringUtils.isEmpty(easemobUser.getUsername())){
					continue;
				}
				userEasemobDao.deleteUserEasemobByUsername(easemobUser.getUsername());
			}
		} while(StringUtils.isNotEmpty(cursor));
		
		
		return yibakerResp;
	}

	@Override
	public YibakerResp deleteUserEasemob(Long yb_user_id) throws Exception
	{
		// TODO Auto-generated method stub
		YibakerResp yibakerResp = new YibakerResp();
		UserEasemob userEasemob = userEasemobDao.findUserEasemob(yb_user_id);
		if(null == userEasemob ){
			throw new YiBakerException(RetEnum.SYS_RESULT_NULL);		
		}
		EasemobResp<EasemobUser> easemobResp = easemobService.deleteEasemobUsers(userEasemob.getUsername());
		List<EasemobUser> easemobUserList =  easemobResp.getEntities();
		if(null == easemobUserList || easemobUserList.isEmpty()){
			throw new YiBakerException(RetEnum.SYS_RESULT_NULL);		
		}
		for(int i=0;i<easemobUserList.size();i++){
			EasemobUser easemobUser = easemobUserList.get(i);
			if(null == easemobUser || StringUtils.isEmpty(easemobUser.getUsername())){
				continue;
			}
			userEasemobDao.deleteUserEasemobByUsername(easemobUser.getUsername());
		}
		return yibakerResp;
	}

	@Override
	public YibakerResp findUserEasemob(Long yb_user_id) throws Exception
	{
		// TODO Auto-generated method stub
		if(null == yb_user_id||yb_user_id==0L){
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(userEasemobDao.findUserEasemob(yb_user_id));
		return yibakerResp;
	}
	
}
