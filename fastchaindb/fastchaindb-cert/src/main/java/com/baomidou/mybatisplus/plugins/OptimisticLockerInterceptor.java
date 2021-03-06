package com.baomidou.mybatisplus.plugins;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.toolkit.ClassUtils;
import com.baomidou.mybatisplus.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;


/**
 * <p>
 * Optimistic Lock Light version<BR>
 * Intercept on {@link Executor}.update;<BR>
 * Support version types: int/Integer, long/Long, java.util.Date, java.sql.Timestamp<BR>
 * For extra types, please define a subclass and override {@code getUpdatedVersionVal}() method.<BR>
 * <BR>
 * How to use?<BR>
 * (1) Define an Entity and add {@link Version} annotation on one entity field.<BR>
 * (2) Add {@link OptimisticLockerInterceptor} into mybatis plugin.
 * <p>
 * How to work?<BR>
 * if update entity with version column=1:<BR>
 * (1) no {@link OptimisticLockerInterceptor}:<BR>
 * SQL: update tbl_test set name='abc' where id=100001;<BR>
 * (2) add {@link OptimisticLockerInterceptor}:<BR>
 * SQL: update tbl_test set name='abc',version=2 where id=100001 and version=1;
 * </p>
 *
 * @author yuxiaobin
 * @since 2017/5/24
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class OptimisticLockerInterceptor implements Interceptor {

    public static final String MP_OPTLOCK_VERSION_ORIGINAL = "MP_OPTLOCK_VERSION_ORIGINAL";
    public static final String MP_OPTLOCK_VERSION_COLUMN = "MP_OPTLOCK_VERSION_COLUMN";
    public static final String MP_OPTLOCK_ET_ORIGINAL = "MP_OPTLOCK_ET_ORIGINAL";
    private static final String NAME_ENTITY = "et";
    private static final String NAME_ENTITY_WRAPPER = "ew";
    private static final String PARAM_UPDATE_METHOD_NAME = "update";
    private final Map<Class<?>, EntityField> versionFieldCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<EntityField>> entityFieldsCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        Object param = args[1];

        // wrapper = ew
        Wrapper ew = null;
        // entity = et
        Object et = null;
        if (param instanceof Map) {
            Map map = (Map) param;
            if (map.containsKey(NAME_ENTITY)) {
                //updateById(et), update(et, wrapper);
                et = map.get(NAME_ENTITY);
            }
            if (map.containsKey(NAME_ENTITY_WRAPPER)) {
                // mapper.update(updEntity, QueryWrapper<>(whereEntity);
                ew = (Wrapper) map.get(NAME_ENTITY_WRAPPER);
            }
            if (et != null) {
                // entity
                String methodId = ms.getId();
                String updateMethodName = methodId.substring(ms.getId().lastIndexOf(".") + 1);
                Class<?> entityClass = et.getClass();
                TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                // fixed github 299
                while (tableInfo == null && entityClass != null) {
                    entityClass = ClassUtils.getUserClass(entityClass.getSuperclass());
                    tableInfo = TableInfoHelper.getTableInfo(entityClass);
                }
                EntityField entityVersionField = this.getVersionField(entityClass, tableInfo);
                if (entityVersionField == null) {
                    return invocation.proceed();
                }
                Field versionField = entityVersionField.getField();
                Object originalVersionVal = entityVersionField.getField().get(et);
                Object updatedVersionVal = getUpdatedVersionVal(originalVersionVal);
                if (PARAM_UPDATE_METHOD_NAME.equals(updateMethodName)) {
                    // update(entity, wrapper)
                    if (originalVersionVal != null) {
                        if (ew == null) {
                            Wrapper aw = new EntityWrapper();
                            aw.eq(entityVersionField.getColumnName(), originalVersionVal);
                            map.put(NAME_ENTITY_WRAPPER, aw);
                            versionField.set(et, updatedVersionVal);
                        } else if (ew instanceof EntityWrapper) {
                            EntityWrapper aw = (EntityWrapper) ew;
                            aw.eq(entityVersionField.getColumnName(), originalVersionVal);
                            versionField.set(et, updatedVersionVal);
                            //TODO: should remove version=oldval condition from aw; 0827
                        }
                    }
                    return invocation.proceed();
                } else {
                    dealUpdateById(entityClass, et, entityVersionField, originalVersionVal, updatedVersionVal, map);
                    Object resultObj = invocation.proceed();
                    if (resultObj instanceof Integer) {
                        Integer effRow = (Integer) resultObj;
                        if (updatedVersionVal != null && effRow != 0 && versionField != null) {
                            //updated version value set to entity.
                            versionField.set(et, updatedVersionVal);
                        }
                    }
                    return resultObj;
                }
            }
        }
        return invocation.proceed();
    }

    /**
     * ??????updateById(entity)???????????????
     *
     * @param entityClass        ?????????
     * @param et                 ??????entity
     * @param entityVersionField
     * @param originalVersionVal ???????????????value
     * @param updatedVersionVal  ???????????????????????????value
     * @param map
     */
    private void dealUpdateById(Class<?> entityClass, Object et, EntityField entityVersionField,
                                Object originalVersionVal, Object updatedVersionVal, Map map) throws IllegalAccessException {
        if (originalVersionVal == null) {
            return;
        }
        List<EntityField> fields = getEntityFields(entityClass);
        Map<String, Object> entityMap = new HashMap<>();
        for (EntityField ef : fields) {
            Field fd = ef.getField();
            entityMap.put(fd.getName(), fd.get(et));
        }
        Field versionField = entityVersionField.getField();
        String versionColumnName = entityVersionField.getColumnName();
        //update to cache
        entityVersionField.setColumnName(versionColumnName);
        entityMap.put(versionField.getName(), updatedVersionVal);
        entityMap.put(MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
        entityMap.put(MP_OPTLOCK_VERSION_COLUMN, versionColumnName);
        entityMap.put(MP_OPTLOCK_ET_ORIGINAL, et);
        map.put(NAME_ENTITY, entityMap);
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Object originalVersionVal) {
        if (null == originalVersionVal) {
            return null;
        }
        Class<?> versionValClass = originalVersionVal.getClass();
        if (long.class.equals(versionValClass)) {
            return ((long) originalVersionVal) + 1;
        } else if (Long.class.equals(versionValClass)) {
            return ((Long) originalVersionVal) + 1;
        } else if (int.class.equals(versionValClass)) {
            return ((int) originalVersionVal) + 1;
        } else if (Integer.class.equals(versionValClass)) {
            return ((Integer) originalVersionVal) + 1;
        } else if (Date.class.equals(versionValClass)) {
            return new Date();
        } else if (Timestamp.class.equals(versionValClass)) {
            return new Timestamp(System.currentTimeMillis());
        }
        //not supported type, return original val.
        return originalVersionVal;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }

    private EntityField getVersionField(Class<?> parameterClass, TableInfo tableInfo) {
        synchronized (parameterClass.getName()) {
            if (versionFieldCache.containsKey(parameterClass)) {
                return versionFieldCache.get(parameterClass);
            }
            // ???????????????
            EntityField field = this.getVersionFieldRegular(parameterClass, tableInfo);
            if (field != null) {
                versionFieldCache.put(parameterClass, field);
                return field;
            }
            return null;
        }
    }

    /**
     * <p>
     * ??????????????????????????????????????????
     * </p>
     *
     * @param parameterClass ?????????
     * @param tableInfo      ???????????????????????????
     * @return
     */
    private EntityField getVersionFieldRegular(Class<?> parameterClass, TableInfo tableInfo) {
        if (parameterClass != Object.class) {
            for (Field field : parameterClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Version.class)) {
                    field.setAccessible(true);
                    String versionPropertyName = field.getName();
                    String versionColumnName = null;
                    for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
                        if (versionPropertyName.equals(fieldInfo.getProperty())) {
                            versionColumnName = fieldInfo.getColumn();
                        }
                    }
                    return new EntityField(field, true, versionColumnName);
                }
            }
            // ????????????
            return this.getVersionFieldRegular(parameterClass.getSuperclass(), tableInfo);
        }
        return null;
    }

    /**
     * ???????????????????????????(??????getter)
     *
     * @param parameterClass
     * @return
     */
    private List<EntityField> getEntityFields(Class<?> parameterClass) {
        if (entityFieldsCache.containsKey(parameterClass)) {
            return entityFieldsCache.get(parameterClass);
        }
        List<EntityField> fields = this.getFieldsFromClazz(parameterClass);
        entityFieldsCache.put(parameterClass, fields);
        return fields;
    }

    private List<EntityField> getFieldsFromClazz(Class<?> parameterClass) {
        List<EntityField> fieldList = new ArrayList<>();
        List<Field> fields = ReflectionKit.getFieldList(parameterClass);
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Version.class)) {
                fieldList.add(new EntityField(field, true));
            } else {
                fieldList.add(new EntityField(field, false));
            }
        }
        return fieldList;
    }

    private class EntityField {

        EntityField(Field field, boolean version) {
            this.field = field;
            this.version = version;
        }

        public EntityField(Field field, boolean version, String columnName) {
            this.field = field;
            this.version = version;
            this.columnName = columnName;
        }

        private Field field;
        private boolean version;
        private String columnName;

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public boolean isVersion() {
            return version;
        }

        public void setVersion(boolean version) {
            this.version = version;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }
}
