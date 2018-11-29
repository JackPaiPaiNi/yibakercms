/*******************************************************************************
 * Copyright (c) 2003-2016,深圳市新联锋科技有限公司
 * File name:TradeServiceImpl.java   Package name:com.xinlianfeng.yibaker.provider.service.impl
 * Project:yibaker-provider BaseVersion:POSS_2.0
 *
 * Description:
 *    TODO
 * Others:
 *
 * History:
 *
 * 1.Date: 2016年1月11日
 *   Author: 闻够良(wengouliang@topeastic.com)
 *   Modification: Initial Creation.
 ******************************************************************************/
package com.xinlianfeng.yibaker.provider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.constant.BakerConsts;
import com.xinlianfeng.yibaker.common.constant.RetEnum;
import com.xinlianfeng.yibaker.common.constant.TradeConsts;
import com.xinlianfeng.yibaker.common.constant.UserConsts;
import com.xinlianfeng.yibaker.common.entity.BakerAccount;
import com.xinlianfeng.yibaker.common.entity.IncomeState;
import com.xinlianfeng.yibaker.common.entity.RecipeSellInfo;
import com.xinlianfeng.yibaker.common.entity.TradeRecord;
import com.xinlianfeng.yibaker.common.entity.UserInfo;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.service.BakerService;
import com.xinlianfeng.yibaker.common.service.TradeRecordService;
import com.xinlianfeng.yibaker.provider.dao.BakerAccountDao;
import com.xinlianfeng.yibaker.provider.dao.IncomeStateDao;
import com.xinlianfeng.yibaker.provider.dao.RecipeDao;
import com.xinlianfeng.yibaker.provider.dao.TradeRecordDao;
import com.xinlianfeng.yibaker.provider.dao.UserInfoDao;

/**
 * @Description: 
 * @Company: POSS软件平台 (www.poss.cn)
 * @Copyright: Copyright (c) 2003-2016
 * @version: POSS_2.0
 * @date: 2016年1月11日 
 * @author 闻够良 (wengouliang@topeastic.com)
 */
@Service("tradeRecordService")
public class TradeRecordServiceImpl implements TradeRecordService
{

	@Autowired
	private TradeRecordDao tradeRecordDao;
	
	@Autowired
	private BakerAccountDao bakerAccountDao;
	
	@Autowired
	private IncomeStateDao incomeStateDao;
	
	@Autowired
	private RecipeDao recipeDao;
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private BakerService bakerService;
	
	/* (non-Javadoc)
	 * @see com.xinlianfeng.yibaker.common.service.TradeRecordService#createTradeRecord(com.xinlianfeng.yibaker.common.entity.TradeRecord)
	 */
	@Override
	@Transactional
	public int createTradeRecord(TradeRecord tradeRecord) throws Exception
	{
		// TODO Auto-generated method stub
		tradeRecordDao.createTradeRecord(tradeRecord);
		if(tradeRecord.getTr_total()==0){//金额为0时，不写收支记录
			return 0;
		}
		this.updateBuyBakerInfo(tradeRecord);
		this.updateSellBakerInfo(tradeRecord);
		this.updatePlatformBakerInfo(tradeRecord);
		return 0;
	}
	
	private void updateBuyBakerInfo(TradeRecord tradeRecord){
		int tr_total =  tradeRecord.getTr_total();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.TRADE_AMOUNT_GT_ZERO);
		}
		
		long yb_user_id = tradeRecord.getBuy_user_id();
		bakerService.updateBakerAccount(yb_user_id, BakerConsts.INST_TYPE_OUT, tr_total,
				BakerConsts.INST_REASON_BUY, tradeRecord.getTr_id(), tradeRecord.getTr_time());
	}
	
	private void updateSellBakerInfo(TradeRecord tradeRecord){
		int tr_total =  tradeRecord.getTr_total();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.TRADE_AMOUNT_GT_ZERO);
		}
		long yb_user_id = tradeRecord.getSell_user_id();
		bakerService.updateBakerAccount(yb_user_id, BakerConsts.INST_TYPE_IN, (int)(tr_total*(1-BakerConsts.SELL_DEDUCT_RATE)),
				BakerConsts.INST_REASON_SELL, tradeRecord.getTr_id(), tradeRecord.getTr_time());
		
	}
	
	private void updatePlatformBakerInfo(TradeRecord tradeRecord){
		int tr_total =  tradeRecord.getTr_total();
		if(tr_total<=0){
			throw new YiBakerException(RetEnum.TRADE_AMOUNT_GT_ZERO);
		}
		long yb_user_id = UserConsts.USER_ID_PLATFORM;
		bakerService.updateBakerAccount(yb_user_id, BakerConsts.INST_TYPE_IN, (int)(tr_total*BakerConsts.SELL_DEDUCT_RATE),
				BakerConsts.INST_REASON_SELL_DEDUCT, tradeRecord.getTr_id(), tradeRecord.getTr_time());
		
	}

	@Override
	public int createTradeRecord(long yb_user_id, long recipe_id) throws Exception
	{
		// TODO Auto-generated method stub
		RecipeSellInfo recipeSellInfo = recipeDao.findRecipeSellInfo(recipe_id);
	    UserInfo buyerInfo = userInfoDao.findOne(yb_user_id);
	    TradeRecord tradeRecord = new TradeRecord();
	    tradeRecord.setBuy_user_id(buyerInfo.getYb_user_id());
	    tradeRecord.setBuy_user_name(buyerInfo.getNickname());
	    tradeRecord.setSell_user_id(recipeSellInfo.getYb_user_id());
	    tradeRecord.setSell_user_name(recipeSellInfo.getNickname());
	    tradeRecord.setTr_quantity(1);
	    tradeRecord.setTr_src_id(recipe_id);
	    tradeRecord.setTr_src_image(recipeSellInfo.getRecipe_image());
	    tradeRecord.setTr_src_name(recipeSellInfo.getRecipe_name());
	    tradeRecord.setTr_src_price(recipeSellInfo.getRecipe_price());
	    tradeRecord.setTr_time(System.currentTimeMillis());
	    tradeRecord.setTr_type(TradeConsts.TR_TYPE_RECIPE);
	    tradeRecord.setTr_total(tradeRecord.getTr_src_price()*tradeRecord.getTr_quantity());
	    
		return this.createTradeRecord(tradeRecord);
	}

	@Override
	public TradeRecord findBuyRecord(TradeRecord tradeRecord) throws Exception
	{
		// TODO Auto-generated method stub
		TradeRecord retTr = tradeRecordDao.findBuyRecord(tradeRecord);
		return retTr;
	}

	@Override
	public boolean findRecipeBuyStatus(long yb_user_id, long recipe_id)
	{
		// TODO Auto-generated method stub
		TradeRecord tr = new TradeRecord();
		tr.setTr_type(TradeConsts.TR_TYPE_RECIPE);
		tr.setTr_src_id(recipe_id);
		tr.setBuy_user_id(yb_user_id);
		TradeRecord retTr = tradeRecordDao.findBuyRecord(tr);
		return (null != retTr);
	}

}
