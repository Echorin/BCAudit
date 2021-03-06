/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.toolkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.mapper.SqlRunner;

/**
 * <p>
 * ???????????????????????????
 * </p>
 *
 * @author hubin sjy
 * @Date 2016-09-09
 */
public class TableInfoHelper {

    private static final Log logger = LogFactory.getLog(TableInfoHelper.class);
    /**
     * ????????????????????????
     */
    private static final Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<>();
    /**
     * ???????????????
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * ???????????????????????????
     * <p>
     *
     * @param clazz ???????????????
     * @return
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        TableInfo tableInfo = tableInfoCache.get(ClassUtils.getUserClass(clazz).getName());
        if(tableInfo!=null){
            return tableInfo;
        }else{
            //????????????????????????
            Class c = clazz;
            while (tableInfo==null && Object.class!=c){
                c = c.getSuperclass();
                tableInfo = tableInfoCache.get(ClassUtils.getUserClass(c).getName());
            }
            if(tableInfo!=null){
                tableInfoCache.put(ClassUtils.getUserClass(clazz).getName(),tableInfo);
            }else{
                //????????????,??????????????????
                logger.warn(ClassUtils.getUserClass(clazz).getName() + "Not Found TableInfoCache.");
            }
        }
        return tableInfo;
    }

    /**
     * <p>
     * ?????????????????????????????????
     * <p>
     *
     * @return
     */
    public static List<TableInfo> getTableInfos() {
        return new ArrayList<>(tableInfoCache.values());
    }

    /**
     * <p>
     * ?????????????????????????????????????????????
     * <p>
     *
     * @param clazz ???????????????
     * @return
     */
    public synchronized static TableInfo initTableInfo(MapperBuilderAssistant builderAssistant, Class<?> clazz) {
        TableInfo tableInfo = tableInfoCache.get(clazz.getName());
        if (StringUtils.checkValNotNull(tableInfo)) {
            if (StringUtils.checkValNotNull(builderAssistant)) {
                tableInfo.setConfigMark(builderAssistant.getConfiguration());
            }
            return tableInfo;
        }
        tableInfo = new TableInfo();
        GlobalConfiguration globalConfig;
        if (null != builderAssistant) {
            tableInfo.setCurrentNamespace(builderAssistant.getCurrentNamespace());
            tableInfo.setConfigMark(builderAssistant.getConfiguration());
            globalConfig = GlobalConfigUtils.getGlobalConfig(builderAssistant.getConfiguration());
        } else {
            // ??????????????????
            globalConfig = GlobalConfigUtils.DEFAULT;
        }
        /* ?????? */
        TableName table = clazz.getAnnotation(TableName.class);
        String tableName = clazz.getSimpleName();
        if (table != null && StringUtils.isNotEmpty(table.value())) {
            tableName = table.value();
        } else {
            // ???????????????????????????
            if (globalConfig.isDbColumnUnderline()) {
                tableName = StringUtils.camelToUnderline(tableName);
            }
            // ??????????????????
            if (globalConfig.isCapitalMode()) {
                tableName = tableName.toUpperCase();
            } else {
                // ???????????????
                tableName = StringUtils.firstToLowerCase(tableName);
            }
            // ???????????????
            if (null != globalConfig.getTablePrefix()) {
                tableName = globalConfig.getTablePrefix() + tableName;
            }
        }
        tableInfo.setTableName(tableName);

        // ?????????????????? KEY ?????????
        if (null != globalConfig.getKeyGenerator()) {
            tableInfo.setKeySequence(clazz.getAnnotation(KeySequence.class));
        }

        /* ?????????????????? */
        if (table != null && StringUtils.isNotEmpty(table.resultMap())) {
            tableInfo.setResultMap(table.resultMap());
        }
        List<TableFieldInfo> fieldList = new ArrayList<>();
        List<Field> list = getAllFields(clazz);
        // ???????????????????????????
        boolean isReadPK = false;
        boolean existTableId = existTableId(list);
        for (Field field : list) {
            /*
             * ??????ID ?????????
             */
            if (!isReadPK) {
                if (existTableId) {
                    isReadPK = initTableId(globalConfig, tableInfo, field, clazz);
                } else {
                    isReadPK = initFieldId(globalConfig, tableInfo, field, clazz);
                }
                if (isReadPK) {
                    continue;
                }

            }
            /*
             * ???????????????
             */
            if (initTableField(globalConfig, tableInfo, fieldList, field, clazz)) {
                continue;
            }

            /*
             * ??????, ?????? camelToUnderline ???????????????????????????????????????, ??????????????? TableField , ?????????????????????
             */
            fieldList.add(new TableFieldInfo(globalConfig, tableInfo, field));
        }

        /* ???????????? */
        tableInfo.setFieldList(globalConfig, fieldList);
        /*
         * ??????????????????????????????????????????
         */
        if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
            logger.warn(String.format("Warn: Could not find @TableId in Class: %s.", clazz.getName()));
        }
        /*
         * ??????
         */
        tableInfoCache.put(clazz.getName(), tableInfo);
        return tableInfo;
    }

    /**
     * <p>
     * ??????????????????????????????
     * </p>
     *
     * @param list ????????????
     * @return
     */
    public static boolean existTableId(List<Field> list) {
        boolean exist = false;
        for (Field field : list) {
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    /**
     * <p>
     * ?????????????????????
     * </p>
     *
     * @param tableInfo
     * @param field
     * @param clazz
     * @return true ???????????????????????????????????? continue;
     */
    private static boolean initTableId(GlobalConfiguration globalConfig, TableInfo tableInfo, Field field, Class<?> clazz) {
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                /*
                 * ??????????????? ?????? > ?????? > ?????? ???
                 */
                // ?????? Sequence ??????????????????
                if (IdType.NONE != tableId.type()) {
                    tableInfo.setIdType(tableId.type());
                } else {
                    tableInfo.setIdType(globalConfig.getIdType());
                }

                /* ?????? */
                String column = field.getName();
                if (StringUtils.isNotEmpty(tableId.value())) {
                    column = tableId.value();
                    tableInfo.setKeyRelated(true);
                } else {
                    // ???????????????????????????
                    if (globalConfig.isDbColumnUnderline()) {
                        column = StringUtils.camelToUnderline(column);
                        tableInfo.setKeyRelated(true);
                    }
                    // ??????????????????
                    if (globalConfig.isCapitalMode()) {
                        column = column.toUpperCase();
                    }
                }
                tableInfo.setKeyColumn(column);
                tableInfo.setKeyProperty(field.getName());
                return true;
            } else {
                throwExceptionId(clazz);
            }
        }
        return false;
    }

    /**
     * <p>
     * ?????????????????????
     * </p>
     *
     * @param tableInfo
     * @param field
     * @param clazz
     * @return true ???????????????????????????????????? continue;
     */
    private static boolean initFieldId(GlobalConfiguration globalConfig, TableInfo tableInfo, Field field, Class<?> clazz) {
        String column = field.getName();
        if (globalConfig.isCapitalMode()) {
            column = column.toUpperCase();
        }
        if (DEFAULT_ID_NAME.equalsIgnoreCase(column)) {
            if (StringUtils.isEmpty(tableInfo.getKeyColumn())) {
                tableInfo.setIdType(globalConfig.getIdType());
                tableInfo.setKeyColumn(column);
                tableInfo.setKeyProperty(field.getName());
                return true;
            } else {
                throwExceptionId(clazz);
            }
        }
        return false;
    }

    /**
     * <p>
     * ??????????????????????????????????????????
     * </p>
     */
    private static void throwExceptionId(Class<?> clazz) {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("There must be only one, Discover multiple @TableId annotation in ");
        errorMsg.append(clazz.getName());
        throw new MybatisPlusException(errorMsg.toString());
    }

    /**
     * <p>
     * ?????????????????????
     * </p>
     *
     * @param globalConfig ????????????
     * @param tableInfo    ?????????
     * @param fieldList    ????????????
     * @param clazz        ??????????????????
     * @return true ???????????????????????????????????? continue;
     */
    private static boolean initTableField(GlobalConfiguration globalConfig, TableInfo tableInfo, List<TableFieldInfo> fieldList,
                                          Field field, Class<?> clazz) {
        /* ???????????????????????????????????? */
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null) {
            String columnName = field.getName();
            if (StringUtils.isNotEmpty(tableField.value())) {
                columnName = tableField.value();
            }
            /*
             * el ??????????????????????????????????????????????????????
             */
            String el = field.getName();
            if (StringUtils.isNotEmpty(tableField.el())) {
                el = tableField.el();
            }
            String[] columns = columnName.split(";");
            String[] els = el.split(";");
            if (columns.length == els.length) {
                for (int i = 0; i < columns.length; i++) {
                    fieldList.add(new TableFieldInfo(globalConfig, tableInfo, columns[i], els[i], field, tableField));
                }
            } else {
                String errorMsg = "Class: %s, Field: %s, 'value' 'el' Length must be consistent.";
                throw new MybatisPlusException(String.format(errorMsg, clazz.getName(), field.getName()));
            }
            return true;
        }
        return false;
    }

    /**
     * ?????????????????????????????????
     *
     * @param clazz ?????????
     * @return
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
        if (CollectionUtils.isNotEmpty(fieldList)) {
            Iterator<Field> iterator = fieldList.iterator();
            while (iterator.hasNext()) {
                Field field = iterator.next();
                /* ?????????????????????????????? */
                TableField tableField = field.getAnnotation(TableField.class);
                if (tableField != null && !tableField.exist()) {
                    iterator.remove();
                }
            }
        }
        return fieldList;
    }

    /**
     * ?????????SqlSessionFactory (???Mybatis????????????)
     *
     * @param sqlSessionFactory
     * @return
     */
    public static void initSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        GlobalConfiguration globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
        // SqlRunner
        SqlRunner.FACTORY = sqlSessionFactory;
        if (globalConfig == null) {
            GlobalConfiguration defaultCache = GlobalConfigUtils.defaults();
            defaultCache.setSqlSessionFactory(sqlSessionFactory);
            GlobalConfigUtils.setGlobalConfig(configuration, defaultCache);
        } else {
            globalConfig.setSqlSessionFactory(sqlSessionFactory);
        }
    }

    /**
     * <p>
     * ????????? KEY ?????????
     * </p>
     */
    public static KeyGenerator genKeyGenerator(TableInfo tableInfo, MapperBuilderAssistant builderAssistant,
                                               String baseStatementId, LanguageDriver languageDriver) {
        IKeyGenerator keyGenerator = GlobalConfigUtils.getKeyGenerator(builderAssistant.getConfiguration());
        if (null == keyGenerator) {
            throw new IllegalArgumentException("not configure IKeyGenerator implementation class.");
        }
        String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        Class<?> resultTypeClass = tableInfo.getKeySequence().clazz();
        StatementType statementType = StatementType.PREPARED;
        String keyProperty = tableInfo.getKeyProperty();
        String keyColumn = tableInfo.getKeyColumn();
        SqlSource sqlSource = languageDriver.createSqlSource(builderAssistant.getConfiguration(),
            keyGenerator.executeSql(tableInfo.getKeySequence().value()), null);
        builderAssistant.addMappedStatement(id, sqlSource, statementType, SqlCommandType.SELECT, null, null, null,
            null, null, resultTypeClass, null, false, false, false,
            new NoKeyGenerator(), keyProperty, keyColumn, null, languageDriver, null);
        id = builderAssistant.applyCurrentNamespace(id, false);
        MappedStatement keyStatement = builderAssistant.getConfiguration().getMappedStatement(id, false);
        SelectKeyGenerator selectKeyGenerator = new SelectKeyGenerator(keyStatement, true);
        builderAssistant.getConfiguration().addKeyGenerator(id, selectKeyGenerator);
        return selectKeyGenerator;
    }

}
