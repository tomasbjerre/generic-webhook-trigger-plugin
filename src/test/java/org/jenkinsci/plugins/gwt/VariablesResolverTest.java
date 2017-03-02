package org.jenkinsci.plugins.gwt;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jenkinsci.plugins.gwt.ExpressionType.JSONPath;
import static org.jenkinsci.plugins.gwt.ExpressionType.XPath;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class VariablesResolverTest {

  @Test
  public void testJSONPathGetAllVariable() throws Exception {
    String resourceName = "gital-mergerequest-comment.json";
    String postContent = getContent(resourceName);

    List<GenericVariable> genericVariables = newArrayList( //
        new GenericVariable("ids", "$..id", JSONPath));
    Map<String, String> variables =
        new VariablesResolver(postContent, genericVariables).getVariables();

    assertThat(variables) //
        .containsEntry("ids_0", "28") //
        .containsEntry("ids_1", "1") //
        .containsEntry("ids_2", "1c3e5deb451353c34264b98c77836012a2106515");
  }

  @Test
  public void testJSONPathGetOneVariable() throws Exception {
    String resourceName = "gital-mergerequest-comment.json";
    String postContent = getContent(resourceName);

    List<GenericVariable> genericVariables = newArrayList( //
        new GenericVariable("user_name", "$.user.name", JSONPath));
    Map<String, String> variables =
        new VariablesResolver(postContent, genericVariables).getVariables();

    assertThat(variables) //
        .containsEntry("user_name", "Administrator");
  }

  @Test
  public void testJSONPathGetTwoVariables() throws Exception {
    String resourceName = "gital-mergerequest-comment.json";
    String postContent = getContent(resourceName);

    List<GenericVariable> genericVariables = newArrayList( //
        new GenericVariable("user_name", "$.user.name", JSONPath), //
        new GenericVariable("project_id", "$.project_id", JSONPath));
    Map<String, String> variables =
        new VariablesResolver(postContent, genericVariables).getVariables();

    assertThat(variables) //
        .containsEntry("user_name", "Administrator") //
        .containsEntry("project_id", "1");
  }

  @Test
  public void testXPathGetOneVariable() throws Exception {
    String resourceName = "example.xml";
    String postContent = getContent(resourceName);

    List<GenericVariable> genericVariables = newArrayList( //
        new GenericVariable("book", "/bookstore/book[1]/title", XPath));
    Map<String, String> variables =
        new VariablesResolver(postContent, genericVariables).getVariables();

    assertThat(variables) //
        .containsEntry("book", "Harry Potter");
  }

  @Test
  public void testHPathGetTwoVariable() throws Exception {
    String resourceName = "example.xml";
    String postContent = getContent(resourceName);

    List<GenericVariable> genericVariables = newArrayList( //
        new GenericVariable("book1", "/bookstore/book[1]/title", XPath), //
        new GenericVariable("book2", "/bookstore/book[2]/title", XPath));
    Map<String, String> variables =
        new VariablesResolver(postContent, genericVariables).getVariables();

    assertThat(variables) //
        .containsEntry("book1", "Harry Potter") //
        .containsEntry("book2", "Learning XML");
  }

  private String getContent(String resourceName) {
    try {
      return Resources.toString(Resources.getResource(resourceName).toURI().toURL(),
          Charsets.UTF_8);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
