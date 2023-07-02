/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache.metadata;

/**
 *
 * @author Erhannis
 */
public abstract class CNode {
    /**
     * The hash of a file, or a random directory id
     */
    public String id; //LEAK //THINK Should this be byte[] or something?
    public String filename;
}
