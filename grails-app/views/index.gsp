<!DOCTYPE html>
<html>
	<head>
		<title>Resting Actor Webservice</title>
	</head>
	<body>
    <h1 id="header">Resting Actor Webservice</h1>
    <p id="introduction">
        The Resting Actor Webservice is a client service that returns the query results from the
        <a href="http://www.imdb.com/">IMDB database</a>.  It is a REST-based application that returns
        results in a JSON format.  At this time, it supports actor name searches via its API.  It does not
        support POST, PUT, or DELETE REST methods because the IMDB service is owned externally.
    </p>
    <h2>Help</h2>
    <p id="help">
        The help document (a hyperlinked copy of the README) is available using the base URL or /help.  For example:<br/><br/>

        http://127.0.0.1:8080 <br/>
        http://127.0.0.1:8080/help
    </p>
    <h2>Construction</h2>
    <p id="construction">
        The Resting Actor Webservice was constructed using the <a href="http://www.grails.org">Grails framework</a>, version 2.3.7.
        Besides the base Grails project setup, the <a href="https://github.com/grails-plugins/grails-rest-client-builder">grails-rest-client-builder plugin</a>
        and the <a href="http://groovy.codehaus.org/modules/http-builder/home.html">Groovy http-builder</a> were used. The application was tested using
        the <a href="https://code.google.com/p/spock/">Spock</a> testing framework.
    </p>
    <h2>Installation</h2>
    <p id="installation">
        The Resting Actor Webservice WAR can placed in a JAVA container, such as <a href="http://tomcat.apache.org/">Tomcat</a>.  It has been tested
        using Tomcat version 7.0.50. The application can also be used locally by installing Grails and then using the command run-app.
    </p>
    <h2>Allowed Commands</h2>
    <p id="allowedCommands1">
        The Resting Actor Webservice API only supports the GET method and this method requires a supplied actor's name in the calling URL.  These calls are
        used with the /api URL component. For example:<br/><br/>

        curl --include -X GET "http://127.0.0.1:8080/restingActor/api/Tom+Wallace"<br/><br/>

        This will return a HTTP Status Code 200 and a JSON object of IMDB search results.  If an actor's name is not supplied, for example:<br/><br/>

        curl --include -X GET "http://127.0.0.1:8080/restingActor/api"<br/><br/>

        then the application will return a HTTP Status Code 400 for missing the required parameter.
    </p>
    <p id="allowedCommand2">
        The Resting Actor Webservice does not support the POST, PUT, or DELETE REST methods in its API because the IMDB database does not allow this
        functionality. Using these methods will result in a HTTP Status Code 501 because they are not implemented.  They also return a JSON object
        with a description of the error.
    </p>
    <h2>Testing</h2>
    <p id="testing">
        The application includes unit tests with the Spock framework.  These can be accessed by installing
        Grails and using the grails test-app unit: command.  The <a href="http://grails.org/plugin/codenarc">Grails codenarc plugin</a>
        code analysis tool was also used and can be accessed by grails codenarc.
    </p>

	</body>
</html>
