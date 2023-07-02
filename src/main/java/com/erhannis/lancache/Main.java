/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import com.erhannis.mathnstuff.utils.Timing;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Erhannis
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Timing t = new Timing();
        t.start();
        System.out.println(t.lap()+" hash...");
        String s = Utils.calculateSHA256(new File("D:\\eyes.log").toPath(), 1024*1024);
        System.out.println(t.lap()+" hash: "+s);
        s = Utils.calculateSHA256(new File("D:\\eyes.log").toPath(), 1024*1024);
        System.out.println(t.lap()+" hash: "+s);
        s = Utils.calculateSHA256(new File("D:\\eyes.log").toPath(), 1024*256);
        System.out.println(t.lap()+" hash: "+s);
        s = Utils.calculateSHA256(new File("D:\\eyes.log").toPath(), 1024*64);
        System.out.println(t.lap()+" hash: "+s);
        s = Utils.calculateSHA256(new File("D:\\eyes.log").toPath(), 1024);
        System.out.println(t.lap()+" hash: "+s);
        s = Utils.calculateSHA256(new File("D:\\eyes.log").toPath(), 1);
        System.out.println(t.lap()+" hash: "+s);
        System.out.println(t.lap()+" done");
        
        System.out.println("Note that you probably need to run this with -Dfile.encoding=UTF-8 told to java!");
        if (args.length != 2) {
//            System.err.println("Usage: java -Dfile.encoding=UTF-8 -jar LANCache.jar <mountpoint> <root>");
//            System.exit(-1);

            //DUMMY
            //args = new String[]{"/home/erhannis/tmp/mnt", "/home/erhannis/Downloads/funny"};
            args = new String[]{"M:\\", "D:\\Installations\\Celestia"};
        }

        try {
            LANCacheFS stub = new LANCacheFS(Paths.get(args[1]));
            stub.mount(Paths.get(args[0]), true, false, new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
