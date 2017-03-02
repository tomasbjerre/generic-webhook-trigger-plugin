package org.jenkinsci.plugins.gwt;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class GenericVariable extends AbstractDescribableImpl<GenericVariable> {

  @Extension public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

  public static class DescriptorImpl extends Descriptor<GenericVariable> {
    @Override
    public String getDisplayName() {
      return "";
    }
  }

  private final ExpressionType expressionType;
  private final String key;
  private final String value;

  @DataBoundConstructor
  public GenericVariable(String key, String value, ExpressionType expressionType) {
    this.key = key;
    this.value = value;
    this.expressionType = expressionType;
  }

  public ExpressionType getExpressionType() {
    return expressionType;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }
}
