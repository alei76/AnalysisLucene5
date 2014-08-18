TokenStream
======

public abstract class TokenStream
extends AttributeSource
implements Closeable

TokenSteam囊括了tokens序列,既包括文档中的域,又包括查询文本。其子类有：

- Tokenizer:一个TokenSteam,其输入是一个Reader
- TokenFilter:一个TokenStream,其输入是另外一个TokenStream,个人理解,相对于一个管道模型

The **workflow of the new TokenStream API** is as follows:

- Instantiation of TokenStream/TokenFilters which add/get attributes to/from the AttributeSource.
- The consumer calls reset().
- The consumer retrieves attributes from the stream and stores local references to all attributes it wants to access.
- The consumer calls incrementToken() until it returns false consuming the attributes after each call.
- The consumer calls end() so that any end-of-stream operations can be performed.
- The consumer calls close() to release any resource when finished using the TokenStream

