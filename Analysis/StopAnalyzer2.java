/**
 * Lucene 4.9 TokenStream管道顺序对处理结果影响的示例
 * */
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class StopAnalyzer2 extends Analyzer{
	private Set stopWords;
	private final Version matchVersion=Version.LUCENE_4_9;
	public StopAnalyzer2(){
		stopWords=StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	}
	public StopAnalyzer2(String []stopWords){
		this.stopWords=StopFilter.makeStopSet(matchVersion, stopWords);
	}
	
	//正确的情况是case2先进行大小写转化,再进行停用词过滤,而case1正好相反,未能过滤大写的停用词
	@Override
	protected TokenStreamComponents createComponents(String arg0, Reader arg1) {
		final Tokenizer source=new LetterTokenizer(matchVersion,arg1);
		//case 1
		//TokenStream result=new LowerCaseFilter(matchVersion,new StopFilter(matchVersion,source,(CharArraySet) stopWords));
		//case 2
		TokenStream result=new StopFilter(matchVersion,new LowerCaseFilter(matchVersion,source),(CharArraySet) stopWords);
		return new TokenStreamComponents(source,result);
	}
	public static void main(String[] args) throws IOException {
		String[] strs=new String[]{"The Quick Brown ..."};
		Analyzer analyzer=new StopAnalyzer2();
		TokenStream stream;
		for(String str:strs){
			stream=analyzer.tokenStream("filed", new StringReader(str));
		    CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		    try {
		    	stream.reset();
		        while (stream.incrementToken()) 
		        	System.out.println(termAtt.toString());
		        stream.end();
		        } finally {
		        stream.close();
		        }
		    }
		}
	}
