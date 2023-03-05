package com.example.sca.ui.cloud.bucket;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.sca.Config;
import com.example.sca.R;
import com.example.sca.ui.cloud.CosServiceFactory;
import com.example.sca.ui.cloud.CosUserInformation;
import com.example.sca.ui.cloud.common.base.BaseActivity;
import com.example.sca.ui.cloud.region.RegionActivity;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.bucket.PutBucketRequest;

/**
 * Created by jordanqin on 2020/6/18.
 * 添加存储桶页面
 * <p>
 * Copyright (c) 2010-2020 Tencent Cloud. All rights reserved.
 */
public class BucketAddActivity extends BaseActivity implements View.OnClickListener {
    private final int REQUEST_REGION = 10001;

    private EditText et_name;
    private TextView tv_region;
    private String region;
    CosUserInformation cosUserInformation = new CosUserInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_activity_add);

        et_name = findViewById(R.id.et_name);
        tv_region = findViewById(R.id.tv_region);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.rl_region).setOnClickListener(this);

        cosUserInformation.setCosUserInformation(Config.COS_SECRET_ID,Config.COS_SECRET_KEY,Config.COS_APP_ID);

        if (cosUserInformation.getCOS_APP_ID().length() == 0 ||
                cosUserInformation.getCOS_SECRET_ID().length() == 0 ||
                cosUserInformation.getCOS_SECRET_KEY().length() == 0) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.rl_region){
            // 进入存储桶区域选择RegionActivity
            startActivityForResult(new Intent(this, RegionActivity.class), REQUEST_REGION);
        } else if(v.getId() == R.id.btn_add){
            // 创建一个存储桶（ADD Bucket）
            addBucket();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_REGION && data!=null){
            String region = data.getStringExtra(RegionActivity.RESULT_REGION);
            String lable = data.getStringExtra(RegionActivity.RESULT_LABLE);
            if(!TextUtils.isEmpty(region) && !TextUtils.isEmpty(lable)){
                tv_region.setText(lable);
                this.region = region;
            }
        }
    }

    // 创建一个存储桶（ADD Bucket）
    private void addBucket(){
        if(TextUtils.isEmpty(et_name.getText())){
            toastMessage("桶名称不能为空");
            return;
        }

        if(TextUtils.isEmpty(region)){
            toastMessage("请选择地区");
            return;
        }

        CosXmlService cosXmlService = CosServiceFactory.getCosXmlService(this, region, cosUserInformation.getCOS_SECRET_ID(), cosUserInformation.getCOS_SECRET_KEY(), true);
        setLoading(true);
        String bucket = et_name.getText() + "-" + cosUserInformation.getCOS_APP_ID(); // 存储桶名称，由bucketname-appid 组成，appid必须填入
        PutBucketRequest putBucketRequest = new PutBucketRequest(bucket);
        // 使用异步回调请求
        cosXmlService.putBucketAsync(putBucketRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                setLoading(false);
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                setLoading(false);
                toastMessage("新建存储桶失败");
                if(clientException!=null) {
                    clientException.printStackTrace();
                }
                if(serviceException!=null) {
                    serviceException.printStackTrace();
                }
            }
        });
    }


}
