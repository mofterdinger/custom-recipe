package com.sap.openrewrite.recipe;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.Literal;
import org.openrewrite.java.tree.J.MethodInvocation;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.marker.SearchResult;

import com.sap.openrewrite.recipe.table.SelectColumns;

public class SelectWithStringSearch extends Recipe {

	private final transient SelectColumns table = new SelectColumns(this);

	@Override
	public String getDisplayName() {
		return "Select with String";
	}

	@Override
	public String getDescription() {
		return "Finds all usages of Select queries with elements identified by plain String.";
	}

	@Override
	public TreeVisitor<?, ExecutionContext> getVisitor() {
		MethodMatcher matcher = new MethodMatcher("com.sap.cds.ql.Select columns(..)");
		return new JavaIsoVisitor<ExecutionContext>() {

			@Override
			public MethodInvocation visitMethodInvocation(MethodInvocation method,
					ExecutionContext p) {
				MethodInvocation m = super.visitMethodInvocation(method, p);
				if (matcher.matches(m)) {
					m = m.withArguments(ListUtils.map(m.getArguments(), a -> {
						if (a instanceof J.Literal && ((J.Literal) a).getType() == JavaType.Primitive.String) {
							table.insertRow(p, new SelectColumns.Row((String) ((J.Literal) a).getValue()));
							return SearchResult.found(a);
						}
						return a;
					}));
				}
				return m;
			}

			@Override
			public Literal visitLiteral(Literal literal, ExecutionContext p) {
				if (literal.getType() == JavaType.Primitive.String && matcher.matches(getCursor().firstEnclosing(J.MethodInvocation.class))) {
					table.insertRow(p, new SelectColumns.Row((String) ((J.Literal) literal).getValue()));
					return SearchResult.found(literal);
				}
				return literal;
			}

		};
	}

}
