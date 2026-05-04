package com.liuxuanhui.aicodehelper.exam.utils;

import com.liuxuanhui.aicodehelper.exam.entity.Answer;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Objects;

public class SaltEncryption {

    public static String saltEncryption(String password, String salt) {
        String current = password + salt;
        return DigestUtils.md5DigestAsHex(current.getBytes());
    }

    public static int getIndex(List<Answer> list, Integer questionId) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getQuestionId(), questionId)) {
                return i;
            }
        }
        return -1;
    }
}
