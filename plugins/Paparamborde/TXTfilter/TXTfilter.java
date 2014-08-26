package Paparamborde.TXTfilter;

import java.io.FilenameFilter;
import java.io.File;

public class TXTfilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".txt"));
    }
}
