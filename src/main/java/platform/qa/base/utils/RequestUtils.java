package platform.qa.base.utils;

import platform.qa.base.exceptions.RequestFilterException;
import platform.qa.entities.context.Request;

import java.util.List;

public class RequestUtils {

    public static Request getLastRequest(List<Request> context, String path) {
        return context.stream()
                .filter(request -> request.getName().equals(path))
                .max(Request::compareTo)
                .orElseThrow(() -> new RequestFilterException("Немає запитів зі шляхом:" + path));
    }
}
