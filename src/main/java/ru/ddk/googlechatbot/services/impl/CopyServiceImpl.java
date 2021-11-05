package ru.ddk.googlechatbot.services.impl;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ddk.googlechatbot.services.CopyService;

import java.util.Random;

@Service
public class CopyServiceImpl implements CopyService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CopyServiceImpl.class.getName());

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
}
