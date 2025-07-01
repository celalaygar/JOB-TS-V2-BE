package com.tracker.job_ts.sprint.util;

import com.tracker.job_ts.Invitation.entity.UserSummary;
import com.tracker.job_ts.auth.entity.User;

public class GenerationCode {


    public static String TASK = "TASK";

    public static String generateProjectCode(String name) {
        String prefix = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        prefix = prefix.length() > 3 ? prefix.substring(0, 3) : prefix;
        String unique = Long.toHexString(System.currentTimeMillis()).toUpperCase();
        return prefix + "-" + unique.substring(unique.length() - 5);
    }
    public static String generateProjectCode(String pre, String name) {
        String prefix = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        prefix = prefix.length() > 3 ? prefix.substring(0, 3) : prefix;
        String unique = Long.toHexString(System.currentTimeMillis()).toUpperCase();
        return pre + "-" + prefix + "-" + unique.substring(unique.length() - 5);
    }
}
