package com.coatardbul.stock.service.base;

import com.coatardbul.stock.common.config.CosConfig;
import com.coatardbul.stock.model.bo.FileBo;
import com.coatardbul.stock.model.bo.MinIoBo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.exception.MultiObjectDeleteException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.DeleteObjectsRequest;
import com.qcloud.cos.model.DeleteObjectsResult;
import com.qcloud.cos.model.ListObjectsRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.region.Region;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 腾讯云COS文件上传工具类
 *
 * @author coatardbul
 */
@Slf4j
@Service
public class MinIoFileService {

    @Autowired
    CosConfig cosConfig;
    @Autowired
    MinioService minioService;
    private COSClient cosClient;

    @Autowired
    public void setClientConfig() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        // SECRETID和SECRETKEY请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        COSCredentials cred = new BasicCOSCredentials(cosConfig.getSecretId(), cosConfig.getSecretKey());
        // 2 设置 bucketName 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(cosConfig.getRegionName());
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);
        this.cosClient = cosClient;
    }


    public void mkdir(String bucketName, String path) {

        minioService.mkdir(bucketName, path);

    }


    /**
     * 上传文件到响应的目录下
     *
     * @param bucketName
     * @param path       文件路径，非强制路径
     * @param file       文件
     * @return
     * @throws Exception
     */
    public String upload(String bucketName, String path, MultipartFile file) throws Exception {

        minioService.upload(bucketName, path, file);
        return minioService.preview(bucketName, org.apache.commons.lang3.StringUtils.isNotBlank(path) ? path + "/" + file.getOriginalFilename() : file.getOriginalFilename());
    }

    /**
     * 用缓冲区来实现这个转换, 即创建临时文件
     * 使用 MultipartFile.transferTo()
     *
     * @param multipartFile
     * @return
     */
    private static File transferToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String prefix = originalFilename.split("\\.")[0];
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File file = File.createTempFile(prefix, suffix);
        multipartFile.transferTo(file);
        return file;
    }

    public void delete(String path, String fileName) {

        minioService.remove(path, fileName);
    }


    private FileBo convert(Bucket bucket) {
        FileBo fileBo = new FileBo();
//        fileBo.setUrl();
//        fileBo.setFilePath();
        fileBo.setBucketName(bucket.name());
        fileBo.setFileName(bucket.name());
        if(bucket.name().contains(".")){
            String substring = bucket.name().substring(bucket.name().lastIndexOf(".")+1, bucket.name().length());
            fileBo.setSuffixName(substring);
        }
        fileBo.setFileType("1");
        return fileBo;

    }

    private FileBo convert(Item item, MinIoBo dto) {
        FileBo fileBo = new FileBo();
        fileBo.setFilePath(dto.getPrefix());
        fileBo.setBucketName(dto.getBucketName());
        String realFileName = item.objectName().replaceAll(dto.getPrefix() + "/", "").replace("/","");
        fileBo.setFileName(realFileName);
        if(realFileName.contains(".")){
            String substring = realFileName.substring(realFileName.lastIndexOf(".")+1, realFileName.length());
            fileBo.setSuffixName(substring);
        }
        fileBo.setObjectName(item.objectName());
        String preview = minioService.preview(dto.getBucketName(), fileBo.getObjectName());
        fileBo.setUrl(preview);
        if (item.objectName().endsWith("/")) {
            fileBo.setFileType("1");
        } else {
            fileBo.setFileType("2");
        }
        fileBo.setSize(item.size());

        return fileBo;

    }

    /**
     * 获取目录下的文件信息
     *
     * @param dto
     * @return
     */
    public List<FileBo> getPathInfo(FileBo dto) {
        List<FileBo> result = new ArrayList<>();
        //bucket名称
        if (!StringUtils.isNotBlank(dto.getBucketName())) {
            List<Bucket> allBuckets = minioService.getAllBuckets();
            if (allBuckets != null && allBuckets.size() > 0) {
                result = allBuckets.stream().map(this::convert).collect(Collectors.toList());
            }
        } else {
            List<Item> allBuckets = new ArrayList<>();
            MinIoBo convert = convert(dto);

            if (StringUtils.isNotBlank(dto.getFilePath())) {
                allBuckets = minioService.listContentObjects(convert);
            } else {
                allBuckets = minioService.listObjects(convert);
            }
            if (allBuckets.size() > 0) {
                result = allBuckets.stream().map(o1 -> convert(o1, convert)).collect(Collectors.toList());
            }
        }
        return result;
    }

    private MinIoBo convert(FileBo dto){
        MinIoBo minIO=new MinIoBo();
        minIO.setPrefix(dto.getFilePath());
        minIO.setObjectName(StringUtils.isNotBlank(dto.getFilePath())?dto.getFilePath()+"/"+dto.getFileName():dto.getFileName());

        //        minIO.setContinuationToken();
//        minIO.setFetchOwner();
//        minIO.setVersionIdMarker();
//        minIO.setIncludeUserMetadata();
//        minIO.setRecursive();
//        minIO.setUseApiVersion1();
//        minIO.setIncludeVersions();
        minIO.setBucketName(dto.getBucketName());
//        minIO.setRegion();
        return minIO;
    }


    public void deleteFolder(String path, String fileName) {
        String delDir = "";
        if (StringUtils.isNotBlank(path)) {
            delDir += path;
        }
        if (StringUtils.isNotBlank(fileName)) {
            delDir += fileName;
        }


        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
// 设置 bucket 名称
        listObjectsRequest.setBucketName(cosConfig.getBucketName());
// prefix 表示列出的对象名以 prefix 为前缀
// 这里填要列出的目录的相对 bucket 的路径
        listObjectsRequest.setPrefix(delDir);
// 设置最大遍历出多少个对象, 一次 listobject 最大支持1000
        listObjectsRequest.setMaxKeys(1000);

// 保存每次列出的结果
        ObjectListing objectListing = null;

        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return;
            } catch (CosClientException e) {
                e.printStackTrace();
                return;
            }

            // 这里保存列出的对象列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();

            ArrayList<DeleteObjectsRequest.KeyVersion> delObjects = new ArrayList<DeleteObjectsRequest.KeyVersion>();

            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                delObjects.add(new DeleteObjectsRequest.KeyVersion(cosObjectSummary.getKey()));
            }

            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(cosConfig.getBucketName());

            deleteObjectsRequest.setKeys(delObjects);

            try {
                DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
                List<DeleteObjectsResult.DeletedObject> deleteObjectResultArray = deleteObjectsResult.getDeletedObjects();
            } catch (MultiObjectDeleteException mde) {
                // 如果部分删除成功部分失败, 返回 MultiObjectDeleteException
                List<DeleteObjectsResult.DeletedObject> deleteObjects = mde.getDeletedObjects();
                List<MultiObjectDeleteException.DeleteError> deleteErrors = mde.getErrors();
            } catch (CosServiceException e) {
                e.printStackTrace();
                return;
            } catch (CosClientException e) {
                e.printStackTrace();
                return;
            }

            // 标记下一次开始的位置
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
    }
}
