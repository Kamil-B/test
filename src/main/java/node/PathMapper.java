package node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PathMapper {

    public void getListOfDirectories(String path) throws IOException {
        Files.list(Paths.get(path)).forEach(p -> System.out.println(p.toString()));
    }

}
