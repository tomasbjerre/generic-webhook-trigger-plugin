package org.jenkinsci.plugins.gwt;

import static com.google.common.base.Charsets.UTF_8;
import static hudson.util.HttpResponses.okJSON;
import static java.util.logging.Level.SEVERE;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import hudson.security.csrf.CrumbExclusion;

@Extension
public class GenericWebHookRequestReceiver extends CrumbExclusion implements UnprotectedRootAction {

  private static final String URL_NAME = "generic-webhook-trigger";
  private static final Logger LOGGER =
      Logger.getLogger(GenericWebHookRequestReceiver.class.getName());

  public HttpResponse doInvoke(StaplerRequest request) {
    List<GenericTrigger> triggers = JobFinder.findAllJobsWithTrigger();
    Map<String, String> triggerResults = new HashMap<>();
    try {
      String postContent = IOUtils.toString(request.getInputStream(), UTF_8.name());
      LOGGER.info("Received invocation from " + request.getReferer() + ":\n" + postContent);
      for (GenericTrigger trigger : triggers) {
        try {
          LOGGER.info("Triggering " + trigger);
          trigger.trigger(postContent);
          triggerResults.put(trigger.toString(), "OK");
        } catch (Exception e) {
          LOGGER.log(SEVERE, trigger.toString(), e);
          triggerResults.put(trigger.toString(), ExceptionUtils.getStackTrace(e));
        }
      }
    } catch (IOException e) {
      LOGGER.log(SEVERE, "", e);
    }
    Map<String, Object> response = new HashMap<>();
    response.put("triggerResults", triggerResults);
    return okJSON(response);
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return URL_NAME;
  }

  @Override
  public boolean process(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String pathInfo = request.getPathInfo();
    if (pathInfo != null && pathInfo.startsWith("/" + URL_NAME + "/")) {
      chain.doFilter(request, response);
      return true;
    }
    return false;
  }
}
