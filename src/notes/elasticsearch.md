# elasticsearch学习笔记
- **列出所有索引**  GET /_cat/indices?v

- **查看索引下的所有type** GET /索引名/_mapping

- **测试索引**

        例子：
        GET /store/_analyze
        {
          "field": "enterpriseInfoGUID",
          "text": "cf6766ac-df00-4651-9c97-39b2e5500b08"
        }
- **对索引操作语法格式**   <REST Verb> /<Index>/<Type>/<ID>

- **添加索引** put /索引名

- **查看索引下的所有类型** GET/索引名/_mapping

- **添加mapping和字段**
		PUT twitter/_mapping/mapping名称
		{
		  "properties": {
		    "email": {
		      "type": "keyword"
		    }
		  }
		}
		
		一个字段可以设置多种类型(应用场景：查询需要分词，聚合不需要分词)
		例子：
			PUT /fields_test/_mapping/_doc
			{
			  "properties": {
			   "city": {
			      "type": "text",
			      "fields": {
			        "raw": { 
			          "type":  "keyword"
			        }
			      }
			    }
			  }
			} 
			GET /fileds_test/_search
			{
			  "query": {
			    "match": {
			      "city": "york"
			    }
			  },
			  "aggs": {
			    "city_term": {
			      "terms": {
			        "field": "city.raw",
			        "size": 10
			      }
			    }
			  }
			}
		
- **ES对null值的处理**

		如果一个字段为null值,那么和不存在此字段一样效果
		例子:
			GET /_search
			{
			    "query": {
			        "exists" : { "field" : "user" }
			    }
			}
			以下三种是不会出现在上述查询结果当中
			{ "user": null }
			{ "user": [] } 
			{ "user": [null] }
			
		如果需要null值为存在的值,那么可以修改字段的属性
		PUT /example
		{
		  "mappings": {
		    "_doc": {
		      "properties": {
		        "user": {
		          "type": "keyword",
		          "null_value": "_null_"
		        }
		      }
		    }
		  }
		}

- **创建ik分词的字段**

		POST /test_ik/_doc/_mapping
		{
		
		    "properties": {
		
		        "content": {
		
		            "type": "text",
		
		            "analyzer": "ik_max_word",
		
		            "search_analyzer": "ik_max_word"
		
		        }
		
		    }
		}
		ik分词器地址:https://github.com/medcl/elasticsearch-analysis-ik

- **创建标准分词器的字段**

		POST /test_ik1/_doc/_mapping
		{
		 "properties": {
		
		        "id2": {
		
		            "type": "text",
		
		            "analyzer": "standard",
		
		            "search_analyzer": "standard"
		
		        }
		
		    }
		}

- **路由** routing,决定文档存储在哪个分片当中，计算公式：shard = hash(routing) % number_of_primary_shards  
		
			routing 是一个可变值，默认是文档的 _id ，也可以设置成一个自定义的值。 routing 通过 hash 函数生成一个数字，
		然后这个数字再除以 number_of_primary_shards （主分片的数量）后得到 余数 。这个分布在 0 number_of_primary_shards-1
		之间的余数，就是我们所寻求的文档所在分片的位置。


## 基本CRUD

- **文档查询**	GET /索引名/类型/文档id?_source=字段1,字段2

- **查看文档是否存在** HEAD /索引名/类型/文档id

- **查看索引字段的分词效果** GET/索引名/_mapping

			例子：
			GET /store/_analyze
			{
			  "field": "enterpriseInfoGUID",
			  "text": "cf6766ac-df00-4651-9c97-39b2e5500b08"
			}

- **修改文档** elasticsearch不能修改文档但是可以使用相同的文档id进行替换 ；put /索引名/类型/相同文档id

        例子
		PUT /website/blog/123
		{
		  "title": "My first blog entry",
		  "text":  "I am starting to get the hang of this...",
		  "date":  "2014/01/02"
		}
		局部更新：
		POST test/_doc/1/_update
		{
		    "doc" : {
		        "name" : "new_name"
		    }
		} 如果name字段存在则修改值，如果不存在，则新建字段 ，如果doc和script同时存在，则doc失效
		
		根据脚本修改文档：
		修改值：
		POST test/_doc/1/_update
		{
		    "script" : {
		        "source": "ctx._source.counter += params.count",
		        "lang": "painless",
		        "params" : {
		            "count" : 4
		        }
		    }
		}
		POST test/_doc/1/_update
		{
		    "script" : {
		        "source": "if (ctx._source.tags.contains(params.tag)) { ctx.op = 'delete' } else { ctx.op = 'none' }",
		        "lang": "painless",
		        "params" : {
		            "tag" : "green"
		        }
		    }
		}
		添加字段：
		POST test/_doc/1/_update
		{
		    "script" : "ctx._source.new_field = 'value_of_new_field'"
		}
		删除字段：
		POST test/_doc/1/_update
		{
		    "script" : "ctx._source.remove('new_field')"
		}
		

- **创建文档** 

	- 第一种方式：通过elasticsearch创建id
	- 第二种方式：自己创建id并加上op_type=create参数或者在 URL 末端使用 /_create
	       
			例子
			POST /website/blog/
			{ ... }
			PUT /website/blog/123?op_type=create
			{ ... }
			PUT /website/blog/123/_create
			{ ... }

- **删除文档** delete /索引名/类型/文档id  **或** 通过查询删除文档

			例子
			POST twitter,blog/_docs,post/_delete_by_query
			{
			  "query": {
			    "match_all": {}
			  }
			}

- **批次操作**  post /索引名/类型/_bulk?pretty
				
			例子：
			POST /customer/_doc/_bulk?pretty
			{"index":{"_id":"1"}}
			{"name": "John Doe" }
			{"index":{"_id":"2"}}
			{"name": "Jane Doe" }
			

- **搜索文档**
	- 空搜索： GET /-search
	- 在某一个索引或者类型中搜索
	        
            例子
			/_search
			在所有的索引中搜索所有的类型
			/gb/_search
			在 gb 索引中搜索所有的类型
			/gb,us/_search
			在 gb 和 us 索引中搜索所有的文档
			/g*,u*/_search
			在任何以 g 或者 u 开头的索引中搜索所有的类型
			/gb/user/_search
			在 gb 索引中搜索 user 类型
			/gb,us/user,tweet/_search
			在 gb 和 us 索引中搜索 user 和 tweet 类型
			/_all/user,tweet/_search
			在所有的索引中搜索 user 和 tweet 类型
	- 分页搜索 Elasticsearch 接受 from 和 size 参数
	
	
            例子
			GET /_search?size=5
			GET /_search?size=5&from=5
			GET /_search?size=5&from=10
	- +，-搜索 + 前缀表示必须与查询条件匹配。类似地， - 前缀表示一定不与查询条件匹配。没有 + 或者 - 的所有其他条件都是可选的——匹配的越多，文档就越相关
	
            例子
			GET /_search?q= +tweet:mary +name:john (查询tweet字段中包含mary并且name中包含john的文档)
			GET /_search?q= +name:(mary john) +date>2014-09-24 +(aggregations geo)
			（查询name 字段中包含 mary 或者 johndate 值大于 2014-09-10all_ 字段包含 aggregations 或者 geo）
			

	- 所有字段搜索 
	
            例子
			GET /_search?q=mary 查询所有字段中包含了mary的文档
			这是把所有字段的值拼接成字符串组成新的字段_all，再查询这个字段是否包含mary
	- 请求体搜索
               
            例子
			GET /_search
			{
			  "from": 30,
			  "size": 10
			}
	- 查询表达式

			例子
			{
			    "bool": {
			        "must":     { "match": { "tweet": "elasticsearch" }},
			        "must_not": { "match": { "name":  "mary" }},
			        "should":   { "match": { "tweet": "full text" }},
			        "filter":   { "range": { "age" : { "gt" : 30 }} }
			    }
			}
			
			{
			    "bool": {
			        "must": { "match":   { "email": "business opportunity" }},
			        "should": [
			            { "match":       { "starred": true }},
			            { "bool": {
			                "must":      { "match": { "folder": "inbox" }},
			                "must_not":  { "match": { "spam": true }}
			            }}
			        ],
			        "minimum_should_match": 1
			    }
			}
			完整例子
			GET /_search
			{
			  "query": {
			    "bool": {
			      "must": [
			          {
			          "term":{
			            "tweet":"elasticsearch"
			          }
			        },
			        {
			          "match": {
			            "name": "mary"
			          }
			        }
			      ],
			      "filter": [
			        {
			          "range": {
			            "date": {
			              "lt":"2014-09-22"
			            }
			          }
			        }
			      ]
			     
			    }
			  },
			  "from":0,
			  "size": 2
			}
	1.match查询  如果你在一个全文字段上使用 match 查询，在执行查询前，它将用正确的分析器去分析查询字符串
	
            例子
			{ "match": { "tweet": "About Search" }}
	2.multi_match查询

			例子
			{
			    "multi_match": {
			        "query":    "full text search",
			        "fields":   [ "title", "body" ]
			    }
			}
	3.range查询
			
			例子
			{
			    "range": {
			        "age": {
			            "gte":  20,
			            "lt":   30
			        }
			    }
			}
	4.term查询 term 查询被用于精确值 匹配，这些精确值可能是数字、时间、布尔或者那些 not_analyzed 的字符串
		
			例子
			{ "term": { "age":    26           }}
			{ "term": { "date":   "2014-09-01" }}
			{ "term": { "public": true         }}
			{ "term": { "tag":    "full_text"  }}
	5.增加filtering的查询
	
			例子
			{
			    "bool": {
			        "must":     { "match": { "title": "how to make millions" }},
			        "must_not": { "match": { "tag":   "spam" }},
			        "should": [
			            { "match": { "tag": "starred" }}
			        ],
			        "filter": {
			          "range": { "date": { "gte": "2014-01-01" }} 
			        }
			    }
			}
		通过将 range 查询移到 filter 语句中，我们将它转成不评分的查询，将不再影响文档的相关性排名。由于它现在是一个不评分的查询，可以使用各种对 filter 查询有效的优化手段来提升性能。

		所有查询都可以借鉴这种方式。将查询移到 bool 查询的 filter 语句中，这样它就自动的转成一个不评分的 filter 了。

		如果你需要通过多个不同的标准来过滤你的文档，bool 查询本身也可以被用做不评分的查询。简单地将它放置到 filter 语句中并在内部构建布尔逻辑：
				
			例子
			{
			    "bool": {
			        "must":     { "match": { "title": "how to make millions" }},
			        "must_not": { "match": { "tag":   "spam" }},
			        "should": [
			            { "match": { "tag": "starred" }}
			        ],
			        "filter": {
			          "bool": { 
			              "must": [
			                  { "range": { "date": { "gte": "2014-01-01" }}},
			                  { "range": { "price": { "lte": 29.99 }}}
			              ],
			              "must_not": [
			                  { "term": { "category": "ebooks" }}
			              ]
			          }
			        }
			    }
			}
	6.constant_score查询  它被经常用于你只需要执行一个 filter 而没有其它查询
			
			例子
			{
			    "constant_score":   {
			        "filter": {
			            "term": { "category": "ebooks" } 
			        }
			    }
			}
	
	7.排序
			
			例子
			GET /_search
			{
			    "query" : {
			        "bool" : {
			            "filter" : { "term" : { "user_id" : 1 }}
			        }
			    },
			    "sort": { "date": { "order": "desc" }}
			}
	
	8.指定返回字段：
		
			GET /bank/_search
			{
			  "query": { "match_all": {} },
			  "_source": ["account_number", "balance"]
			}
	9.对每个搜索结果进行脚本计算 script_field
				
			GET /_search
			{
			    "query" : {
			        "match_all": {}
			    },
			    "script_fields" : {
			        "test1" : {
			            "script" : {
			                "lang": "painless",
			                "source": "doc['my_field_name'].value * 2"
			            }
			        },
			        "test2" : {
			            "script" : {
			                "lang": "painless",
			                "source": "doc['my_field_name'].value * params.factor",
			                "params" : {
			                    "factor"  : 2.0
			                }
			            }
			        }
			    }
			}
   10.排除分数小于指定数的结果
		
			GET /_search
			{
			    "min_score": 0.5,
			    "query" : {
			        "term" : { "user" : "kimchy" }
			    }
			}
   11.分析查询语句  profile
	
			GET /twitter/_search
			{
			  "profile": true,
			  "query" : {
			    "match" : { "message" : "some number" }
			  }
			}

  12.自定义脚本查询,script query,只能用于filter查询中
		
			GET /_search
			{
			    "query": {
			        "bool" : {
			            "filter" : {
			                "script" : {
			                    "script" : {
			                        "source": "doc['num1'].value > 1",
			                        "lang": "painless"
			                     }
			                }
			            }
			        }
			    }
			}
			可以自定义参数:
			GET /_search
			{
			    "query": {
			        "bool" : {
			            "filter" : {
			                "script" : {
			                    "script" : {
			                        "source" : "doc['num1'].value > params.param1",
			                        "lang"   : "painless",
			                        "params" : {
			                            "param1" : 5
			                        }
			                    }
			                }
			            }
			        }
			    }
			}
			
- **一些选项** 

 1. 减少返回结果展示信息： filter_path=返回key的集合

			例子：
			GET /_search?q=elasticsearch&filter_path=took,hits.hits._id,hits.hits._score
			{
			  "took" : 3,
			  "hits" : {
			    "hits" : [
			      {
			        "_id" : "0",
			        "_score" : 1.6375021
			      }
			    ]
			  }
			}
 2. 显示错误栈信息: error_trace=true
 
			例子：
			GET /accounts/_search?size=aa&error_trace=true


 3. 超时
		 
			PUT twitter/_doc/1?timeout=5m
			{
			    "user" : "kimchy",
			    "post_date" : "2009-11-15T14:12:12",
			    "message" : "trying out Elasticsearch"
			}
 4. 直接获取source字段，排除其他字段

			GET twitter/_doc/1/_source	

- **聚合**
	
	指标：
	1. extended_stats：对搜索结果中的字段进行最大，最小之类的运算
		
			GET /exams/_search
			{
			    "size": 0,
			    "aggs" : {
			        "grades_stats" : { "extended_stats" : { "field" : "grade" } }
			    }
			} 
	2. Percentile： 数据百分比占比聚合,可用于查找异常
		
			GET latency/_search
			{
			    "size": 0,
			    "aggs" : {
			        "load_time_outlier" : {
			            "percentiles" : {
			                "field" : "load_time" 
			            }
			        }
			    }
			}
	3. Percentile Ranks：查询小于指定值的百分比
		
			GET latency/_search
			{
			    "size": 0,
			    "aggs" : {
			        "load_time_ranks" : {
			            "percentile_ranks" : {
			                "field" : "load_time", 
			                "values" : [500, 600]
			            }
			        }
			    }
			}
   桶：
	 1. composite: 复合聚合（笛卡尔乘积）,比如聚合不同年龄段的男女数量
    
				GET /accounts/_search
				{
				  "size": 0,
				  "aggs": {
				    "my_buckets": {
				      "composite": {
				        "sources": [
				          {
				            "histo": {
				              "histogram": {
				                "field": "age",
				                "interval": 10
				              }
				            }
				          },
				          {
				            "gender":{
				              "terms": {
				                "field": "gender.keyword"
				              }
				            }         
				          }
				        ],
				        "size": 100
				      }
				    }
				  }
			}
     2. date histogram: 时间直方图聚合 ; 注意：不支持分数值间隔，单位是天以上的不支持写任意数，只能是1 
    
				POST /sales/_search?size=0
				{
				    "aggs" : {
				        "sales_over_time" : {
				            "date_histogram" : {
				                "field" : "date",
				                "interval" : "90m"
								"format" : "yyyy-MM-dd"
				            }
				        }
				    }
				}
				
				可以设置offset偏移量
				GET my_index/_search?size=0
				{
				  "aggs": {
				    "by_day": {
				      "date_histogram": {
				        "field":     "date",
				        "interval":  "day",
				        "offset":    "+6h"
				      }
				    }
				  }
				} 从一天的6点到第二天的6点

	3. date range :时间范围聚合； 
	
			GET /store/SalesOrder/_search
			{
			  "size": 0,
			  "aggs": {
			    "createTime": {
			      "date_range": {
			        "field": "businessDay",
			        "ranges": [
			          {
			            "from": "2017-12-01",
			            "to": "2018-04-01"
			          }
			        ]
			      }
			    }
			  }
			}
	4. geo distance : 地理位置距离范围聚合
	
			GET /museums/_search
			{
			  "size": 0,
			  "aggs": {
			    "rings_around_amsterdam": {
			      "geo_distance": {
			        "field": "location",
			        "origin": {
			          "lat": 52.376,
			          "lon": 4.894
			        },
			        "ranges": [
			           { "to" : 100000 },
			           { "from" : 100000, "to" : 300000 },
			           { "from" : 300000 }
			        ]
			      }
			    }
			  }
			}
	管道聚合
		
	 语法:>：指定父子聚合关系   例如 sales_per_month>sales ：在sales_per_month聚合下的sales聚合

	1. avg_bucket:基于兄弟聚合求平均值
	
			GET /accounts/_search
			{
			  "size": 0,
			  "aggs": {
			    "gender": {
			      "terms": {
			        "field": "gender.keyword",
			        "size": 10
			      }
			      ,"aggs": {
			        "age": {
			          "avg": {
			            "field": "age"
			          }
			        }
			      }
			    }
			    ,"avg_bucket":{
			      "avg_bucket": {
			        "buckets_path": "gender>age"
			         
			      }
			    }
			  }
			} 对每个年龄桶的平均值指标求平均值
	2.derivative:基于父聚合求导
			
			POST /sales/_search
			{
			    "size": 0,
			    "aggs" : {
			        "sales_per_month" : {
			            "date_histogram" : {
			                "field" : "date",
			                "interval" : "month"
			            },
			            "aggs": {
			                "sales": {
			                    "sum": {
			                        "field": "price"
			                    }
			                },
			                "sales_deriv": {
			                    "derivative": {
			                        "buckets_path": "sales" 
			                    }
			                }
			            }
			        }
			    }
			}
	3.max_bucket: 基于兄弟聚合对桶的指标求最大值
	4.min_bucket:基于兄弟聚合对桶的指标求最小值
	5.sum_bucket:基于兄弟聚合对桶的指标求和
				
			{
			    "aggs" : {
			        "sales_per_month" : {
			            "date_histogram" : {
			                "field" : "date",
			                "interval" : "month"
			            },
			            "aggs": {
			                "sales": {
			                    "sum": {
			                        "field": "price"
			                    }
			                }
			            }
			        },
			        "max_monthly_sales": {        //输出兄弟聚合 sales_per_month 的每月销售总和 sales 的最大一个桶
			            "max_bucket": {
			                "buckets_path": "sales_per_month>sales" 
			            }
			        },
			        "min_monthly_sales": {         //输出兄弟聚合 sales_per_month 的每月销售总和 sales 的最小一个桶
			            "min_bucket": {
			                "buckets_path": "sales_per_month>sales" 
			            }
			        },
			        "sum_monthly_sales": {         //输出兄弟聚合 sales_per_month 的每月销售总和 sales 的最小一个桶
			            "sum_bucket": {
			                "buckets_path": "sales_per_month>sales" 
			            }
			        }
			    }
			}
	6.percetiles bucket聚合:基于兄弟聚合的某个权值，计算权值的百分百

			POST /sales/_search
			{
			    "size": 0,
			    "aggs" : {
			        "sales_per_month" : {
			            "date_histogram" : {
			                "field" : "date",
			                "interval" : "month"
			            },
			            "aggs": {
			                "sales": {
			                    "sum": {
			                        "field": "price"
			                    }
			                }
			            }
			        },
			        "percentiles_monthly_sales": {
			            "percentiles_bucket": {
			                "buckets_path": "sales_per_month>sales", 
			                "percents": [ 25.0, 50.0, 75.0 ] 
			            }
			        }
			    }
			}
	7.cumulative_sum聚合：基于父聚合（只能是histogram或date_histogram类型）的某个权值	
	
			{
			    "aggs" : {
			        "sales_per_month" : {
			            "date_histogram" : {
			                "field" : "date",
			                "interval" : "month"
			            },
			            "aggs": {
			                "sales": {
			                    "sum": {
			                        "field": "price"
			                    }
			                },
			                "cumulative_sales": {
			                    "cumulative_sum": {
			                        "buckets_path": "sales" 
			                    }
			                }
			            }
			        }
			    }
			}	
	8. bucket script聚合：基于父聚合的【一个或多个权值】，对这些权值通过脚本进行运算
	
			{
			    "aggs" : {
			        "sales_per_month" : {
			            "date_histogram" : {
			                "field" : "date",
			                "interval" : "month"
			            },
			            "aggs": {
			                "total_sales": {
			                    "sum": {
			                        "field": "price"
			                    }
			                },
			                "t-shirts": {
			                  "filter": {
			                    "term": {
			                      "type": "t-shirt"
			                    }
			                  },
			                  "aggs": {
			                    "sales": {
			                      "sum": {
			                        "field": "price"
			                      }
			                    }
			                  }
			                },
			                "t-shirt-percentage": {
			                    "bucket_script": {
			                        "buckets_path": {                    //对两个权值进行计算
			                          "tShirtSales": "t-shirts>sales",
			                          "totalSales": "total_sales"
			                        },
			                        "script": "params.tShirtSales / params.totalSales * 100"
			                    }
			                }
			            }
			        }
			    }
			}

	9. bucket selector聚合
	
			{
			    "bucket_selector": {
			        "buckets_path": {
			            "my_var1": "the_sum", 
			            "my_var2": "the_value_count"
			        },
			        "script": "my_var1 > my_var2"    // true 则保留该桶；false 则丢弃
			    }
			}	
- **elasticsearch配置**

- 1. 配置数据存储路径和日志路径
	
			在ealstcisearch.yml文件中设置
			path.data: /var/lib/elasticsearch
			path.logs: /var/log/elasticsearch
 2. 配置集群名字
 
			cluster.name: 集群名
 3. 配置节点名
  
			node.name: 节点名
 4. 配置discovery.zen.minimum_master_nodes，如果没有这个设置，一个遭受网络故障的集群就有可能使集群分裂成两个独立的集群——一个分裂的大脑——这将导致数据丢失
 
			discovery.zen.minimum_master_nodes: 数字(计算公式： (master_eligible_nodes / 2) + 1)  	
 5. 由开发环境转到生产环境的elasticsearch的重要配置详见：
	  
			https://www.elastic.co/guide/en/elasticsearch/reference/current/system-config.html

**其他一些注意事项**

- es是近实时搜索,所以在插入一条数据时,并不能立即搜索到,es会一秒刷新一次索引,然后才会查询到

		文档地址:https://www.elastic.co/guide/cn/elasticsearch/guide/current/near-real-time.html
 
			

		
		    
