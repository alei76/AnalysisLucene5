Lucene的分析过程
====

分析(Analysis),在Lucene中指将域文本(Field)转换成最基本的最基本的索引表示单元-项(Item)的过程,作用于建立索引阶段和检索阶段。

Lucene只索引文本内容,因此在Analysis之前,需要进行Parsing和Tokenization：

- Parsing：从如HTML,PDF,XML等文件中提取文本内容。
- Tokenization：如Stemming,Stop Words Filtering,Text Normalization,Synonym Expansion等

#剖析分析器#

理解Lucene分析器的架构和构件。

Anylyzer一个抽象类，是所有分析器的基类。继承Analyzer的子类必须在 createComponents(String, Reader)函数中定义TokenStreamComponents，该Component随后被tokenStream(String,Reader)调用。

抽象类TokenStream是一个能在调用后产生语汇单元序列的类，TokenStream类有两个子类，Tokenizer类和TokenFilter类。

相当于管道，将文本内容通过Reader读取，然后Tokenizer创建Tokenstream，然后通过TokenFilter进行处理，最后产生Tokens：

Reader->Tokenizer->TokenFilter->TokenFilter->TokenFilter->...->Tokens

TokenStream继承了AttributeSource，可以指定TokenStream的token属性。

TokenStream API的工作流如下：

- Instantiation of TokenStream/TokenFilters which add/get attributes to/from the AttributeSource.
- The consumer calls reset().
- The consumer retrieves attributes from the stream and stores local references to all attributes it wants to access.
- The consumer calls incrementToken() until it returns false consuming the attributes after each call.
- The consumer calls end() so that any end-of-stream operations can be performed.
- The consumer calls close() to release any resource when finished using the TokenStream.

AnalyzerDemo.java列举了一个使用该工作流的例子

TokenStream继承类AttributeSource并生成子类。AttributeSource提供addAttribute方法，可以在程序非运行期间提供增强类型并且能够完全扩展的属性操作，这带来了很好的运行性能。

在**AnalyzerDemo.java**演示了以下四个属性类的添加

```java
CharTermAttribute termAtt=stream.addAttribute(CharTermAttribute.class);
PositionIncrementAttribute posAtt=stream.addAttribute(PositionIncrementAttribute.class);
OffsetAttribute offAtt=stream.addAttribute(OffsetAttribute.class);
TypeAttribute typeAtt=stream.addAttribute(TypeAttribute.class);
```

显而易见，管道中过滤器的顺序可以影响最终的处理结果。例如，StopFilter类之前应该进行大小写转换，因为StopFilter只处理小写的停用词。

**StopAnalyzer2.java**展示了过滤器顺序有何影响。

```java
	protected TokenStreamComponents createComponents(String fieldName) {
		final Tokenizer source=new LetterTokenizer(matchVersion);
		TokenStream result=new LowerCaseFilter(matchVersion,new StopFilter(matchVersion,source,(CharArraySet) stopWords));
//		TokenStream result=new StopFilter(matchVersion,new LowerCaseFilter(matchVersion,source),(CharArraySet) stopWords);
		return new TokenStreamComponents(source,result);
	}
```

对文本"The Quick Duck ..."进行处理，如果使用上述版本的StopAnalyzer2，即先停用词，再进行大小写处理，那么结果为"the quick duck"；如果使用注释掉的管道顺序的话，那么结果为"quick duck"。

开心了，分析过程的原理也就这些了。总结下：**分析器会简单地定义一个语汇单元链，该链的开端为新语汇单元(TokenStream)的初始数据源，其后跟随着任意数量的用于修改语汇单元的TokenFilter类。语汇单元还包括一个属性集，Lucene会通过各种不同的方法来存储该集合。**

#近音词查询#

**MetaPhoneDemo.java**中演示了该示例。

编写了一个自定义的分析器，用它将单词转化为它的词根，这个分析器是通过**Apache Commons Codec**项目中的Metaphone算法实现的。

#同义词,别名和其它表示相同意义的词#


#参考#

http://lucene.apache.org/core/4_9_0/core/org/apache/lucene/analysis/package-summary.html

































