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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import jdk.internal.joptsimple.internal.Strings;

/**
 *
 * @author Erhannis
 */
public class FileMapper {

    private final Path cacheDir;
    private final MemoryFileDb db; //DUMMY Abstract di

    public FileMapper(Path dataDir) {
        this.cacheDir = dataDir.resolve("cache");
        MemoryFileDb mfdb = new MemoryFileDb(); //THINK Pass in as param?
        this.db = mfdb;
    }
    
    public void addEmptyDirectory(String virtualParentPath, String filename) {
        String parentId = virtualPathToId(virtualParentPath);
        CDir parent = (CDir)db.getNodeById(parentId);
        CDir child = new CDir();
        child.filename = filename;
        //DUMMY Check duplicates
        db.addNode(parent, child);
    }
    
    //THINK `File` instead?  Any number of alternatives?
    /**
     * virtualPath includes the filename.
     * @param externalFilePath
     * @param virtualPath
     * @throws Exception 
     */
    public void addExternalFile(Path externalFilePath, String virtualPath) throws Exception {
        String hash = Utils.calculateSHA256(externalFilePath);
        Path targetCacheFile = cacheDir.resolve(hash);
        String[] vps = virtualPath.split("/");
        String parentId = this.virtualPathToId(vps[vps.length-2]);
        CDir parent = (CDir)db.getNodeById(parentId);
        CFile file = new CFile();
        file.id = hash;
        file.filename = vps[vps.length-1]; //DUMMY //NEXT Is this "/filename" or "filename"?
        if (!Files.exists(targetCacheFile)) {
            Files.copy(externalFilePath, targetCacheFile);
        }
        db.addNode(parent, file);
    }
    
    //CHECK Test
    public void checkConsistency() throws IOException {
        ArrayList<String> fails = new ArrayList<>();
        for (Path f : Files.list(cacheDir).collect(Collectors.toList())) {
            String hash = Utils.calculateSHA256(f);
            String filename = f.getFileName().toString();
            if (!Objects.equals(hash, f.getFileName().toString())) {
                fails.add(hash);
            }
        }
        if (!fails.isEmpty()) {
            String err = "Files failed hash check:\n"+Strings.join(fails, "\n");
            throw new RuntimeException(err);
        }
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
