/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.base.utils;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.io.FilenameUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Log4j2
public class FileUtils {

    public static final String TEST_RESOURCES_FOLDER = "src/test/resources/";

    public static String readFromFile(String path, String name) {
        try {
            Path pathToFile = Path.of(path, FilenameUtils.getName(name));
            log.info("Read from file: " + pathToFile);
            return Files.readString(pathToFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("File was not found!: ", e);
        }
    }

    @SneakyThrows(IOException.class)
    public static <T> T readFileToObject(String resourcePath, String name, Class<T> clazzValue, ObjectMapper mapper) {
        Path pathToFile = Path.of(resourcePath, FilenameUtils.getName(name));
        return mapper.readValue(pathToFile.toFile(), clazzValue);
    }

    public static <T> List<T> readFilesToObjectList(String resourcePath, Class<T> clazzValue, ObjectMapper mapper) {
        int nameCount = Path.of(resourcePath).getNameCount();
        return IntStream.range(0, nameCount)
                .mapToObj(i -> readFileToObject(resourcePath, Path.of(resourcePath).getName(i).toString(), clazzValue
                        , mapper))
                .collect(Collectors.toList());
    }

    /**
     * @param filePath - path to file
     * @return - name of file extracted from path
     */
    public static String getFileNameFromPath(String filePath) {
        String jsonFileName = filePath;
        if (filePath.contains("/")) {
            jsonFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return jsonFileName;
    }

    /**
     * @param filePath - partial path to file from steps
     * @return - full path to file to src/test/resources+filePath folder
     */
    public static String getFilePath(String filePath) {
        String endPath = "";
        if (filePath.contains("/")) {
            String endPathTmp = filePath.substring(0, filePath.lastIndexOf("/"));
            endPath = endPathTmp.startsWith("/") ? endPathTmp.substring(1) : endPathTmp;
        }
        return TEST_RESOURCES_FOLDER + endPath;
    }
}
