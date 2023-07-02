/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 *
 * @author Erhannis
 */
public class SimpleFileAttributes implements BasicFileAttributes {
    public FileTime lastModifiedTime;
    public FileTime lastAccessTime;
    public FileTime creationTime;
    public boolean isRegularFile;
    public boolean isDirectory;
    public boolean isSymbolicLink;
    public boolean isOther;
    public long size;
    public Object fileKey;

    @Override
    public FileTime lastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public FileTime lastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public FileTime creationTime() {
        return creationTime;
    }

    @Override
    public boolean isRegularFile() {
        return isRegularFile;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    @Override
    public boolean isOther() {
        return isOther;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public Object fileKey() {
        return fileKey;
    }
}
