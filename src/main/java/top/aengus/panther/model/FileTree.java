package top.aengus.panther.model;

import lombok.Data;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
}
