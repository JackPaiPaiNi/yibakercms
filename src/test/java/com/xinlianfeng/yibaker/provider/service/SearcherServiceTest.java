package com.xinlianfeng.yibaker.provider.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.resp.RecipeDetailResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;
import com.xinlianfeng.yibaker.provider.dao.RecipeDao;
import com.xinlianfeng.yibaker.provider.service.impl.SearcherService;

/**
 * @Description: 
 * @date: 2015-10-29 
 * @author Tanglinquan 
 */
@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Component
@Lazy
public class SearcherServiceTest
{
	@Autowired
	private SearcherService searcherService;

	@Autowired
	private RecipeDao recipeDao;

	@Test
	public void searcherRecipeId() throws IOException, ParseException
	{
		String keyword = "饼干";
		List<Long> ids = searcherService.searcherRecipeId(keyword);
		RecipeListResp searchList = recipeDao.searchList(ids);
		List<RecipeDetailResp> recipelist = searchList.getRecipelist();
		for (RecipeDetailResp recipe : recipelist)
		{
			System.out.println("name = " + recipe.getRecipe().getRecipe_name() + ",content = " + recipe.getRecipe().getRecipe_content());
			Assert.assertTrue(recipe.getRecipe().getRecipe_name().indexOf(keyword) > 0 || recipe.getRecipe().getRecipe_content().indexOf(keyword) > 0);
		}
	}
}
