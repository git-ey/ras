package com.ey.plugin.interceptor;

import com.ey.util.Const;
import com.ey.util.Jurisdiction;
import com.ey.util.sql.SqlUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qingsheng.chen@hand-china.com
 */
@SuppressWarnings("ConstantConditions")
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class SqlParserInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(SqlParserInterceptor.class);
    private static final String COLUMN_FUND_ID = "FUND_ID";
    private static ThreadLocal<List<Column>> handleColumns = new ThreadLocal<>();
    private static ThreadLocal<AtomicInteger> tableIndex = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            if (Jurisdiction.getSession() != null
                    && Jurisdiction.getSession().getAttribute(Const.SESSION_USERNAME) != null
                    && StringUtils.hasText(Jurisdiction.getUsername())) {
                BoundSql boundSql = SqlUtils.getBoundSql(invocation);
                if (boundSql == null) {
                    logger.error("[Error] No SQL found.");
                } else {
                    Statement statement = CCJSqlParserUtil.parse(boundSql.getSql());
                    statement = handleStatement(statement, getArgs(invocation, statement));
                    return SqlUtils.resetSql(invocation, boundSql, statement.toString()).proceed();
                }
            }
        } catch (Exception e) {
            logger.error("[Error] An error occurred while parsing sql.", e);
        } finally {
            handleColumns.remove();
            tableIndex.remove();
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // unnecessary
    }

    private Map getArgs(Invocation invocation, Statement statement) {
        Object[] args = invocation.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof MapperMethod.ParamMap && statement instanceof Select) {
                    return (Map) arg;
                }
                if (arg != null && !(arg instanceof MappedStatement) && statement instanceof Update) {
                    return BeanMap.create(arg);
                }
                if (arg != null && !(arg instanceof MappedStatement) && statement instanceof Insert) {
                    return BeanMap.create(arg);
                }
            }
        }
        return Collections.emptyMap();
    }

    private Statement handleStatement(Statement statement, Map args) throws JSQLParserException {
        // 查询
        if (statement instanceof Select) {
            statement = handleSelect((Select) statement, args);
        }
        return statement;
    }

    private Select handleSelect(Select select, Map args) throws JSQLParserException {
        select.setSelectBody(handleSelectBody(select.getSelectBody(), args));
        return select;
    }

    private SelectBody handleSelectBody(SelectBody selectBody, Map args) throws JSQLParserException {
        // 普通SQL
        if (selectBody instanceof PlainSelect) {
            selectBody = handlePlainSelect((PlainSelect) selectBody, args);
        } else /* 复杂Sql(UNION,INTERSECT,MINUS,EXCEPT) */ if (selectBody instanceof SetOperationList) {
            selectBody = handleSetOperationList((SetOperationList) selectBody, args);
        }
        return selectBody;
    }

    private SetOperationList handleSetOperationList(SetOperationList setOperationList, Map args) throws JSQLParserException {
        List<SelectBody> selects = setOperationList.getSelects();
        if (!CollectionUtils.isEmpty(selects)) {
            for (int i = 0; i < selects.size(); ++i) {
                selects.set(i, handleSelectBody(selects.get(i), args));
            }
        }
        return setOperationList;
    }

    private PlainSelect handlePlainSelect(PlainSelect plainSelect, Map args) throws JSQLParserException {
        // 拦截列
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (!CollectionUtils.isEmpty(selectItems)) {
            for (int i = 0; i < selectItems.size(); ++i) {
                SelectItem selectItem = selectItems.get(i);
                if (selectItem instanceof SelectExpressionItem && ((SelectExpressionItem) selectItem).getExpression() instanceof SubSelect) {
                    SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                    selectExpressionItem.setExpression(handleSubSelect((SubSelect) selectExpressionItem.getExpression(), args));
                } else if (selectItem instanceof SelectExpressionItem && ((SelectExpressionItem) selectItem).getExpression() instanceof Column) {
                    SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                    selectExpressionItem.setExpression(handleSubSelect((Column) selectExpressionItem.getExpression(), args));
                } else {
                    selectItem = handleSelectItem(selectItem, args);
                }
                selectItems.set(i, selectItem);
            }
        }
        // 拦截FROM
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            FromItem afterHandlerFromItem = handleTable((Table) fromItem, args);
            plainSelect.setFromItem(afterHandlerFromItem);
        } else if (fromItem instanceof SubSelect) {
            handleSubSelect((SubSelect) fromItem, args);
        }
        // 拦截JOIN
        List<Join> joins = plainSelect.getJoins();
        if (!CollectionUtils.isEmpty(joins)) {
            for (Join join : joins) {
                handleJoin(join, args);
            }
        }
        // 拦截WHERE
        Expression where = plainSelect.getWhere();
        if (where != null) {
            handleExpression(where, args);
        }
        plainSelect.setWhere(handleHandledColumns(where));
        return plainSelect;
    }

    private Expression handleHandledColumns(Expression where) throws JSQLParserException {
        List<Column> handleColumnList = handleColumns.get();
        if (!CollectionUtils.isEmpty(handleColumnList)) {
            return createHandledColumnExpression(where, handleColumnList);
        }
        return null;
    }

    private Expression createHandledColumnExpression(Expression where, List<Column> handleColumnList) throws JSQLParserException {
        for (Column handleColumn : handleColumnList) {
            where = createHandledColumnExpression(where, handleColumn);
        }
        return where;
    }

    private Expression createHandledColumnExpression(Expression expression, Column handledColumn) throws JSQLParserException {
        InExpression inExpression = new InExpression(handledColumn, createSubSelect());
        if (expression != null) {
            expression = new AndExpression(expression, inExpression);
        } else {
            expression = inExpression;
        }
        return expression;
    }

    private SubSelect createSubSelect() throws JSQLParserException {
        Select select = (Select) CCJSqlParserUtil.parse(getHandledSql());
        SubSelect subSelect = new SubSelect();
        subSelect.setSelectBody(select.getSelectBody());
        return subSelect;
    }

    private String getHandledSql() {
        String tableAlias = "SUF_" + getTableIndex();
        return "SELECT " + tableAlias + "FUND_ID FROM SYS_USER_FUND " + tableAlias + "  WHERE " + tableAlias + ".USERNAME = '" + Jurisdiction.getUsername() + "'";
    }

    private int getTableIndex() {
        AtomicInteger atomicInteger = tableIndex.get();
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(0);
            tableIndex.set(atomicInteger);
        }
        return atomicInteger.getAndIncrement();
    }


    private SubSelect handleSubSelect(SubSelect subSelect, Map args) throws JSQLParserException {
        subSelect.setSelectBody(handleSelectBody(subSelect.getSelectBody(), args));
        return subSelect;
    }

    private Column handleSubSelect(Column column, Map args) {
        String columnName = column.getColumnName();
        if (columnName != null && columnName.length() >= COLUMN_FUND_ID.length()) {
            String subColumn = columnName.substring(columnName.length() - COLUMN_FUND_ID.length());
            if (COLUMN_FUND_ID.equalsIgnoreCase(subColumn)) {
                List<Column> handleColumnList = handleColumns.get();
                if (handleColumnList == null) {
                    handleColumnList = new ArrayList<>();
                    handleColumns.set(handleColumnList);
                }
                handleColumnList.add(column);
            }
        }
        return column;
    }

    private void handleExpression(Expression expression, Map args) throws JSQLParserException {
        if (expression instanceof AndExpression) {
            handleExpression(((AndExpression) expression).getLeftExpression(), args);
            handleExpression(((AndExpression) expression).getRightExpression(), args);
        }
        if (expression instanceof OrExpression) {
            handleExpression(((OrExpression) expression).getLeftExpression(), args);
            handleExpression(((OrExpression) expression).getRightExpression(), args);
        }
        if (expression instanceof ExistsExpression) {
            handleExpression(((ExistsExpression) expression).getRightExpression(), args);
        }
        if (expression instanceof SubSelect) {
            handleSubSelect((SubSelect) expression, args);
        }
        if (expression instanceof Parenthesis) {
            handleExpression(((Parenthesis) expression).getExpression(), args);
        }
    }

    private void handleJoin(Join join, Map args) throws JSQLParserException {
        FromItem joinFromItem = join.getRightItem();
        if (joinFromItem instanceof Table) {
            join.setRightItem(handleTable((Table) joinFromItem, args));
        } else if (joinFromItem instanceof SubSelect) {
            handleSubSelect((SubSelect) joinFromItem, args);
        }
    }

    private SelectItem handleSelectItem(SelectItem selectItem, Map args) {
        logger.error("[Error] Unresolved SelectItem : {}", selectItem.getClass());
        return selectItem;
    }

    private FromItem handleTable(Table table, Map args) {
        return table;
    }
}
