Lucene的分析过程
====

分析(Analysis),在Lucene中指将域文本(Field)转换成最基本的最基本的索引表示单元-项(Item)的过程,作用于建立索引阶段和检索阶段。

Lucene只索引文本内容,因此在Analysis之前,需要进行Parsing和Tokenization：

- Parsing：从如HTML,PDF,XML等文件中提取文本内容。
- Tokenization：如Stemming,Stop Words Filtering,Text Normalization,Synonym Expansion等

#Analysis核心类#

- Analyzer：负责创建索引和检索使用的TokenStream
- CharFilter
- Tokenizer：一个将文本切分成tokens的TokenStream,Analyzer使用Tokenizer作为Analysis的第一步
- TokenFilter：一个可以处理Tokenizer生成的token的TokenStream



#本目录说明#

- TokenStream.md -> Tokens流介绍
- 


#参考#

http://lucene.apache.org/core/4_9_0/core/org/apache/lucene/analysis/package-summary.html

































