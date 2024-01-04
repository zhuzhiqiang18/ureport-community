/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.bstek.ureport.build.assertor.Assertor;
import com.bstek.ureport.build.assertor.EqualsAssertor;
import com.bstek.ureport.build.assertor.EqualsGreatThenAssertor;
import com.bstek.ureport.build.assertor.EqualsLessThenAssertor;
import com.bstek.ureport.build.assertor.GreatThenAssertor;
import com.bstek.ureport.build.assertor.InAssertor;
import com.bstek.ureport.build.assertor.LessThenAssertor;
import com.bstek.ureport.build.assertor.LikeAssertor;
import com.bstek.ureport.build.assertor.NotEqualsAssertor;
import com.bstek.ureport.build.assertor.NotInAssertor;
import com.bstek.ureport.dsl.ReportParserLexer;
import com.bstek.ureport.dsl.ReportParserParser;
import com.bstek.ureport.exception.ReportParseException;
import com.bstek.ureport.expression.function.AvgFunction;
import com.bstek.ureport.expression.function.ColumnFunction;
import com.bstek.ureport.expression.function.CountFunction;
import com.bstek.ureport.expression.function.FormatDateFunction;
import com.bstek.ureport.expression.function.FormatNumberFunction;
import com.bstek.ureport.expression.function.Function;
import com.bstek.ureport.expression.function.GetFunction;
import com.bstek.ureport.expression.function.JsonFunction;
import com.bstek.ureport.expression.function.ListFunction;
import com.bstek.ureport.expression.function.MaxFunction;
import com.bstek.ureport.expression.function.MinFunction;
import com.bstek.ureport.expression.function.OrderFunction;
import com.bstek.ureport.expression.function.ParameterFunction;
import com.bstek.ureport.expression.function.ParameterIsEmptyFunction;
import com.bstek.ureport.expression.function.RangeFunction;
import com.bstek.ureport.expression.function.RowFunction;
import com.bstek.ureport.expression.function.SumFunction;
import com.bstek.ureport.expression.function.date.DateAddFunction;
import com.bstek.ureport.expression.function.date.DateFunction;
import com.bstek.ureport.expression.function.date.DayFunction;
import com.bstek.ureport.expression.function.date.MonthFunction;
import com.bstek.ureport.expression.function.date.ToDayFunction;
import com.bstek.ureport.expression.function.date.WeekFunction;
import com.bstek.ureport.expression.function.date.YearFunction;
import com.bstek.ureport.expression.function.math.AbsFunction;
import com.bstek.ureport.expression.function.math.CeilFunction;
import com.bstek.ureport.expression.function.math.ChnFunction;
import com.bstek.ureport.expression.function.math.ChnMoneyFunction;
import com.bstek.ureport.expression.function.math.CosFunction;
import com.bstek.ureport.expression.function.math.ExpFunction;
import com.bstek.ureport.expression.function.math.FloorFunction;
import com.bstek.ureport.expression.function.math.Log10Function;
import com.bstek.ureport.expression.function.math.LogFunction;
import com.bstek.ureport.expression.function.math.MedianFunction;
import com.bstek.ureport.expression.function.math.ModeFunction;
import com.bstek.ureport.expression.function.math.PowFunction;
import com.bstek.ureport.expression.function.math.RandomFunction;
import com.bstek.ureport.expression.function.math.RoundFunction;
import com.bstek.ureport.expression.function.math.SinFunction;
import com.bstek.ureport.expression.function.math.SqrtFunction;
import com.bstek.ureport.expression.function.math.StdevpFunction;
import com.bstek.ureport.expression.function.math.TanFunction;
import com.bstek.ureport.expression.function.math.VaraFunction;
import com.bstek.ureport.expression.function.page.PageAvgFunction;
import com.bstek.ureport.expression.function.page.PageCountFunction;
import com.bstek.ureport.expression.function.page.PageMaxFunction;
import com.bstek.ureport.expression.function.page.PageMinFunction;
import com.bstek.ureport.expression.function.page.PageNumberFunction;
import com.bstek.ureport.expression.function.page.PageRowsFunction;
import com.bstek.ureport.expression.function.page.PageSumFunction;
import com.bstek.ureport.expression.function.page.PageTotalFunction;
import com.bstek.ureport.expression.function.string.IndexOfFunction;
import com.bstek.ureport.expression.function.string.LengthFunction;
import com.bstek.ureport.expression.function.string.LowerFunction;
import com.bstek.ureport.expression.function.string.ReplaceFunction;
import com.bstek.ureport.expression.function.string.SubstringFunction;
import com.bstek.ureport.expression.function.string.TrimFunction;
import com.bstek.ureport.expression.function.string.UpperFunction;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.Op;
import com.bstek.ureport.expression.parse.ExpressionErrorListener;
import com.bstek.ureport.expression.parse.ExpressionVisitor;
import com.bstek.ureport.expression.parse.builder.BooleanExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.CellObjectExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.CellPositionExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.CurrentCellDataExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.CurrentCellValueExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.DatasetExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.ExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.FunctionExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.IntegerExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.NullExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.NumberExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.RelativeCellExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.SetExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.StringExpressionBuilder;
import com.bstek.ureport.expression.parse.builder.VariableExpressionBuilder;

/**
 * @author Jacky.gao
 * @since 2016年12月24日
 */
public class ExpressionUtils {

	public static final String EXPR_PREFIX = "${";

	public static final String EXPR_SUFFIX = "}";

	private static ExpressionVisitor exprVisitor;

	private static Map<String, Function> functions = new HashMap<String, Function>();

	private static Map<Op, Assertor> assertorsMap = new HashMap<Op, Assertor>();

	private static List<ExpressionBuilder> expressionBuilders = new ArrayList<ExpressionBuilder>();

	private static List<String> cellNameList = new ArrayList<String>();

	private static String[] LETTERS = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
			"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	static {
		expressionBuilders.add(new StringExpressionBuilder());
		expressionBuilders.add(new VariableExpressionBuilder());
		expressionBuilders.add(new BooleanExpressionBuilder());
		expressionBuilders.add(new IntegerExpressionBuilder());
		expressionBuilders.add(new DatasetExpressionBuilder());
		expressionBuilders.add(new FunctionExpressionBuilder());
		expressionBuilders.add(new NumberExpressionBuilder());
		expressionBuilders.add(new CellPositionExpressionBuilder());
		expressionBuilders.add(new RelativeCellExpressionBuilder());
		expressionBuilders.add(new SetExpressionBuilder());
		expressionBuilders.add(new CellObjectExpressionBuilder());
		expressionBuilders.add(new NullExpressionBuilder());
		expressionBuilders.add(new CurrentCellValueExpressionBuilder());
		expressionBuilders.add(new CurrentCellDataExpressionBuilder());

		assertorsMap.put(Op.Equals, new EqualsAssertor());
		assertorsMap.put(Op.EqualsGreatThen, new EqualsGreatThenAssertor());
		assertorsMap.put(Op.EqualsLessThen, new EqualsLessThenAssertor());
		assertorsMap.put(Op.GreatThen, new GreatThenAssertor());
		assertorsMap.put(Op.LessThen, new LessThenAssertor());
		assertorsMap.put(Op.NotEquals, new NotEqualsAssertor());
		assertorsMap.put(Op.In, new InAssertor());
		assertorsMap.put(Op.NotIn, new NotInAssertor());
		assertorsMap.put(Op.Like, new LikeAssertor());

		for (int i = 0; i < LETTERS.length; i++) {
			cellNameList.add(LETTERS[i]);
		}

		for (int i = 0; i < LETTERS.length; i++) {
			String name = LETTERS[i];
			for (int j = 0; j < LETTERS.length; j++) {
				cellNameList.add(name + LETTERS[j]);
			}
		}

		// 数值函数
		setFunction(new CountFunction());
		setFunction(new SumFunction());
		setFunction(new MaxFunction());
		setFunction(new MinFunction());
		setFunction(new ListFunction());
		setFunction(new AvgFunction());
		setFunction(new OrderFunction());
		
		// 时间函数
		setFunction(new WeekFunction());
		setFunction(new DayFunction());
		setFunction(new MonthFunction());
		setFunction(new YearFunction());
		setFunction(new DateFunction());
		setFunction(new FormatNumberFunction());
		setFunction(new FormatDateFunction());
		setFunction(new ToDayFunction());
		setFunction(new DateAddFunction());
		
		
		setFunction(new GetFunction());
		setFunction(new AbsFunction());
		setFunction(new CeilFunction());
		setFunction(new ChnFunction());
		setFunction(new ChnMoneyFunction());
		setFunction(new CosFunction());
		setFunction(new ExpFunction());
		setFunction(new FloorFunction());
		setFunction(new Log10Function());
		setFunction(new LogFunction());
		setFunction(new PowFunction());
		setFunction(new RandomFunction());
		setFunction(new RoundFunction());
		setFunction(new SinFunction());
		setFunction(new SqrtFunction());
		setFunction(new TanFunction());
		setFunction(new StdevpFunction());
		setFunction(new VaraFunction());
		setFunction(new ModeFunction());
		setFunction(new MedianFunction());

		// 字符串函数
		setFunction(new LengthFunction());
		setFunction(new LowerFunction());
		setFunction(new IndexOfFunction());
		setFunction(new ReplaceFunction());
		setFunction(new SubstringFunction());
		setFunction(new TrimFunction());
		setFunction(new UpperFunction());

		setFunction(new PageTotalFunction());
		setFunction(new PageNumberFunction());
		setFunction(new PageAvgFunction());
		setFunction(new PageCountFunction());
		setFunction(new PageMaxFunction());

		setFunction(new PageMinFunction());
		setFunction(new PageRowsFunction());
		setFunction(new PageSumFunction());

		setFunction(new ParameterFunction());
		setFunction(new ParameterIsEmptyFunction());
		setFunction(new JsonFunction());

		setFunction(new RowFunction());
		setFunction(new ColumnFunction());
		
		setFunction(new RangeFunction());
	}

	public static List<String> getCellNameList() {
		return cellNameList;
	}

	public static Function getFunction(String functionName) {
		return functions.get(functionName.toLowerCase());
	}

	public static Map<Op, Assertor> getAssertorsMap() {
		return assertorsMap;
	}

	public static boolean conditionEval(Op op, Object left, Object right) {
		Assertor assertor = assertorsMap.get(op);
		boolean result = assertor.eval(left, right);
		return result;
	}

	public static Expression parseExpression(String text) {
		ANTLRInputStream antlrInputStream = new ANTLRInputStream(text);
		ReportParserLexer lexer = new ReportParserLexer(antlrInputStream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		ReportParserParser parser = new ReportParserParser(tokenStream);
		ExpressionErrorListener errorListener = new ExpressionErrorListener();
		parser.addErrorListener(errorListener);
		exprVisitor = new ExpressionVisitor(expressionBuilders);
		Expression expression = exprVisitor.visitEntry(parser.entry());
		String error = errorListener.getErrorMessage();
		if (error != null) {
			throw new ReportParseException("Expression parse error:" + error);
		}
		return expression;
	}

	public static ExpressionVisitor getExprVisitor() {
		return exprVisitor;
	}

	private static void setFunction(Function function) {
		functions.put(function.name(), function);
	}
}
