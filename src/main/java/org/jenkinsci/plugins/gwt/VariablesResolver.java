package org.jenkinsci.plugins.gwt;

import static com.google.common.collect.Maps.newHashMap;
import static org.jenkinsci.plugins.gwt.ExpressionType.JSONPath;
import static org.jenkinsci.plugins.gwt.ExpressionType.XPath;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;

public class VariablesResolver {

  private List<GenericVariable> genericVariables = Lists.newArrayList();
  private final String postContent;

  public VariablesResolver(String postContent, List<GenericVariable> genericVariables) {
    this.postContent = postContent;
    this.genericVariables = genericVariables;
  }

  public Map<String, String> getVariables() {
    if (genericVariables == null) {
      return newHashMap();
    }
    Map<String, String> map = newHashMap();
    for (GenericVariable gv : genericVariables) {
      Object resolved = resolve(gv);

      if (resolved instanceof List) {
        int i = 0;
        for (Object o : (List<?>) resolved) {
            map.put(gv.getKey() + "_" + i, o.toString());
            i++;
        }
      } else {
        map.put(gv.getKey(), resolved.toString());
      }
    }
    return map;
  }

  private Object resolve(GenericVariable gv) {
    try {
      if (gv != null && gv.getValue() != null && !gv.getValue().isEmpty()) {
        if (gv.getExpressionType() == JSONPath) {
          return JsonPath.read(postContent, gv.getValue());
        } else if (gv.getExpressionType() == XPath) {
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          InputSource inputSource =
              new InputSource(new ByteArrayInputStream(postContent.getBytes()));
          Document doc = builder.parse(inputSource);
          XPathFactory xPathfactory = XPathFactory.newInstance();
          XPath xpath = xPathfactory.newXPath();
          XPathExpression expr = xpath.compile(gv.getValue());
          return expr.evaluate(doc);
        } else {
          throw new IllegalStateException("Not recognizing " + gv.getExpressionType());
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(
          "Unable to resolve " + gv.getExpressionType() + " in:\n" + postContent, e);
    }
    return "";
  }
}
