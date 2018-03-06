package node;

import org.junit.Test;

import java.io.IOException;

public class PathMapperTest {

    @Test
    public void test() throws IOException {
        PathMapper path = new PathMapper();
        path.getListOfDirectories("C:/git");
    }
}