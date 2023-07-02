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
    
    //NEXT wait, what abt files in the root dir
    
    @Override
    public CNode getNodeById(String id) {
        return nodes.get(id);
    }
}
