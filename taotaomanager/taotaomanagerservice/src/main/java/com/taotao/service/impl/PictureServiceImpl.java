package com.taotao.service.impl;

import com.jcraft.jsch.SftpException;
import com.taotao.common.utils.FtpUtil;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.SFTPUtil;
import com.taotao.service.PictureService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传服务
 * Created by HuHaifan on 2017/4/19.
 */
@Service
public class PictureServiceImpl implements PictureService {

    @Value("${FTP_ADDRESS}")
    private String FTP_ADDRESS;
    @Value("${FTP_PORT}")
    private Integer FTP_PORT;
    @Value("${FTP_USERNAME}")
    private String FTP_USERNAME;
    @Value("${FTP_PASSWORD}")
    private String FTP_PASSWORD;
    @Value("${FTP_BASEPATH}")
    private String FTP_BASEPATH;
    @Value("${IMAGE_BASE_URL}")
    private String IMAGE_BASE_URL;

    @Override
    public Map uploadPicture(MultipartFile uploadFile)  {
        Map resultMap = new HashMap();
        //生成一个文件名
        //取原始文件名
        String oldName = uploadFile.getOriginalFilename();
        //生成新文件名
        //UUID.randomUUID();
        String newName = IDUtils.genImageName();
        newName += oldName.substring(oldName.lastIndexOf("."));
        //图片上传
        try {
            SFTPUtil sftpUtil = new SFTPUtil(FTP_ADDRESS, FTP_PORT, FTP_USERNAME, FTP_PASSWORD);
            sftpUtil.connect();
            boolean result = sftpUtil.uploadFile(FTP_BASEPATH, newName, uploadFile.getInputStream());
            if (!result) {
                resultMap.put("error", 1);
                resultMap.put("message", "文件上传失败");
                return resultMap;
            }
        } catch (SftpException e) {
            resultMap.put("error", 1);
            resultMap.put("message", "文件上传失败,发生异常");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultMap.put("error", 0);
        resultMap.put("url", IMAGE_BASE_URL + "/" + newName);
        return resultMap;
    }
}
