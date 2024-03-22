package com.sap.cds.openrewrite.recipe;

import com.sap.cds.openrewrite.recipe.table.SelectColumns;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.TypeMatcher;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J.MethodInvocation;
import org.openrewrite.marker.SearchResult;

public class CdsDataNameSearch extends Recipe {

	private final SelectColumns table = new SelectColumns(this);

	@Override
	public String getDisplayName() {
		return "Select with String";
	}

	@Override
	public String getDescription() {
		return "Finds all usages of get / put at com.sap.cds.CdsData.";
	}

	@Override
	public TreeVisitor<?, ExecutionContext> getVisitor() {
		MethodMatcher matcherGet = new MethodMatcher("java.util.Map get(..)", true);
		MethodMatcher matcherSet = new MethodMatcher("java.util.Map put(..)", true);
		TypeMatcher typeMatcher = new TypeMatcher("com.sap.cds.CdsData");

		return new JavaIsoVisitor<>() {
			@Override
			public MethodInvocation visitMethodInvocation(MethodInvocation method, ExecutionContext ctx) {
				@Nullable
				Expression select = method.getSelect();

				if (select != null && typeMatcher.matches(select.getType())
						&& (matcherGet.matches(method) || matcherSet.matches(method))) {
					table.insertRow(ctx, new SelectColumns.Row(method.getArguments().get(0).printTrimmed()));
					return SearchResult.found(method);
				}

				return method;
			}
		};
	}

}
