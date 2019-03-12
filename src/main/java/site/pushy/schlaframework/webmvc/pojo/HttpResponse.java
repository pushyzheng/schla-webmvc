package site.pushy.schlaframework.webmvc.pojo;

import site.pushy.schlaframework.webmvc.annotation.Controller;
import site.pushy.schlaframework.webmvc.annotation.RestController;
import site.pushy.schlaframework.webmvc.enums.ContentType;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

/**
 * @author Pushy
 * @since 2019/3/7 17:17
 */
@Data
public class HttpResponse {

    private HttpResponseStatus status;

    private ContentType contentType;

    public HttpResponse() {
        status = HttpResponseStatus.OK;
        contentType = ContentType.PLAIN;
    }

    public HttpResponse(Object controller) {
        status = HttpResponseStatus.OK;
        if (controller != null) {
            processContentType(controller);
        } else {
            contentType = ContentType.PLAIN;
        }
    }

    private void processContentType(Object controller) {
        if (controller.getClass().isAnnotationPresent(Controller.class)) {
            contentType = controller.getClass().getAnnotation(Controller.class).contentType();
        }
        else if (controller.getClass().isAnnotationPresent(RestController.class)) {
            contentType = controller.getClass().getAnnotation(RestController.class).contentType();
        }
        else {
            contentType = ContentType.PLAIN;
        }
    }

}
