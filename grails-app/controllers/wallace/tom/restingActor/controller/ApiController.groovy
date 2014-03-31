package wallace.tom.restingActor.controller

import wallace.tom.restingActor.Constants
import wallace.tom.restingActor.service.ImdbRequestService

class ApiController {

    ImdbRequestService imdbRequestService

    static final String POST_NOT_SUPPORTED_DESCRIPTION = 'POST request is for an unimplemented REST method'
    static final String PUT_NOT_SUPPORTED_DESCRIPTION = 'PUT request is for an unimplemented REST method'
    static final String DELETE_NOT_SUPPORTED_DESCRIPTION = 'DELETE request is for an unimplemented REST method'
    static final String MISSING_REQUIRED_INPUT = 'Missing or invalid required parameter actor name'

    // Grails provides functionality to limit controller actions to HTTP request method (http://grails.org/doc/2.3.x/ref/Controllers/allowedMethods.html).
    // Returns a 405 (Method Not Allowed) code if an incorrect HTTP method is used.
    // By locking the controller actions here, we provide validation for REST requests. This is already covered by the
    // URL mappings, but reinforced here in case they are bypassed.
    static allowedMethods = [
            get   : Constants.HTTP_GET,
            post  : Constants.HTTP_POST,
            put   : Constants.HTTP_PUT,
            delete: Constants.HTTP_DELETE
    ]

    def get(String actorName) {
        log.info("GET request to api with actor name [$actorName]")
        render(contentType: Constants.JSON_MIME_TYPE) {
            imdbRequestService.collectQueryResponse(actorName)
        }
        return [:]
    }

    // Not implemented
    def post(String actorName) {
        log.info("POST request to api with actor name [$actorName]")

        renderProblem(POST_NOT_SUPPORTED_DESCRIPTION, Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED)
        return [:]
    }

    // Not implemented
    def put(String actorName) {
        log.info("PUT request to api with actor name [$actorName]")

        renderProblem(PUT_NOT_SUPPORTED_DESCRIPTION, Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED)
        return [:]
    }

    // Not implemented
    def delete(String actorName) {
        log.info("DELETE request to api with actor name [$actorName]")

        renderProblem(DELETE_NOT_SUPPORTED_DESCRIPTION, Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED)
        return [:]
    }

    // Controller default handler for IllegalArgumentException
    @SuppressWarnings('UnusedMethodParameter')      // Needed for the controller to map the IllegalArgumentException
    def handleIllegalArgumentException(IllegalArgumentException e) {
        log.info("Missing or invalid required parameter actor name")

        renderProblem(MISSING_REQUIRED_INPUT, Constants.HTTP_RESPONSE_CODE_BAD_REQUEST)
        return [:]
    }

    private renderProblem(String description, Integer status) {
        log.info(description)
        render(status: status, contentType: Constants.JSON_MIME_TYPE) {
            responseDetails = description
            responseStatus = status
        }
    }
}
