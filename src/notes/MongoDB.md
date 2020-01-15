# Mongo

**权限设置**

    use db
    db.createUser({user:"admin",pwd:"123456",roles:[{role: 'userAdminAnyDatabase', db: 'admin'},{role: 'readWrite', db: 'admin'}]})
    db.auth('admin', '123456')
            
    Read：允许用户读取指定数据库
    readWrite：允许用户读写指定数据库
    dbAdmin：允许用户在指定数据库中执行管理函数，如索引创建、删除，查看统计或访问system.profile
    userAdmin：允许用户向system.users集合写入，可以找指定数据库里创建、删除和管理用户
    clusterAdmin：只在admin数据库中可用，赋予用户所有分片和复制集相关函数的管理权限。
    readAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读权限
    readWriteAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读写权限
    userAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的userAdmin权限
    dbAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的dbAdmin权限。
    root：只在admin数据库中可用。超级账号，超级权限
    
**数据类型**

| 数据类型 | 描述 |
|:-----------|:------:|
| String | 字符串。存储数据常用的数据类型。在 MongoDB 中，UTF-8 编码的字符串才是合法的 |
| Integer | 整型数值。用于存储数值。根据你所采用的服务器，可分为 32 位或 64 位 |
| Boolean | 布尔值。用于存储布尔值（真/假） |
| Double | 双精度浮点值。用于存储浮点值 |
| Min/Max keys | 将一个值与 BSON（二进制的 JSON）元素的最低值和最高值相对比 |
| Array | 用于将数组或列表或多个值存储为一个键 |
| Timestamp | 时间戳。记录文档修改或添加的具体时间 |
| Object | 用于内嵌文档 |
| Null | 用于创建空值 |
| Symbol | 符号。该数据类型基本上等同于字符串类型，但不同的是，它一般用于采用特殊符号类型的语言 |
| Date | 日期时间。用 UNIX 时间格式来存储当前日期或时间。你可以指定自己的日期时间：创建 Date 对象，传入年月日信息 |
| Object ID | 对象 ID。用于创建文档的 ID |
| Binary Data | 二进制数据。用于存储二进制数据 |
| Code | 代码类型。用于在文档中存储 JavaScript 代码 |
| Regular expression | 正则表达式 |
    
