package node.utils;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PathUtilsTest {

    @Test
    public void givenWrongFilePath_whenCreatingFile_returnFalse(){
        assertThat(PathUtils.createFile(Paths.get("/path/does/not/exist"))).isEqualTo(false);
    }

    @Test
    public void givenWrongFilePath_whenDeletingFile_returnFalse(){
        assertThat(PathUtils.deleteFile(Paths.get("/path/does/not/exist"))).isEqualTo(false);
    }

    @Test
    public void givenFile_whenGetSubdirectories_returnEmptyList(){
       assertThat(PathUtils.getSubdirectories(Paths.get("/path/does/not/exist"))).isEqualTo(new ArrayList<>());
    }
}
