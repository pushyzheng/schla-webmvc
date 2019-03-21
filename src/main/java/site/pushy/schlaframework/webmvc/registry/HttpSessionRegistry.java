package site.pushy.schlaframework.webmvc.registry;

import site.pushy.schlaframework.webmvc.pojo.HttpSession;
import site.pushy.schlaframework.webmvc.util.UUIDUtil;

import java.util.HashMap;

/**
 * @author Pushy
 * @since 2019/3/17 19:59
 */
public class HttpSessionRegistry {

    private static final HashMap<String, HttpSession> sessionMap = new HashMap<>();

    /**
     * Create a session object and put it to sessionMap
     * @return the id of session
     */
    public static HttpSession addSession() {
        String sessionId = UUIDUtil.getString();
        synchronized (sessionMap) {
            HttpSession session = new HttpSession(sessionId);
            sessionMap.put(sessionId, session);
            return session;
        }
    }

    /**
     * Get whether the session in sessionMap
     * @param sessionId the id of session
     */
    public static boolean containsSession(String sessionId){
        synchronized (sessionMap) {
            return sessionMap.containsKey(sessionId);
        }
    }

    public static HttpSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

}
