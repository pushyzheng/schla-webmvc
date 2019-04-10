package site.pushy.schlaframework.webmvc.enums;

/**
 * @author Pushy
 * @since 2019/3/7 17:44
 */
public enum ContentType {

    JSON("application/json; charset=UTF-8"),
    HTML("text/html; charset=UTF-8"),
    PLAIN("text/plain; charset=UTF-8");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
