/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import com.erhannis.lancache.metadata.CFile;
import com.erhannis.mathnstuff.utils.Timing;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 *
 * @author Erhannis
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Note that you probably need to run this with -Dfile.encoding=UTF-8 told to java!");
        if (args.length != 2) {
//            System.err.println("Usage: java -Dfile.encoding=UTF-8 -jar LANCache.jar <mountpoint> <root>");
//            System.exit(-1);

            //DUMMY
            //args = new String[]{"/home/erhannis/tmp/mnt", "/home/erhannis/Downloads/funny"};
            args = new String[]{"M:\\", "G:\\lancache_data"};
        }

        try {
            FileMapper fm = new FileMapper(Paths.get(args[1]));
            
            {
                Path cachedir = Path.of("G:\\lancache_data\\cache");
                // Clear cache
                for (Path f : Files.list(cachedir).collect(Collectors.toList())) {
                    Files.delete(f);
                }
                
                fm.addExternalFile(Path.of("D:\\Downloads\\1679415240.vetkar_anyu34_0.png"), "/test.png");
                fm.addEmptyDirectory("/", "subdir");
                fm.addExternalFile(Path.of("D:\\Downloads\\1679415240.vetkar_anyu34_0.png"), "/subdir/test.png");
            }
            
            LANCacheFS lc = new LANCacheFS(fm);
            lc.mount(Paths.get(args[0]), true, true, new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
