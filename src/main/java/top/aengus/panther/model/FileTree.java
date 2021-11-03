package top.aengus.panther.model;

import lombok.Data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class FileTree {

    private File currFile;

    private List<File> files;

    private List<FileTree> directories;

    public FileTree(File currFile) {
        this.currFile = currFile;
    }

    public void append(File file) {
        if (files == null) {
            files = new LinkedList<>();
        }
        this.files.add(file);
    }

    public void append(FileTree directory) {
        if (directories == null) {
            directories = new LinkedList<>();
        }
        this.directories.add(directory);
    }

    public void forEachFile(Consumer<? super File> consumer) {
        if (files != null) {
            files.forEach(consumer);
        }
    }

    public void forEachDir(Consumer<? super FileTree> consumer) {
        if (directories != null) {
            directories.forEach(consumer);
        }
    }
}
