/**
 * Lucene 4.9 TokenStream工作流示例
 * 以及几种内置分析器的使用示例
 * */

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;

public class AnalyzerDemo {
	private final static Version matchVersion=Version.LUCENE_4_9;;
	
	final static String[] examples={
		"The quick brown fox jumped over the lazy dog",
		"XY&Z Corporation - xyz@example.com",
		"我爱北京天安门，天安门前太阳升"
	};
	
	private static final Analyzer[] analyzers= new Analyzer[]{
		new WhitespaceAnalyzer(matchVersion),
		new SimpleAnalyzer(matchVersion),
		new StopAnalyzer(matchVersion),
		new StandardAnalyzer(matchVersion),
		new SmartChineseAnalyzer(matchVersion)
	};
	
	public static void handle(String text) throws IOException{
		System.out.print("分析文本：");
		System.out.println(text);
		for(Analyzer a:analyzers){
			System.out.println(a.getClass().getSimpleName());
			TokenStream stream=a.tokenStream("field", new StringReader(text));
			CharTermAttribute termAtt=stream.addAttribute(CharTermAttribute.class);
			PositionIncrementAttribute posAtt=stream.addAttribute(PositionIncrementAttribute.class);
			OffsetAttribute offAtt=stream.addAttribute(OffsetAttribute.class);
			TypeAttribute typeAtt=stream.addAttribute(TypeAttribute.class);
			try{
				stream.reset();
				int pos=0;
				while(stream.incrementToken()){
					int inc=posAtt.getPositionIncrement();
					if(inc>0){
						pos+=inc;
						System.out.print(pos+": ");
					}
					System.out.print("["+termAtt+":"+offAtt.startOffset()+"->"+offAtt.endOffset()+":"+typeAtt.type()+"] ");
				}
				System.out.println("\n");
				stream.end();
			}finally{
				stream.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		for(String s:examples)
			handle(s);
	}
}
