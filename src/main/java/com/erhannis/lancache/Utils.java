/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 *
 * @author Erhannis
 */
public class Utils {
    public static String calculateSHA256(Path file, int size) throws Exception {
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file)) {
            try (DigestInputStream dis = new DigestInputStream(is, sha256Digest)) {
                if (size != 1) {
                    byte[] buffer = new byte[size];
                    while (dis.read(buffer, 0, buffer.length) != -1) ; //empty loop to clear the data //LEAK //THINK Is this slow?                
                } else {
                    while (dis.read() != -1) ; //empty loop to clear the data //LEAK //THINK Is this slow?
                }
                sha256Digest = dis.getMessageDigest();
            }
        }

        // Convert the byte to hex format
        StringBuilder result = new StringBuilder();
        for (byte b : sha256Digest.digest()) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}
