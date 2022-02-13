package com.homeproject.namematcher;

import static org.junit.jupiter.api.Assertions.*;

import static com.homeproject.namematcher.util.Constants.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


public class QuerySearchTests {
	private Directory directory;
	private Analyzer analyzer;
	
	@BeforeEach
	public void setup() throws IOException {
		analyzer = new StandardAnalyzer();
	    directory = new RAMDirectory();
	    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
	    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    	IndexWriter writer = new IndexWriter(directory, indexWriterConfig);	
    	
	    writer.addDocument(getDoc("1", "jonathon smythe"));
	    writer.addDocument(getDoc("2", "jonathan smith"));
	    writer.addDocument(getDoc("3", "johnathon smyth"));
	    writer.addDocument(getDoc("4", "johnny smith"));
	    writer.addDocument(getDoc("5", "jonny smith"));
	    writer.addDocument(getDoc("6", "johnathon smythe"));
	    
	    writer.close();
	}
	
	@AfterEach
	public void tearDown() throws IOException {
		directory.close();
	}
	
	private Document getDoc(String fileName, String line) throws IOException {
		    Document doc = new Document();
		    doc.add(new StringField(LUCENE_FILE_NAME, fileName, Field.Store.YES)); 
	    	doc.add(new TextField(LUCENE_RAW_CONTENT, line, Field.Store.YES));
		    return doc;
		  }
	  
	@ParameterizedTest
	@ValueSource(strings= {"jonathin smoth", "smoth jonathin"})
	public void givenMultiWordWhenFoundThenCorrect(String inputName) throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
		FuzzyLikeThisQuery flt = new FuzzyLikeThisQuery(LUCENE_MAX_NUM_TERMS, analyzer);
		flt.addTerms(inputName, LUCENE_RAW_CONTENT, 0.3f, 1);
	    Query query = flt.rewrite(searcher.getIndexReader());
	    
	    Set<String> queryTermsInStr = new HashSet<>();
	    try(TokenStream tokenStream = analyzer.tokenStream(LUCENE_RAW_CONTENT, inputName)) {
	        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
	        tokenStream.reset();      
	        while (tokenStream.incrementToken()) {
	        	queryTermsInStr.add(attr.toString());
	        }
	    }
        
        assertTrue(queryTermsInStr.contains("jonathin"));
        assertTrue(queryTermsInStr.contains("smoth"));
        
        TopDocs topDocs = searcher.search(query, 10);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        
        assertEquals(6, documents.size());
		reader.close();
	}
	
	@ParameterizedTest
	@ValueSource(strings= {"to the jonathan smith", "smith, jonathan, jonathan and smith"})
	public void givenWithStopwordWhenFoundThenCorrect(String inputName) throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
		FuzzyLikeThisQuery flt = new FuzzyLikeThisQuery(LUCENE_MAX_NUM_TERMS, analyzer);
		flt.addTerms(inputName, LUCENE_RAW_CONTENT, 0.3f, 1);
	    Query query = flt.rewrite(searcher.getIndexReader());
	    
	    Set<String> queryTermsInStr = new HashSet<>();
	    TokenStream tokenStream = analyzer.tokenStream(LUCENE_RAW_CONTENT, inputName);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();      
        while (tokenStream.incrementToken()) {
        	queryTermsInStr.add(attr.toString());
        }
        
        assertTrue(queryTermsInStr.contains("jonathan"));
        assertTrue(queryTermsInStr.contains("smith"));
        
        TopDocs topDocs = searcher.search(query, 10);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
        	documents.add(searcher.doc(scoreDoc.doc));
        }
        
        assertEquals(6, documents.size());
		reader.close();
	}
	
	@ParameterizedTest
	@ValueSource(strings= {"elizabeth dale"})
	public void givenBugWordWhenNotFoundThenCorrect(String inputName) throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    
		FuzzyLikeThisQuery flt = new FuzzyLikeThisQuery(LUCENE_MAX_NUM_TERMS, analyzer);
		flt.addTerms(inputName, LUCENE_RAW_CONTENT, 0.3f, 1);
	    Query query = flt.rewrite(searcher.getIndexReader());
	    
	    Set<String> queryTermsInStr = new HashSet<>();
	    TokenStream tokenStream = analyzer.tokenStream(LUCENE_RAW_CONTENT, inputName);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();      
        while (tokenStream.incrementToken()) {
        	queryTermsInStr.add(attr.toString());
        }
        
        TopDocs topDocs = searcher.search(query, 10);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }
        
        assertEquals(0, documents.size());
		reader.close();
	}
}
