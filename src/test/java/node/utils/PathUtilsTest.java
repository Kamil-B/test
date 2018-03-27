package node.utils;

import org.junit.Test;

import java.nio.file.Paths;

public class PathUtilsTest {

    @Test
    public void givenWrongFilePath_whenCreatingFile_throwException(){
        PathUtils.createFile(Paths.get("/file/does/not/exist"));
    }

}
