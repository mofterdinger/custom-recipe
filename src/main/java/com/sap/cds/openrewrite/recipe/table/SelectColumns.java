package com.sap.cds.openrewrite.recipe.table;

import org.openrewrite.Column;
import org.openrewrite.DataTable;
import org.openrewrite.Recipe;

public class SelectColumns extends DataTable<SelectColumns.Row> {

	public SelectColumns(Recipe recipe) {
		super(recipe, "Select Columns Table", "The table.");
	}

	public record Row(
			@Column(displayName = "Column String Literal", description = "The String literal passed to columns().") String columnName) {
	}

}
