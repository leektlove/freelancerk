package com.freelancerk.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MysqlPasswordUtil {

    public static void main(String[] args) {
        log.info("<<< encrypted password: {}", passwordFunc("76811014"));
    }

    public static String passwordFunc(String inpara) {
        byte[] bpara = new byte[inpara.length()];
        byte[] rethash;
        int i;
        for (i=0; i < inpara.length(); i++)
            bpara[i] = (byte)(inpara.charAt(i) & 0xff );
        try {
            MessageDigest sha1er = MessageDigest.getInstance("SHA1");
            rethash = sha1er.digest(bpara); // stage1
            rethash = sha1er.digest(rethash); // stage2
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        StringBuffer r = new StringBuffer(41);
        r.append("*");
        for (i=0; i < rethash.length; i++) {
            String x = Integer.toHexString(rethash[i] & 0xff).toUpperCase();
            if (x.length()<2)
                r.append("0");
            r.append(x);
        }
        return r.toString();
    }
}
