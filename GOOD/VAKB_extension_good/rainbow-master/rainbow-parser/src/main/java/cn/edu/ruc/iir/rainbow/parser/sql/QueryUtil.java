/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.ruc.iir.rainbow.parser.sql;

import cn.edu.ruc.iir.rainbow.parser.sql.tree.AliasedRelation;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.AllColumns;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.CoalesceExpression;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.ComparisonExpression;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.ComparisonExpressionType;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Expression;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.FunctionCall;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.GroupBy;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Identifier;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.LogicalBinaryExpression;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.OrderBy;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.QualifiedName;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Query;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.QueryBody;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.QuerySpecification;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Relation;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Row;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.SearchedCaseExpression;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Select;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.SelectItem;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.SingleColumn;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.SortItem;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.StringLiteral;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Table;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.TableSubquery;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.Values;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.WhenClause;
import cn.edu.ruc.iir.rainbow.parser.sql.tree.*;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public final class QueryUtil
{
    private QueryUtil() {}

    public static Identifier identifier(String name)
    {
        return new Identifier(name);
    }

    public static Identifier quotedIdentifier(String name)
    {
        return new Identifier(name, true);
    }

    public static SelectItem unaliasedName(String name)
    {
        return new SingleColumn(identifier(name));
    }

    public static SelectItem aliasedName(String name, String alias)
    {
        return new SingleColumn(identifier(name), identifier(alias));
    }

    public static Select selectList(Expression... expressions)
    {
        return selectList(asList(expressions));
    }

    public static Select selectList(List<Expression> expressions)
    {
        ImmutableList.Builder<SelectItem> items = ImmutableList.builder();
        for (Expression expression : expressions) {
            items.add(new SingleColumn(expression));
        }
        return new Select(false, items.build());
    }

    public static Select selectList(SelectItem... items)
    {
        return new Select(false, ImmutableList.copyOf(items));
    }

    public static Select selectAll(List<SelectItem> items)
    {
        return new Select(false, items);
    }

    public static Table table(QualifiedName name)
    {
        return new Table(name);
    }

    public static Relation subquery(Query query)
    {
        return new TableSubquery(query);
    }

    public static SortItem ascending(String name)
    {
        return new SortItem(identifier(name), SortItem.Ordering.ASCENDING, SortItem.NullOrdering.UNDEFINED);
    }

    public static Expression logicalAnd(Expression left, Expression right)
    {
        return new LogicalBinaryExpression(LogicalBinaryExpression.Type.AND, left, right);
    }

    public static Expression equal(Expression left, Expression right)
    {
        return new ComparisonExpression(ComparisonExpressionType.EQUAL, left, right);
    }

    public static Expression caseWhen(Expression operand, Expression result)
    {
        return new SearchedCaseExpression(ImmutableList.of(new WhenClause(operand, result)), Optional.empty());
    }

    public static Expression functionCall(String name, Expression... arguments)
    {
        return new FunctionCall(QualifiedName.of(name), ImmutableList.copyOf(arguments));
    }

    public static Values values(Row... row)
    {
        return new Values(ImmutableList.copyOf(row));
    }

    public static Row row(Expression... values)
    {
        return new Row(ImmutableList.copyOf(values));
    }

    public static Relation aliased(Relation relation, String alias, List<String> columnAliases)
    {
        return new AliasedRelation(
                relation,
                identifier(alias),
                columnAliases.stream()
                        .map(QueryUtil::identifier)
                        .collect(Collectors.toList()));
    }

    public static SelectItem aliasedNullToEmpty(String column, String alias)
    {
        return new SingleColumn(new CoalesceExpression(identifier(column), new StringLiteral("")), identifier(alias));
    }

    public static OrderBy ordering(SortItem... items)
    {
        return new OrderBy(ImmutableList.copyOf(items));
    }

    public static Query simpleQuery(Select select)
    {
        return query(new QuerySpecification(
                select,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()));
    }

    public static Query simpleQuery(Select select, Relation from)
    {
        return simpleQuery(select, from, Optional.empty(), Optional.empty());
    }

    public static Query simpleQuery(Select select, Relation from, OrderBy orderBy)
    {
        return simpleQuery(select, from, Optional.empty(), Optional.of(orderBy));
    }

    public static Query simpleQuery(Select select, Relation from, Expression where)
    {
        return simpleQuery(select, from, Optional.of(where), Optional.empty());
    }

    public static Query simpleQuery(Select select, Relation from, Expression where, OrderBy orderBy)
    {
        return simpleQuery(select, from, Optional.of(where), Optional.of(orderBy));
    }

    public static Query simpleQuery(Select select, Relation from, Optional<Expression> where, Optional<OrderBy> orderBy)
    {
        return simpleQuery(select, from, where, Optional.empty(), Optional.empty(), orderBy, Optional.empty());
    }

    public static Query simpleQuery(Select select, Relation from, Optional<Expression> where, Optional<GroupBy> groupBy, Optional<Expression> having, Optional<OrderBy> orderBy, Optional<String> limit)
    {
        return query(new QuerySpecification(
                select,
                Optional.of(from),
                where,
                groupBy,
                having,
                orderBy,
                limit));
    }

    public static Query singleValueQuery(String columnName, String value)
    {
        Relation values = values(row(new StringLiteral((value))));
        return simpleQuery(
                selectList(new AllColumns()),
                aliased(values, "t", ImmutableList.of(columnName)));
    }

    public static Query singleValueQuery(String columnName, boolean value)
    {
        Relation values = values(row(value ? BooleanLiteral.TRUE_LITERAL : BooleanLiteral.FALSE_LITERAL));
        return simpleQuery(
                selectList(new AllColumns()),
                aliased(values, "t", ImmutableList.of(columnName)));
    }

    public static Query query(QueryBody body)
    {
        return new Query(
                Optional.empty(),
                body,
                Optional.empty(),
                Optional.empty());
    }
}
