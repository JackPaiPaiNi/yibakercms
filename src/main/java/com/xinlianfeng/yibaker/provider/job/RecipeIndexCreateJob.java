package com.xinlianfeng.yibaker.provider.job;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.xinlianfeng.yibaker.common.entity.Recipe;
import com.xinlianfeng.yibaker.common.entity.SearcherInfo.IndexFieldType;
import com.xinlianfeng.yibaker.common.exception.YiBakerException;
import com.xinlianfeng.yibaker.common.service.RecipeService;

/**
 * @Description: 
 * @date: 2015-10-29 
 * @author Tanglinquan 
 */
@Component
public class RecipeIndexCreateJob
{
	/** */
	private static final Logger logger = LoggerFactory.getLogger(RecipeIndexCreateJob.class);

	/** */
	@Autowired
	private RecipeService recipeService;

	/** */
	@Autowired
	private IndexWriter recipeWriter;
	
	@Autowired
	private IndexWriter recipeWriter3;
	/***
	 * 启动两个方法
	 *
	 */
	public void start(){
		work();
		work_3();
	}

	/***
	 * Yibaker3.0接口 Lucene 检索
	 */
	public void work_3()
	{
		try
		{
			logger.info("更新recipe索引.................. START");

			List<Recipe> recipes = recipeService.getSearchAll();
			recipeWriter3.deleteAll();
			recipeWriter3.commit();

			for (Recipe recipe : recipes)
			{
				long recipe_id = recipe.getRecipe_id();
				String recipeName = recipe.getRecipe_name();
				String recipeBrief = recipe.getRecipe_brief();
				String recipeContent = recipe.getRecipe_content();
				String keyword = recipe.getKeyword();
				Document document = new Document();
				document.add(new LongField(IndexFieldType.INDEX_RECIPE_ID.name(), recipe_id, Field.Store.YES)); // 菜谱编号

				// 中文
				if (StringUtils.isNotEmpty(recipeName))
				{
					document.add(new TextField(IndexFieldType.INDEX_RECIPE_NAME.name(), recipeName, Field.Store.YES)); // 菜谱名称
				}

				if (StringUtils.isNotEmpty(keyword))
				{
					document.add(new TextField(IndexFieldType.INDEX_RECIPE_KEYWORD.name(), keyword, Field.Store.YES)); // 菜谱名称
				}

//				if (StringUtils.isNotEmpty(recipeBrief))
//				{
//					document.add(new TextField(IndexFieldType.INDEX_RECIPE_BRIEF.name(), recipeBrief, Field.Store.YES)); // 菜谱简介
//				}
//
//				if (StringUtils.isNotEmpty(recipeContent))
//				{
//					document.add(new TextField(IndexFieldType.INDEX_RECIPE_CONTENT.name(), recipeContent, Field.Store.YES)); // 菜谱内容描述
//				}

				// 创建索引
				recipeWriter3.addDocument(document);
				logger.debug("update or insert index == > work_3" + document);
			}
			logger.info("更新recipe索引.................. END work_3");
		}
		catch (Exception e)
		{
			throw new YiBakerException("创建更新索引[recipe]失败:", e);
		}
		finally
		{
			try
			{
				recipeWriter3.commit();
			}
			catch (Exception e)
			{
				throw new YiBakerException("创建commit索引[recipe]失败:", e);
			}
		}
	}
	/**
	 * 创建食谱索引
	 * 
	 * @return void
	 */
	public void work()
	{
		try
		{
			logger.info("更新recipe索引.................. START     work_3");

			List<Recipe> recipes = recipeService.getSearch();
			recipeWriter.deleteAll();
			recipeWriter.commit();

			for (Recipe recipe : recipes)
			{
				long recipe_id = recipe.getRecipe_id();
				String recipeName = recipe.getRecipe_name();
				String recipeBrief = recipe.getRecipe_brief();
				String recipeContent = recipe.getRecipe_content();
				String keyword = recipe.getKeyword();
				Document document = new Document();
				document.add(new LongField(IndexFieldType.INDEX_RECIPE_ID.name(), recipe_id, Field.Store.YES)); // 菜谱编号

				// 中文
				if (StringUtils.isNotEmpty(recipeName))
				{
					document.add(new TextField(IndexFieldType.INDEX_RECIPE_NAME.name(), recipeName, Field.Store.YES)); // 菜谱名称
				}

				if (StringUtils.isNotEmpty(keyword))
				{
					document.add(new TextField(IndexFieldType.INDEX_RECIPE_KEYWORD.name(), keyword, Field.Store.YES)); // 菜谱名称
				}

//				if (StringUtils.isNotEmpty(recipeBrief))
//				{
//					document.add(new TextField(IndexFieldType.INDEX_RECIPE_BRIEF.name(), recipeBrief, Field.Store.YES)); // 菜谱简介
//				}
//
//				if (StringUtils.isNotEmpty(recipeContent))
//				{
//					document.add(new TextField(IndexFieldType.INDEX_RECIPE_CONTENT.name(), recipeContent, Field.Store.YES)); // 菜谱内容描述
//				}

				// 创建索引
				recipeWriter.addDocument(document);
				logger.debug("update or insert index  == > work" + document);
			}
			logger.info("更新recipe索引.................. END work");
		}
		catch (Exception e)
		{
			throw new YiBakerException("创建更新索引[recipe]失败:", e);
		}
		finally
		{
			try
			{
				recipeWriter.commit();
			}
			catch (Exception e)
			{
				throw new YiBakerException("创建commit索引[recipe]失败:", e);
			}
		}
	}
}
