package com.homeproject.namematcher.lucene;

import static com.homeproject.namematcher.util.Checker.*;
import static com.homeproject.namematcher.util.Constants.*;
import static com.homeproject.namematcher.util.FileUtil.*;

import java.util.*;
import java.io.*;
import java.util.logging.Logger;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery;


public class Indexer implements Closeable {
	private final static Logger LOGGER = Logger.getLogger(Indexer.class.getName());
	
	private final static float DEFAULT_MIN_SIMILARITY = 2.0f;
	private final static int DEFAULT_PREFIX_LENGTH = 1;
    
    private Directory indexDir;
    private Analyzer analyzer;
    private IndexWriter writer;
    
    public Indexer(String inputFile) throws IOException {   	
    	indexDir = new RAMDirectory();
    	analyzer = new StandardAnalyzer();
    	
    	IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    	indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    	writer = new IndexWriter(indexDir, indexWriterConfig);	
    	
    	
		InputStream fis = readAsIOStream(inputFile, this.getClass());
		List<String> lines = readFromIOStream(fis);
		for(String line: lines) {
			Document doc = getDocument(inputFile, line);
			writer.addDocument(doc);
		}
    	writer.close();
    }
    
    @Override
    public void close() throws IOException {
    	LOGGER.info("Index is closed");
    	indexDir.close();
    }

    public Document getDocument(String fileName, String line)
    		throws IOException {
    	String filePath = getFileFromFileName(fileName, this.getClass()).getName();
    	Document doc = new Document();
    	doc.add(new StringField(LUCENE_FILE_NAME, fileName, Field.Store.YES)); 
    	doc.add(new StringField(LUCENE_FILE_PATH, filePath, Field.Store.YES));
    	doc.add(new TextField(LUCENE_RAW_CONTENT, line, Field.Store.YES));
    	
    	return doc;
    }
    
//    public Set<String> extractTermsInStr(String text) throws IOException {
//        Set<String> queryTermsInStr = new HashSet<String>();
//        TokenStream tokenStream = analyzer.tokenStream(LUCENE_RAW_CONTENT, text);
//        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
//        tokenStream.reset();
//        while (tokenStream.incrementToken()) {
//        	queryTermsInStr.add(attr.toString());
//        }
//        return queryTermsInStr;
//    }
    
//    public void addFileToIndex(String inputFile) throws IOException {
//    	LOGGER.info(String.format("Indexing %s", inputFile));
//    	
//    	IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
//    	indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//    	IndexWriter writer = new IndexWriter(indexDir, indexWriterConfig);	
//    	
//    	InputStream fis = readAsIOStream(inputFile, this.getClass());
//    	List<String> lines = readFromIOStream(fis);
//    	for(String line: lines) {
//    		Document doc = getDocument(inputFile, line);
//    		writer.addDocument(doc);
//    	}
//    	writer.close();
//    }
    
    public List<Document> searchIndex(Query query) throws IOException {
    	LOGGER.info("Searching by query");
    	
    	IndexReader indexReader = DirectoryReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 100);
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {      	
        	if(isGoodScore(topDocs.getMaxScore(), scoreDoc.score)) {
        		documents.add(searcher.doc(scoreDoc.doc));    		
        	}   	  
        }
        indexReader.close();
        return documents;
    }
    
	public List<Document> searchIndex(String inField, String queryString)
			throws IOException, ParseException {
		LOGGER.info(String.format("Creating query from %s in %s field", inField, queryString));
		Query query = new QueryParser(inField, analyzer).parse(queryString);
        return searchIndex(query);
	}
    
    public List<Document> searchIndexWithFuzzyQuery(String nameQueryString) throws IOException {
    	LOGGER.info(String.format("Matching %s", nameQueryString));
    	
    	Set<String> analyzedQueryString = new HashSet<>();
	    try(TokenStream tokenStream = analyzer.tokenStream(LUCENE_RAW_CONTENT, nameQueryString)) {
	        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
	        tokenStream.reset();      
	        while (tokenStream.incrementToken()) {
	        	analyzedQueryString.add(attr.toString());
	        }
	    }
        
    	FuzzyLikeThisQuery flt = new FuzzyLikeThisQuery(LUCENE_MAX_NUM_TERMS, analyzer);
    	for(String str: analyzedQueryString) {
    		flt.addTerms(str, LUCENE_RAW_CONTENT, DEFAULT_MIN_SIMILARITY, DEFAULT_PREFIX_LENGTH);
    	}
    	//Query query = flt.rewrite(searcher.getIndexReader());
    	return searchIndex(flt);
    }
	  
}
