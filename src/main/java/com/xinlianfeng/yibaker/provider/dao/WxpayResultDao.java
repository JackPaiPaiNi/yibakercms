package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.entity.WxpayResult;

public interface WxpayResultDao
{

	int createWxpayResult(WxpayResult wxpayResult);
	
	WxpayResult findWxpayResult(@Param("out_trade_no") String out_trade_no);
	
	WxpayResult findWxpayResultByOid(@Param("wxpy_oid") Long wxpy_oid);
	
	WxpayResult findWxpayTradeStateByOid(@Param("wxpy_oid") Long wxpy_oid);
	
	int updateWxpayTradeStateByOrderno(WxpayResult wxpayResult);
	
	List<WxpayResult> findNonfinalWxpayResults();
}
