package ru.ddk.googlechatbot.services.impl;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ddk.googlechatbot.services.FileService;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

@Service
public class FileServiceImpl implements FileService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FileServiceImpl.class.getName());

    private static final String commandFileSize = "hdfs dfs -du -s -h  hdfs:///";

    @Override
    public boolean copyFile(String sourcePath, String targetPath) {
        try {
            int r = new Random().ints(100, 5000).findFirst().orElse(1000);
            logger.info("copy " + targetPath + " pause " + r);
            Thread.sleep(r);
            return true;
        }catch (Exception e)
        {
            logger.error(e.toString());
            return false;
        }
    }

    @Override
    public String getFileInfo(String pathFile) {
        try {
            Process process = Runtime.getRuntime().exec(String.format("%s%s", commandFileSize, pathFile));
            Scanner result = new Scanner(process.getInputStream());
            return result.toString();

        } catch (Exception e)
        {
            logger.error(e.fillInStackTrace().getMessage());
            return e.getMessage();
        }
    }

}
