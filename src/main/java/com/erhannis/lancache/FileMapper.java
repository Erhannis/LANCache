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

/**
 *
 * @author Erhannis
 */
public class FileMapper {

    private final Path cacheDir;
    private final FileDb db;

    public FileMapper(Path dataDir) {
        this.cacheDir = dataDir.resolve("cache");
        MemoryFileDb mfdb = new MemoryFileDb();
        //DUMMY //NEXT Load with dummy data or something
        this.db = mfdb;
    }
    
    /**
     * Looks up the id of the CNode stored at the given virtual path.
     * @param virtual
     * @return 
     */
    public abstract String virtualPathToId(String virtualPath);
    
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
            asdf;
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
