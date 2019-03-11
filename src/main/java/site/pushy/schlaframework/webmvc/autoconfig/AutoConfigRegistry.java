package site.pushy.schlaframework.webmvc.autoconfig;

/**
 * @author Pushy
 * @since 2019/3/8 13:31
 */
public class AutoConfigRegistry {

    public static PropertiesConfigReader reader;

    public static void setReader(PropertiesConfigReader reader) {
        AutoConfigRegistry.reader = reader;
    }
}
