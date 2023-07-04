/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erhannis
 */
public class Utils {
    private static final int BUF = 1024*256; // Did a few tests, this one was best by a small margin
    
    public static String calculateSHA256(Path file) throws IOException {
        try {
            MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
            try (InputStream is = Files.newInputStream(file)) {
                try (DigestInputStream dis = new DigestInputStream(is, sha256Digest)) {
                    byte[] buffer = new byte[BUF];
                    while (dis.read(buffer, 0, buffer.length) != -1); //empty loop to clear the data
                    sha256Digest = dis.getMessageDigest();
                }
            }
            
            // Convert the byte to hex format
            StringBuilder result = new StringBuilder();
            for (byte b : sha256Digest.digest()) {
                result.append(String.format("%02x", b));
            }
            
            return result.toString();
        }   catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
