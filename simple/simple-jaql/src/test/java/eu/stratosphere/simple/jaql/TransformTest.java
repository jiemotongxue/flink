package eu.stratosphere.simple.jaql;

import org.junit.Test;

import eu.stratosphere.sopremo.JsonPath;
import eu.stratosphere.sopremo.Transformation;
import eu.stratosphere.sopremo.ValueAssignment;
import eu.stratosphere.sopremo.JsonPath.Arithmetic.ArithmeticOperator;
import eu.stratosphere.sopremo.operator.Projection;
import eu.stratosphere.sopremo.operator.Source;

public class TransformTest extends ParserTestCase {
	@Test
	public void shouldParseEmptyTransform() {
		assertParseResult(new Projection(new Transformation(), new Source(createJsonArray(1L, 2L, 3L))),
			"[1, 2, 3] -> transform {}");
	}

	@Test
	public void shouldParseSimpleTransform() {
		Source source = new Source(createJsonArray(createObject("a", 1L, "b", 2L), createObject("a", 3L, "b", 4L)));
		Transformation transformation = new Transformation();
		transformation.addMapping(new ValueAssignment("c", new JsonPath.Arithmetic(createPath("$", "a"),
			ArithmeticOperator.MULTIPLY, createPath("$", "b"))));
		assertParseResult(new Projection(transformation, source),
			"[{a: 1, b: 2}, {a: 3, b: 4}] -> transform { c: $.a * $.b }");
	}

	@Test
	public void shouldParseNestedTransform() {
		Source source = new Source(createJsonArray(createObject("a", 1L, "b", 2L), createObject("a", 3L, "b", 4L)));
		Transformation transformation = new Transformation();
		Transformation nestedTransformation = new Transformation("c");
		transformation.addMapping(nestedTransformation);
		nestedTransformation.addMapping(new ValueAssignment("a", createPath("$", "a")));
		nestedTransformation.addMapping(new ValueAssignment("d", createPath("$", "b")));
		assertParseResult(new Projection(transformation, source),
			"[{a: 1, b: 2}, {a: 3, b: 4}] -> transform { c: { $.a, d: $.b} }");
	}

}
