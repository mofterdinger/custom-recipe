package com.sap.cds.openrewrite.recipe;

import static org.openrewrite.java.Assertions.java;

import com.sap.cds.openrewrite.recipe.table.SelectColumns;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

class CdsDataNameRewriteTest implements RewriteTest {

	@Override
	public void defaults(RecipeSpec spec) {
		spec.recipe(new CdsDataNameRewriteRecipes()).parser(JavaParser.fromJavaVersion().classpath("cds4j-api"));
	}

	@Test
	void testGet() {
		rewriteRun(java("""
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
					        CdsData.create().getPath("MyElement");
						}

					}
				"""));
	}

	@Test
	void testPut() {
		rewriteRun(java("""
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
					        CdsData.create().putPath("MyElement", null);
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
