/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import com.erhannis.lancache.metadata.FileDb;
import com.erhannis.lancache.metadata.CDir;
import com.erhannis.lancache.metadata.CFile;
import com.erhannis.lancache.metadata.CNode;
import com.erhannis.lancache.metadata.MemoryFileDb;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Erhannis
 */
public class FileMapper {

    private final Path cacheDir;
    private final MemoryFileDb db; //DUMMY Abstract di

    public FileMapper(Path dataDir) {
        this.cacheDir = dataDir.resolve("cache");
        MemoryFileDb mfdb = new MemoryFileDb();
        
        //DUMMY Load with dummy data or something
        
        CFile f;
        
        CDir root = new CDir();
        root.id = "/";
        root.filename = "/";
        mfdb.addOrphanNode(root);
        
        // 1679415240.vetkar_anyu34_0.png b53b0f17f7c97f8fb9d6b91613075b79a33fd4f849a636a766a4ae8de2020de1
        f = new CFile();
        f.id = "b53b0f17f7c97f8fb9d6b91613075b79a33fd4f849a636a766a4ae8de2020de1";
        f.filename = "1679415240.vetkar_anyu34_0.png";
        mfdb.addNode(root, f);
        
        this.db = mfdb;
    }
    
    /**
     * Looks up the id of the CNode stored at the given virtual path.
     * @param virtual
     * @return 
     */
    public String virtualPathToId(String virtualPath) {
        /*
        Heck, wait, I've found a disjunction.
        Do I...have to parse the path in order to get a node?
        Bleh.
        */
        // On both windows and linux, the path shows up split by /
        List<String> parts = new ArrayList<>(Arrays.asList(virtualPath.split("/")));
        CNode n = (CNode)db.getRoot();
        partsLoop: while (!parts.isEmpty()) {
            String s = parts.remove(0);
            if (s.isEmpty()) {
                continue;
            }
            for (CNode sn : ((CDir)n).nodes) {
                if (s.equals(sn.filename)) {
                    n = sn;
                    continue partsLoop;
                }
            }
            return null; //DUMMY Sketchy
        }
        return n.id;
    }
    
    /**
     * Get file attributes.  If file doesn't exist, return null.
     * @param virtualPath
     * @return 
     */
    public BasicFileAttributes virtualPathToAttrs(String virtualPath) throws IOException {
        String id = virtualPathToId(virtualPath);
        CNode n = db.getNodeById(id);
        if (n == null) {
            return null;
        }
        if (n instanceof CFile) {
            Path p = fileIdToRealPath(((CFile) n).id);
            BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
            return attrs;
        } else if (n instanceof CDir) {
            SimpleFileAttributes att = new SimpleFileAttributes();
            att.size = 0; //DUMMY ???
            att.isDirectory = true;
            att.lastAccessTime = FileTime.fromMillis(0); //DUMMY
            att.creationTime = FileTime.fromMillis(0); //DUMMY
            att.lastModifiedTime = FileTime.fromMillis(0); //DUMMY
            return att;
        } else {
            throw new RuntimeException("Unhandled file class! "+n.getClass());
        }
    }
    
    public Path fileIdToRealPath(String fileId) {
        return cacheDir.resolve(fileId); //RAINY //LEAK Maybe split into levels like some hashed file systems?
    }
    
    /**
     * Takes a virtual path and returns the path of the real file it's stored as.
     * //DUMMY //THINK This prevents chunking, I think
     * @param virtual
     * @return 
     */
    public Path virtualFilePathToRealFilePath(String virtualPath) {
        if (!isRegularFile(virtualPath)) {
            throw new IllegalArgumentException("Not a file! "+virtualPath);
        }
        String id = virtualPathToId(virtualPath);
        return fileIdToRealPath(id);
    }
    
    
    
    //THINK Kinda weird to export, but saves a lot of boilerplate
    public CNode getNodeById(String virtualPath) {
        String id = virtualPathToId(virtualPath);
        CNode n = db.getNodeById(id);
        return n;
    }
    
    public boolean isDirectory(String virtualPath) {
        String id = virtualPathToId(virtualPath);
        CNode n = db.getNodeById(id);
        return n instanceof CDir;
    }

    public boolean isRegularFile(String virtualPath) {
        String id = virtualPathToId(virtualPath);
        CNode n = db.getNodeById(id);
        return n instanceof CFile;
    }
}
