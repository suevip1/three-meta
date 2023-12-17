package com.coatardbul.stock.model.bo;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/5/27
 *
 * @author Su Xiaolei
 */
@Data
public class FileBo {

    private String bucketName;

    private String url;

    /**
     * 文件路径
     */
    private String filePath;

    private String fileName;

    /**
     * 文件后缀
     */
    private String suffixName;

    /**
     * 文件的全路径
     */
    private String objectName;

    private long size;

    /**
     * 1 目录 2 文件
     */
    private String fileType;
}
