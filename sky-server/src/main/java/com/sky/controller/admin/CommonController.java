package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@Api(tags = "通用文件上传接口")
@RequestMapping("/admin/common")
public class CommonController {
//
//    @Autowired
//    private AliOssUtil aliOssUtil;
//    @ApiOperation(value = "文件上传")
//    @PostMapping("/upload")
//    public Result<String> upload(MultipartFile file) throws IOException {
//        log.info("文件上传："+file);
//        String originFilename=file.getOriginalFilename();
//        String extension=originFilename.substring(originFilename.lastIndexOf("."));
//        String objectName= UUID.randomUUID().toString()+extension;
//        String image= aliOssUtil.upload(file.getBytes(),objectName);
//        return Result.success(image);
//    }
@Autowired
private AliOssUtil aliOssUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-url}")
    private String accessUrl; // 前端可访问的 URL 前缀

    @ApiOperation("文件上传接口")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf('.'));
        String newFileName = UUID.randomUUID().toString() + extension;
        File dest = new File(uploadDir + File.separator + newFileName);
        try {
            file.transferTo(dest);
            // 返回前端可访问的 URL
            String fileUrl = accessUrl + "/" + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }
}
