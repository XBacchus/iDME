-- 将mysql数据库中text字段类型修改为longtext

CREATE or REPLACE PROCEDURE update_database_field_type_from_text_to_longtext()
    LANGUAGE plpgsql AS
$$

BEGIN
    -- 处理属性库问题，属性库存在就进行刷新
    IF EXISTS (select *
               from information_schema.tables
               where table_name = 'xdm_exadefinition'
                 and table_schema = CURRENT_SCHEMA) THEN
UPDATE xdm_exadefinition
SET "constraint" = jsonb_set("constraint"::jsonb, '{length}', '"524288"')
where TYPE = 'TEXT'
  AND ("constraint"::jsonb ->> 'length')::INT > 524288;
END IF;
END
$$;

-- 刷新属性关系表
CREATE or REPLACE PROCEDURE update_exdefinition_rel_filed_column_name()
    LANGUAGE plpgsql AS
$$
DECLARE
exa_rel_id                  VARCHAR(255);
    exa_rel_column_name VARCHAR(255);
    exa_rel_column_no   varchar(255);

    -- 根据属性表中类型为text并且长度大于等于524288的进行查询
    ext_column_name_id CURSOR FOR
select r.id, r.columnName
from xdm_exadefinitionlink_rel r
         inner join xdm_exadefinition m on
        r._sourceId = m.id
where r._targetType = 'TypeDefinition'
  and m.type = 'TEXT'
  and (m."constraint"::JSON ->> 'length')::INT >= 524288;

BEGIN
OPEN ext_column_name_id;

LOOP
-- 结束条件

FETCH ext_column_name_id INTO exa_rel_id, exa_rel_column_name;

        exit
when not FOUND;

        -- 取出最后的编码
select split_part(exa_rel_column_name, '_', 3) into exa_rel_column_no;

execute format('update xdm_exadefinitionlink_rel set columnname = ''text_524288_%s'' where id = ''%s''',
               exa_rel_column_no, exa_rel_id);
END LOOP;

CLOSE ext_column_name_id;
END
$$;

-- 刷新已生成的宽表字段
CREATE OR REPLACE PROCEDURE alter_ext_table_column()
    LANGUAGE plpgsql AS
$$

DECLARE
exa_table_name          VARCHAR(255);
    exa_column_name VARCHAR(255);
    exa_column_no   varchar(255);
    exa_attr_length int default 0;
    ext_table_name_cursor CURSOR FOR
select table_name, column_name
from information_schema.COLUMNS
where table_schema = CURRENT_SCHEMA
  and data_type = 'text'
  and column_name like 'text%'
  and lower(table_name) in (select lower(tablename)
                            from xdm_typedefinition
                            where applicationNameEn != 'XDM'
  and _parentNodeId =
    (select id
    from xdm_typedefinition
    where nameEn = 'EXAValue' and _tenantId = -1)
  and nameEn like '%StringEXAValue');
BEGIN

OPEN ext_table_name_cursor;
-- 处理表中含有text类型的表
LOOP
FETCH ext_table_name_cursor INTO exa_table_name, exa_column_name;
        -- 结束条件
        exit
when not FOUND;

        -- 判断字段长度是否大于524288
select split_part(exa_column_name, '_', 2)::INT into exa_attr_length;

if exa_attr_length > 524288 then
            -- 获取编号
select split_part(exa_column_name, '_', 3) into exa_column_no;

-- 动态SQL，确保SQL语句的语法正确
execute format('alter table "%s" rename "%s" to "text_524288_%s"', exa_table_name, exa_column_name,
               exa_column_no);

end if;

END LOOP;

CLOSE ext_table_name_cursor;
END
$$;


-- 重组json数组
CREATE OR REPLACE PROCEDURE update_json_array_length(INOUT json_array json)
    LANGUAGE plpgsql AS
$$

DECLARE
i                           INT DEFAULT 0;
    n                   INT;
    element             JSON;
    updated_json        JSON;
    element_type        VARCHAR(255);
    element_length      INT;
    element_last_length INT;
BEGIN

    --     select json_array_length(json_array) into n;
--     select json_array into updated_json;
    n := json_array_length(json_array);
    updated_json := json_array;

    WHILE i < n
        loop
            element := json_array -> i;
            element_type := element ->> 'attrType';

            IF element_type = 'TEXT' THEN

                element_length := (element ->> 'attrLength')::INT;

                IF element_length > 524288 THEN

                    element := jsonb_set(element::jsonb, '{attrLength}', '524288');

                    updated_json := jsonb_set(updated_json::jsonb, format('{%s}', i)::text[], element::jsonb);
END IF;

                if (element ->> 'lastAttrLength' != '' and element ->> 'lastAttrLength' is not null) then
                    element_last_length := (element ->> 'lastAttrLength')::INT;
                    IF element_last_length > 524288 then
                        element := jsonb_set(element::jsonb, '{lastAttrLength}', '524288');

                        updated_json := jsonb_set(updated_json::jsonb, format('{%s}', i)::text[], element::jsonb);
end if;
end if;
END IF;

            i := i + 1;
END loop;
    json_array := updated_json;
END
$$;

-- 更新json数组
CREATE OR REPLACE PROCEDURE update_table_json_column()
    LANGUAGE plpgsql As
$$

DECLARE
original_json  JSON;
    row_id BIGINT;
    cur CURSOR FOR select t.id, t.extattrconfig
                           from xdm_typedefinition t,
                                jsonb_array_elements(t.extattrconfig::jsonb) ext
                           where t.applicationnameen != 'XDM'
                             and ext ->> 'attrType' = 'TEXT';
BEGIN
OPEN cur;

LOOP
FETCH cur INTO row_id, original_json;
        exit
when not FOUND;

CALL update_json_array_length(original_json::json);

UPDATE xdm_typedefinition SET extAttrConfig = original_json WHERE id = row_id;
END LOOP;

CLOSE cur;
END
$$;

-- 查询表是否存在
CREATE PROCEDURE select_exadefinition_and_rel_exists()
    LANGUAGE plpgsql AS
$$

BEGIN
    -- 判断表是否存在
    if ((select count(1)
         from information_schema.TABLES
         where (TABLE_NAME = 'xdm_exadefinitionlink_rel' or TABLE_NAME = 'xdm_exadefinition')
           and TABLE_SCHEMA = CURRENT_SCHEMA) = 2) then
        -- 刷新历史数据列字段类型
        call update_database_field_type_from_text_to_longtext();
        -- 刷新属性关系表中column_name列的数据
call update_exdefinition_rel_filed_column_name();
-- 更新宽表列名
call alter_ext_table_column();
end if;
    -- 判断xdm_typedefinition表是否存在
    if ((select count(1)
         from information_schema.TABLES
         where TABLE_NAME = 'xdm_typedefinition'
           and TABLE_SCHEMA = CURRENT_SCHEMA) = 1) then
        call update_table_json_column();
end if;
END
$$;

CALL select_exadefinition_and_rel_exists();
-- 删除存储过程
DROP PROCEDURE IF EXISTS select_exadefinition_and_rel_exists;
DROP PROCEDURE IF EXISTS update_database_field_type_from_text_to_longtext;
DROP PROCEDURE IF EXISTS update_exdefinition_rel_filed_column_name;
DROP PROCEDURE IF EXISTS alter_ext_table_column;
DROP PROCEDURE IF EXISTS update_table_json_column;
DROP PROCEDURE IF EXISTS update_json_array_length;
