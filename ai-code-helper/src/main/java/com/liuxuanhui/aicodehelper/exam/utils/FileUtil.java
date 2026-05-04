package com.liuxuanhui.aicodehelper.exam.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Component
public class FileUtil {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8888/files}")
    private String urlPrefix;

    private static final Set<String> ALLOWED_EXT = Set.of(".pdf", ".png", ".jpg");

    public String uploadToLocal(MultipartFile file) {
        String originName = file.getOriginalFilename();
        if (originName == null || originName.isBlank()) {
            return "文件名不能为空";
        }

        int dotIndex = originName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "文件缺少扩展名";
        }
        String ext = originName.substring(dotIndex).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            return "上传的文件类型错误，只能上传 pdf、jpg、png";
        }

        // 按日期分目录：uploads/png/20231009/xxx.png
        String fileType = ext.substring(1); // 去掉点
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 磁盘保存路径（相对于项目根目录）
        Path saveDir = Paths.get(uploadPath, fileType, dateDir).toAbsolutePath();
        File folder = saveDir.toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File dest = new File(folder, originName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return "上传失败：" + e.getMessage();
        }

        // 返回可访问的 URL：http://localhost:8888/files/png/20231009/xxx.png
        return urlPrefix + "/" + fileType + "/" + dateDir + "/" + originName;
    }
}
