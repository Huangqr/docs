
**1.查询父级项**
```
select ha.*
from
(select @r as abc, (select @r := fid from hsa_agent where guid = abc) as parent_id
from (select @r := #{agentGuid}) vars, hsa_agent h) temp
left join hsa_agent ha on temp.parent_id = ha.guid
where ha.deleted = 0	    
```
		
**2.查询子级项**
```
SELECT * FROM
(SELECT * FROM hsa_agent) realname_sorted,
(SELECT @pv :='1908271708526360003') initialisation
WHERE (FIND_IN_SET(fid, @pv) > 0 And @pv := concat(@pv, ',', guid))
```

**存储过程**
    
    循环生成语句并执行
    
    DELIMITER //
    CREATE PROCEDURE alter_table_column()
    BEGIN
    DECLARE `@i` INT(11);
    DECLARE `@sqlstr` LONGTEXT;
    SET `@i`=0;
    WHILE `@i` < (select COUNT(*) from information_schema.tables where table_schema='store') DO
    SET @sqlstr = CONCAT(
    "ALTER TABLE ",
    (select table_name from information_schema.tables where table_schema='store' LIMIT `@i`,1),
    " ADD `test` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    );
    PREPARE stmt FROM @sqlstr;
    EXECUTE stmt;
    SET `@i` = `@i` + 1;
    END WHILE;
    END;
    CALL alter_table_column();
    DROP PROCEDURE alter_table_column;

		