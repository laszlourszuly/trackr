package com.echsylon.komoot.picture

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.echsylon.atlantis.Atlantis
import com.echsylon.komoot.picture.FlickrClientTestHelper.Companion.prepareAtlantisError
import com.echsylon.komoot.picture.FlickrClientTestHelper.Companion.prepareAtlantisSuccess
import org.junit.After
import org.junit.Test

/**
 * Unit tests for the FlickrClient class.
 */
class FlickrClientTest {
    companion object {
        var atlantis: Atlantis? = null
    }

    @After
    fun afterTest() {
        atlantis?.stop()
    }

    @Test
    fun when_server_returns_success__non_empty_result_is_returned() {
        atlantis = prepareAtlantisSuccess("{ 'photos': { 'photo': [ {'id': '0', 'owner': '', 'unused': 'z'} ] } }")
        atlantis?.start()

        val flickrClient = FlickrClient("http://localhost:8080", "test_api_key")
        val pictures = flickrClient.getPictures(1.0, 2.0, 0.2f)
        assertThat(pictures).isNotEmpty()
    }

    @Test
    fun when_server_returns_success__washed_data_is_correct() {
        atlantis = prepareAtlantisSuccess("{ 'photos': { 'photo': [ {'id': '1', 'owner': 'A', 'unused': 'y'} ] } }")
        atlantis?.start()

        val flickrClient = FlickrClient("http://localhost:8080", "test_api_key")
        val pictures = flickrClient.getPictures(3.0, 4.0, 0.2f)

        assertThat(pictures[0].id).isEqualTo("1")
        assertThat(pictures[0].owner).isEqualTo("A")
        assertThat(pictures[0].latitude).isEqualTo(0.0)
        assertThat(pictures[0].longitude).isEqualTo(0.0)
    }

    @Test
    fun when_server_returns_error__empty_list_is_returned() {
        atlantis = prepareAtlantisError(404, "Not found")
        atlantis?.start()

        val flickrClient = FlickrClient("http://localhost:8080", "test_api_key")
        val pictures = flickrClient.getPictures(1.0, 2.0, 0.2f)
        assertThat(pictures).isEmpty()
    }

}