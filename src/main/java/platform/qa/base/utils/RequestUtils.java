package platform.qa.base.utils;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substringBetween;

import platform.qa.base.exceptions.RequestFilterException;
import platform.qa.entities.context.Request;

import java.util.List;
import java.util.Map;

public class RequestUtils {

    public static Request getLastRequest(List<Request> context, String path) {
        return context.stream()
                .filter(request -> request.getName().equals(path))
                .max(Request::compareTo)
                .orElseThrow(() -> new RequestFilterException("Немає запитів зі шляхом:" + path));
    }

    public static boolean hasCurlyBracketsInQueryParameters(Map<String, ?> parametersWithIds) {
        return parametersWithIds.entrySet().stream()
                .anyMatch(entry -> isNotEmpty(substringBetween(String.valueOf(entry.getValue()), "{", "}")));
    }
}
