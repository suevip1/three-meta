package com.coatardbul.stock.service.base;

import cn.hutool.core.io.FastByteArrayOutputStream;
import com.coatardbul.stock.model.bo.MinIoBo;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioService {
    //必须使用注入的方式否则会出现空指针
    @Autowired
    MinioClient minioClient;

    /**
     * 查看存储bucket是否存在
     *  bucketName 需要传入桶名
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     *  bucketName 需要传入桶名
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 删除存储bucket
     * bucketName 需要传入桶名
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            return buckets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 文件上传
     *
     * @param file 文件
     * BucketName 需要传入桶名
     * @return Boolean
     */
    public String upload(String bucketName,String prefix,MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)){
            throw new RuntimeException();
        }
        String objectName =StringUtils.isNotBlank(prefix)?prefix + "/" + originalFilename:originalFilename;
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).build();
            //文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }

    public void mkdir(String bucketName,String path) {
        String objectName = path + "/";

        PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(bucketName).object(objectName)
                .stream(new ByteArrayInputStream(new byte[] {}), 0, -1).build();
        //文件名称相同会覆盖
        try {
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 预览
     * @param fileName
     * BucketName 需要传入桶名
     * @return
     */
    public String preview(String bucketName,String fileName){
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(bucketName).object(fileName).method(Method.GET).build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     * @param fileName 文件名称
     * BucketName 需要传入桶名
     * @param res response
     * @return Boolean
     */
    public void download(String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket("BucketName")
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)){
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
                while ((len=response.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                // res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()){
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看头一层文件对象
     * BucketName 需要传入桶名
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects(MinIoBo dto) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(dto.getBucketName())
                        .prefix(dto.getPrefix()).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }

    /**
     * 必须有prefix
     * 获取目录下的文件
     * @param dto
     * @return
     */
    public List<Item> listContentObjects(MinIoBo dto) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(dto.getBucketName())
                        .prefix(dto.getPrefix()).recursive(true) .build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                String replacedStr = item.objectName().replaceAll(dto.getPrefix() + "/", "");
                boolean contains =replacedStr.length()>1&& replacedStr.contains("/")&& !replacedStr.endsWith("/");
                if(!contains){
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }


    /**
     * 删除
     * @param fileName
     * BucketName 需要传入桶名
     * @return
     * @throws Exception
     */
    public boolean remove(String bucketName,String fileName){
        try {
            minioClient.removeObject( RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
