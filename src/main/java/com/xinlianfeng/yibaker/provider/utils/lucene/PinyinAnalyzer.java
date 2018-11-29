package com.xinlianfeng.yibaker.provider.utils.lucene;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;

/**
 * 
 * @Description: 自定义拼音分词器
 * @date: 2015-10-31
 * @author Tanglinquan
 */
public class PinyinAnalyzer extends Analyzer
{
	private final int minGram;
	private final int maxGram;
	private final boolean useSmart;

	public PinyinAnalyzer()
	{
		super();
		this.maxGram = PinyinConstants.DEFAULT_MAX_GRAM;
		this.minGram = PinyinConstants.DEFAULT_MIN_GRAM;
		this.useSmart = PinyinConstants.DEFAULT_IK_USE_SMART;
	}

	public PinyinAnalyzer(boolean useSmart)
	{
		super();
		this.maxGram = PinyinConstants.DEFAULT_MAX_GRAM;
		this.minGram = PinyinConstants.DEFAULT_MIN_GRAM;
		this.useSmart = useSmart;
	}

	public PinyinAnalyzer(int maxGram)
	{
		super();
		this.maxGram = maxGram;
		this.minGram = PinyinConstants.DEFAULT_MIN_GRAM;
		this.useSmart = PinyinConstants.DEFAULT_IK_USE_SMART;
	}

	public PinyinAnalyzer(int maxGram, boolean useSmart)
	{
		super();
		this.maxGram = maxGram;
		this.minGram = PinyinConstants.DEFAULT_MIN_GRAM;
		this.useSmart = useSmart;
	}

	public PinyinAnalyzer(int minGram, int maxGram, boolean useSmart)
	{
		super();
		this.minGram = minGram;
		this.maxGram = maxGram;
		this.useSmart = useSmart;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName)
	{
		Reader reader = new BufferedReader(new StringReader(fieldName));
		Tokenizer tokenizer = new IKTokenizer5x(reader, useSmart);
		// 转拼音
		TokenStream tokenStream = new PinyinTokenFilter(tokenizer, PinyinConstants.DEFAULT_FIRST_CHAR, PinyinConstants.DEFAULT_MIN_TERM_LRNGTH);
		// 对拼音进行NGram处理
		tokenStream = new PinyinNGramTokenFilter(tokenStream, this.minGram, this.maxGram);
		tokenStream = new LowerCaseFilter(tokenStream);
		tokenStream = new StopFilter(tokenStream, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		return new Analyzer.TokenStreamComponents(tokenizer, tokenStream);
	}
	}
