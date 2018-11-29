package com.xinlianfeng.yibaker.provider.job;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xinlianfeng.yibaker.common.entity.RecipeIngredient;
import com.xinlianfeng.yibaker.common.entity.SearcherInfo.IndexFieldType;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.provider.dao.RecipeIngredientDao;

/**
 * @Description:
 * @date: 2015-10-29
 * @author Tanglinquan
 */
@Component
public class RecipeIngredientIndexCreateJob
{
	/** */
	private static final Logger logger = LoggerFactory.getLogger(RecipeIndexCreateJob.class);

	/** */
	@Autowired
	private RecipeIngredientDao recipeIngredientDao;

	/** */
	@Autowired
	private IndexWriter recipeIngredientWriter;
	
	@Autowired
	private IndexWriter recipeIngredientWriter3;
	/***
	 * 启动
	 */
	public void start(){
		work();
		work_3();
	}
	/**
	 * 创建食谱材料索引
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void work()
	{
		try
		{
			logger.info("更新recipe ingredient索引.................. START");
			List<RecipeIngredient> recipeIngredients = recipeIngredientDao.getRecipeIngredientAll();
			recipeIngredientWriter.deleteAll();
			recipeIngredientWriter.commit();
			for (RecipeIngredient recipeIngredient : recipeIngredients)
			{
				long ingredientId = recipeIngredient.getIngredient_id();
				long recipeId = recipeIngredient.getRecipe_id();
				String ingredientName = recipeIngredient.getIngredient_name();
				if (StringUtils.isNotEmpty(ingredientName))
				{
					Document document = new Document();
					document.add(new LongField(IndexFieldType.INDEX_INGREDIENT_ID.name(), ingredientId, Field.Store.YES)); // 菜谱编号
					document.add(new LongField(IndexFieldType.INDEX_RECIPE_ID.name(), recipeId, Field.Store.YES)); // 菜谱编号
					document.add(new TextField(IndexFieldType.INDEX_INGREDIENT_NAME.name(), ingredientName, Field.Store.YES)); // 菜谱内容描述

					// 创建索引
					logger.debug("update or insert index == >" + document);
					recipeIngredientWriter.addDocument(document);
				}
			}
			logger.info("更新recipe ingredient索引.................. END");
		}
		catch (Exception e)
		{
			throw new YiBakerException("创建更新索引[recipe ingredient]失败:", e);
		}
		finally
		{
			try
			{
				recipeIngredientWriter.commit();
			}
			catch (Exception e)
			{
				throw new YiBakerException("创建commit索引[recipe]失败:", e);
			}
		}
	}
	/**
	 * 创建食谱材料索引
	 * 
	 * @throws IOException
	 * @return void
	 */
	public void work_3()
	{
		try
		{
			logger.info("更新recipe ingredient索引.................. START RecipeIngredient");
			List<RecipeIngredient> recipeIngredients = recipeIngredientDao.getRecipeIngredient();
			recipeIngredientWriter3.deleteAll();
			recipeIngredientWriter3.commit();
			for (RecipeIngredient recipeIngredient : recipeIngredients)
			{
				long ingredientId = recipeIngredient.getIngredient_id();
				long recipeId = recipeIngredient.getRecipe_id();
				String ingredientName = recipeIngredient.getIngredient_name();
				if (StringUtils.isNotEmpty(ingredientName))
				{
					Document document = new Document();
					document.add(new LongField(IndexFieldType.INDEX_INGREDIENT_ID.name(), ingredientId, Field.Store.YES)); // 菜谱编号
					document.add(new LongField(IndexFieldType.INDEX_RECIPE_ID.name(), recipeId, Field.Store.YES)); // 菜谱编号
					document.add(new TextField(IndexFieldType.INDEX_INGREDIENT_NAME.name(), ingredientName, Field.Store.YES)); // 菜谱内容描述

					// 创建索引
					logger.debug("update or insert index == >" + document);
					recipeIngredientWriter3.addDocument(document);
				}
			}
			logger.info("更新recipe ingredient索引.................. END RecipeIngredient");
		}
		catch (Exception e)
		{
			throw new YiBakerException("创建更新索引[recipe ingredient]失败:", e);
		}
		finally
		{
			try
			{
				recipeIngredientWriter3.commit();
			}
			catch (Exception e)
			{
				throw new YiBakerException("创建commit索引[recipe]失败:", e);
			}
		}
	}
}
