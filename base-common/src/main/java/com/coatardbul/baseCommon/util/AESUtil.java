package com.coatardbul.baseCommon.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类
 *
 * @author ACGkaka
 * @since 2021-06-18 19:11:03
 */
@Slf4j
public class AESUtil {
//    /**
//     * 日志相关
//     */
//    private static final log log = logFactory.getlog(AESUtil.class);
    /**
     * 编码
     */
    private static final String ENCODING = "UTF-8";
    /**
     * 算法定义
     */
    private static final String AES_ALGORITHM = "AES";
    /**
     * 指定填充方式
     */
    private static final String CIPHER_PADDING = "AES/ECB/PKCS5Padding";
    private static final String CIPHER_CBC_PADDING = "AES/CBC/PKCS5Padding";
    /**
     * 偏移量(CBC中使用，增强加密算法强度)
     */
    private static final String IV_SEED = "1234567812345678";

    /**
     * AES加密
     * @param content 待加密内容
     * @param aesKey  密码
     * @return
     */
    public static String encrypt(String content, String aesKey){
        if(StringUtils.isBlank(content)){
            log.info("AES encrypt: the content is null!");
            return null;
        }
        //判断秘钥是否为16位
        if(StringUtils.isNotBlank(aesKey) && aesKey.length() == 16){
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置加密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_PADDING);
                //选择加密
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                //根据待加密内容生成字节数组
                byte[] encrypted = cipher.doFinal(content.getBytes(ENCODING));
                //返回base64字符串
                return Base64Utils.encodeToString(encrypted);
            } catch (Exception e) {
                log.info("AES encrypt exception:" + e.getMessage());
                throw new RuntimeException(e);
            }

        }else {
            log.info("AES encrypt: the aesKey is null or error!");
            return null;
        }
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param aesKey  密码
     * @return
     */
    public static String decrypt(String content, String aesKey){
        if(StringUtils.isBlank(content)){
            log.info("AES decrypt: the content is null!");
            return null;
        }
        //判断秘钥是否为16位
        if(StringUtils.isNotBlank(aesKey) && aesKey.length() == 16){
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置解密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_PADDING);
                //选择解密
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);

                //先进行Base64解码
                byte[] decodeBase64 = Base64Utils.decodeFromString(content);

                //根据待解密内容进行解密
                byte[] decrypted = cipher.doFinal(decodeBase64);
                //将字节数组转成字符串
                return new String(decrypted, ENCODING);
            } catch (Exception e) {
                log.info("AES decrypt exception:" + e.getMessage());
                throw new RuntimeException(e);
            }

        }else {
            log.info("AES decrypt: the aesKey is null or error!");
            return null;
        }
    }

    /**
     * AES_CBC加密
     *
     * @param content 待加密内容
     * @param aesKey  密码
     * @return
     */
    public static String encryptCBC(String content, String aesKey){
        if(StringUtils.isBlank(content)){
            log.info("AES_CBC encrypt: the content is null!");
            return null;
        }
        //判断秘钥是否为16位
        if(StringUtils.isNotBlank(aesKey) && aesKey.length() == 16){
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置加密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_CBC_PADDING);
                //偏移
                IvParameterSpec iv = new IvParameterSpec(IV_SEED.getBytes(ENCODING));
                //选择加密
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
                //根据待加密内容生成字节数组
                byte[] encrypted = cipher.doFinal(content.getBytes(ENCODING));
                //返回base64字符串
                return Base64Utils.encodeToString(encrypted);
            } catch (Exception e) {
                log.info("AES_CBC encrypt exception:" + e.getMessage());
                throw new RuntimeException(e);
            }

        }else {
            log.info("AES_CBC encrypt: the aesKey is null or error!");
            return null;
        }
    }

    /**
     * AES_CBC解密
     *
     * @param content 待解密内容
     * @param aesKey  密码
     * @return
     */
    public static String decryptCBC(String content, String aesKey){
        if(StringUtils.isBlank(content)){
            log.info("AES_CBC decrypt: the content is null!");
            return null;
        }
        //判断秘钥是否为16位
        if(StringUtils.isNotBlank(aesKey) && aesKey.length() == 16){
            try {
                //对密码进行编码
                byte[] bytes = aesKey.getBytes(ENCODING);
                //设置解密算法，生成秘钥
                SecretKeySpec skeySpec = new SecretKeySpec(bytes, AES_ALGORITHM);
                //偏移
                IvParameterSpec iv = new IvParameterSpec(IV_SEED.getBytes(ENCODING));
                // "算法/模式/补码方式"
                Cipher cipher = Cipher.getInstance(CIPHER_CBC_PADDING);
                //选择解密
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

                //先进行Base64解码
                byte[] decodeBase64 = Base64Utils.decodeFromString(content);

                //根据待解密内容进行解密
                byte[] decrypted = cipher.doFinal(decodeBase64);
                //将字节数组转成字符串
                return new String(decrypted, ENCODING);
            } catch (Exception e) {
                log.info("AES_CBC decrypt exception:" + e.getMessage());
                throw new RuntimeException(e);
            }

        }else {
            log.info("AES_CBC decrypt: the aesKey is null or error!");
            return null;
        }
    }

    public static void main(String[] args) {
        // AES支持三种长度的密钥：128位、192位、256位。
        // 代码中这种就是128位的加密密钥，16字节 * 8位/字节 = 128位。
        String random = RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz1234567890");
        System.out.println("随机key:" + random);
        System.out.println();

        System.out.println("---------加密---------");
        String aesResult = encrypt("本站针对md5、sha1、sha256等全球通用公开的加密算法进行反向查询，通过穷举字符组合的方式，创建了明文密文对应查询数据库，创建的记录约90万亿条，占用硬盘超过500TB，查询成功率95%以上，很多复杂密文只有本站才可查询。本站专注于各种公开算法，已稳定运行17年。", random);
        System.out.println("aes加密结果:" + aesResult);
        System.out.println();

        System.out.println("---------解密---------");
        String decrypt = decrypt(aesResult, random);
        System.out.println("aes解密结果:" + decrypt);
        System.out.println();


        System.out.println("--------AES_CBC加密解密---------");
        String cbcResult = encryptCBC("测试AES加密12456", random);
        System.out.println("aes_cbc加密结果:" + cbcResult);
        System.out.println();

        System.out.println("---------解密CBC---------");
        String cbcDecrypt = decryptCBC(cbcResult, random);
        System.out.println("aes解密结果:" + cbcDecrypt);
        System.out.println();
    }
}
