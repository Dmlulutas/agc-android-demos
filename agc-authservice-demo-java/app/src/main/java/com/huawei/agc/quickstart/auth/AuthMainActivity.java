/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.agc.quickstart.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.ProfileRequest;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AuthMainActivity extends AppCompatActivity implements View.OnClickListener {
    AGConnectAuth agConnectAuth;
    LinearLayout layoutSingout;
    ImageView imgViewPhoto;
    TextView txtViewNickName;
    TextView txtViewEmail;
    TextView txtViewPhone;
    Button btnSignout;
    Button btnUpdateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_main);
        agConnectAuth = AGConnectAuth.getInstance();
        initView();
        showUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        layoutSingout = findViewById(R.id.layout_SingOut);
        btnSignout = findViewById(R.id.btn_Signout);
        btnSignout.setOnClickListener(this);
        imgViewPhoto = findViewById(R.id.imgView_photo);
        imgViewPhoto.setOnClickListener(this);
        txtViewNickName = findViewById(R.id.txtView_NickName);
        txtViewEmail = findViewById(R.id.txtView_email);
        txtViewPhone = findViewById(R.id.txtView_phone);
        btnUpdateName = findViewById(R.id.btn_update_name);
        btnUpdateName.setOnClickListener(this);

        findViewById(R.id.btn_link).setOnClickListener(this);
    }

    private void showUserInfo() {
        AGConnectUser agConnectUser = agConnectAuth.getCurrentUser();
        String nickname = agConnectUser.getDisplayName();
        String email = agConnectUser.getEmail();
        String phone = agConnectUser.getPhone();
        String photoUrl = agConnectUser.getPhotoUrl();
        txtViewNickName.setText(nickname);
        txtViewEmail.setText(email);
        txtViewPhone.setText(phone);
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.get().load(photoUrl).into(imgViewPhoto);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Signout:
                if (agConnectAuth.getCurrentUser() != null) {
                    agConnectAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }
                break;
            case R.id.imgView_photo:
                updateAvatar();
                break;
            case R.id.btn_update_name:
                updateDisplayName();
                break;
            case R.id.btn_link:
                link();
                break;
        }
    }

    private void link() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_link, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        ListView listView = view.findViewById(R.id.list_view);
        List<LinkEntity> linkEntityList = initLink();
        LinkAdapter linkAdapter = new LinkAdapter(dialog.getContext(), R.layout.list_link_item, linkEntityList);
        listView.setAdapter(linkAdapter);
        dialog.show();
    }

    private List<LinkEntity> initLink() {
        List<LinkEntity> linkEntityList = new ArrayList<>();
        linkEntityList.add(new LinkEntity("HMS", R.mipmap.huawei, HWIDActivity.class));
        linkEntityList.add(new LinkEntity("Facebook ", R.mipmap.facebook, FacebookActivity.class));
        linkEntityList.add(new LinkEntity("Twitter", R.mipmap.twitter, TwitterActivity.class));
        linkEntityList.add(new LinkEntity("WeiXin", R.mipmap.wechat, WeiXinActivity.class));
        linkEntityList.add(new LinkEntity("HWGame", R.mipmap.huawei, HWGameActivity.class));
        linkEntityList.add(new LinkEntity("QQ", R.mipmap.qq, QQActivity.class));
        linkEntityList.add(new LinkEntity("WeiBo", R.mipmap.weibo, WeiboActivity.class));
        linkEntityList.add(new LinkEntity("Google ", R.mipmap.google_plus, GoogleActivity.class));
        linkEntityList.add(new LinkEntity("GoogleGame", R.mipmap.google_plus, GooglePlayActivity.class));
        linkEntityList.add(new LinkEntity("SelfBuild ", R.mipmap.ic_launcher, SelfBuildActivity.class));
        linkEntityList.add(new LinkEntity("Phone", R.mipmap.ic_launcher, PhoneActivity.class));
        linkEntityList.add(new LinkEntity("Email", R.mipmap.ic_launcher, EmailActivity.class));
        return linkEntityList;
    }

    private void updateDisplayName() {
        update(new UpdateCallback() {
            @Override
            public void onUpdate(String data) {
                if (agConnectAuth.getCurrentUser() != null) {
                    ProfileRequest userProfile = new ProfileRequest.Builder()
                        .setDisplayName(data)
                        .build();
                    agConnectAuth.getCurrentUser().updateProfile(userProfile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showUserInfo();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(AuthMainActivity.this, "update disolayname fail:" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }

    private void updateAvatar() {
        update(new UpdateCallback() {
            @Override
            public void onUpdate(String data) {
                if (agConnectAuth.getCurrentUser() != null) {
                    ProfileRequest userProfile = new ProfileRequest.Builder()
                        .setPhotoUrl(data)
                        .build();
                    agConnectAuth.getCurrentUser().updateProfile(userProfile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showUserInfo();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(AuthMainActivity.this, "update photoUrl fail:" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }
        });
    }

    private void update(final UpdateCallback callback) {
        LinearLayout layout = new LinearLayout(AuthMainActivity.this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText editText = new EditText(AuthMainActivity.this);
        layout.addView(editText);
        AlertDialog.Builder dialogBuilder =
            new AlertDialog.Builder(AuthMainActivity.this).setTitle("Update");
        dialogBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // input photoUrl or display name
                String data = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(data)) {
                    callback.onUpdate(data);
                }
            }
        });
        dialogBuilder.setView(layout);
        dialogBuilder.show();
    }

    interface UpdateCallback {
        void onUpdate(String data);
    }
}
