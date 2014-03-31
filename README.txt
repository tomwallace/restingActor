Resting Actor Webservice

INTRODUCTION

The Resting Actor Webservice is a client service that returns the query results from the IMDB database.  It is a
REST-based application that returns results in a JSON format.  At this time, it supports actor name searches via its API.
It does not support POST, PUT, or DELETE REST methods because the IMDB service is owned externally.


HELP

The help document (a hyperlinked copy of the README) is available using the base URL or /help.  For example:

http://127.0.0.1:8080
http://127.0.0.1:8080/help


CONSTRUCTION

The Resting Actor Webservice was constructed using the Grails framework, version 2.3.7. Besides the base Grails project
setup, the grails-rest-client-builder plugin and the Groovy http-builder were used. The application was tested using
the Spock testing framework.


INSTALLATION

The Resting Actor Webservice WAR can placed in a JAVA container, such as Tomcat.  It has been tested using Tomcat
version 7.0.50. The application can also be used locally by installing Grails and then using the command run-app.


ALLOWED COMMANDS

The Resting Actor Webservice API only supports the GET method and this method requires a supplied actor's name in the
calling URL.  These calls are used with the /api URL component. For example:

curl --include -X GET "http://127.0.0.1:8080/restingActor/api/Tom+Wallace"

This will return a HTTP Status Code 200 and a JSON object of IMDB search results.  If an actor's name is not supplied,
for example:

curl --include -X GET "http://127.0.0.1:8080/restingActor/api"

then the application will return a HTTP Status Code 400 for missing the required parameter.

The Resting Actor Webservice does not support the POST, PUT, or DELETE REST methods in its API because the IMDB database
does not allow this functionality. Using these methods will result in a HTTP Status Code 501 because they are not
implemented.  They also return a JSON object with a description of the error.


TESTING

The application includes unit tests with the Spock framework.  These can be accessed by installing Grails and using the
grails test-app unit: command.  The Grails codenarc plugin code analysis tool was also used and can be accessed by
grails codenarc.
