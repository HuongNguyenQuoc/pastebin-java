package com.example.pastebin.paste.shortener;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ShortenerService {
    private static final String BASE62_ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String base62Encode(BigInteger num) {
        if (num.equals(BigInteger.ZERO)) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        BigInteger base = BigInteger.valueOf(62);
        BigInteger n = num;
        while (n.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divRem = n.divideAndRemainder(base);
            sb.insert(0, BASE62_ALPHABET.charAt(divRem[1].intValue()));
            n = divRem[0];
        }
        return sb.toString();
    }

    public String generateShortLink() {
        try {
            String raw = System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextDouble(); // threadlocalrandom just get the random value between 0.0 and 1.0
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digestBytes = md5.digest(raw.getBytes());

            BigInteger number = new BigInteger(1, digestBytes); // 1 in biginterger class just infor is positive number

            String encoded = base62Encode(number);
            return encoded.substring(0, Math.min(encoded.length(), 7)); // return the first 7 characters of the encoded string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
