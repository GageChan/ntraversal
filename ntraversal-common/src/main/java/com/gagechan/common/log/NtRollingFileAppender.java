package com.gagechan.common.log;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;

/**
 * @author GageChan
 */
public class NtRollingFileAppender extends RollingFileAppender {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public void rollOver() {
        File target;
        File file;

        long nextRollover = 0;
        if (qw != null) {
            long size = ((CountingQuietWriter)qw).getCount();
            nextRollover = size + maxFileSize;
        }
        LogLog.debug("maxBackupIndex=" + maxBackupIndex);

        boolean renameSucceeded = true;
        if (maxBackupIndex > 0) {
            // 所有文件名序号加1
            for (int i = maxBackupIndex - 1; i >= 1 && renameSucceeded; i--) {
                file = new File(genFileName(fileName, i));
                if (file.exists()) {
                    target = new File(genFileName(fileName, i + 1));
                    renameSucceeded = file.renameTo(target);
                }
            }

            if (renameSucceeded) {
                target = new File(genFileName(fileName, 1));
                this.closeFile();
                file = new File(fileName);
                renameSucceeded = file.renameTo(target);
                if (!renameSucceeded) {
                    try {
                        this.setFile(fileName, true, bufferedIO, bufferSize);
                    } catch (IOException e) {
                        if (e instanceof InterruptedIOException) {
                            Thread.currentThread().interrupt();
                        }
                        LogLog.error("setFile(" + fileName + ", true) call failed.", e);
                    }
                }
            }
        }
        if (renameSucceeded) {
            try {
                this.setFile(fileName, false, bufferedIO, bufferSize);
                nextRollover = 0;
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("setFile(" + fileName + ", false) call failed.", e);
            }
        }
    }

    private String genFileName(String name, int index) {
        String val = "_" + df.format(new Date()) + "_";
        String fileName = "";
        if (index > 0) {
            String num = index < maxBackupIndex ? "0" + index : String.valueOf(index);
            fileName = name.replace(".log", "") + val + num + ".log";
        } else {
            fileName = name;
        }
        return fileName;
    }

    public void setFile(String file) {
        String val = file.trim();
        String prefix = val.substring(0, val.lastIndexOf(".log"));
        String suffix = val.substring(val.lastIndexOf(".log"));
        fileName = prefix + "."+ df.format(new Date()) + suffix;
    }
}