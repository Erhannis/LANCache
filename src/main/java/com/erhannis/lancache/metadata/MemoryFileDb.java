/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache.metadata;

import java.util.HashMap;

/**
 *
 * @author Erhannis
 */
public class MemoryFileDb extends FileDb {
    public HashMap<String, CNode> nodes = new HashMap<>();
    
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
        parent.nodes.add(child);
        nodes.put(child.id, child);
    }
}
