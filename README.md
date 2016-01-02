
A simple java project to syncornise github forks. It use GitHub API v3.

It saves personal data to log file sometimes (for example auth TOKEN), so use it carefully.

# Build, install, run

This project is a maven project, so:

* import to an eclipse workspace (use eclipse mars+)
* build it, so maven download some jar to local repository
* File | export |runnable jar file
  + select the project Main class in the Launch configuration 
  + select a destination to JAR file
  + select `Extract required libraries into generated JAR` (or package)
  + (optional) save ANT script (and configure builder in Project | properties | Builders | New | Ant builders)


After run, there is a JAR file in the target folder. (In windows) open a CMD in the generated JAR folder and type

```
java -jar generated_jar_name.jar your_auth_token
```

# Plans

* GUI
* DB

# Github API documentation

* https://developer.github.com/v3/
* https://developer.github.com/guides/getting-started/

# Similar examples

* http://h3manth.com/new/blog/2013/auto-notify-git-pull/
* http://h3manth.com/new/blog/2013/auto-syncing-a-forked-git-repository-with-the-parent/
