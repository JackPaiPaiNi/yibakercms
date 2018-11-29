package com.xinlianfeng.yibaker.provider.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.xinlianfeng.yibaker.common.resp.RecipeDetailResp;
import com.xinlianfeng.yibaker.common.resp.RecipeListResp;
import com.xinlianfeng.yibaker.common.service.RecipeService;
import com.xinlianfeng.yibaker.provider.utils.lucene.Chinese2PY;

/**
 * @Description: 
 * @date: 2015-10-30 
 * @author Tanglinquan 
 */
@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Component
public class RecipeServiceTest
{

	@Autowired
	private RecipeService recipeService;

	@Test
	public void searchRecipeList() throws Exception
	{
		String keyword = "饼干";
		RecipeListResp searchRecipeList = recipeService.searchRecipeList(keyword, 0, Integer.MAX_VALUE);
		List<RecipeDetailResp> recipelist = searchRecipeList.getRecipelist();
		for (RecipeDetailResp recipe : recipelist)
		{
			System.out.println(recipe.getRecipe().getRecipe_name() + "---" + recipe.getRecipe().getRecipe_content());
			Assert.assertTrue(recipe.getRecipe().getRecipe_name().indexOf(keyword) > 0 || recipe.getRecipe().getRecipe_content().indexOf(keyword) > 0);
		}

		searchRecipeList = recipeService.searchRecipeList(Chinese2PY.getPinYin(keyword), 0, Integer.MAX_VALUE);
		recipelist = searchRecipeList.getRecipelist();
		for (RecipeDetailResp recipe : recipelist)
		{
			System.out.println(recipe.getRecipe().getRecipe_name() + "---" + recipe.getRecipe().getRecipe_content());
			Assert.assertTrue(recipe.getRecipe().getRecipe_name().indexOf(keyword) > 0 || recipe.getRecipe().getRecipe_content().indexOf(keyword) > 0);
		}
	}
}
