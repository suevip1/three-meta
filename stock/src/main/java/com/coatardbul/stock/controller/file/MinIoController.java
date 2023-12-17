package com.coatardbul.stock.controller.file;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.model.bo.FileBo;
import com.coatardbul.stock.service.base.MinIoFileService;
import com.coatardbul.stock.service.base.MinioService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * Note:FTP 相关
 * <p>
 * Date: 2022/5/27
 *
 * @author Su Xiaolei
 */
@Slf4j
@RestController
@Api(tags = "")
@RequestMapping("/minIo")
public class MinIoController {


    @Autowired
    MinIoFileService minIoFileService;

    @Autowired
    MinioService minioService;

    @RequestMapping(path = "/getHeadList", method = RequestMethod.POST)
    public CommonResult<List<FileBo>> getHeadList(@RequestBody FileBo dto) throws Exception {
        List<FileBo> pathInfo = minIoFileService.getPathInfo(dto);
        return CommonResult.success(pathInfo);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public CommonResult<String> cosUpload(HttpServletRequest req) throws Exception {
        MultiValueMap<String, MultipartFile> mf = null;
        if (req instanceof MultipartHttpServletRequest) {
            mf = ((MultipartHttpServletRequest) req).getMultiFileMap();
        }
        List<MultipartFile> files = mf.get("file");
        log.info("此次上传文件个数：{}", files.size());
        MultipartFile file = files.get(0);
        String upload = minIoFileService.upload(req.getParameter("bucketName"), req.getParameter("path"), file);
        return CommonResult.success(upload) ;
    }


    @RequestMapping(path = "/mkdir", method = RequestMethod.POST)
    public CommonResult mkdir(@RequestBody FileBo dto) throws Exception {
         minIoFileService.mkdir(dto.getBucketName(),dto.getFilePath());
         return CommonResult.success(null);
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public CommonResult delete(@RequestBody FileBo dto) throws Exception {
         minIoFileService.delete(dto.getBucketName(),dto.getObjectName());
        return CommonResult.success(null);
    }
    @RequestMapping(path = "/deleteFolder", method = RequestMethod.POST)
    public CommonResult deleteFolder(@RequestBody FileBo dto) throws Exception {
        minIoFileService.delete(dto.getBucketName(),dto.getObjectName());
        return CommonResult.success(null);
    }
}
