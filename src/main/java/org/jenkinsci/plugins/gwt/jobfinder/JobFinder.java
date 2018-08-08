package org.jenkinsci.plugins.gwt.jobfinder;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jenkinsci.plugins.gwt.FoundJob;
import org.jenkinsci.plugins.gwt.GenericTrigger;

import com.google.common.annotations.VisibleForTesting;

import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import jenkins.model.ParameterizedJobMixIn.ParameterizedJob;

public final class JobFinder {

  private JobFinder() {}

  private static JobFinderImpersonater jobFinderImpersonater = new JobFinderImpersonater();

  @VisibleForTesting
  static void setJobFinderImpersonater(final JobFinderImpersonater jobFinderImpersonater) {
    JobFinder.jobFinderImpersonater = jobFinderImpersonater;
  }

  public static List<FoundJob> findAllJobsWithTrigger(final String givenToken) {

    final List<FoundJob> found = new ArrayList<>();

    final List<ParameterizedJob> candidateProjects =
        jobFinderImpersonater.getAllParameterizedJobsByImpersonation();
    for (final ParameterizedJob candidateJob : candidateProjects) {
      if (authenticationTokenMatches(candidateJob, givenToken)) {
        final GenericTrigger genericTriggerOpt = findGenericTrigger(candidateJob.getTriggers());
        if (genericTriggerOpt != null) {
          found.add(new FoundJob(candidateJob.getFullName(), genericTriggerOpt));
        }
      }
    }

    return found;
  }

  @SuppressWarnings("deprecation")
  private static boolean authenticationTokenMatches(
      final ParameterizedJob candidateJob, final String givenToken) {
    final hudson.model.BuildAuthorizationToken authToken = candidateJob.getAuthToken();

    final boolean jobHasAuthToken = jobHasAuthToken(authToken);
    final boolean authTokenWasGiven = !isNullOrEmpty(givenToken);
    if (jobHasAuthToken && authTokenWasGiven) {
      final boolean authTokenMatchesQueryToken = authToken.getToken().equals(givenToken);
      if (authTokenMatchesQueryToken) {
        return true;
      } else {
        return false;
      }
    }
    if (!jobHasAuthToken && !authTokenWasGiven) {
      return true;
    }
    return false;
  }

  @SuppressWarnings("deprecation")
  private static boolean jobHasAuthToken(final hudson.model.BuildAuthorizationToken authToken) {
    return authToken != null && !isNullOrEmpty(authToken.getToken());
  }

  private static GenericTrigger findGenericTrigger(
      final Map<TriggerDescriptor, Trigger<?>> triggers) {
    if (triggers == null) {
      return null;
    }
    for (final Trigger<?> candidate : triggers.values()) {
      if (candidate instanceof GenericTrigger) {
        return (GenericTrigger) candidate;
      }
    }
    return null;
  }
}
