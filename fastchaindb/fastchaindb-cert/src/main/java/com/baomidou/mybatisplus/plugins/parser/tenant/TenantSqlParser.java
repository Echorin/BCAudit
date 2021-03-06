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
package com.baomidou.mybatisplus.plugins.parser.tenant;

import java.util.List;

import com.baomidou.mybatisplus.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.plugins.parser.AbstractJsqlParser;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;

/**
 * <p>
 * ?????? SQL ????????? TenantId ?????? ???
 * </p>
 *
 * @author hubin
 * @since 2017-09-01
 */
public class TenantSqlParser extends AbstractJsqlParser {

    private TenantHandler tenantHandler;

    /**
     * select ????????????
     */
    @Override
    public void processSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            processPlainSelect((PlainSelect) selectBody);
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                processSelectBody(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    processSelectBody(plainSelect);
                }
            }
        }
    }

    /**
     * <p>
     * insert ????????????
     * </p>
     */
    @Override
    public void processInsert(Insert insert) {
        if (this.tenantHandler.doTableFilter(insert.getTable().getName())) {
            // ??????????????????
            return;
        }
        insert.getColumns().add(new Column(this.tenantHandler.getTenantIdColumn()));
        if (insert.getSelect() != null) {
            processPlainSelect((PlainSelect) insert.getSelect().getSelectBody(), true);
        } else if (insert.getItemsList() != null) {
            // fixed github pull/295
            ItemsList itemsList = insert.getItemsList();
            if (itemsList instanceof MultiExpressionList) {
                List<ExpressionList> exprList = ((MultiExpressionList) itemsList).getExprList();
                for (ExpressionList el : exprList) {
                    el.getExpressions().add(tenantHandler.getTenantId());
                }
            } else {
                ((ExpressionList) insert.getItemsList()).getExpressions().add(tenantHandler.getTenantId());
            }
        } else {
            throw new MybatisPlusException("Failed to process multiple-table update, please exclude the tableName or statementId");
        }
    }

    /**
     * <p>
     * update ????????????
     * </p>
     */
    @Override
    public void processUpdate(Update update) {
        List<Table> tableList = update.getTables();
        if (null == tableList || tableList.size() >= 2) {
            throw new MybatisPlusException("Failed to process multiple-table update, please exclude the statementId");
        }
        Table table = tableList.get(0);
        if (this.tenantHandler.doTableFilter(table.getName())) {
            // ??????????????????
            return;
        }
        update.setWhere(this.andExpression(table, update.getWhere()));
    }

    /**
     * <p>
     * delete ????????????
     * </p>
     */
    @Override
    public void processDelete(Delete delete) {
        if (this.tenantHandler.doTableFilter(delete.getTable().getName())) {
            // ??????????????????
            return;
        }
        delete.setWhere(this.andExpression(delete.getTable(), delete.getWhere()));
    }

    /**
     * <p>
     * delete update ?????? where ??????
     * </p>
     */
    protected BinaryExpression andExpression(Table table, Expression where) {
        //??????where???????????????
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        equalsTo.setRightExpression(tenantHandler.getTenantId());
        if (null != where) {
            return new AndExpression(equalsTo, where);
        }
        return equalsTo;
    }

    /**
     * <p>
     * ?????? PlainSelect
     * </p>
     */
    protected void processPlainSelect(PlainSelect plainSelect) {
        processPlainSelect(plainSelect, false);
    }

    /**
     * <p>
     * ?????? PlainSelect
     * </p>
     *
     * @param plainSelect
     * @param addColumn   ?????????????????????,insert into select???????????????
     */
    protected void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            if (this.tenantHandler.doTableFilter(fromTable.getName())) {
                // ??????????????????
                return;
            }
            plainSelect.setWhere(builderExpression(plainSelect.getWhere(), fromTable));
            if (addColumn) {
                plainSelect.getSelectItems().add(new SelectExpressionItem(new Column(this.tenantHandler.getTenantIdColumn())));
            }
        } else {
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            for (Join join : joins) {
                processJoin(join);
                processFromItem(join.getRightItem());
            }
        }
    }

    /**
     * ??????????????????
     */
    protected void processFromItem(FromItem fromItem) {
        if (fromItem instanceof SubJoin) {
            SubJoin subJoin = (SubJoin) fromItem;
            if (subJoin.getJoin() != null) {
                processJoin(subJoin.getJoin());
            }
            if (subJoin.getLeft() != null) {
                processFromItem(subJoin.getLeft());
            }
        } else if (fromItem instanceof SubSelect) {
            SubSelect subSelect = (SubSelect) fromItem;
            if (subSelect.getSelectBody() != null) {
                processSelectBody(subSelect.getSelectBody());
            }
        } else if (fromItem instanceof ValuesList) {
            logger.debug("Perform a subquery, if you do not give us feedback");
        } else if (fromItem instanceof LateralSubSelect) {
            LateralSubSelect lateralSubSelect = (LateralSubSelect) fromItem;
            if (lateralSubSelect.getSubSelect() != null) {
                SubSelect subSelect = lateralSubSelect.getSubSelect();
                if (subSelect.getSelectBody() != null) {
                    processSelectBody(subSelect.getSelectBody());
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    protected void processJoin(Join join) {
        if (join.getRightItem() instanceof Table) {
            Table fromTable = (Table) join.getRightItem();
            if (this.tenantHandler.doTableFilter(fromTable.getName())) {
                // ??????????????????
                return;
            }
            join.setOnExpression(builderExpression(join.getOnExpression(), fromTable));
        }
    }

    /**
     * ????????????
     */
    protected Expression builderExpression(Expression expression, Table table) {
        //???????????????
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        equalsTo.setRightExpression(tenantHandler.getTenantId());
        //??????????????????????????????????????? "and null" ????????????????????????
        if (expression == null) {
            return equalsTo;
        } else {
            if (expression instanceof BinaryExpression) {
                BinaryExpression binaryExpression = (BinaryExpression) expression;
                if (binaryExpression.getLeftExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getLeftExpression());
                }
                if (binaryExpression.getRightExpression() instanceof FromItem) {
                    processFromItem((FromItem) binaryExpression.getRightExpression());
                }
            }
            return new AndExpression(equalsTo, expression);
        }
    }

    /**
     * <p>
     * ????????????????????????<br>
     * tableName.tenantId ??? tableAlias.tenantId
     * </p>
     *
     * @param table ?????????
     * @return ??????
     */
    protected Column getAliasColumn(Table table) {
        StringBuilder column = new StringBuilder();
        if (null == table.getAlias()) {
            column.append(table.getName());
        } else {
            column.append(table.getAlias().getName());
        }
        column.append(".");
        column.append(this.tenantHandler.getTenantIdColumn());
        return new Column(column.toString());
    }

    public TenantHandler getTenantHandler() {
        return tenantHandler;
    }

    public void setTenantHandler(TenantHandler tenantHandler) {
        this.tenantHandler = tenantHandler;
    }
}
