import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class IndexDemo {
	public static void main(String[] args) throws IOException, ParseException {
		Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_4_9);
		Directory directory=new RAMDirectory();//索引存储在内存中
		IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_4_9,analyzer);
		IndexWriter iwriter=new IndexWriter(directory,config);
		Document doc=new Document();
		String text="This is the text to be indexed";
		doc.add(new Field("fieldname",text,TextField.TYPE_STORED));
		iwriter.addDocument(doc);
		iwriter.close();
		
		/*------以上为建立索引的过程------*/
		/*------以下为根据索引进行检索过程------*/
		DirectoryReader ireader=DirectoryReader.open(directory);
		IndexSearcher isearcher=new IndexSearcher(ireader);
		QueryParser parser=new QueryParser(Version.LUCENE_4_9,"fieldname",analyzer);
		Query query=parser.parse(text);
		ScoreDoc[] hits=isearcher.search(query,null,1000).scoreDocs;
		System.out.println(hits.length);
		for(int i=0;i<hits.length;i++){
			Document hitDoc=isearcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("fieldname"));
		}
		ireader.close();
		directory.close();
	}
}
