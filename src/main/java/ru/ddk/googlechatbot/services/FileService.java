package ru.ddk.googlechatbot.services;

public interface FileService {
    boolean copyFile(String sourcePath, String targetPath);
    String getFileInfo(String pathFile);
}
