package com.purplesq.purplesq;

import android.content.Context;
import android.provider.Settings.Secure;

import com.squareup.okhttp.Request;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okio.Buffer;

/**
 * Created by nishant on 27/07/15.
 */
public class Utils {

    public static String getDeviceHash(Context context, String email) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return md5(androidId + email);

    }

    private static final String md5(final String stringToEncode) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(stringToEncode.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                while (hex.length() < 2) {
                    hex = "0" + hex;
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static boolean isValidEmailAddress(String emailAddress) {
        if (emailAddress.contains("@")) {
            String emailLocalPart = emailAddress.substring(emailAddress.indexOf("@"));
            if (emailLocalPart.length() > 64) {
                return false;
            }
        }

        if (emailAddress.length() > 254) {
            return false;
        }

        String expression = "[A-Za-z0-9][\\.\\w]*+@([\\w]+\\.)+[A-Za-z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static boolean isNumeric(String phone) {
        String str = phone.replaceAll("/+", "");

        String expression = "^[0-9][0-9]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }
}
