# Generic Webhook Trigger Plugin

[![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/generic-webhook-trigger-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/generic-webhook-trigger-plugin/)

This is a Jenkins plugin that can:
 1. Receive any *HTTP POST* request, *http://JENKINS_URL/jenkins/generic-webhook-trigger/invoke*
 2. Extract values with [JSONPath](https://github.com/jayway/JsonPath) or [XPath](https://www.w3schools.com/xml/xpath_syntax.asp)
 3. Contribute those values as variables to the build

This means it can trigger on any webhook, like:
* [Bitbucket Cloud](https://confluence.atlassian.com/bitbucket/manage-webhooks-735643732.html)
* [Bitbucket Server](https://marketplace.atlassian.com/plugins/com.nerdwin15.stash-stash-webhook-jenkins/server/overview)
* [GitHub](https://developer.github.com/webhooks/)
* [GitLab](https://docs.gitlab.com/ce/user/project/integrations/webhooks.html)
* [Gogs](https://gogs.io/docs/features/webhook)
* [Assembla](https://blog.assembla.com/AssemblaBlog/tabid/12618/bid/107614/Assembla-Bigplans-Integration-How-To.aspx)
* An many many more!

The original use case was to build merge/pull requests. This is easily done with a shell script build step.

```
git clone $PULL_REQUEST_TO_HTTP_CLONE_URL  
cd *  
git reset --hard $PULL_REQUEST_TO_HASH  
git status  
git remote add from $PULL_REQUEST_FROM_HTTP_CLONE_URL  
git fetch from
git merge $PULL_REQUEST_FROM_HASH  
git --no-pager log --max-count=10 --graph --abbrev-commit

#compile command here ...
```

You may want to report back to the invoking system. [HTTP Request Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTTP+Request+Plugin) is a very convinient plugin for that. 

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Generic+Webhook+Trigger+Plugin).

# Screenshots

![Generic trigger](https://github.com/tomasbjerre/generic-webhook-trigger-plugin/blob/master/sandbox/generic-trigger.png)
![Using variables in shell](https://github.com/tomasbjerre/generic-webhook-trigger-plugin/blob/master/sandbox/build-shell.png)


## Job DSL Plugin

This plugin can be used with the Job DSL Plugin.

```
job('example genericTrigger') {
 triggers {
  genericTrigger {
   genericVariables {
    genericVariable {
     key("variable")
     value("JSONPath or XPath expression")
     expressionType("JSONPath")
    }
   }
  }
 }
}
```

## Pipeline Plugin

This plugin can be used with the Pipeline Plugin:

```
properties([
 gitLabConnection('gitlab'),
 pipelineTriggers([
  [$class: 'GenericTrigger',
   genericVariables: [
    [expressionType: 'JSONPath', key: 'variable1', value: 'expression1'],
    [expressionType: 'JSONPath', key: 'variable2', value: 'expression2']
   ]
  ]
 ])
])

```

# Plugin development
More details on Jenkins plugin development is available [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

There is a ```/build.sh``` that will perform a full build and test the plugin. You may have a look at sandbox/settings.xml on how to configure your Maven settings.

A release is created like this. You need to clone from jenkinsci-repo, with https and have username/password in settings.xml.
```
mvn release:prepare release:perform
```
