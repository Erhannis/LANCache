/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache.metadata;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author Erhannis
 */
public class MemoryFileDb extends FileDb {
    public HashMap<String, CNode> nodes = new HashMap<>();
    
    public MemoryFileDb() {
        this(true);
    }
    
    public MemoryFileDb(boolean addRoot) {
        if (addRoot) {
            CDir root = new CDir();
            root.id = "/";
            root.filename = "/";
            addOrphanNode(root);
        }
    }
    
    //THINK Also createRoot?
    public CNode getRoot() {
        return nodes.get("/"); //SHAME Probably works, but ugh
    }
    
    @Override
    public CNode getNodeById(String id) {
        return nodes.get(id);
    }
    
    /**
     * This should probably only be used for the root dir
     * @param node 
     */
    public void addOrphanNode(CNode node) {
        nodes.put(node.id, node);
    }
    
    public void addNode(CDir parent, CNode child) {
        if (parent.nodes.stream().anyMatch((n) -> Objects.equals(n.id, child.id))) {
            throw new IllegalArgumentException("Duplicate filename!"); //CHECK Permit overwrite?
        }
        parent.nodes.add(child);
        nodes.put(child.id, child);
    }
}
