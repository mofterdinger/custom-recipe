package com.sap.cds.openrewrite.recipe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.java.Assertions.java;

import com.sap.cds.openrewrite.recipe.table.SelectColumns;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class CdsDataNameSearchTest implements RewriteTest {

	@Override
	public void defaults(RecipeSpec spec) {
		spec.recipe(new CdsDataNameSearch()).parser(JavaParser.fromJavaVersion().classpath("cds4j-api"));
	}

	@Test
	void testGet() {
		rewriteRun(spec -> spec.dataTable(SelectColumns.Row.class, (rows) -> {
//			assertThat(rows).containsExactly(new SelectColumns.Row("MyElement"));
		}), java("""
					import com.sap.cds.CdsData;

					class Test {

						void test() {
							CdsData.create().get("MyElement");
						}

					}
				""", """
					import com.sap.cds.CdsData;

					class Test {

						void test() {
							/*~~>*/CdsData.create().get("MyElement");
						}

					}
				"""));
	}

	@Test
	void testPut() {
		rewriteRun(spec -> spec.dataTable(SelectColumns.Row.class, (rows) -> {
//			assertThat(rows).containsExactly(new SelectColumns.Row("MyElement"));
		}), java("""
					import com.sap.cds.CdsData;

					class Test {

						void test() {
							CdsData.create().put("MyElement", null);
						}

					}
				""", """
					import com.sap.cds.CdsData;

					class Test {

						void test() {
							/*~~>*/CdsData.create().put("MyElement", null);
						}

					}
				"""));
	}

	@Test
	void testMapPut() {
		rewriteRun(java("""
					import java.util.HashMap;

					class Test {

						void test() {
							new HashMap<String, Object>().put("MyElement", null);
						}

					}
				"""));
	}

}
