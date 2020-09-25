package com.leyou.service;


import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    //fast上传
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif", "image/jpg");
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    public String upload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        //检查文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            //log没有配置会记录在哪里
            logger.info("文件不合法" + originalFilename);
            return null;
        }

        //检查文件内容
        try {
            InputStream inputStream = file.getInputStream();
            BufferedImage read = ImageIO.read(inputStream);
            if (read == null) {
                logger.info("文件内容不合法：{}" + originalFilename);
                return null;
            }

//            file.transferTo(new File("F:\\Java\\Code\\SpringBoot\\leyouImages\\" + originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            return "http://image.leyou.com/" +storePath.getFullPath();
        } catch (IOException e) {
            logger.error("内部服务器错误 {}" + originalFilename);
            e.printStackTrace();
        }



        //返回url
        return null;
    }

    public Boolean delete(String url) {
        String realPath = StringUtils.substringAfterLast(url, "http://image.leyou.com/");
        fastFileStorageClient.deleteFile(realPath);
        return true;
    }

}
