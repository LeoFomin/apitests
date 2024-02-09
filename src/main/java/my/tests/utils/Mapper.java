package my.tests.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.TestNGException;

import java.io.IOException;
import java.util.List;

public class Mapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> List<T> jsonToListOfObject(String json, Class<T> objectType) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, objectType));
        } catch (IOException e) {
            e.printStackTrace();
            throw new TestNGException("Не удалось привести json к объекту", e);
        }
    }
}
