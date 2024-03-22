package com.sap.cds.openrewrite.recipe;

import com.google.errorprone.refaster.annotation.AfterTemplate;
import com.google.errorprone.refaster.annotation.BeforeTemplate;
import com.sap.cds.CdsData;

import org.openrewrite.java.template.RecipeDescriptor;

public class CdsDataNameRewrite {

	@RecipeDescriptor(name = "RewriteGetToGetPath", description = "Rewrites CdsData.get() with CdsData.getPath()")
	public static class RewriteGetToGetPath {

		@BeforeTemplate
		public Object getBefore(CdsData data, String name) {
			return data.get(name);
		}

		@AfterTemplate
		public Object getPathAfter(CdsData data, String path) {
			return data.getPath(path);
		}
	}

	@RecipeDescriptor(name = "RewritePutToPutPath", description = "Rewrites CdsData.put() with CdsData.putPath()")
	public static class RewritePutToPutPath {

		@BeforeTemplate
		public Object putBefore(CdsData data, String name, Object value) {
			return data.put(name, value);
		}

		@AfterTemplate
		public Object putPathAfter(CdsData data, String path, Object value) {
			return data.putPath(path, value);
		}

	}
}
