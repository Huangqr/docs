package com.holderzone.saas.store.trading.center.utils;

import com.holderzone.framework.util.JacksonUtils;
import com.holderzone.saas.store.dto.common.UserInfoDTO;

public final class ThreadLocalCache {

    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void put(String str) {
        THREAD_LOCAL.set(str);
    }

    public static void putEnterpriseGuid(String enterpriseGuid) {
        String json = "{\"enterpriseGuid\":\"" + enterpriseGuid + "\"}";
        THREAD_LOCAL.set(json);
    }

    public static UserInfoDTO get() {
        String result = THREAD_LOCAL.get();
        return JacksonUtils.toObject(UserInfoDTO.class, result);
    }

    public static String getJsonStr() {
        return THREAD_LOCAL.get();
    }

    public static String getEnterpriseGuid() {
        String result = THREAD_LOCAL.get();
        UserInfoDTO userInfoDTO = JacksonUtils.toObject(UserInfoDTO.class, result);
        if (null != userInfoDTO) {
            return userInfoDTO.getEnterpriseGuid();
        }
        return null;
    }

    public static String getUserGuid() {
        String result = THREAD_LOCAL.get();
        UserInfoDTO userInfoDTO = JacksonUtils.toObject(UserInfoDTO.class, result);
        if (null != userInfoDTO) {
            return userInfoDTO.getUserGuid();
        }
        return null;
    }

    public static String getUserName() {
        String result = THREAD_LOCAL.get();
        UserInfoDTO userInfoDTO = JacksonUtils.toObject(UserInfoDTO.class, result);
        if (null != userInfoDTO) {
            return userInfoDTO.getUserName();
        }
        return null;
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
