/**
 * Lucene 4.9 实现近音词查询的分析器示例
 * */

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.codec.language.Metaphone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class MetaPhoneDemo {
	private static Version matchVersion=Version.LUCENE_4_9;
	
	static final class MetaphoneReplacementAnalyzer extends Analyzer{
		@Override
		protected TokenStreamComponents createComponents(String arg0,
				Reader arg1) {
			Tokenizer source=new LetterTokenizer(matchVersion,arg1);
			TokenStream result=new MetaphoneReplacementFilter(source);
			return new TokenStreamComponents(source,result);
		}
	}

	/*TokenFilter根据等效音素替换词汇单元*/
	static final class MetaphoneReplacementFilter extends TokenFilter{
		static final String METAPHONE="metaphone";
		Metaphone metaphoner=new Metaphone();
		final CharTermAttribute termAtt=addAttribute(CharTermAttribute.class);
		final TypeAttribute typeAtt=addAttribute(TypeAttribute.class);
		protected MetaphoneReplacementFilter(TokenStream input) {
			super(input);
		}
		@Override
		public final boolean incrementToken() throws IOException {
			if(!input.incrementToken())
				return false;
			String encoded=metaphoner.encode(termAtt.toString());
			termAtt.append(encoded);
			typeAtt.setType(METAPHONE);
			return true;
		}
	}
	
	//近音词分析器显示结果
	public static void test1() throws IOException{
		String[] strs=new String[]{
				"kool kat",
				"cool cat",
				"The quick brown fox jumped over the lazy dog",
				"Tha quik brown phox jumpd ovear tha lzai dag"};
		MetaphoneReplacementAnalyzer analyzer=new MetaphoneReplacementAnalyzer();
		TokenStream stream;
		for(String str:strs){
			stream=analyzer.tokenStream("filed", new StringReader(str));
		    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		    try {
		    	stream.reset();
		        while (stream.incrementToken()) 
		        	System.out.print("["+termAtt+"] ");
		        System.out.println();
		        stream.end();
		        } finally {
		        stream.close();
		        }
		    }
	}
	
	//搜索近音词
	public static void test2() throws IOException, ParseException{
		Directory dir=new RAMDirectory();
		Analyzer analyzer=new MetaphoneReplacementAnalyzer();
		IndexWriterConfig iwc=new IndexWriterConfig(matchVersion,analyzer);
		IndexWriter writer=new IndexWriter(dir,iwc);
		Document doc=new Document();
		doc.add(new Field("contents","cool cat",TextField.TYPE_STORED));//添加contents域
		writer.addDocument(doc);//建立索引
		writer.close();
		IndexSearcher searcher=new IndexSearcher(DirectoryReader.open(dir));
		Query query=new QueryParser(Version.LUCENE_4_9,"contents",analyzer).parse("kool cat");//根据kool cat检索文档
		TopDocs hits=searcher.search(query, 1);
		System.out.println("命中的个数为"+hits.totalHits);
		Document result=searcher.doc(hits.scoreDocs[0].doc);
		System.out.println(result.get("contents"));
	}
	
	/*搜索近音词*/
	public static void main(String[] args) throws IOException, ParseException {
		test1();
		test2();
		System.out.println("测试完成");
	}
}

