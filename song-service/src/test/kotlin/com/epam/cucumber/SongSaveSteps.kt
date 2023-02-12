package com.epam.cucumber

import com.epam.controller.SongController
import com.epam.view.SongIdView
import com.epam.view.SongView
import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SongSaveSteps: SpringCucumberBase(){
    @Autowired
    private lateinit var mapper: ObjectMapper
    private lateinit var songView: SongView
    private var actual: SongIdView? = null

    @Given("song with resource id {int}")
    fun setup(id: Int) {
        songView = SongView(
                resourceId = id
        )
    }

    @When("attempt to save it")
    fun action() {
        actual = executePost(mapper.writeValueAsString(songView), "/api/songs",SongIdView::class.java)
    }

    @Then("it should return the id of the saved song")
    fun validation() {
        Assertions.assertTrue(actual?.id!! > 0)
    }
}