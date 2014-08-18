import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Version;

public class InvokeTheAnalyzer {
	public static void main(String[] args) throws IOException {
		Version matchVersion=Version.LUCENE_5_0;
		Analyzer analyzer=new StandardAnalyzer(matchVersion);
		TokenStream ts=analyzer.tokenStream("myfield", new StringReader("一些测试文本用例"));
		OffsetAttribute offsetAtt=ts.addAttribute(OffsetAttribute.class);
		
	}
}
