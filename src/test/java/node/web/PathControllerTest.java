package node.web;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.extern.slf4j.Slf4j;
import node.utils.PathUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Mockito.when;
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
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        String toCreate = fs.getPath("").toAbsolutePath().toString().replaceAll("\\\\", "/") + "/test.txt";

        this.mockMvc.perform(MockMvcRequestBuilders.get("/path/create")
                .param("path", toCreate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.path").value(toCreate))
                .andExpect(jsonPath("$.action").value("create"))
                .andExpect(jsonPath("$.result").value("true"))
                .andExpect(jsonPath("$.reason").value("null"));
    }

    @Test
    public void givenPathURI_whenDelete_thenVerifyResponse() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path file = Files.createFile(fs.getPath("deleteTest.txt"));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/path/delete")
                .param("path", file.toAbsolutePath().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.path").value(file.toAbsolutePath().toString()))
                .andExpect(jsonPath("$.action").value("delete"))
                .andExpect(jsonPath("$.result").value("true"))
                .andExpect(jsonPath("$.reason").value("null"));
    }
}
