package com.echsylon.komoot.picture

import com.echsylon.atlantis.Atlantis
import com.echsylon.atlantis.Configuration
import com.echsylon.atlantis.MockRequest
import com.echsylon.atlantis.MockResponse

/**
 * This class provides convenience methods to compose an Atlantis instance for
 * the given test. Atlantis is used to "mock the Internet" at a socket level.
 */
class FlickrClientTestHelper {

    companion object {
        // The photos search url template
        private const val TEST_URL = "/services/rest/" +
                "\\?method=flickr.photos.search" +
                "\\&api_key=.*" +
                "\\&lat=.*" +
                "\\&lon=.*" +
                "\\&radius=.*" +
                "\\&radius_units=km" +
                "\\&extras=.*" +
                "\\&format=json" +
                "\\&nojsoncallback=1"

        /**
         * Prepares an Atlantis instance that will serve the given JSON string
         * as a successful response to the photos search query. The caller is
         * responsible for starting Atlantis (by calling Atlantis#start()) at
         * his convenience.
         *
         * @param responseJson The response body to serve.
         * @return The prepared Atlantis instance.
         */
        fun prepareAtlantisSuccess(responseJson: String): Atlantis {
            val mockResponse: MockResponse = MockResponse.Builder()
                .setStatus(200, "OK")
                .setBody(responseJson)
                .build()

            val mockRequest: MockRequest = MockRequest.Builder()
                .setMethod("GET")
                .setUrl(TEST_URL)
                .addResponse(mockResponse)
                .build()

            val configuration: Configuration = Configuration.Builder()
                .addRequest(mockRequest)
                .build()

            return Atlantis(configuration)
        }

        /**
         * Prepares an Atlantis instance that will serve an error response to
         * the photos search query. The caller is responsible for starting
         * Atlantis (by calling Atlantis#start()) at his convenience.
         *
         * @param status The HTTP (error) status code.
         * @param message The corresponding message.
         * @return The prepared Atlantis instance.
         */
        fun prepareAtlantisError(status: Int, message: String): Atlantis {
            val mockResponse: MockResponse = MockResponse.Builder()
                .setStatus(status, message)
                .setBody("")
                .build()

            val mockRequest: MockRequest = MockRequest.Builder()
                .setMethod("GET")
                .setUrl(TEST_URL)
                .addResponse(mockResponse)
                .build()

            val configuration: Configuration = Configuration.Builder()
                .addRequest(mockRequest)
                .build()

            return Atlantis(configuration)
        }
    }

}