package com.xinlianfeng.yibaker.provider.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinlianfeng.yibaker.common.entity.SystemParam;
import com.xinlianfeng.yibaker.common.resp.KeyWord;
import com.xinlianfeng.yibaker.common.service.KeyWordService;
import com.xinlianfeng.yibaker.provider.dao.KeyWordDao;
import com.xinlianfeng.yibaker.provider.dao.SystemParamDao;

@Service("keyWordService")
public class KeyWordImpl implements KeyWordService {
	
	@Autowired
	private SystemParamDao systemParam;
	
	@Autowired
	private KeyWordDao keyWordDao;
	/***
	 * 关键字
	 */
	@Override
	public List<KeyWord> getKeyWordList() {
		
		int limit =	6;
		if(systemParam.findOneByKey("KEY_WORD_NUMBER")!=null){
			if(!systemParam.findOneByKey("KEY_WORD_NUMBER").getParam_value().isEmpty())
			{
				limit = Integer.valueOf(systemParam.findOneByKey("KEY_WORD_NUMBER").getParam_value());
			}
		}
		return keyWordDao.getKeyWords(limit);
	}
}
