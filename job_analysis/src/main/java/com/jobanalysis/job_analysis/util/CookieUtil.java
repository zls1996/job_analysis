package com.jobanalysis.job_analysis.util;

import com.jobanalysis.job_analysis.entity.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

/**
 * Created By 朱立松 on 2019/1/17
 * Http请求中的Cookie操作工具
 */
public class CookieUtil {

    /**
     * 根据用户id来生成cookie
     * @param response
     * @return
     */
    public static void generateCookieForUser(String username, HttpServletResponse response) {

        //生成新的记录用户的cookie
        Cookie cookie = new Cookie("sid", username);
        //添加Cookie
        response.addCookie(cookie);
    }


    /**
     * 随机生成n位字母加数字组合
     * @param length
     * @return
     */
    private static String generateStr(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
        // 输出字母还是数字
        String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;

    }


}
