package io.github.enzostefanello.campusreserva;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.containers.GenericContainer;

@WebMvcTest
@Import(ValidationProblemDetailTests.ValidationController.class)
class ValidationProblemDetailTests {

    private static final String PATH = "/test/validation";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void webSliceDoesNotLoadDatabaseInfrastructure() {
        assertThat(context.getBeanNamesForType(DataSource.class)).isEmpty();
        assertThat(context.getBeanNamesForType(Flyway.class)).isEmpty();
        assertThat(context.getBeanNamesForType(GenericContainer.class)).isEmpty();
    }

    @Test
    void validBodySucceeds() throws Exception {
        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Campus\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Campus"));
    }

    @Test
    void invalidRequiredFieldReturnsProblemDetail() throws Exception {
        assertProblem(mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\" \"}")), 400);
    }

    @Test
    void malformedJsonReturnsProblemDetail() throws Exception {
        assertProblem(mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":")), 400);
    }

    @Test
    void unsupportedMethodReturnsProblemDetail() throws Exception {
        assertProblem(mockMvc.perform(get(PATH)), 405);
    }

    private void assertProblem(ResultActions response, int expectedStatus) throws Exception {
        response.andExpect(status().is(expectedStatus))
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$").value(anyOf(
                        not(hasKey("type")), hasEntry("type", "about:blank"))))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").isNotEmpty())
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.detail").isString())
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.instance").value(PATH));
    }

    @RestController
    static class ValidationController {

        @PostMapping(PATH)
        ValidationRequest validate(@Valid @RequestBody ValidationRequest request) {
            return request;
        }
    }

    record ValidationRequest(@NotBlank String name) {
    }
}
