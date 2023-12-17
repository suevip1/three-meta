package com.coatardbul.stock.model.bo;

import lombok.Data;

/**
 * <p>
 * Note:     * @see /io/minio/ListObjectsArgs.class
 * <p>
 * Date: 2022/5/27
 *
 * @author Su Xiaolei
 */
@Data
public class MinIoBo {

    private String delimiter = "";
    private boolean useUrlEncodingType = true;
    private String keyMarker; // 'marker' for ListObjectsV1 and 'startAfter' for ListObjectsV2.
    private int maxKeys = 1000;
    /**
     * 文件名称前缀
     */
    private String prefix = "";

    private String objectName;


    private String continuationToken; // only for ListObjectsV2.
    private boolean fetchOwner; // only for ListObjectsV2.
    private String versionIdMarker; // only for GetObjectVersions.
    private boolean includeUserMetadata; // MinIO extension applicable to ListObjectsV2.
    private boolean recursive;
    private boolean useApiVersion1;
    private boolean includeVersions;
    protected String bucketName;
    protected String region;
}
