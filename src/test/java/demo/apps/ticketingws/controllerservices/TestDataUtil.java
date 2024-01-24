package demo.apps.ticketingws.controllerservices;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jsa000y
 */
public class TestDataUtil {
    public TestDataUtil() {
    }

    private static InputStream readFromJsonFile(String fileName) throws IOException {
        return TestDataUtil.class.getClassLoader().getResourceAsStream(fileName);
    }

    public static <T> T getTypeFromJson(String jsonFileName, Class<T> type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(readFromJsonFile(jsonFileName), type);
    }
}
