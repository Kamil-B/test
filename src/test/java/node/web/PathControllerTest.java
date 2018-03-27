package node.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@WebMvcTest(PathController.class)
public class PathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenPathURI_whenCreate_thenVerifyResponse() throws Exception {
        String toCreate = "src/test/resources/test.txt";

        this.mockMvc.perform(MockMvcRequestBuilders.get("/path/create")
                .param("path", toCreate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.path").value(toCreate))
                .andExpect(jsonPath("$.action").value("create"))
                .andExpect(jsonPath("$.result").value("true"));

        assertThat(Paths.get(toCreate)).exists();
    }

    @Test
    public void givenPathURI_whenDelete_thenVerifyResponse() throws Exception {
        Path file = Files.createFile(Paths.get("src/test/resources/test.txt"));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/path/delete")
                .param("path", file.toAbsolutePath().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.path").value(file.toAbsolutePath().toString()))
                .andExpect(jsonPath("$.action").value("delete"))
                .andExpect(jsonPath("$.result").value("true"));

        assertThat(file).doesNotExist();
    }

    @Test
    public void givenPathURI_whenUnsupportedAction_thenGetException() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/path/update")
                .param("path", "src/test/resources/test.txt"))
        .andExpect(status().isInternalServerError());
    }

    @After
    public void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get("src/test/resources/test.txt"));
    }
}
