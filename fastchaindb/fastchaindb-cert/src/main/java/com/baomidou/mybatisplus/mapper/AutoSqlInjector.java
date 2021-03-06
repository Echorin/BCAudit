/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.enums.SqlMethod;
import com.baomidou.mybatisplus.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.baomidou.mybatisplus.toolkit.SqlReservedWords;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * SQL ???????????????
 * </p>
 *
 * @author hubin sjy tantan
 * @Date 2016-09-09
 */
public class AutoSqlInjector implements ISqlInjector {

    private static final Log logger = LogFactory.getLog(AutoSqlInjector.class);

    protected Configuration configuration;
    protected LanguageDriver languageDriver;
    protected MapperBuilderAssistant builderAssistant;

    /**
     * <p>
     * CRUD ????????????????????? ????????????????????????
     * </p>
     *
     * @param builderAssistant
     * @param mapperClass
     */
    @Override
    public void inspectInject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
        if (!mapperRegistryCache.contains(className)) {
            inject(builderAssistant, mapperClass);
            mapperRegistryCache.add(className);
        }
    }

    /**
     * ???????????? crudSql
     */
    @Override
    public void inject(MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        this.configuration = builderAssistant.getConfiguration();
        this.builderAssistant = builderAssistant;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();

        //?????? ???????????? PLUS ?????? > ???????????? (???????????????????????????Mybatis??????)
        /*if (!globalCache.isDbColumnUnderline()) {
            globalCache.setDbColumnUnderline(configuration.isMapUnderscoreToCamelCase());
        }*/
        Class<?> modelClass = extractModelClass(mapperClass);
        if (null != modelClass) {
            /**
             * ????????? SQL ??????
             */
            if (this.getGlobalConfig().isSqlParserCache()) {
                PluginUtils.initSqlParserInfoCache(mapperClass);
            }
            TableInfo table = TableInfoHelper.initTableInfo(builderAssistant, modelClass);
            injectSql(builderAssistant, mapperClass, modelClass, table);
        }
    }

    /**
     * <p>
     * ??????SQL
     * </p>
     *
     * @param builderAssistant
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSql(MapperBuilderAssistant builderAssistant, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        /**
         * #148 ????????????????????????????????????????????????
         */
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            /** ?????? */
            this.injectDeleteByIdSql(false, mapperClass, modelClass, table);
            this.injectDeleteByIdSql(true, mapperClass, modelClass, table);
            /** ?????? */
            this.injectUpdateByIdSql(true, mapperClass, modelClass, table);
            this.injectUpdateByIdSql(false, mapperClass, modelClass, table);
            /** ?????? */
            this.injectSelectByIdSql(false, mapperClass, modelClass, table);
            this.injectSelectByIdSql(true, mapperClass, modelClass, table);
        } else {
            // ????????????????????? ????????????
            logger.warn(String.format("%s ,Not found @TableId annotation, Cannot use Mybatis-Plus 'xxById' Method.",
                modelClass.toString()));
        }
        /**
         * ??????????????????????????????
         */
        /** ?????? */
        this.injectInsertOneSql(true, mapperClass, modelClass, table);
        this.injectInsertOneSql(false, mapperClass, modelClass, table);
        /** ?????? */
        this.injectDeleteSql(mapperClass, modelClass, table);
        this.injectDeleteByMapSql(mapperClass, table);
        /** ?????? */
        this.injectUpdateSql(mapperClass, modelClass, table);
        /** ?????? (????????? set ??????) */
        this.injectUpdateForSetSql(mapperClass, modelClass, table);
        /** ?????? */
        this.injectSelectByMapSql(mapperClass, modelClass, table);
        this.injectSelectOneSql(mapperClass, modelClass, table);
        this.injectSelectCountSql(mapperClass, modelClass, table);
        this.injectSelectListSql(SqlMethod.SELECT_LIST, mapperClass, modelClass, table);
        this.injectSelectListSql(SqlMethod.SELECT_PAGE, mapperClass, modelClass, table);
        this.injectSelectMapsSql(SqlMethod.SELECT_MAPS, mapperClass, modelClass, table);
        this.injectSelectMapsSql(SqlMethod.SELECT_MAPS_PAGE, mapperClass, modelClass, table);
        this.injectSelectObjsSql(SqlMethod.SELECT_OBJS, mapperClass, modelClass, table);
        /** ??????????????? */
        this.inject(configuration, builderAssistant, mapperClass, modelClass, table);
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    public void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass,
                       Class<?> modelClass, TableInfo table) {
        // to do nothing
    }

    /**
     * ??????????????????,??????????????????????????????T???????????????
     *
     * @param mapperClass
     * @return
     */
    protected Class<?> extractModelClass(Class<?> mapperClass) {
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(typeArray)) {
                    for (Type t : typeArray) {
                        if (t instanceof TypeVariable || t instanceof WildcardType) {
                            target = null;
                            break;
                        } else {
                            target = (ParameterizedType) type;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }

    /**
     * <p>
     * ???????????? SQL ??????
     * </p>
     *
     * @param selective   ??????????????????
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectInsertOneSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        /*
         * INSERT INTO table <trim prefix="(" suffix=")" suffixOverrides=",">
		 * <if test="xx != null">xx,</if> </trim> <trim prefix="values ("
		 * suffix=")" suffixOverrides=","> <if test="xx != null">#{xx},</if>
		 * </trim>
		 */
        KeyGenerator keyGenerator = new NoKeyGenerator();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();
        SqlMethod sqlMethod = selective ? SqlMethod.INSERT_ONE : SqlMethod.INSERT_ONE_ALL_COLUMN;

        fieldBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        String keyProperty = null;
        String keyColumn = null;

        // ???????????????????????????,??????????????????????????????????????????
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            if (table.getIdType() == IdType.AUTO) {
                /** ???????????? */
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = table.getKeyProperty();
                keyColumn = table.getKeyColumn();
            } else {
                if (null != table.getKeySequence()) {
                    keyGenerator = TableInfoHelper.genKeyGenerator(table, builderAssistant, sqlMethod.getMethod(), languageDriver);
                    keyProperty = table.getKeyProperty();
                    keyColumn = table.getKeyColumn();
                    fieldBuilder.append(table.getKeyColumn()).append(",");
                    placeholderBuilder.append("#{").append(table.getKeyProperty()).append("},");
                } else {
                    /** ?????????????????????ID */
                    fieldBuilder.append(table.getKeyColumn()).append(",");
                    // ???????????????????????????
                    placeholderBuilder.append("#{").append(table.getKeyProperty()).append("},");
                }
            }
        }

        // ?????? IF ????????????
        boolean ifTag;
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            // ???FieldIgnore,INSERT_UPDATE,INSERT ????????????false
            ifTag = !(FieldFill.INSERT == fieldInfo.getFieldFill()
                || FieldFill.INSERT_UPDATE == fieldInfo.getFieldFill());
            if (selective && ifTag) {
                fieldBuilder.append(convertIfTagIgnored(fieldInfo, false));
                fieldBuilder.append(fieldInfo.getColumn()).append(",");
                fieldBuilder.append(convertIfTagIgnored(fieldInfo, true));
                placeholderBuilder.append(convertIfTagIgnored(fieldInfo, false));
                placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
                placeholderBuilder.append(convertIfTagIgnored(fieldInfo, true));
            } else {
                fieldBuilder.append(fieldInfo.getColumn()).append(",");
                placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");
            }
        }
        fieldBuilder.append("\n</trim>");
        placeholderBuilder.append("\n</trim>");
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(),
            placeholderBuilder.toString());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty,
            keyColumn);
    }

    /**
     * <p>
     * ?????? entity ???????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectDeleteSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * ?????? map ???????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param table
     */
    protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereByMap(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * ???????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectDeleteByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
        SqlSource sqlSource;
        // ?????????????????????get??????????????????????????????????????????key????????????
        String idStr = table.getKeyProperty();
        if (batch) {
            sqlMethod = SqlMethod.DELETE_BATCH_BY_IDS;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"coll\" separator=\",\">");
            ids.append("#{item}");
            ids.append("\n</foreach>");
            idStr = ids.toString();
        }
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getKeyColumn(), idStr);
        sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * ???????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectUpdateByIdSql(boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = selective ? SqlMethod.UPDATE_BY_ID : SqlMethod.UPDATE_ALL_COLUMN_BY_ID;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table, "et."), table.getKeyColumn(),
            "et." + table.getKeyProperty(),
            "<if test=\"et instanceof java.util.Map\">"
                + "<if test=\"et.MP_OPTLOCK_VERSION_ORIGINAL!=null\">"
                + "and ${et.MP_OPTLOCK_VERSION_COLUMN}=#{et.MP_OPTLOCK_VERSION_ORIGINAL}"
                + "</if>"
                + "</if>"
        );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * ?????????????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectUpdateSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.UPDATE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(true, table, "et."), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * ?????????????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectUpdateForSetSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.UPDATE_FOR_SET;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), customSqlSet(), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>
     * ???????????? SQL ??????
     * </p>
     *
     * @param batch       ?????????????????????
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectByIdSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
        SqlSource sqlSource;
        if (batch) {
            sqlMethod = SqlMethod.SELECT_BATCH_BY_IDS;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"coll\" separator=\",\">");
            ids.append("#{item}");
            ids.append("\n</foreach>");
            sqlSource = languageDriver.createSqlSource(configuration, String.format(sqlMethod.getSql(),
                sqlSelectColumns(table, false), table.getTableName(), table.getKeyColumn(), ids.toString()), modelClass);
        } else {
            sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(), sqlSelectColumns(table, false),
                table.getTableName(), table.getKeyColumn(), table.getKeyProperty()), Object.class);
        }
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * ?????? map ?????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectByMapSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), sqlWhereByMap(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * ?????????????????????????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectOneSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), sqlWhere(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * ??????EntityWrapper???????????????????????? SQL ??????
     * </p>
     *
     * @param sqlMethod
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectListSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, true), table.getTableName(),
            sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>
     * ??????EntityWrapper???????????????????????? SQL ??????
     * </p>
     *
     * @param sqlMethod
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectMapsSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, true), table.getTableName(),
            sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Map.class, table);
    }

    /**
     * <p>
     * ??????EntityWrapper???????????????????????? SQL ??????
     * </p>
     *
     * @param sqlMethod
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectObjsSql(SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        String sql = String.format(sqlMethod.getSql(), sqlSelectObjsColumns(table), table.getTableName(),
            sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Object.class, table);
    }

    /**
     * <p>
     * ??????EntityWrapper?????????????????? SQL ??????
     * </p>
     *
     * @param mapperClass
     * @param modelClass
     * @param table       ?????????
     */
    protected void injectSelectCountSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereEntityWrapper(table));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Integer.class, null);
    }

    /**
     * <p>
     * Sql ????????????
     * </p>
     */
    protected String sqlCondition(String condition, String column, String property) {
        return String.format(condition, column, property);
    }

    /**
     * <p>
     * EntityWrapper????????????select where
     * </p>
     *
     * @param table ?????????
     * @return String
     */
    protected String sqlWhereEntityWrapper(TableInfo table) {
        StringBuilder where = new StringBuilder(128);
        where.append("\n<where>");
        where.append("\n<if test=\"ew!=null\">");
        where.append("\n<if test=\"ew.entity!=null\">");
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            where.append("\n<if test=\"ew.entity.").append(table.getKeyProperty()).append("!=null\">\n");
            where.append(table.getKeyColumn()).append("=#{ew.entity.").append(table.getKeyProperty()).append("}");
            where.append("\n</if>");
        }
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append(convertIfTag(fieldInfo, "ew.entity.", false));
            where.append(" AND ").append(this.sqlCondition(fieldInfo.getCondition(),
                fieldInfo.getColumn(), "ew.entity." + fieldInfo.getEl()));
            where.append(convertIfTag(fieldInfo, true));
        }
        where.append("\n</if>");
        where.append("\n<if test=\"ew!=null and ew.sqlSegment!=null and ew.notEmptyOfWhere\">\n${ew.sqlSegment}\n</if>");
        where.append("\n</if>");
        where.append("\n</where>");
        where.append("\n<if test=\"ew!=null and ew.sqlSegment!=null and ew.emptyOfWhere\">\n${ew.sqlSegment}\n</if>");
        return where.toString();
    }

    /**
     * <p>
     * SQL ?????? set ??????
     * </p>
     *
     * @param selective ??????????????????
     * @param table     ?????????
     * @param prefix    ??????
     * @return
     */
    protected String sqlSet(boolean selective, TableInfo table, String prefix) {
        StringBuilder set = new StringBuilder();
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\">");

        // ?????? IF ????????????
        boolean ifTag;
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            // ????????????????????????,???FieldIgnore,UPDATE,INSERT_UPDATE?????????false
            ifTag = !(FieldFill.UPDATE == fieldInfo.getFieldFill()
                || FieldFill.INSERT_UPDATE == fieldInfo.getFieldFill());
            if (selective && ifTag) {
                if (StringUtils.isNotEmpty(fieldInfo.getUpdate())) {
                    set.append(fieldInfo.getColumn()).append("=");
                    set.append(String.format(fieldInfo.getUpdate(), fieldInfo.getColumn())).append(",");
                } else {
                    set.append(convertIfTag(true, fieldInfo, prefix, false));
                    set.append(fieldInfo.getColumn()).append("=#{");
                    if (null != prefix) {
                        set.append(prefix);
                    }
                    set.append(fieldInfo.getEl()).append("},");
                    set.append(convertIfTag(true, fieldInfo, null, true));
                }
            } else if (FieldFill.INSERT != fieldInfo.getFieldFill()) {
                // ????????????????????????
                set.append(fieldInfo.getColumn()).append("=#{");
                if (null != prefix) {
                    set.append(prefix);
                }
                set.append(fieldInfo.getEl()).append("},");
            }
        }
        set.append("\n</trim>");
        return set.toString();
    }

    /**
     * <p>
     * SQL ??????????????? set ??????
     * </p>
     *
     * @return
     */
    protected String customSqlSet() {
        StringBuilder set = new StringBuilder();
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\">");
        set.append("\n${setStr}");
        set.append("\n</trim>");
        return set.toString();
    }

    /**
     * <p>
     * ?????????????????????SQL??????
     * </p>
     *
     * @param convertStr
     * @return
     */
    protected String sqlWordConvert(String convertStr) {
        GlobalConfiguration globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        return SqlReservedWords.convert(globalConfig, convertStr);
    }

    /**
     * <p>
     * SQL ?????????????????????
     * </p>
     *
     * @param table
     * @param entityWrapper ???????????????????????????
     * @return
     */
    protected String sqlSelectColumns(TableInfo table, boolean entityWrapper) {
        StringBuilder columns = new StringBuilder();
        if (null != table.getResultMap()) {
            /*
             * ?????? resultMap ????????????
			 */
            if (entityWrapper) {
                columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
            }
            columns.append("*");
            if (entityWrapper) {
                columns.append("</otherwise></choose>");
            }
        } else {
            /*
             * ????????????
			 */
            if (entityWrapper) {
                columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
            }
            List<TableFieldInfo> fieldList = table.getFieldList();
            int size = 0;
            if (null != fieldList) {
                size = fieldList.size();
            }

            // ????????????
            if (StringUtils.isNotEmpty(table.getKeyProperty())) {
                if (table.isKeyRelated()) {
                    columns.append(table.getKeyColumn()).append(" AS ").append(sqlWordConvert(table.getKeyProperty()));
                } else {
                    columns.append(sqlWordConvert(table.getKeyProperty()));
                }
                if (size >= 1) {
                    // ??????????????????????????????
                    columns.append(",");
                }
            }

            if (size >= 1) {
                // ????????????
                int i = 0;
                Iterator<TableFieldInfo> iterator = fieldList.iterator();
                while (iterator.hasNext()) {
                    TableFieldInfo fieldInfo = iterator.next();
                    // ??????????????????
                    String wordConvert = sqlWordConvert(fieldInfo.getProperty());
                    if (fieldInfo.getColumn().equals(wordConvert)) {
                        columns.append(wordConvert);
                    } else {
                        // ?????????????????????
                        columns.append(fieldInfo.getColumn());
                        columns.append(" AS ").append(wordConvert);
                    }
                    if (i + 1 < size) {
                        columns.append(",");
                    }
                    i++;
                }
            }
            if (entityWrapper) {
                columns.append("</otherwise></choose>");
            }
        }

		/*
         * ??????????????????????????????
		 */
        return columns.toString();
    }

    /**
     * <p>
     * SQL ??????selectObj sqlselect
     * </p>
     *
     * @param table ???????????????????????????
     * @return
     */
    protected String sqlSelectObjsColumns(TableInfo table) {
        StringBuilder columns = new StringBuilder();
        /*
         * ????????????
		 */
        columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
        // ????????????
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            if (table.isKeyRelated()) {
                columns.append(table.getKeyColumn()).append(" AS ").append(sqlWordConvert(table.getKeyProperty()));
            } else {
                columns.append(sqlWordConvert(table.getKeyProperty()));
            }
        } else {
            // ???????????????
            List<TableFieldInfo> fieldList = table.getFieldList();
            if (CollectionUtils.isNotEmpty(fieldList)) {
                TableFieldInfo fieldInfo = fieldList.get(0);
                // ??????????????????
                String wordConvert = sqlWordConvert(fieldInfo.getProperty());
                if (fieldInfo.getColumn().equals(wordConvert)) {
                    columns.append(wordConvert);
                } else {
                    // ?????????????????????
                    columns.append(fieldInfo.getColumn());
                    columns.append(" AS ").append(wordConvert);
                }
            }
        }
        columns.append("</otherwise></choose>");
        return columns.toString();
    }

    /**
     * <p>
     * SQL ????????????
     * </p>
     */
    protected String sqlWhere(TableInfo table) {
        StringBuilder where = new StringBuilder();
        where.append("\n<where>");
        if (StringUtils.isNotEmpty(table.getKeyProperty())) {
            where.append("\n<if test=\"ew.").append(table.getKeyProperty()).append("!=null\">\n");
            where.append(table.getKeyColumn()).append("=#{ew.").append(table.getKeyProperty()).append("}");
            where.append("\n</if>");
        }
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append(convertIfTag(fieldInfo, "ew.", false));
            where.append(" AND ").append(this.sqlCondition(fieldInfo.getCondition(),
                fieldInfo.getColumn(), "ew." + fieldInfo.getEl()));
            where.append(convertIfTag(fieldInfo, true));
        }
        where.append("\n</where>");
        return where.toString();
    }

    /**
     * <p>
     * SQL map ????????????
     * </p>
     */
    protected String sqlWhereByMap(TableInfo table) {
        StringBuilder where = new StringBuilder();
        where.append("\n<if test=\"cm!=null and !cm.isEmpty\">");
        where.append("\n<where>");
        where.append("\n<foreach collection=\"cm.keys\" item=\"k\" separator=\"AND\">");
        where.append("\n<if test=\"cm[k] != null\">");
        where.append("\n").append(SqlReservedWords.convert(getGlobalConfig(), "${k}")).append(" = #{cm[${k}]}");
        where.append("\n</if>");
        where.append("\n</foreach>");
        where.append("\n</where>");
        where.append("\n</if>");
        return where.toString();
    }

    /**
     * <p>
     * IF ??????????????????
     * </p>
     *
     * @param ignored   ????????????
     * @param fieldInfo ????????????
     * @param prefix    ????????????
     * @param close     ??????????????????
     * @return
     */
    protected String convertIfTag(boolean ignored, TableFieldInfo fieldInfo, String prefix, boolean close) {
        /** ???????????? */
        FieldStrategy fieldStrategy = fieldInfo.getFieldStrategy();
        if (fieldStrategy == FieldStrategy.IGNORED) {
            if (ignored) {
                return "";
            }
            // ?????????????????????????????????
            fieldStrategy = this.getGlobalConfig().getFieldStrategy();
        }

        // ????????????
        if (close) {
            return "</if>";
        }

        /** ???????????? */
        String property = fieldInfo.getProperty();
        Class propertyType = fieldInfo.getPropertyType();
        property = StringUtils.removeIsPrefixIfBoolean(property, propertyType);
        if (null != prefix) {
            property = prefix + property;
        }
        // ????????????
        if (fieldStrategy == FieldStrategy.NOT_EMPTY) {
            if (StringUtils.isCharSequence(propertyType)) {
                return String.format("\n\t<if test=\"%s!=null and %s!=''\">", property, property);
            } else {
                return String.format("\n\t<if test=\"%s!=null \">", property);
            }
        } else {
            // FieldStrategy.NOT_NULL
            return String.format("\n\t<if test=\"%s!=null\">", property);
        }
    }

    protected String convertIfTagIgnored(TableFieldInfo fieldInfo, boolean close) {
        return convertIfTag(true, fieldInfo, null, close);
    }

    protected String convertIfTag(TableFieldInfo fieldInfo, String prefix, boolean close) {
        return convertIfTag(false, fieldInfo, prefix, close);
    }

    protected String convertIfTag(TableFieldInfo fieldInfo, boolean close) {
        return convertIfTag(fieldInfo, null, close);
    }

    /**
     * ??????
     */
    public MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType,
                                                    TableInfo table) {
        if (null != table) {
            String resultMap = table.getResultMap();
            if (null != resultMap) {
                /** ?????? resultMap ??????????????? */
                return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null,
                    new NoKeyGenerator(), null, null);
            }
        }

        /** ???????????? */
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null, resultType,
            new NoKeyGenerator(), null, null);
    }

    /**
     * ??????
     */
    public MappedStatement addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource,
                                                    KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.INSERT, modelClass, null, Integer.class,
            keyGenerator, keyProperty, keyColumn);
    }

    /**
     * ??????
     */
    public MappedStatement addDeleteMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    /**
     * ??????
     */
    public MappedStatement addUpdateMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, Integer.class,
            new NoKeyGenerator(), null, null);
    }

    public MappedStatement addMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource,
                                              SqlCommandType sqlCommandType, Class<?> parameterClass, String resultMap, Class<?> resultType,
                                              KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + "." + id;
        if (hasMappedStatement(statementName)) {
            System.err.println("{" + statementName
                + "} Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
            return null;
        }
        /** ?????????????????? */
        boolean isSelect = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            isSelect = true;
        }
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
            parameterClass, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
            configuration.getDatabaseId(), languageDriver, null);
    }

    // --------------------------------------------------------SqlRunner------------------------------------------------------------

    @Override
    public void injectSqlRunner(Configuration configuration) {
        this.configuration = configuration;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        initSelectList();
        initSelectObjs();
        initInsert();
        initUpdate();
        initDelete();
        initCount();
    }

    /**
     * ??????????????????MappedStatement
     *
     * @param mappedStatement
     * @return
     */
    private boolean hasMappedStatement(String mappedStatement) {
        return configuration.hasStatement(mappedStatement, false);
    }

    /**
     * ????????????MappedStatement
     *
     * @param mappedStatement
     * @param sqlSource       ?????????sqlSource
     * @param resultType      ?????????????????????
     */
    @SuppressWarnings("serial")
    private void createSelectMappedStatement(String mappedStatement, SqlSource sqlSource, final Class<?> resultType) {
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatement, sqlSource, SqlCommandType.SELECT)
            .resultMaps(new ArrayList<ResultMap>() {
                {
                    add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, new ArrayList<ResultMapping>(0))
                        .build());
                }
            }).build();
        // ??????
        configuration.addMappedStatement(ms);
    }

    /**
     * ????????????MappedStatement
     *
     * @param mappedStatement
     * @param sqlSource       ?????????sqlSource
     * @param sqlCommandType  ?????????sqlCommandType
     */
    @SuppressWarnings("serial")
    private void createUpdateMappedStatement(String mappedStatement, SqlSource sqlSource, SqlCommandType sqlCommandType) {
        MappedStatement ms = new MappedStatement.Builder(configuration, mappedStatement, sqlSource, sqlCommandType).resultMaps(
            new ArrayList<ResultMap>() {
                {
                    add(new ResultMap.Builder(configuration, "defaultResultMap", int.class, new ArrayList<ResultMapping>(0))
                        .build());
                }
            }).build();
        // ??????
        configuration.addMappedStatement(ms);
    }

    /**
     * initSelectList
     */
    private void initSelectList() {
        if (hasMappedStatement(SqlRunner.SELECT_LIST)) {
            logger.warn("MappedStatement 'SqlRunner.SelectList' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createSelectMappedStatement(SqlRunner.SELECT_LIST, sqlSource, Map.class);
    }

    /**
     * initSelectObjs
     */
    private void initSelectObjs() {
        if (hasMappedStatement(SqlRunner.SELECT_OBJS)) {
            logger.warn("MappedStatement 'SqlRunner.SelectObjs' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Object.class);
        createSelectMappedStatement(SqlRunner.SELECT_OBJS, sqlSource, Object.class);
    }

    /**
     * initCount
     */
    private void initCount() {
        if (hasMappedStatement(SqlRunner.COUNT)) {
            logger.warn("MappedStatement 'SqlRunner.Count' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createSelectMappedStatement(SqlRunner.COUNT, sqlSource, Integer.class);
    }

    /**
     * initInsert
     */
    private void initInsert() {
        if (hasMappedStatement(SqlRunner.INSERT)) {
            logger.warn("MappedStatement 'SqlRunner.Insert' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createUpdateMappedStatement(SqlRunner.INSERT, sqlSource, SqlCommandType.INSERT);
    }

    /**
     * initUpdate
     */
    private void initUpdate() {
        if (hasMappedStatement(SqlRunner.UPDATE)) {
            logger.warn("MappedStatement 'SqlRunner.Update' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createUpdateMappedStatement(SqlRunner.UPDATE, sqlSource, SqlCommandType.UPDATE);
    }

    /**
     * initDelete
     */
    private void initDelete() {
        if (hasMappedStatement(SqlRunner.DELETE)) {
            logger.warn("MappedStatement 'SqlRunner.Delete' Already Exists");
            return;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, SqlRunner.SQLScript, Map.class);
        createUpdateMappedStatement(SqlRunner.DELETE, sqlSource, SqlCommandType.DELETE);
    }

    /**
     * <p>
     * ????????????
     * </p>
     */
    protected GlobalConfiguration getGlobalConfig() {
        return GlobalConfigUtils.getGlobalConfig(configuration);
    }

}
