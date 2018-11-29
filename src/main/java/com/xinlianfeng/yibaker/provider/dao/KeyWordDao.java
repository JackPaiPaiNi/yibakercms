package com.xinlianfeng.yibaker.provider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xinlianfeng.yibaker.common.resp.KeyWord;

public interface KeyWordDao {
	/***
	 * 获取关键字
	 * @param limit
	 * @return
	 */
	List<KeyWord> getKeyWords(@Param("limit") int limit);
}
