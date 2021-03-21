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
package com.baomidou.mybatisplus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.List;

/**
 * <p>
 * 继承 XMLLanguageDriver 重装构造函数，使用自定义 ParameterHandler
 * </p>
 *
 * @author hubin
 * @Date 2016-03-11
 */
public class MybatisXMLLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject,
                                                   BoundSql boundSql) {


        String sql = boundSql.getSql();
        List<ParameterMapping> list = boundSql.getParameterMappings();


        StringBuilder sBuffer = new StringBuilder();

        String className="com.oschain.fastchaindb.system.model.Role";
        // 获得属性名


        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(clazz.isInstance(parameterObject)) {

//            try {
//
//               // Class t = (Class)clazz.newInstance();
//
//
//
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }

        }


        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            sBuffer.append("属性名\t：" + field.getName() + "\n");
            System.out.println("属性名\t：" + field.getName());
            sBuffer.append("-属性类型\t：" + field.getType() + "\n");
            System.out.println("-属性类型\t：" + field.getType());
        }

        // 获得方法名
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.toString().indexOf(className) > 0) {
                sBuffer.append("方法名\t：" + method.toString().substring(method.toString().indexOf(className)) + "\n");
                System.out.println("方法名\t：" + method.toString().substring(method.toString().indexOf(className)));
            }
        }
        sBuffer.append("--------------------------------------------------------------------------------" + "\n");
        System.out.println("--------------------------------------------------------------------------------");

//        for (int i = 0; i < parameterMappings.size(); i++) {
//            final ParameterMapping parameterMapping = parameterMappings.get(i);
//            if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
//                if (ResultSet.class.equals(parameterMapping.getJavaType())) {
//                    handleRefCursorOutputParameter((ResultSet) cs.getObject(i + 1), parameterMapping, metaParam);
//                } else {
//                    final TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
//                    metaParam.setValue(parameterMapping.getProperty(), typeHandler.getResult(cs, i + 1));
//                }
//            }
//        }


        /* 使用自定义 ParameterHandler */
        return new MybatisDefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    public static  <T> T get(Class<T> clz,Object o){
        if(clz.isInstance(o)){
            return clz.cast(o);
        }
        return null;
    }
}
