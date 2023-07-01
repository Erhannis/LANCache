/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.lancache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;
import jnr.ffi.Pointer;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;

/**
 *
 * @author Erhannis
 */
public class LANCacheFS extends FuseStubFS {

    private final Path root;

    public LANCacheFS(Path root) {
        this.root = root;
    }

    @Override
    public int getattr(String path, FileStat stat) {
        //System.out.println("getattr "+path+" : "+stat);
        Path p = Paths.get(root.toString(), path);
        if (!Files.exists(p)) {
            return -ErrorCodes.ENOENT();
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
            stat.st_mode.set(0444 | 0222 | 0111 | (attrs.isDirectory() ? FileStat.S_IFDIR : FileStat.S_IFREG));
            stat.st_nlink.set(attrs.isDirectory() ? (short) 2 : (short) 1);
            stat.st_uid.set(getContext().uid.get());
            stat.st_gid.set(getContext().gid.get());
            stat.st_rdev.set(0);
            stat.st_size.set(attrs.size());
            stat.st_blksize.set(4096);
            stat.st_blocks.set((int) ((attrs.size() + 4095) / 4096));
            stat.st_atim.tv_sec.set(attrs.lastModifiedTime().to(TimeUnit.SECONDS));
            stat.st_mtim.tv_sec.set(attrs.lastModifiedTime().to(TimeUnit.SECONDS));
            stat.st_ctim.tv_sec.set(attrs.creationTime().to(TimeUnit.SECONDS));
        } catch (IOException e) {
            return -ErrorCodes.EIO();
        }
        return 0;
    }

    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filler, @off_t long offset, FuseFileInfo fi) {
        Path p = Paths.get(root.toString(), path);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(p)) {
            for (Path subPath : ds) {
                filler.apply(buf, subPath.getFileName().toString(), null, 0);
            }
        } catch (IOException e) {
            return -ErrorCodes.EIO();
        }
        return 0;
    }

    @Override
    public int read(String path, Pointer buf, @size_t long size, @off_t long offset, FuseFileInfo fi) {
        System.out.println("read " + path + " @" + offset + ":x" + size);
        Path p = Paths.get(root.toString(), path);

        if (!Files.isRegularFile(p)) {
            System.out.println("err isdir");
            return -ErrorCodes.EISDIR();
        }

        try {
            File f = p.toFile();
            if (offset >= f.length()) {
                System.out.println("err eof");
                return 0;
            }
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            long remaining = f.length() - offset;
            int red = (int)Math.min(size, remaining);
            byte[] data = new byte[red];
            raf.seek(offset);
            raf.readFully(data);
            buf.put(0, data, (int) 0, red);
            raf.close();
            System.out.println("ret diff "+red);
            return red;
        } catch (IOException e) {
            System.out.println("err io");
            e.printStackTrace();
            return -ErrorCodes.EIO();
        } catch (Throwable t) {
            System.err.println("ERR ERR");
            t.printStackTrace();
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int open(String path, FuseFileInfo fi) {
        Path p = Paths.get(root.toString(), path);

        if (!Files.isRegularFile(p)) {
            return -ErrorCodes.EISDIR();
        }

        if (!Files.isReadable(p)) {
            return -ErrorCodes.EACCES();
        }

        return 0;
    }

    @Override
    public Pointer init(Pointer conn) {
        System.out.println("Filesystem mounted");
        return conn;
    }

    @Override
    public void destroy(Pointer initResult) {
        System.out.println("Filesystem unmounting");
    }
}
