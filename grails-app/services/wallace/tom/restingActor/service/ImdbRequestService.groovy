package wallace.tom.restingActor.service

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.URIBuilder
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import wallace.tom.restingActor.Constants

class ImdbRequestService {

    static transactional = false // No database operations at all.

    static final String IMDB_BASE_URL = 'http://www.imdb.com/xml/find'

    static final String ACCEPTABLE_DESCRIPTION_ACTOR = 'Actor,'
    static final String ACCEPTABLE_DESCRIPTION_ACTRESS = 'Actress,'

    /**
     * Queries IMDB's (undocumented) API with a actor's name as the query parameter.  Collects the results
     * in JSON format.
     *
     * @param actorName
     * @param builder
     * @return JSON
     * @throws IllegalArgumentException through validation method
     */
    JSONElement collectQueryResponse(String actorName, RestBuilder builder = new RestBuilder()) {
        log.info("Making request to IMDB database for actor name [$actorName]")
        validateActorName(actorName)

        String url = buildQueryUrl(actorName)

        RestResponse response = builder.get(url) {
            // IMDB requires the User-Agent header to be set in order to render the request
            header(HttpHeaders.USER_AGENT, 'Mozilla/5.0 Ubuntu/8.10 Firefox/27.0.1')
            accept(Constants.JSON_MIME_TYPE)
        }

        String limitedResultsAsString = limitJsonToActors(response.json.toString())

        return JSON.parse(limitedResultsAsString)
    }

    /**
     * Performs validation on the passed actorName, which cannot be null or an empty string.
     *
     * Protected method to allow for tests, rather than private
     *
     * @param actorName
     * @throws IllegalArgumentException
     */
    static protected void validateActorName(String actorName) {
        if (!actorName) {
            throw new IllegalArgumentException('Actor Name is a required parameter and cannot be null.')
        }
        if (actorName == '') {
            throw new IllegalArgumentException('Actor Name is a required parameter and cannot be an empty string.')
        }
    }

    /**
     * Builds the URL used in the IMDB query
     * Example final URL is:  http://www.imdb.com/xml/find?json=1&nr=1&nm=on&q=jennifer+garner
     *
     * Protected method to allow for tests, rather than private
     *
     * @param actorName
     * @return
     */
    static protected String buildQueryUrl(String actorName) {
        URIBuilder uri = new URIBuilder(IMDB_BASE_URL)
        Map queryParams = [
                json: '1',
                nr  : '1',
                nm  : 'on',
                q   : actorName
        ]

        // Assign our query params
        uri.query = queryParams
        return uri.toString()
    }

    /**
     * Limits a JSON to only those entries that have the string 'Actor,' or 'Actress,' in the description.
     * Otherwise the results contain a variety of other entries from the IMDB database.
     * This is done on string representations of the JSON object to aid testing.
     *
     * Protected method to allow for tests, rather than private
     *
     * @param imdbQueryResponseAsString
     * @param limited query results as string
     */
    static protected String limitJsonToActors(String imdbQueryResponseAsString) {
        JsonSlurper slurper = new JsonSlurper()
        def result = slurper.parseText(imdbQueryResponseAsString)

        // Iterate over various initial name-based keys from IMDB
        ['name_approx', 'name_exact'].each { initialKey ->
            result[initialKey]?.removeAll {
                !it.description.contains(ACCEPTABLE_DESCRIPTION_ACTOR) && !it.description.contains(ACCEPTABLE_DESCRIPTION_ACTRESS)
            }
        }

        // Convert the object representation from Maps and Lists to proper JSON
        return JsonOutput.toJson(result)
    }

}
