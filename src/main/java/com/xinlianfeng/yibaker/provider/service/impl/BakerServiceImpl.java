/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:BakerServiceImpl.java   Package name:com.xinlianfeng.yibaker.provider.service.impl
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年1月12日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.constant.BakerConsts;
import com.xinlianfeng.yibaker.common.constant.MsgGroup;
import com.xinlianfeng.yibaker.common.constant.MsgSendType;
import com.xinlianfeng.yibaker.common.constant.MsgType;
import com.xinlianfeng.yibaker.common.constant.RankEnum;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.constant.alipay.AlipayConsts;
import com.xinlianfeng.yibaker.common.constant.alipay.AlipayConsts.NotifyTradeStatus;
import com.xinlianfeng.yibaker.common.constant.wxpay.TradeState;
import com.xinlianfeng.yibaker.common.constant.wxpay.TradeType;
import com.xinlianfeng.yibaker.common.entity.AlipayOrder;
import com.xinlianfeng.yibaker.common.entity.AlipayResult;
import com.xinlianfeng.yibaker.common.entity.BakerAccount;
import com.xinlianfeng.yibaker.common.entity.CodeXchngRecord;
import com.xinlianfeng.yibaker.common.entity.IncomeState;
import com.xinlianfeng.yibaker.common.entity.RechargeRecord;
import com.xinlianfeng.yibaker.common.entity.RewardRecord;
import com.xinlianfeng.yibaker.common.entity.SNSMsgInfo;
import com.xinlianfeng.yibaker.common.entity.SystemParam;
import com.xinlianfeng.yibaker.common.entity.TradeRecord;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.entity.WxpayOrder;
import com.xinlianfeng.yibaker.common.entity.WxpayResult;
import com.xinlianfeng.yibaker.common.entity.XchngCode;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.resp.YibakerPageResp;
import com.xinlianfeng.yibaker.common.resp.YibakerResp;
import com.xinlianfeng.yibaker.common.service.BakerService;
import com.xinlianfeng.yibaker.common.util.AlipayUtil;
import com.xinlianfeng.yibaker.common.util.OrderNoGenerator;
import com.xinlianfeng.yibaker.common.util.RedeemCodeManager;
import com.xinlianfeng.yibaker.common.util.WxpayUtil;
import com.xinlianfeng.yibaker.provider.component.SNSMsgSender;
import com.xinlianfeng.yibaker.provider.dao.AlipayOrderDao;
import com.xinlianfeng.yibaker.provider.dao.AlipayResultDao;
import com.xinlianfeng.yibaker.provider.dao.BakerAccountDao;
import com.xinlianfeng.yibaker.provider.dao.CodeXchngRecordDao;
import com.xinlianfeng.yibaker.provider.dao.IncomeStateDao;
import com.xinlianfeng.yibaker.provider.dao.RechargeRecordDao;
import com.xinlianfeng.yibaker.provider.dao.RewardRecordDao;
import com.xinlianfeng.yibaker.provider.dao.SystemParamDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;
import com.xinlianfeng.yibaker.provider.dao.WxpayOrderDao;
import com.xinlianfeng.yibaker.provider.dao.WxpayResultDao;
import com.xinlianfeng.yibaker.provider.dao.XchngCodeDao;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年1月12日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Service("bakerService")
public class BakerServiceImpl implements BakerService
{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${yibaker.wxpay.appid}")
	private String wxpayAppid;
	
	@Value("${yibaker.wxpay.mch_id}")
	private String wxpayMch_id;
	
	@Value("${yibaker.wxpay.secret}")
	private String wxpaySecret;
	
	@Value("${yibaker.wxpay.notify_url}")
	private String wxNotify_url;
	
	@Value("${yibaker.paygood.bakers_recharge}")
	private String payTitle;
	
	@Value("${yibaker.alipay.appid}")
	private String alipayAppid;
	
	@Value("${yibaker.alipay.pid}")
	private String alipayPid;
	
	@Value("${yibaker.alipay.seller_id}")
	private String alipaySeller_id;
	
	@Value("${yibaker.alipay.my_prv_key}")
	private String myPrvKey;
	
	@Value("${yibaker.alipay.alipay_pub_key}")
	private String alipayPubKey;
	
	@Value("${yibaker.alipay.notify_url}")
	private String alipayNotify_url;
	
	@Value("${yibaker.recharge.maxamountinday:200000}")
	private int maxamountinday;
	
	@Value("${yibaker.recharge.maxamountintime:100000}")
	private int maxamountintime;
	
	@Value("${yibaker.recharge.xrate:100}")
	private int xrate;
	
	/**
	 * 0: test pay;
	 * 1: product pay;
	 */
	@Value("${yibaker.recharge.is_product_pay:0}")
	private int productPay;
	
	/**
	 * 0: pay with input amount;
	 * 1: every pay with only one cent;
	 */
	@Value("${yibaker.recharge.is_only_onecent_pay:0}")
	private int payOnlyOneCent;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private IncomeStateDao incomeStateDao;
	
	@Autowired
	private RechargeRecordDao  rechargeRecordDao; 
	
	@Autowired
	private WxpayOrderDao  wxpayOrderDao; 
	
	@Autowired
	private WxpayResultDao  wxpayResultDao; 
	
	@Autowired
	private AlipayOrderDao  alipayOrderDao; 
	
	@Autowired
	private AlipayResultDao  alipayResultDao; 
	
	@Autowired
	private RewardRecordDao  rewardRecordDao; 
	
	@Autowired
	private XchngCodeDao  xchngCodeDao; 
	
	@Autowired
	private CodeXchngRecordDao  codeXchngRecordDao; 
	
	@Autowired
	private BakerAccountDao bakerAccountDao;
	
	@Autowired
	private SystemParamDao systemParamDao;
	
	@Autowired
	private SNSMsgSender snsMsgSender;

	/* (non-Javadoc)
	 * @see com.xinlianfeng.yibaker.common.service.BakerService#findIncomeStates(java.lang.Long)
	 */
	@Override
	public List<IncomeState> findIncomeStates(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		return incomeStateDao.findIncomeStates(yb_user_id);
	}

	@Override
	public int findIncomeStatesCnt(Long yb_user_id)
	{
		// TODO Auto-generated method stub
		return incomeStateDao.findIncomeStatesCnt(yb_user_id);
	}

	@Override
	public YibakerPageResp findIncomeStates(Long yb_user_id, Long last_time, Integer count)
	{

		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<IncomeState> incomeStateList = incomeStateDao.findIncomeStatesByPage(yb_user_id, last_time, count);
			
			if (null == incomeStateList || 0 == incomeStateList.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			YibakerPageResp yibakerPageResp = new YibakerPageResp();
			yibakerPageResp.setData(incomeStateList);
			int size = incomeStateList.size();
			int total = this.findIncomeStatesCnt(yb_user_id);
			yibakerPageResp.setCount(size);
			yibakerPageResp.setTotal(total);

			return yibakerPageResp;
		} catch (DataAccessException e)
		{
			log.error(RetEnum.BAKER_INCOME_GETLIST_FAIL.mesg(), e);
			throw new YiBakerException(RetEnum.BAKER_INCOME_GETLIST_FAIL);					
		}
	}

	@Transactional
	@Override
	public WxpayOrder createRechargeRecord(RechargeRecord rechargeRecord)
	{
		// TODO Auto-generated method stub
		//验证充值记录实体类的必填字段信息的完整性,规范性
		if(null == rechargeRecord.getUser_id()||null == rechargeRecord.getCharge_amount()||null==rechargeRecord.getCharge_way()){
			throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
		}
		//验证充值金额和充值方式
		if(rechargeRecord.getCharge_amount()<=0||(rechargeRecord.getCharge_way()!=BakerConsts.RECHARGE_WAY_WXPAY&&rechargeRecord.getCharge_way()!=BakerConsts.RECHARGE_WAY_ALIPAY)){
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);
		}
		//验证单次充值金额是否超过限定值
		if(rechargeRecord.getCharge_amount()>this.maxamountintime){
			throw new YiBakerException(RetEnum.CHARGE_AMOUNT_TOOMUCH_ONETIME);
		}
		
		int amountYuan = rechargeRecord.getCharge_amount()/this.xrate;
		if(0==amountYuan){
			throw new YiBakerException(RetEnum.CHARGE_AMOUNT_GT_ZERO);
		}
		//验证当天充值总额是否超过限定值
		int amountCharged = rechargeRecordDao.findChargeAmountToday(rechargeRecord);
		if(amountCharged+rechargeRecord.getCharge_amount()>this.maxamountinday){
			throw new YiBakerException(RetEnum.CHARGE_AMOUNT_TOOMUCH_ONEDAY);
		}
		//设置充值金额等额的贝壳数
		rechargeRecord.setCharge_bakers(amountYuan*BakerConsts.CNY_BAKER_RATE);
		//step1: create a business order
		//创建一个交易订单
		//设置临时充值订单号
		rechargeRecord.setCharge_orderno(OrderNoGenerator.generateDefault());
		//根据用户编号查询用户信息
		UserInfo userInfo = this.userInfoDao.findOne(rechargeRecord.getUser_id());
		//设置用户昵称
		rechargeRecord.setUser_name(userInfo.getNickname());
		//设置充值状态
		rechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYING);
		//设置充值时间
		rechargeRecord.setCharge_time(System.currentTimeMillis());
		//创建充值记录
		rechargeRecordDao.createRechargeRecord(rechargeRecord);
		//设置充值订单号
		rechargeRecord.setCharge_orderno(OrderNoGenerator.generate(BakerConsts.INST_REASON_RECHARGE, rechargeRecord.getRecharge_id()));
		//更新充值订单号
	    rechargeRecordDao.updateChargeOrderno(rechargeRecord);
		
		//step2:send a request to weixin's pay system for creating wxpay order
		//发送一个请求到微信支付系统来创建一个微信支付订单
		WxpayOrder wxpayOrder = new WxpayOrder();
		//设置微信订单应用编号
		wxpayOrder.setAppid(this.wxpayAppid);
		//设置微信订单商户号
		wxpayOrder.setMch_id(this.wxpayMch_id);
		//设置微信订单商品描述
		wxpayOrder.setBody(payTitle);
		//设置微信订单随机字符串
		wxpayOrder.setNonce_str(WxpayUtil.nonceStr());
		//设置微信支付通知URL
		wxpayOrder.setNotify_url(wxNotify_url);
		//设置商户订单号
		wxpayOrder.setOut_trade_no(rechargeRecord.getCharge_orderno());
		//设置货币类型
		wxpayOrder.setFee_type("CNY");
		
		wxpayOrder.setSpbill_create_ip(rechargeRecord.getSpbill_create_ip());
		//设置微信订单货物标签
		wxpayOrder.setGoods_tag("WXG");
		wxpayOrder.setLimit_pay("no_credit");
	
//		wxpayOrder.setSign(sign);
		long ts = System.currentTimeMillis();
		String time_start = DateFormatUtils.format(ts, "yyyyMMddHHmmss");
		wxpayOrder.setTime_start(time_start);
		long te = ts+7 * 24 * 60 * 60 * 1000;
		String time_expire = DateFormatUtils.format(te, "yyyyMMddHHmmss");
		wxpayOrder.setTime_expire(time_expire);
		if(productPay == 0 && payOnlyOneCent == 1){
			//Test model, only pay one cent;
			wxpayOrder.setTotal_fee(1);
		}else{
			wxpayOrder.setTotal_fee(rechargeRecord.getCharge_amount());
		}
		
		wxpayOrder.setTrade_type(TradeType.APP.name());
		
		Map<String, String> map = new HashMap<String, String>();
		
        map.put("body", this.payTitle);
        map.put("mch_id", wxpayOrder.getMch_id());
        map.put("appid", wxpayOrder.getAppid());

        map.put("nonce_str", wxpayOrder.getNonce_str());
        map.put("notify_url", wxpayOrder.getNotify_url());
        map.put("fee_type", wxpayOrder.getFee_type());
        map.put("spbill_create_ip", wxpayOrder.getSpbill_create_ip());
        map.put("time_start", wxpayOrder.getTime_start());
        map.put("time_expire", wxpayOrder.getTime_expire());
        map.put("goods_tag", wxpayOrder.getGoods_tag());
        map.put("limit_pay", wxpayOrder.getLimit_pay());
        map.put("out_trade_no", wxpayOrder.getOut_trade_no());
        map.put("total_fee", wxpayOrder.getTotal_fee().toString());
        map.put("trade_type", wxpayOrder.getTrade_type());
	        
	    String sign = WxpayUtil.signature(map, wxpaySecret);
	    wxpayOrder.setSign(sign);
	    wxpayOrder.setSecret(wxpaySecret);
		
		wxpayOrderDao.createWxpayOrder(wxpayOrder);
		
		return wxpayOrder;
	}

	@Transactional
	@Override
	public int updateRechargeRecord(WxpayOrder wxpayOrder)
	{
		// TODO Auto-generated method stub
		this.wxpayOrderDao.updateWxpayOrder(wxpayOrder);
		return 0;
	}

	@Transactional
	@Override
	public YibakerResp createRewardRecord(RewardRecord rewardRecord)
	{
		// TODO Auto-generated method stub
		if(null == rewardRecord.getRewarder_user_id()||null==rewardRecord.getRewardee_user_id()||null == rewardRecord.getReward_bakers()){
			throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
		}
		if(rewardRecord.getReward_bakers().intValue()<=0){
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);
		}
		int[] rewardParams = this.findRewardParam4User();
		if(rewardRecord.getReward_bakers().intValue()>rewardParams[0]){
			throw new YiBakerException(RetEnum.REWARD_BAKERS_OVER_MAX);
		}
		
		int times = rewardRecordDao.findRewardRecordCntToday(rewardRecord);
		if(times>=rewardParams[1]){
			throw new YiBakerException(RetEnum.REWARD_TIMES4DAY_OVER_MAX);
		}
		
		UserInfo rewarderUserInfo = this.userInfoDao.findOne(rewardRecord.getRewarder_user_id());
		UserInfo rewardeeUserInfo = this.userInfoDao.findOne(rewardRecord.getRewardee_user_id());
		BakerAccount bakerAccount = bakerAccountDao.findOne(rewarderUserInfo.getYb_user_id());
		if(bakerAccount.getBalance()<rewardRecord.getReward_bakers()){
			throw new YiBakerException(RetEnum.BAKER_INSUFFICIENT_BALANCE);
		}
		if(null == rewarderUserInfo||null == rewardeeUserInfo ){
			throw new YiBakerException(RetEnum.USER_GETOTHERINFO_FAIL);
		}
		rewardRecord.setRewarder_user_name(rewarderUserInfo.getNickname());
		rewardRecord.setRewardee_user_name(rewardeeUserInfo.getNickname());
		
		rewardRecord.setReward_msg("焙壳打赏");
		rewardRecord.setReward_time(System.currentTimeMillis());
		rewardRecord.setReward_orderno(OrderNoGenerator.generateDefault());
		
     	rewardRecordDao.createRewardRecord(rewardRecord);
     	rewardRecord.setReward_orderno(OrderNoGenerator.generate(BakerConsts.INST_REASON_REWARD, rewardRecord.getReward_no()));
     	rewardRecordDao.updateRewardOrderno(rewardRecord);
     	updateRewarderBakerInfo(rewardRecord);
     	updateRewardeeBakerInfo(rewardRecord);
     	
     	try
		{
			SNSMsgInfo msgInfo = new SNSMsgInfo();
			msgInfo.setFrom_user_id(rewardRecord.getRewarder_user_id());
			msgInfo.setMsg_type(MsgType.MSG_601);
			msgInfo.setSend_type(MsgSendType.P2P);
			msgInfo.setSrc_id(rewardRecord.getReward_no());
			snsMsgSender.sendSNSMsg(rewardRecord.getRewardee_user_id(), MsgGroup.REWARD, msgInfo);
		} catch (Exception e)
		{
			log.error(">>>>>>>>>>>>>>>>>>>" + RetEnum.SNS_SENDMSG_FAIL.mesg(), e);
		}
     	
		return new YibakerResp();
	}
	
	private void updateRewarderBakerInfo(RewardRecord rewardRecord){
		int tr_total =  rewardRecord.getReward_bakers();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.REWARD_BAKERS_GT_ZERO);
		}
		
		this.updateBakerAccount(rewardRecord.getRewarder_user_id(), BakerConsts.INST_TYPE_OUT, tr_total, 
				BakerConsts.INST_REASON_REWARD,	rewardRecord.getReward_no(), rewardRecord.getReward_time());
	}
	
	private void updateRewardeeBakerInfo(RewardRecord rewardRecord){
		int tr_total =  rewardRecord.getReward_bakers();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.REWARD_BAKERS_GT_ZERO);
		}
		
		this.updateBakerAccount(rewardRecord.getRewardee_user_id(), BakerConsts.INST_TYPE_IN, tr_total, 
				BakerConsts.INST_REASON_REWARD,	rewardRecord.getReward_no(), rewardRecord.getReward_time());
	}

	@Transactional
	@Override
	public YibakerResp createXchngCodes(XchngCode xchngCode)
	{
		// TODO Auto-generated method stub
		xchngCode.setXchng_type(1);
		xchngCode.setXchng_cnt(10);
		
		xchngCode.setXchng_status((byte)0);
		xchngCode.setXchng_ctime(1459251723004L);
		xchngCode.setXchng_etime(1459856523004L);
		
		for(int i=RedeemCodeManager.INDEX_MIN, max = RedeemCodeManager.INDEX_MIN+1000;i<=max;i++){
			xchngCode.setXchng_pwd(RandomStringUtils.randomNumeric(6));
			xchngCode.setXchng_idx(i);
			xchngCode.setXchng_rnd(RedeemCodeManager.rand());
			String code = RedeemCodeManager.conv(xchngCode.getXchng_idx())+RedeemCodeManager.conv(xchngCode.getXchng_rnd());
			xchngCode.setXchng_code(code);
			xchngCodeDao.createXchngCode(xchngCode);
		}
		
		return new YibakerResp();
	}

	@Override
	public YibakerResp findXchngCode(XchngCode xchngCode)
	{
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(xchngCode.getXchng_code())||StringUtils.isEmpty(xchngCode.getXchng_pwd())){
			throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
		}
		String redeem = xchngCode.getXchng_code();
		if(!RedeemCodeManager.isValidChar(redeem)){
			throw new YiBakerException(RetEnum.REDEEM_FORMAT_INCORRECT);	
		}
		int[] nums = RedeemCodeManager.numMap(redeem);
		xchngCode.setXchng_idx(nums[0]);
		xchngCode.setXchng_rnd(nums[1]);
		
		XchngCode retXchngCode = xchngCodeDao.findXchngCode(xchngCode);
		
		if(null == retXchngCode){
			throw new YiBakerException(RetEnum.REDEEM_NOT_EXIST);	
		}
		if (BakerConsts.REDEEM_STATUS_USED.intValue() == retXchngCode.getXchng_status().intValue()){
			throw new YiBakerException(RetEnum.REDEEM_IS_USED);	
		}
		long currtime = System.currentTimeMillis();
		if(currtime>retXchngCode.getXchng_etime()){
			retXchngCode.setXchng_status(BakerConsts.REDEEM_STATUS_EXPIRED);
			xchngCodeDao.updateXchngCodeStatus(retXchngCode);
		}
		if (BakerConsts.REDEEM_STATUS_EXPIRED.intValue() == retXchngCode.getXchng_status().intValue() ){
			throw new YiBakerException(RetEnum.REDEEM_IS_EXPIRED);	
		}
		if(!xchngCode.getXchng_pwd().equals(retXchngCode.getXchng_pwd())){
			throw new YiBakerException(RetEnum.REDEEM_PWD_INCORRECT);	
		}
		String xchng_vcode = RandomStringUtils.randomAlphanumeric(32);
		retXchngCode.setXchng_vcode(xchng_vcode);
		xchngCodeDao.updateXchngCodeVcode(retXchngCode);
		YibakerResp yibakerResp =  new YibakerResp();
		XchngCode respXchngCode = new XchngCode();
		respXchngCode.setXchng_cid(retXchngCode.getXchng_cid());
		respXchngCode.setXchng_code(retXchngCode.getXchng_code());
		respXchngCode.setXchng_cnt(retXchngCode.getXchng_cnt());
		respXchngCode.setXchng_vcode(xchng_vcode);
		yibakerResp.setData(respXchngCode);
		return yibakerResp;
	}

	@Transactional
	@Override
	public YibakerResp createCodeXchngRecord(CodeXchngRecord codeXchngRecord)
	{
		// TODO Auto-generated method stub
		if(null == codeXchngRecord.getXchng_user_id()||null == codeXchngRecord.getXchng_cid()||StringUtils.isEmpty(codeXchngRecord.getXchng_vcode())){
			throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
		}
		XchngCode xchngCode = new XchngCode();
		xchngCode.setXchng_cid(codeXchngRecord.getXchng_cid());
		xchngCode.setXchng_vcode(codeXchngRecord.getXchng_vcode());
		
		XchngCode retXchngCode = xchngCodeDao.findOneByVcode(xchngCode);
		if(null == retXchngCode){
			throw new YiBakerException(RetEnum.REDEEM_FAIL);	
		}
		if (BakerConsts.REDEEM_STATUS_USED.intValue() == retXchngCode.getXchng_status().intValue()){
			throw new YiBakerException(RetEnum.REDEEM_IS_USED);	
		}
		codeXchngRecord.setXchng_code(retXchngCode.getXchng_code());
		codeXchngRecord.setXchng_bakers(retXchngCode.getXchng_cnt());
		codeXchngRecord.setXchng_time(System.currentTimeMillis());
		UserInfo userInfo = this.userInfoDao.findOne(codeXchngRecord.getXchng_user_id());
		codeXchngRecord.setXchng_user_name(userInfo.getNickname());
		codeXchngRecord.setXchng_orderno(OrderNoGenerator.generateDefault());
		
		codeXchngRecordDao.createCodeXchngRecord(codeXchngRecord);
		codeXchngRecord.setXchng_orderno(OrderNoGenerator.generate(BakerConsts.INST_REASON_XCHNG, codeXchngRecord.getXchng_no()));
		codeXchngRecordDao.updateCodeXchngRecordOrderno(codeXchngRecord);
		retXchngCode.setXchng_status(BakerConsts.REDEEM_STATUS_USED);
		xchngCodeDao.updateXchngCodeStatus(retXchngCode);
		updateCodeXchngerBakerInfo(codeXchngRecord);
		YibakerResp yibakerResp =  new YibakerResp();
		return yibakerResp;
	}

	private void updateCodeXchngerBakerInfo(CodeXchngRecord codeXchngRecord){
		
		int tr_total =  codeXchngRecord.getXchng_bakers();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.REDEEM_BAKERS_GT_ZERO);
		}
		
		this.updateBakerAccount(codeXchngRecord.getXchng_user_id(), BakerConsts.INST_TYPE_IN, tr_total, 
				BakerConsts.INST_REASON_XCHNG,	codeXchngRecord.getXchng_no(), codeXchngRecord.getXchng_time());
	}

	@Override
	public void updateBakerAccount( final Long yb_user_id, final Byte inst_type, final  Integer inst_delta, final Byte inst_reason,
			final Long inst_reason_srcid, final Long inst_time)
	{
		// TODO Auto-generated method stub
		BakerAccount bakerAccount = bakerAccountDao.findOne(yb_user_id);
		if(null == bakerAccount){
			throw new YiBakerException(RetEnum.BAKER_ACCOUNT_NOT_EXIST);
		}
		//对该用户焙壳账户记录数据进行上读锁，直至执行完transactional（通过记录主键进行锁记录，避免锁表）
		//避免并发时出现用户账号贝壳数收支不对
		bakerAccount = bakerAccountDao.findOneByPKForUpdate(bakerAccount.getBa_id());
		
		IncomeState incomeState = new IncomeState();
		incomeState.setInst_account_id(bakerAccount.getBa_id());
		incomeState.setInst_type(inst_type);
		incomeState.setInst_pre_balance(bakerAccount.getBalance());
		incomeState.setInst_reason(inst_reason);
		incomeState.setInst_reason_srcid(inst_reason_srcid);
		incomeState.setInst_time(inst_time);
		incomeState.setInst_delta(inst_delta);
		if(BakerConsts.INST_TYPE_IN.byteValue()==inst_type.byteValue()){
			incomeState.setInst_post_balance(incomeState.getInst_pre_balance()+incomeState.getInst_delta());
		} else {
			incomeState.setInst_post_balance(incomeState.getInst_pre_balance()-incomeState.getInst_delta());
		}
		
		
		incomeStateDao.createIncomeState(incomeState);
		bakerAccountDao.updateBakerAccount(incomeState);
		
		if(BakerConsts.INST_TYPE_IN.byteValue()==inst_type.byteValue()){//检查是否升级
			UserInfo userInfo = this.userInfoDao.findOne(yb_user_id);
			int level_id = userInfo.getLevel_id();
			int new_level_id = RankEnum.gain_rank(bakerAccount.getAccum_income()+incomeState.getInst_delta(), level_id);
			if(0==new_level_id){//没有升级
				return;
			}
			userInfo.setLevel_id(new_level_id);
			userInfoDao.updateUserLevel(userInfo);
    	}
	}

	@Transactional
	@Override
	public int updateRechargeRecord(WxpayResult wxpayResult)
	{
		// TODO Auto-generated method stub
//		wxpayResultDao.createWxpayResult(wxpayResult);
		
		RechargeRecord rechargeRecord = new RechargeRecord();
		rechargeRecordDao.updateRechargeRecord(rechargeRecord);
		return 0;
	}

	@Override
	public YibakerResp findRechargeRecord(WxpayOrder wxpayOrder)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public int createWxpayResult(WxpayResult wxpayResult)
	{
		// TODO Auto-generated method stub
		WxpayOrder wxpayOrder = new WxpayOrder();
		wxpayOrder.setAppid(wxpayResult.getAppid());
		wxpayOrder.setMch_id(wxpayResult.getMch_id());
		wxpayOrder.setOut_trade_no(wxpayResult.getOut_trade_no());
		WxpayOrder retWxpayOrder = wxpayOrderDao.findOne(wxpayOrder);
		
		if(null == retWxpayOrder){
			throw new YiBakerException(RetEnum.CHARGE_WXORDER_NOT_FOUND);
		}
		wxpayResult.setWxpy_oid(retWxpayOrder.getWxpy_oid());
		wxpayResultDao.createWxpayResult(wxpayResult);
		
		RechargeRecord rechargeRecord = new RechargeRecord();
		rechargeRecord.setCharge_orderno(wxpayResult.getOut_trade_no());
		RechargeRecord retRechargeRecord = rechargeRecordDao.findOneByOrderno(rechargeRecord);
		if(retRechargeRecord.getCharge_status().byteValue() == BakerConsts.RECHARGE_STATUS_PAYING){
			TradeState tradeState = TradeState.valueOf(wxpayResult.getTrade_state());
			
			
			switch(tradeState){
			
					case SUCCESS:{
							retRechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYOK);
							this.rechargeRecordDao.updateRechargeRecord(retRechargeRecord);
							this.updateRechargeBakerInfo(retRechargeRecord);
						}
						break;
						
					case CLOSED:
					case PAYERROR:
					case REVOKED:{
							retRechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYFAIL);
							this.rechargeRecordDao.updateRechargeRecord(retRechargeRecord);
						}
						break;
						
					case USERPAYING:
					case NOTPAY:
					case REFUND:
						break;
					}
		}
		
		return 0;
	}

	@Override
	public String findWxpaySecret()
	{
		// TODO Auto-generated method stub
		return this.wxpaySecret;
	}

	@Override
	public WxpayResult findWxpayResult(String out_trade_no)
	{
		// TODO Auto-generated method stub
		return wxpayResultDao.findWxpayResult(out_trade_no);
	}

	@Override
	public WxpayResult findWxpayResult(WxpayOrder wxpayOrder)
	{
		// TODO Auto-generated method stub
		if(null == wxpayOrder||StringUtils.isEmpty(wxpayOrder.getAppid())
				||StringUtils.isEmpty(wxpayOrder.getMch_id())||StringUtils.isEmpty(wxpayOrder.getPrepay_id())){
			throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
		}
		WxpayOrder retWxpayOrder = this.wxpayOrderDao.findOneByPrepayid(wxpayOrder);
		WxpayResult wxpayResult = wxpayResultDao.findWxpayTradeStateByOid(retWxpayOrder.getWxpy_oid());
		TradeState tradeState = TradeState.valueOf(wxpayResult.getTrade_state());
		log.info("out_trade_no="+wxpayResult.getOut_trade_no()+";  tradeState="+tradeState.name());
		
		return wxpayResult;
	}

	@Transactional
	@Override
	public void updateWxpayResultTradeState(WxpayResult wxpayResult)
	{
		// TODO Auto-generated method stub
		TradeState tradeState = TradeState.valueOf(wxpayResult.getTrade_state());
		log.info("tradeState="+tradeState.name()+"; out_trade_no="+wxpayResult.getOut_trade_no());
		wxpayResultDao.updateWxpayTradeStateByOrderno(wxpayResult);
		
		switch(tradeState){
		
			case SUCCESS:{
					RechargeRecord rechargeRecord = new RechargeRecord();
					rechargeRecord.setCharge_orderno(wxpayResult.getOut_trade_no());
					rechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYOK);
					this.rechargeRecordDao.updateRechargeRecord(rechargeRecord);
					RechargeRecord rechargeRecord2 = rechargeRecordDao.findOneByOrderno(rechargeRecord);
					this.updateRechargeBakerInfo(rechargeRecord2);
			    } 
				break;
				
			case PAYERROR:
			case CLOSED:
			case REVOKED:{
					RechargeRecord rechargeRecord = new RechargeRecord();
					rechargeRecord.setCharge_orderno(wxpayResult.getOut_trade_no());
					rechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYFAIL);
					this.rechargeRecordDao.updateRechargeRecord(rechargeRecord);
				}
				break;
				
			case USERPAYING:
			case NOTPAY:
			case	REFUND:
				break;
		}
		
	}
	
    private void updateRechargeBakerInfo(RechargeRecord rechargeRecord){
		
		int tr_total =  rechargeRecord.getCharge_bakers();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.CHARGE_BAKERS_GT_ZERO);
		}
		
		this.updateBakerAccount(rechargeRecord.getUser_id(), BakerConsts.INST_TYPE_IN, tr_total, 
				BakerConsts.INST_REASON_RECHARGE,	rechargeRecord.getRecharge_id(), rechargeRecord.getCharge_time());
	}

	@Override
	public WxpayResult findWxpayTradeState(WxpayOrder wxpayOrder)
	{
		// TODO Auto-generated method stub
		if(null == wxpayOrder||StringUtils.isEmpty(wxpayOrder.getAppid())
				||StringUtils.isEmpty(wxpayOrder.getMch_id())||StringUtils.isEmpty(wxpayOrder.getPrepay_id())){
			throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
		}
		WxpayOrder retWxpayOrder = this.wxpayOrderDao.findOneByPrepayid(wxpayOrder);
		WxpayResult wxpayResult = wxpayResultDao.findWxpayTradeStateByOid(retWxpayOrder.getWxpy_oid());
		if(null == wxpayResult){
			wxpayResult = new WxpayResult();
			wxpayResult.setOut_trade_no(retWxpayOrder.getOut_trade_no());
		} else {
			TradeState tradeState = TradeState.valueOf(wxpayResult.getTrade_state());
			log.info("out_trade_no="+wxpayResult.getOut_trade_no()+";  tradeState="+tradeState.name());
		}
		
		
		return wxpayResult;
	}

	@Override
	public int findIncomeStatesCnt(Long yb_user_id, Byte inst_reason)
	{
		// TODO Auto-generated method stub
		return incomeStateDao.findIncomeStatesCntForReason(yb_user_id, inst_reason);
	}

	@Override
	public YibakerPageResp findIncomeStates(Long yb_user_id, Long last_time, Integer count, Byte inst_reason)
	{
		// TODO Auto-generated method stub
		if (0 ==yb_user_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		try
		{
			if (0 == count)
			{
				count = 10;//默认分页数为10
			}
			
			List<IncomeState> incomeStateList =  incomeStateDao.findIncomeStatesByPageForReason(yb_user_id, last_time, count, inst_reason);
			
			if (null == incomeStateList || 0 == incomeStateList.size())
			{
				throw new YiBakerException(RetEnum.SYS_RESULT_NULL);					
			}
			
			YibakerPageResp yibakerPageResp = new YibakerPageResp();
			yibakerPageResp.setData(incomeStateList);
			int size = incomeStateList.size();
			int total = this.findIncomeStatesCnt(yb_user_id, inst_reason);
			yibakerPageResp.setCount(size);
			yibakerPageResp.setTotal(total);

			return yibakerPageResp;
		} catch (DataAccessException e)
		{
			log.error(RetEnum.BAKER_INCOME_GETLIST_FAIL.mesg(), e);
			throw new YiBakerException(RetEnum.BAKER_INCOME_GETLIST_FAIL);					
		}
	}

	@Override
	public YibakerResp findIncomeStateDetail(Long yb_user_id, Long inst_id)
	{
		// TODO Auto-generated method stub
		if (null == yb_user_id||0 ==yb_user_id||null == inst_id|| 0 == inst_id)
		{
			throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);		
		}
		
		IncomeState incomeState= incomeStateDao.findOne(inst_id);
		if(null == incomeState){
			throw new YiBakerException(RetEnum.SYS_RESULT_NULL);		
		}
		IncomeState incomeState2 = incomeStateDao.findIncomeStateDetail(yb_user_id, inst_id, incomeState.getInst_reason());
		YibakerResp yibakerResp = new YibakerResp();
		yibakerResp.setData(incomeState2);
		return yibakerResp;
	}

	@Override
	public List<WxpayResult> findNonfinalWxpayResults()
	{
		// TODO Auto-generated method stub
		return this.wxpayResultDao.findNonfinalWxpayResults();
	}

	@Override
	public List<WxpayOrder> findWxpayOrdersWithoutResult()
	{
		// TODO Auto-generated method stub
		return this.wxpayOrderDao.findWxpayOrdersWithoutResult();
	}

	@Transactional
	@Override
	public AlipayOrder createRechargeRecord4Alipay(RechargeRecord rechargeRecord)
	{
		// TODO Auto-generated method stub
				if(null == rechargeRecord.getUser_id()||null == rechargeRecord.getCharge_amount()||null==rechargeRecord.getCharge_way()){
					throw new YiBakerException(RetEnum.SYS_REQ_NULL);	
				}
				if(rechargeRecord.getCharge_amount()<=0||(rechargeRecord.getCharge_way()!=BakerConsts.RECHARGE_WAY_WXPAY&&rechargeRecord.getCharge_way()!=BakerConsts.RECHARGE_WAY_ALIPAY)){
					throw new YiBakerException(RetEnum.SYS_REQ_CHECK_FAIL);
				}
				
				if(rechargeRecord.getCharge_amount()>this.maxamountintime){
					throw new YiBakerException(RetEnum.CHARGE_AMOUNT_TOOMUCH_ONETIME);
				}
				
				int amountYuan = rechargeRecord.getCharge_amount()/this.xrate;
				if(0==amountYuan){
					throw new YiBakerException(RetEnum.CHARGE_AMOUNT_GT_ZERO);
				}
				int amountCharged = rechargeRecordDao.findChargeAmountToday(rechargeRecord);
				if(amountCharged+rechargeRecord.getCharge_amount()>this.maxamountinday){
					throw new YiBakerException(RetEnum.CHARGE_AMOUNT_TOOMUCH_ONEDAY);
				}
				rechargeRecord.setCharge_bakers(amountYuan*BakerConsts.CNY_BAKER_RATE);
				//step1: create a business order
				rechargeRecord.setCharge_orderno(OrderNoGenerator.generateDefault());
				
				UserInfo userInfo = this.userInfoDao.findOne(rechargeRecord.getUser_id());
				rechargeRecord.setUser_name(userInfo.getNickname());
				rechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYING);
				rechargeRecord.setCharge_time(System.currentTimeMillis());
				rechargeRecordDao.createRechargeRecord(rechargeRecord);
				rechargeRecord.setCharge_orderno(OrderNoGenerator.generate(BakerConsts.INST_REASON_RECHARGE, rechargeRecord.getRecharge_id()));
			    rechargeRecordDao.updateChargeOrderno(rechargeRecord);
				
				//step2:send a request to alipay's pay system for creating alipay order
			    AlipayOrder  alipayOrder = new AlipayOrder();
			    alipayOrder.set_input_charset("UTF-8");
//			    alipayOrder.setApp_id(this.alipayAppid);
			    alipayOrder.setOut_trade_no(rechargeRecord.getCharge_orderno());
			    alipayOrder.setSubject(this.payTitle);
			    alipayOrder.setPartner(alipayPid);
			    alipayOrder.setGoods_type("1");
			    alipayOrder.setBody(payTitle);
			    alipayOrder.setPayment_type("1");
			    alipayOrder.setService(AlipayConsts.SERVICE_NAME);
			    alipayOrder.setGoods_type("1");
			    BigDecimal b1 = new BigDecimal(rechargeRecord.getCharge_amount());
				BigDecimal b2 = new BigDecimal(100);
				BigDecimal total_fee = b1.divide(b2);
				if(productPay == 0 && payOnlyOneCent == 1){
					//Test model, only pay one cent;
					//total_fee = new BigDecimal(1); 
					BigDecimal b3 = new BigDecimal(1);
					BigDecimal b4 = new BigDecimal(100);
					total_fee =  b3.divide(b4);
				}
			    alipayOrder.setTotal_fee(total_fee);
			    alipayOrder.setNotify_url(alipayNotify_url);
			    alipayOrder.setSeller_id(alipaySeller_id);
			    alipayOrder.setSign_type(AlipayConsts.SIGN_TYPE);
			    alipayOrder.setSign("");
			    alipayOrderDao.createAlipayOrder(alipayOrder);    
				
				return alipayOrder;
	}

	@Override
	public String findAlipayPubKey()
	{
		// TODO Auto-generated method stub
		return this.alipayPubKey;
	}

	@Override
	public String findAlipayMyPrvKey()
	{
		// TODO Auto-generated method stub
		return this.myPrvKey;
	}

	@Override
	public int updateAlipayOrderSign(AlipayOrder alipayOrder)
	{
		// TODO Auto-generated method stub
		return alipayOrderDao.updateAlipayOrderSign(alipayOrder);
	}

	@Transactional
	@Override
	public int createAlipayResult(AlipayResult alipayResult)
	{
		// TODO Auto-generated method stub
		AlipayOrder alipayOrder = alipayOrderDao.findOneByOrderno(alipayResult.getOut_trade_no());
		if(null == alipayOrder ){
			throw new YiBakerException(RetEnum.CHARGE_ALIORDER_NOT_FOUND);
		}
		alipayResult.setAlpy_oid(alipayOrder.getAlpy_oid());
		AlipayResult oldAlipayResult = alipayResultDao.findOneByOrderno(alipayResult.getOut_trade_no());
		if(null == oldAlipayResult){
			alipayResultDao.createAlipayResult(alipayResult);
		}
		RechargeRecord paramRechargeRecord = new RechargeRecord();
		paramRechargeRecord.setCharge_orderno(alipayResult.getOut_trade_no());
		RechargeRecord retRechargeRecord = rechargeRecordDao.findOneByOrderno(paramRechargeRecord);
		log.info("retRechargeRecord's charge_status="+retRechargeRecord.getCharge_status().byteValue());
		
		if(retRechargeRecord.getCharge_status().byteValue() == BakerConsts.RECHARGE_STATUS_PAYING){
			
			NotifyTradeStatus  notifyTradeStatus = NotifyTradeStatus.valueOf(alipayResult.getTrade_status());
			switch(notifyTradeStatus){
			
				case  	TRADE_SUCCESS:
				case 	TRADE_FINISHED:{
					alipayResultDao.updateAlipayResultStatus(alipayResult);
					retRechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYOK);
					this.rechargeRecordDao.updateRechargeRecord(retRechargeRecord);
					this.updateRechargeBakerInfo(retRechargeRecord);
				}
						break;
						
				case		TRADE_CLOSED:{
					alipayResultDao.updateAlipayResultStatus(alipayResult);
					retRechargeRecord.setCharge_status(BakerConsts.RECHARGE_STATUS_PAYFAIL);
					this.rechargeRecordDao.updateRechargeRecord(retRechargeRecord);
				}
						break;
						
				case 	WAIT_BUYER_PAY:
						break;
			}
		}
		
		return 0;
	}
	
	private int[] findRewardParam4User(){
		int[] retVal = new int[]{100, 3};//default's value
		SystemParam systemParam = this.systemParamDao.findOneByKey("REWARD_USER");
		if(null == systemParam){
			log.warn("System param 'REWARD_USER' doesnot exist!");
			return retVal;
		} 
		String param_value = systemParam.getParam_value();
		if(StringUtils.isEmpty(param_value)){
			log.warn("The value of System param 'REWARD_USER'  is empty!");
			return retVal;
		}
		String[] pvs = param_value.split(",");
		if(pvs.length!=2){
			log.warn("The value of System param 'REWARD_USER'  is illegal!");
			return retVal;
		}
		retVal = new int[] {Integer.parseInt(pvs[0]), Integer.parseInt(pvs[1])};
		return retVal;
	}

	

}
