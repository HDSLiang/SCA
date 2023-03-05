package com.example.sca.ui.cloud.encryptalgorithm;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    //    private String filePath = Environment.getExternalStorageDirectory().getPath()+ "/test/test.jpg";


    //AES加密使用的秘钥，注意的是秘钥的长度必须是16位

    private static final String AES_KEY = "MyDifficultPassw";

    private static final String CIPHER_ALGORITHM = "AES";  //默认模式为 AES/ECB
    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
    private static final String CIPHER_ALGORITHM_ECB = "AES/ECB/PKCS5Padding";
    private static final String CIPHER_ALGORITHM_CTR = "AES/CTR/NoPadding";


    private String outPath;
    private final String TAG = "ImageEncrypt";


    /**
     * 使用AES-CTR加密标准进行加密
     * filename -- 带后缀文件名
     *
     * @return outPath；
     */
    public  String aesEncrypt(String filePath, String filename) {

        // AES加密后的文件夹路径
        String Path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "encryptphoto" + File.separator;
        File enImagePath = new File(Path);
        if (!enImagePath.exists()) { // 文件夹不存在，则创建
            enImagePath.mkdir();
        }

        try {
            // AES加密后的图片文件路径
            String outPath = Path + filename;

            FileInputStream fis;
            fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(outPath);
            //SecretKeySpec此类来根据一个字节数组构造一个 SecretKey
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            //Cipher类为加密和解密提供密码功能,获取实例
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CTR);
            //初始化
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(getIV()));
            //CipherOutputStream 为加密输出流
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            cos.flush();
            cos.close();
            fis.close();
            this.outPath = outPath;

        } catch (Exception e) {
            Log.e(TAG, "加密失败 " + e.getMessage(), e);
//            e.printStackTrace();
        }
        Log.e(TAG, "outPath: " + this.outPath);
        return this.outPath;
    }


    /**
     * 使用AES-CTR标准解密
     *
     * @return image
     */
    public Bitmap aesDecrypt(String outPath) {
        Bitmap image = null;
        try {
            FileInputStream fis;
            fis = new FileInputStream(outPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CTR);
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
            //CipherInputStream 为加密输入流
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            out.flush();
            out.close();
            cis.close();
            //获取字节流显示图片
            byte[] bytes = out.toByteArray();
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "解密失败 " + e.getMessage(), e);

        }

        return image;

    }

    public String aesDecrypt( String sourceFilePath ,String filename) throws Exception {
        String destFilePath=Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "cosShare" + File.separator;
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath+filename);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            FileInputStream in = new FileInputStream(sourceFile);
            FileOutputStream out = new FileOutputStream(destFile);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),"AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CTR);
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
            CipherOutputStream cout = new CipherOutputStream(out, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                cout.write(cache, 0, nRead);
                cout.flush();
            }
            cout.close();
            out.close();
            in.close();
        }
        return destFile.getAbsolutePath();
    }



    /**
     * 使用AES加密标准进行加密
     * filename -- 带后缀文件名
     *
     * @return outPath；
     */
    public String aesECBEncrypt(String filePath, String filename) {

        // AES加密后的文件夹路径
        String Path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "encryptphoto" + File.separator;
        File enImagePath = new File(Path);
        if (!enImagePath.exists()) { // 文件夹不存在，则创建
            enImagePath.mkdir();
        }

        try {
            // AES加密后的图片文件路径
            String outPath = Path + filename;

            FileInputStream fis;
            fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(outPath);
            //SecretKeySpec此类来根据一个字节数组构造一个 SecretKey
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            //Cipher类为加密和解密提供密码功能,获取实例
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
            //初始化
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            //CipherOutputStream 为加密输出流
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            cos.flush();
            cos.close();
            fis.close();
            this.outPath = outPath;

        } catch (Exception e) {
            Log.e(TAG, "加密失败 " + e.getMessage(), e);
//            e.printStackTrace();
        }
        Log.e(TAG, "outPath: " + this.outPath);
        return this.outPath;
    }


    /**
     * 使用AES标准解密
     *
     * @return image
     */
    public Bitmap aesECBDecrypt(String outPath) {
        Bitmap image = null;
        try {
            FileInputStream fis;
            fis = new FileInputStream(outPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            //CipherInputStream 为加密输入流
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            out.flush();
            out.close();
            cis.close();
            //获取字节流显示图片
            byte[] bytes = out.toByteArray();
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "解密失败 " + e.getMessage(), e);

        }
        Log.e(TAG, "解密成功 ");

        return image;

    }

    /**
     * 使用AES——CBC加密标准进行加密
     * filename -- 带后缀文件名
     *
     * @return outPath；
     */
    public String aesCBCEncrypt(String filePath, String filename) {

        // AES加密后的文件夹路径
        String Path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "encryptphoto" + File.separator;
        File enImagePath = new File(Path);
        if (!enImagePath.exists()) { // 文件夹不存在，则创建
            enImagePath.mkdir();
        }

        try {
            // AES加密后的图片文件路径
            String outPath = Path + filename;

            FileInputStream fis;
            fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(outPath);
            //SecretKeySpec此类来根据一个字节数组构造一个 SecretKey
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            //Cipher类为加密和解密提供密码功能,获取实例
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            //初始化
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(getIV()));
            //CipherOutputStream 为加密输出流
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            cos.flush();
            cos.close();
            fis.close();
            this.outPath = outPath;

        } catch (Exception e) {
            Log.e(TAG, "加密失败 " + e.getMessage(), e);
//            e.printStackTrace();
        }
        Log.e(TAG, "outPath: " + this.outPath);
        return this.outPath;
    }


    /**
     * 使用AES——CBC标准解密
     *
     * @return image
     */
    public Bitmap aesCBCDecrypt(String outPath) {
        Bitmap image = null;
        try {
            FileInputStream fis;
            fis = new FileInputStream(outPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            SecretKeySpec sks = new SecretKeySpec(AES_KEY.getBytes(),
                    "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(getIV()));
            //CipherInputStream 为加密输入流
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            out.flush();
            out.close();
            cis.close();
            //获取字节流显示图片
            byte[] bytes = out.toByteArray();
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "解密失败 " + e.getMessage(), e);

        }

        return image;

    }

    private byte[] getIV() {
        String iv = "1234567812345678";
        return iv.getBytes();
    }

    /**
     * 使用AES加密标准进行加密
     * filename -- 带后缀文件名
     *
     * @return outPath；
     */
    public String aesEncryptShare(String filePath, String filename, String key) {

        // AES加密后的文件夹路径
        String Path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "encryptphoto" + File.separator;
        File enImagePath = new File(Path);
        if (!enImagePath.exists()) { // 文件夹不存在，则创建
            enImagePath.mkdir();
        }

        try {
            // AES加密后的图片文件路径
            String outPath = Path + filename;

            FileInputStream fis;
            fis = new FileInputStream(filePath);
            FileOutputStream fos = new FileOutputStream(outPath);
            //SecretKeySpec此类来根据一个字节数组构造一个 SecretKey
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(),
                    "AES");
            //Cipher类为加密和解密提供密码功能,获取实例
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CTR);
            //初始化
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(getIV()));
            //CipherOutputStream 为加密输出流
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            cos.flush();
            cos.close();
            fis.close();
            this.outPath = outPath;

        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "共享加密失败 " + e.getMessage(), e);

        }
        Log.e(TAG, "outPath: " + this.outPath);
        return this.outPath;
    }

    /**
     * 使用AES标准解密
     *
     * @return image
     */
    public Bitmap aesDecryptShare(String outPath, String key) {
        Bitmap image = null;
        try {
            FileInputStream fis;
            fis = new FileInputStream(outPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(),
                    "AES");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CTR);
            cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(getIV()));
            //CipherInputStream 为加密输入流
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[1024];
            while ((b = cis.read(d)) != -1) {
                out.write(d, 0, b);
            }
            out.flush();
            out.close();
            cis.close();
            //获取字节流显示图片
            byte[] bytes = out.toByteArray();
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "共享解密失败 " + e.getMessage(), e);
        }
        return image;

    }


}
