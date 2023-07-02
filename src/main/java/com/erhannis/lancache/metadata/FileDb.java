/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache.metadata;

import com.erhannis.lancache.metadata.CNode;

/**
 *
 * @author Erhannis
 */
public abstract class FileDb {
    public abstract CNode getNodeById(String id);
}
