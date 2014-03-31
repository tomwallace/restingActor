package wallace.tom.restingActor.controller

import grails.test.mixin.TestFor
import wallace.tom.restingActor.Constants
import wallace.tom.restingActor.service.ImdbRequestService

@TestFor(ApiController)
class ApiControllerSpecification extends spock.lang.Specification {

    ImdbRequestService imdbRequestService = Mock(ImdbRequestService)

    def setup() {
        controller.imdbRequestService = imdbRequestService
    }

    def "Test get"() {
        given:

        def actorName = 'Tom'

        when:

        controller.get(actorName)

        then:

        1 * imdbRequestService.collectQueryResponse(actorName)

        response.contentType.contains(Constants.JSON_MIME_TYPE)

    }

    def "Test post"() {
        given:

        def actorName = 'Tom'

        when:

        controller.post(actorName)

        then:

        response.contentType.contains(Constants.JSON_MIME_TYPE)
        response.status == Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED
        response.json.responseDetails == controller.POST_NOT_SUPPORTED_DESCRIPTION
        response.json.responseStatus == Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED
    }

    def "Test put"() {
        given:

        def actorName = 'Tom'

        when:

        controller.put(actorName)

        then:

        response.contentType.contains(Constants.JSON_MIME_TYPE)
        response.status == Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED
        response.json.responseDetails == controller.PUT_NOT_SUPPORTED_DESCRIPTION
        response.json.responseStatus == Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED
    }

    def "Test delete"() {
        given:

        def actorName = 'Tom'

        when:

        controller.delete(actorName)

        then:

        response.contentType.contains(Constants.JSON_MIME_TYPE)
        response.status == Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED
        response.json.responseDetails == controller.DELETE_NOT_SUPPORTED_DESCRIPTION
        response.json.responseStatus == Constants.HTTP_RESPONSE_CODE_NOT_IMPLEMENTED
    }

    def "Test handleIllegalArgumentException"() {
        when:

        controller.handleIllegalArgumentException(new IllegalArgumentException())

        then:

        response.contentType.contains(Constants.JSON_MIME_TYPE)
        response.status == Constants.HTTP_RESPONSE_CODE_BAD_REQUEST
        response.json.responseDetails == controller.MISSING_REQUIRED_INPUT
        response.json.responseStatus == Constants.HTTP_RESPONSE_CODE_BAD_REQUEST
    }
}
