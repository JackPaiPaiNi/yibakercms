package com.xinlianfeng.yibaker.provider.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xinlianfeng.server.common.cache.local.MemoryAnnotation;
import com.xinlianfeng.server.common.cache.local.MemoryInterceptor;
import com.xinlianfeng.yibaker.common.entity.SearcherInfo;
import com.xinlianfeng.yibaker.common.entity.SearcherInfo.IndexFieldType;
import com.xinlianfeng.yibaker.provider.utils.lucene.PinyinAnalyzer;

/**
 * @Description:
 * @date: 2015-10-29
 * @author Tanglinquan
 */
public class SearcherService
{
	/** */
	private static final Logger logger = LoggerFactory.getLogger(SearcherService.class);

	/** */
	private static SearcherService searcherService;

	/** */
	@Autowired
	private PinyinAnalyzer pinyinAnalyzer;

	/** */
	@Autowired
	private FSDirectory recipeDirectory;

	/** */
	@Autowired
	private FSDirectory recipeIngredientDirectory;
	
	/** */
	@Autowired
	private FSDirectory recipeDirectory3;

	/** */
	@Autowired
	private FSDirectory recipeIngredientDirectory3;

	private DirectoryReader recipeDirectoryReader;

	private DirectoryReader recipeIngredientDirectoryReader;
	
	private DirectoryReader recipeDirectoryReader3;

	private DirectoryReader recipeIngredientDirectoryReader3;

	private IndexSearcher recipeSearcher;

	private IndexSearcher recipeIngredientSearcher;
	

	private IndexSearcher recipeSearcher3;

	private IndexSearcher recipeIngredientSearcher3;

	/**
	 * 本地缓存
	 * 
	 * @throws IOException
	 */
	public static synchronized final SearcherService getInstance() throws IOException
	{
		if (searcherService == null)
		{
			MemoryInterceptor<SearcherService> memoryInterceptor = new MemoryInterceptor<SearcherService>();
			SearcherService service = new SearcherService();
			searcherService = memoryInterceptor.createProxy(service);
		}
		return searcherService;
	}

	/**
	 * 搜索菜谱ID
	 * 
	 * @param keyword
	 * @throws IOException
	 * @return List<Long>
	 * @throws ParseException
	 */
	@MemoryAnnotation(time = 600)
	public List<Long> searcherRecipeId(String keyword) throws IOException, ParseException
	{
		
		logger.info("searcher keyword : " + keyword);
		IndexSearcher rSearcher = this.getRecipeSearcher();
		IndexSearcher riSearcher = this.getRecipeIngredientSearcher();
		List<SearcherInfo> searcherInfos = new ArrayList<SearcherInfo>();
		// 食谱中文分词搜索
		QueryParser indexPNParser = new QueryParser(IndexFieldType.INDEX_RECIPE_NAME.name(), pinyinAnalyzer);
		indexPNParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query indexRNQuery = indexPNParser.parse(keyword);
		searcherInfos.addAll(this.searcher(rSearcher, IndexFieldType.INDEX_RECIPE_NAME, indexRNQuery));

		QueryParser indexkwParser = new QueryParser(IndexFieldType.INDEX_RECIPE_KEYWORD.name(), pinyinAnalyzer);
		indexkwParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query indexkwQuery = indexkwParser.parse(keyword);
		searcherInfos.addAll(this.searcher(rSearcher, IndexFieldType.INDEX_RECIPE_KEYWORD, indexkwQuery));

//		QueryParser indexRBParser = new QueryParser(IndexFieldType.INDEX_RECIPE_BRIEF.name(), pinyinAnalyzer);
//		indexRBParser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query indexRBQuery = indexRBParser.parse(keyword);
//		searcherInfos.addAll(this.searcher(rSearcher, IndexFieldType.INDEX_RECIPE_BRIEF, indexRBQuery));

//		QueryParser indexRCParser = new QueryParser(IndexFieldType.INDEX_RECIPE_CONTENT.name(), pinyinAnalyzer);
//		indexRCParser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query indexRCQuery = indexRCParser.parse(keyword);
//		searcherInfos.addAll(this.searcher(rSearcher, IndexFieldType.INDEX_RECIPE_CONTENT, indexRCQuery));

		// 食材中文分词搜索
		QueryParser indexINParser = new QueryParser(IndexFieldType.INDEX_INGREDIENT_NAME.name(), pinyinAnalyzer);
		indexINParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query indexINQuery = indexINParser.parse(keyword);
		searcherInfos.addAll(this.searcher(riSearcher, IndexFieldType.INDEX_INGREDIENT_NAME, indexINQuery));

		// 排序
		searcherInfoSort(searcherInfos);

		List<Long> ids = new ArrayList<Long>();

		// 去重复
		Set<SearcherInfo> hashSet = new HashSet<SearcherInfo>(searcherInfos);
		for (SearcherInfo searcherInfo : hashSet)
		{
			ids.add(Long.valueOf(searcherInfo.getRecipeId()));
		}
		
		return ids;
	}
	/**
	 * 搜索菜谱ID
	 * 
	 * @param keyword
	 * @throws IOException
	 * @return List<Long>
	 * @throws ParseException
	 */
	@MemoryAnnotation(time = 600)
	public List<Long> searcherRecipeId3(String keyword) throws IOException, ParseException
	{
		
		logger.info("searcher keyword : " + keyword);
		IndexSearcher rSearcher3 = this.getRecipeSearcher3();
		IndexSearcher riSearcher3 = this.getRecipeIngredientSearcher3();
		List<SearcherInfo> searcherInfos3 = new ArrayList<SearcherInfo>();
		// 食谱中文分词搜索
		QueryParser indexPNParser = new QueryParser(IndexFieldType.INDEX_RECIPE_NAME.name(), pinyinAnalyzer);
		indexPNParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query indexRNQuery = indexPNParser.parse(keyword);
		searcherInfos3.addAll(this.searcher(rSearcher3, IndexFieldType.INDEX_RECIPE_NAME, indexRNQuery));

		QueryParser indexkwParser = new QueryParser(IndexFieldType.INDEX_RECIPE_KEYWORD.name(), pinyinAnalyzer);
		indexkwParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query indexkwQuery = indexkwParser.parse(keyword);
		searcherInfos3.addAll(this.searcher(rSearcher3, IndexFieldType.INDEX_RECIPE_KEYWORD, indexkwQuery));

//		QueryParser indexRBParser = new QueryParser(IndexFieldType.INDEX_RECIPE_BRIEF.name(), pinyinAnalyzer);
//		indexRBParser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query indexRBQuery = indexRBParser.parse(keyword);
//		searcherInfos.addAll(this.searcher(rSearcher, IndexFieldType.INDEX_RECIPE_BRIEF, indexRBQuery));

//		QueryParser indexRCParser = new QueryParser(IndexFieldType.INDEX_RECIPE_CONTENT.name(), pinyinAnalyzer);
//		indexRCParser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query indexRCQuery = indexRCParser.parse(keyword);
//		searcherInfos.addAll(this.searcher(rSearcher, IndexFieldType.INDEX_RECIPE_CONTENT, indexRCQuery));

		// 食材中文分词搜索
		QueryParser indexINParser = new QueryParser(IndexFieldType.INDEX_INGREDIENT_NAME.name(), pinyinAnalyzer);
		indexINParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query indexINQuery = indexINParser.parse(keyword);
		searcherInfos3.addAll(this.searcher(riSearcher3, IndexFieldType.INDEX_INGREDIENT_NAME, indexINQuery));

		// 排序
		searcherInfoSort(searcherInfos3);

		List<Long> ids = new ArrayList<Long>();

		// 去重复
		Set<SearcherInfo> hashSet = new HashSet<SearcherInfo>(searcherInfos3);
		for (SearcherInfo searcherInfo : hashSet)
		{
			ids.add(Long.valueOf(searcherInfo.getRecipeId()));
		}
		
		return ids;
	}

	private List<SearcherInfo> searcher(IndexSearcher searcher, IndexFieldType fieldType, Query query) throws IOException
	{
		List<SearcherInfo> searcherInfos = new ArrayList<SearcherInfo>();
		logger.info("searcher fieldType[" + fieldType + "] 分词：" + query.toString());
		TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
		logger.info("searcher fieldType[" + fieldType + "] 总共匹配多少个：" + topDocs.totalHits);
		ScoreDoc[] hits = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : hits)
		{
			logger.info("匹配得分：" + scoreDoc.score);
			logger.info("文档索引ID：" + scoreDoc.doc);
			Document document = searcher.doc(scoreDoc.doc);
			String recipeId = document.get(IndexFieldType.INDEX_RECIPE_ID.name());
			String recipeName = document.get(fieldType.name());
			logger.info("食谱ID：" + recipeId);
			logger.info("食谱name：" + recipeName);
			searcherInfos.add(new SearcherInfo(scoreDoc.doc, fieldType, recipeId, scoreDoc.score));
		}
		return searcherInfos;
	}

	private void searcherInfoSort(List<SearcherInfo> searcherInfos)
	{
		Collections.sort(searcherInfos, new Comparator<SearcherInfo>()
		{
			@Override
			public int compare(SearcherInfo o1, SearcherInfo o2)
			{
				// 根据字段排序
				if (o1.getFieldType().getSequence() < o2.getFieldType().getSequence())
				{
					return 1;
				}
				else if (o1.getFieldType().getSequence() > o2.getFieldType().getSequence())
				{
					return -1;
				}
				else
				{
					// 根据匹配分数排序
					if (o1.getScore() < o2.getScore())
					{
						return 1;
					}
					else if (o1.getScore() > o2.getScore())
					{
						return -1;
					}
					else
					{
						return 0;
					}
				}
			}
		});
	}

	public synchronized IndexSearcher getRecipeSearcher() throws IOException
	{
		if (this.recipeDirectoryReader == null)
		{
			this.recipeDirectoryReader = DirectoryReader.open(recipeDirectory);
			this.recipeSearcher = new IndexSearcher(recipeDirectoryReader);
		}

		if (!recipeDirectoryReader.isCurrent())
		{
			logger.info("RecipeSearcher current openIfChanged start!");
			DirectoryReader openIfChanged = DirectoryReader.open(recipeDirectory);
			recipeDirectoryReader.close();
			this.recipeDirectoryReader = openIfChanged;
			this.recipeSearcher = new IndexSearcher(openIfChanged);
			logger.info("RecipeSearcher current openIfChanged end!");
		}

		return this.recipeSearcher;
	}
	public synchronized IndexSearcher getRecipeSearcher3() throws IOException
	{
		if (this.recipeDirectoryReader3 == null)
		{
			this.recipeDirectoryReader3 = DirectoryReader.open(recipeDirectory3);
			this.recipeSearcher3 = new IndexSearcher(recipeDirectoryReader3);
		}

		if (!recipeDirectoryReader3.isCurrent())
		{
			logger.info("RecipeSearcher3 current openIfChanged start!");
			DirectoryReader openIfChanged = DirectoryReader.open(recipeDirectory3);
			recipeDirectoryReader3.close();
			this.recipeDirectoryReader3 = openIfChanged;
			this.recipeSearcher3 = new IndexSearcher(openIfChanged);
			logger.info("RecipeSearcher3 current openIfChanged3 end!");
		}

		return this.recipeSearcher3;
	}

	public synchronized IndexSearcher getRecipeIngredientSearcher() throws IOException
	{
		if (this.recipeIngredientDirectoryReader == null)
		{
			this.recipeIngredientDirectoryReader = DirectoryReader.open(recipeIngredientDirectory);
			this.recipeIngredientSearcher = new IndexSearcher(recipeDirectoryReader);
		}

		if (!recipeIngredientDirectoryReader.isCurrent())
		{
			logger.info("RecipeIngredientSearcher current openIfChanged start!");
			DirectoryReader openIfChanged = DirectoryReader.open(recipeIngredientDirectory);
			this.recipeIngredientDirectoryReader.close();
			this.recipeIngredientDirectoryReader = openIfChanged;
			this.recipeIngredientSearcher = new IndexSearcher(openIfChanged);
			logger.info("RecipeIngredientSearcher current openIfChanged end!");
		}

		return this.recipeIngredientSearcher;
	}
	public synchronized IndexSearcher getRecipeIngredientSearcher3() throws IOException
	{
		if (this.recipeIngredientDirectoryReader3 == null)
		{
			this.recipeIngredientDirectoryReader3 = DirectoryReader.open(recipeIngredientDirectory3);
			this.recipeIngredientSearcher3 = new IndexSearcher(recipeDirectoryReader3);
		}

		if (!recipeIngredientDirectoryReader3.isCurrent())
		{
			logger.info("RecipeIngredientSearcher3 current openIfChanged start!");
			DirectoryReader openIfChanged = DirectoryReader.open(recipeIngredientDirectory3);
			this.recipeIngredientDirectoryReader3.close();
			this.recipeIngredientDirectoryReader3 = openIfChanged;
			this.recipeIngredientSearcher3 = new IndexSearcher(openIfChanged);
			logger.info("RecipeIngredientSearcher3 current openIfChanged end!");
		}

		return this.recipeIngredientSearcher3;
	}
}
