package com.example.sca.ui.cloud.transfer;


import static com.example.sca.ui.cloud.object.ObjectActivity.ACTIVITY_EXTRA_BUCKET_NAME;
import static com.example.sca.ui.cloud.object.ObjectActivity.ACTIVITY_EXTRA_FOLDER_NAME;
import static com.example.sca.ui.cloud.object.ObjectActivity.ACTIVITY_EXTRA_REGION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.sca.Config;
import com.example.sca.R;
import com.example.sca.ui.cloud.CosServiceFactory;
import com.example.sca.ui.cloud.CosUserInformation;
import com.example.sca.ui.cloud.common.FilePathHelper;
import com.example.sca.ui.cloud.common.Utils;
import com.example.sca.ui.cloud.common.base.BaseActivity;
import com.example.sca.ui.cloud.encryptalgorithm.AESUtils;
import com.example.sca.ui.cloud.encryptalgorithm.FastBlur;
import com.example.sca.ui.cloud.encryptalgorithm.ImageUtil;
import com.example.sca.ui.cloud.object.ObjectActivity;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;


/*
 * Created by jordanqin on 2020/6/18.
 * ??????????????????
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class UploadActivity extends BaseActivity implements View.OnClickListener {
    private final int OPEN_FILE_CODE = 10001;


    //????????????????????????
    private ImageView iv_image;
    //??????????????????
    private TextView tv_name;
    //????????????
    private TextView tv_state;
    //????????????
    private TextView tv_progress;
    //???????????????
    private ProgressBar pb_upload;

    //?????????????????????????????????
    private Button btn_left;
    //?????????????????????????????????
    private Button btn_right;
    private RadioGroup radgroup;

    private String bucketName;
    private String bucketRegion;
    private String folderName;
    private AESUtils aesUtils;
    private CosUserInformation cosUserInformation;


    /*
     * {@link CosXmlService} ???????????? COS ??????????????????????????????????????? COS ??????????????? API ?????????
     * <p>
     * ?????????{@link CosXmlService} ???????????????????????? region???????????????????????????????????? region ???
     * Bucket????????????????????? {@link CosXmlService} ?????????
     */
    private CosXmlService cosXmlService;

    /*
     * {@link TransferManager} ?????????????????? {@link CosXmlService} ???????????????????????????????????????
     * ??????????????? COS ????????? COS ?????????????????????????????????????????????
     */
    private TransferManager transferManager;
    private COSXMLUploadTask cosxmlTask;
    /*
     * ????????????????????? COS ??????
     */
    private String currentUploadPath; // ????????????

    private final String photoPrefix = "picture";
    private static final String TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        bucketName = getIntent().getStringExtra(ACTIVITY_EXTRA_BUCKET_NAME);
        bucketRegion = getIntent().getStringExtra(ACTIVITY_EXTRA_REGION);
        folderName = getIntent().getStringExtra(ACTIVITY_EXTRA_FOLDER_NAME);

        iv_image = findViewById(R.id.iv_image);
        tv_name = findViewById(R.id.tv_name);
        tv_state = findViewById(R.id.tv_state);
        tv_progress = findViewById(R.id.tv_progress);
        pb_upload = findViewById(R.id.pb_upload);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        radgroup = findViewById(R.id.radioGroup);

        btn_right.setOnClickListener(this);
        btn_left.setOnClickListener(this);

        cosUserInformation = new CosUserInformation();
        cosUserInformation.setCosUserInformation(Config.COS_SECRET_ID,Config.COS_SECRET_KEY,Config.COS_APP_ID);


        if (cosUserInformation.getCOS_SECRET_ID().length() == 0 || cosUserInformation.getCOS_SECRET_KEY().length() == 0) {
            finish();
        }


        cosXmlService = CosServiceFactory.getCosXmlService(this, bucketRegion,
                cosUserInformation.getCOS_SECRET_ID(), cosUserInformation.getCOS_SECRET_KEY(), true);
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);

        aesUtils = new AESUtils();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.choose_photo) {
            //???????????????????????? ??????????????????????????????????????????
            if (cosxmlTask == null) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, OPEN_FILE_CODE);
                return true;
            } else {
                toastMessage("???????????????????????????????????????????????????");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_left) {
            if ("??????".contentEquals(btn_left.getText())) {

                // ???????????? ???????????????????????????
                Intent intent2 = new Intent(this, ObjectActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent2);

                upload();

                //???????????????????????????
                for (int i = 0; i < radgroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radgroup.getChildAt(i);

                    if (rd.isChecked()) {
                        //??????????????????????????????????????????
                        //            //??????????????????????????????????????????
                        Bitmap bitmap = BitmapFactory.decodeFile(currentUploadPath);
                        Bitmap suoxiao = ThumbnailUtils.extractThumbnail(bitmap,400,400);
                        String filename = getFileNameWithSuffix(currentUploadPath);
                        switch (i){
                            case 0: // ??????????????????????????????????????????????????????
                                String thumbnailPath = saveBitmapToGallery(this, suoxiao, filename);
                                Log.e(TAG, "????????????????????????: " + thumbnailPath);
                                String thbfilename = getFileNameWithSuffix(thumbnailPath);  // ????????????????????????
                                String thumbnailUploadPath= aesUtils.aesEncrypt(thumbnailPath, thbfilename);
                                Log.e(TAG, "?????????????????????: "+ thumbnailUploadPath );
                                uploadthumbnail(thumbnailUploadPath);
                                //??????????????????
                                deleteImage(thumbnailPath);
                                break;

                            case 1: //???????????????????????????
                                Bitmap doBlurBitmap = doBlur(suoxiao);
                                String doBlurthbPath = saveBitmapToGallery(this, doBlurBitmap, filename);
                                Log.e(TAG, "?????????????????????: "+ doBlurthbPath );
                                uploadthumbnail(doBlurthbPath);
                                break;

                            case 2://???????????????????????????
                                Bitmap waterMaskBitmap = waterMaskVideoPhoto(suoxiao);
                                String waterMaskthbPath = saveBitmapToGallery(this, waterMaskBitmap, filename);
                                Log.e(TAG, "?????????????????????: "+ waterMaskthbPath );
                                uploadthumbnail(waterMaskthbPath);
                                break;

                            case 3://???????????????????????????
                                Bitmap greyBitmap = ImageUtil.bitmap2Gray(suoxiao);
                                String greythbPath = saveBitmapToGallery(this, greyBitmap, filename);
                                Log.e(TAG, "?????????????????????: "+ greythbPath );
                                uploadthumbnail(greythbPath);
                                break;
                        }
                        break;
                    }
                }

            } else {//??????
                if (cosxmlTask != null) {
                    // ????????????
                    cosxmlTask.cancel();
                    finish();
                } else {
                    toastMessage("????????????");
                }
            }
        } else if (v.getId() == R.id.btn_right) {
            if ("??????".contentEquals(btn_right.getText())) {
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.IN_PROGRESS) {
                    // ????????????
                    cosxmlTask.pause();
                    btn_right.setText("??????");
                } else {
                    toastMessage("????????????");
                }
            } else {//??????
                if (cosxmlTask != null && cosxmlTask.getTaskState() == TransferState.PAUSED) {
                    // ???????????????????????????????????????
                    cosxmlTask.resume();
                    btn_right.setText("??????");
                } else {
                    toastMessage("????????????");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_FILE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String path = FilePathHelper.getAbsPathFromUri(this, data.getData());
            if (TextUtils.isEmpty(path)) {
                iv_image.setImageBitmap(null);
                tv_name.setText("");
            } else {
                //????????????????????????URI???????????????
                //????????????????????????????????????????????????????????????
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    iv_image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (path != null)
                    tv_name.setText(getFileNameWithSuffix(path));
                else
                    tv_name.setText(null);
            }

            currentUploadPath = path;


            pb_upload.setProgress(0);
            tv_progress.setText("");
            tv_state.setText("???");
        }
    }

    /**
     * ??????????????????
     *
     * @param state ?????? {@link TransferState}
     */
    private void refreshUploadState(final TransferState state) {
        uiAction(new Runnable() {
            @Override
            public void run() {
                tv_state.setText(state.toString());
            }
        });

    }

    /**
     * ??????????????????
     *
     * @param progress ?????????????????????
     * @param total    ???????????????
     */
    private void refreshUploadProgress(final long progress, final long total) {
        uiAction(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                pb_upload.setProgress((int) (100 * progress / total));
                tv_progress.setText(Utils.readableStorageSize(progress) + "/" + Utils.readableStorageSize(total));
            }
        });
    }

    private void upload(){

        if (TextUtils.isEmpty(currentUploadPath)) {
            toastMessage("??????????????????");
            return;
        }

        if (cosxmlTask == null) {
            //???????????????????????????????????????
            String filename = getFileNameWithSuffix(currentUploadPath);

            // AES ???????????????
            String encryptimagepath = aesUtils.aesEncrypt(currentUploadPath, filename);


            File file = new File(encryptimagepath);
            String cosPath;

            if (TextUtils.isEmpty(folderName)) {
                cosPath = photoPrefix + File.separator + file.getName();
            } else {
                cosPath = folderName + File.separator + file.getName();
            }
            // ????????????
            cosxmlTask = transferManager.upload(bucketName, cosPath,
                    encryptimagepath, null);

            // ????????????
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(final TransferState state) {
                    refreshUploadState(state);
                }
            });

            //????????????????????????
            cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(final long complete, final long target) {
                    refreshUploadProgress(complete, target);
                }
            });

            //????????????????????????
            cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;

                    cosxmlTask = null;
                    toastMessage("????????????");
                    setResult(RESULT_OK);
                    uiAction(new Runnable() {
                        @Override
                        public void run() {
                            btn_left.setVisibility(View.GONE);
                            btn_right.setVisibility(View.GONE);
                        }
                    });
                    //??????????????????
                    deleteImage(encryptimagepath);

                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                        cosxmlTask = null;
                        uiAction(new Runnable() {
                            @Override
                            public void run() {
                                pb_upload.setProgress(0);
                                tv_progress.setText("");
                                tv_state.setText("???");
                            }
                        });
                    }
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                    if (serviceException != null) {
                        serviceException.printStackTrace();
                    }
                }
            });
            btn_left.setText("??????");
        }
    }

    private void uploadthumbnail(String path) {

        if (TextUtils.isEmpty(path)) {
            Log.e(TAG, "uploadthumbnail: ????????????" );
            return;
        }

        if (cosxmlTask != null) {

            File file = new File(path);
            String cosPaththb = photoPrefix + File.separator + "thumbnail" + File.separator + file.getName();

            // ????????????
            cosxmlTask = transferManager.upload(bucketName, cosPaththb,
                    path, null);

            // ????????????
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(final TransferState state) {
                    refreshUploadState(state);
                }
            });

            //????????????????????????
            cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(long complete, long target) {
                    // todo Do something to update progress...
                }
            });
            //????????????????????????
            cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult uploadResult =
                            (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                    Log.e(TAG, "onSuccess:????????????????????? ");
                    //??????????????????????????????
                    deleteImage(path);

                }

                // ??????????????? kotlin ???????????????????????????????????????????????????????????????????????????????????? onFail ???????????????
                // clientException ???????????? CosXmlClientException????serviceException ???????????? CosXmlServiceException?
                @Override
                public void onFail(CosXmlRequest request,
                                   @Nullable CosXmlClientException clientException,
                                   @Nullable CosXmlServiceException serviceException) {
                    if (clientException != null) {
                        clientException.printStackTrace();
                    } else {
                        Objects.requireNonNull(serviceException).printStackTrace();
                    }
                    Log.e(TAG, "onFail: ?????????????????????" );

                }
            });
            //????????????????????????, ????????????????????????
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(TransferState state) {
                    // todo notify transfer state
                }
            });

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cosXmlService != null) {
            cosXmlService.release();
        }
    }


    /**
     * ????????????????????????
     */
    public String getFileNameWithSuffix(String path) {
        if(TextUtils.isEmpty(path)){
            return "";
        }
        int start = path.lastIndexOf("/");
        if (start != -1 ) {
            return path.substring(start + 1);
        } else {
            return "";
        }
    }
    protected void uiAction(Runnable runnable){
        findViewById(android.R.id.content).post(runnable);
    }


    /*
     * ????????????????????????
     *
     * @param bmp
     * @param bitName
     */
    public static String saveBitmapToGallery(Context context, Bitmap bmp, String bitName) {
        // ??????????????????
        File galleryPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "thumbnail" + File.separator);
        if (!galleryPath.exists()) {
            galleryPath.mkdirs();
        }

        String fileName ="thumbnail_" + bitName;
        File file = new File(galleryPath, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Log.e(TAG, "saveBitmapToGallery: "+ file.getAbsolutePath() );

        return file.getAbsolutePath();

    }


    public void deleteImage(String path){
        File file = new File(path);
        //?????????????????????
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});
        //?????????????????????
        boolean delete = file.delete();

    }
    private Bitmap doBlur(Bitmap bitmap){
        int scaleRatio = 5;
        int blurRadius = 10;

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() / scaleRatio,
                bitmap.getHeight() / scaleRatio,
                false);
        return FastBlur.doBlur(scaledBitmap, blurRadius, true);
    }
    private Bitmap waterMaskVideoPhoto(Bitmap bitmap) {

        //??????Drawble????????????????????????????????????
        Bitmap mask = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.smile);
        Log.e(TAG," mask????????? "+mask.getWidth());
        Log.e(TAG," mask????????? "+mask.getHeight());

//            //??????????????????????????????????????????
//            Bitmap suoxiao = ThumbnailUtils.extractThumbnail(mask,200,200);
//            Log.e(TAG," ?????????mask????????? "+suoxiao.getWidth());
//            Log.e(TAG," ?????????mask????????? "+suoxiao.getHeight());
        bitmap = ImageUtil.createWaterMaskCenter(bitmap,mask);
        return bitmap;
    }

}
