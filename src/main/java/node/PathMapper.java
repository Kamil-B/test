package node;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PathMapper {

    public void getListOfDirectories(String path) throws IOException {
        List<Path> paths = Files.list(Paths.get(path)).map(Path::getFileName).collect(Collectors.toList());

    }



}
