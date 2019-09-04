package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;
import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.TestFunctions;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment {
    View fragment_layout;
    ImageView ProfilePic;
    LinearLayout layout_info;
    TextView txtNameSurname;
    TextView txtUsername;
    TextView txtPoints;
    Button btnEdit;
    LinearLayout layout_edit;
    EditText etxtEditName;
    EditText etxtEditSurname;
    TextView txtChangePass;
    Button btnOk;
    Button btnCancel;
    ImageButton btnTakePic;
    ImageButton btnChoosePic;
    Dialog dialogChangePass;
    Button dialogBtnOk;
    Button dialogBtnCancel;
    EditText etxtDialogOldPass;
    EditText etxtDialogNewPass;
    EditText etxtDialogConfirmPass;
    TextView dialogTxtError;
    Uri mImageUri;
    private static MyProfileFragment instance;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    public static MyProfileFragment getInstance(){
        if(instance==null)
            instance=new MyProfileFragment();
        return instance;
    }
    public static void Restart(){
        instance=null;
    }


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragment_layout = inflater.inflate(R.layout.fragment_my_profile,container,false);
        txtNameSurname = (TextView)fragment_layout.findViewById(R.id.textView_NameSurnameMP);
        txtUsername = (TextView)fragment_layout.findViewById(R.id.textView_UsernameMP);
        txtPoints = (TextView)fragment_layout.findViewById(R.id.textView_PointsMP);
        ProfilePic = fragment_layout.findViewById(R.id.imageView_ProfilePicMP);
        //TODO: set Text View for pass change to be like hyperlink
        txtChangePass = (TextView)fragment_layout.findViewById(R.id.textView_changePassMP);
        btnEdit = (Button)fragment_layout.findViewById(R.id.button_EditMP);
        btnOk = (Button)fragment_layout.findViewById(R.id.button_OkMP);
        btnTakePic = fragment_layout.findViewById(R.id.button_TakePicMP);
        btnChoosePic = fragment_layout.findViewById(R.id.button_ChoosePicMP);
        btnCancel = (Button)fragment_layout.findViewById(R.id.button_CancelMP);
        etxtEditName = (EditText)fragment_layout.findViewById(R.id.EditText_NameMP);
        etxtEditSurname= (EditText)fragment_layout.findViewById(R.id.EditText_SurnameMP);
        layout_info = (LinearLayout) fragment_layout.findViewById(R.id.LinearLayout_UserInfo);
        layout_edit = (LinearLayout) fragment_layout.findViewById(R.id.LinearLayout_EditUserInfo);
        dialogChangePass= new Dialog(getContext());
        dialogChangePass.setContentView(R.layout.dialog_change_password);
        dialogChangePass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBtnOk=dialogChangePass.findViewById(R.id.button_okDCP);
        dialogBtnCancel=dialogChangePass.findViewById(R.id.button_cancelDCP);
        dialogTxtError=dialogChangePass.findViewById(R.id.TextView_ErrorDCP);
        etxtDialogOldPass=dialogChangePass.findViewById(R.id.EditText_oldPassDCP);
        etxtDialogNewPass=dialogChangePass.findViewById(R.id.EditText_newPassDCP);
        etxtDialogConfirmPass=dialogChangePass.findViewById(R.id.EditText_confirmPassDCP);

        layout_edit.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new EditBtnListener());
        btnCancel.setOnClickListener(new CancleBtnListener());
        btnOk.setOnClickListener(new OkBtnListener());
        txtChangePass.setOnClickListener(new LtxtListener());
        dialogChangePass.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogTxtError.setVisibility(View.GONE);
                etxtDialogConfirmPass.getText().clear();
                etxtDialogNewPass.getText().clear();
                etxtDialogOldPass.getText().clear();

            }
        });

        dialogBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass= etxtDialogNewPass.getText().toString();
                String confirmPass= etxtDialogConfirmPass.getText().toString();
                //check old pass

                if(!newPass.equals(confirmPass)){
                    dialogTxtError.setText(getString(R.string.passError_matchPass));
                    dialogTxtError.setVisibility(View.VISIBLE);
                    return;
                }
                //update database
                dialogChangePass.cancel();
            }
        });
        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        //TODO: change this with camera access function
        btnTakePic.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                            //  setRadnomImageUri();
                                              openCamera();

                                          }});
        dialogBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass.cancel();
            }
        });
        String NameSurname= DomainController.getUser().getName() + " " + DomainController.getUser().getSurname();
        txtNameSurname.setText(NameSurname);
        txtUsername.setText(DomainController.getUser().getUsername());
        txtPoints.setText(DomainController.getUser().getPointsStirng());
        if(DomainController.getUser().getImageUrl()!=null) {
            Picasso.get().load(DomainController.getUser().getImageUrl()).into(ProfilePic);
        }
        else
            ProfilePic.setImageResource(R.drawable.profile_picture);


        return fragment_layout;
    }
    void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    void openCamera()
    {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(imageTakeIntent.resolveActivity(getContext().getPackageManager()) != null)
        {
            startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Log.i("onChoosePic:", mImageUri.getPath());
            mImageUri = data.getData();
            ProfilePic.setImageURI(mImageUri);
            DomainController.getUser().UpdatePicture(mImageUri, new OnUploadDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode ==RESULT_OK)
        {
            Uri imageUri = data.getData();
            Log.i("IMAGE URI:",imageUri.toString());
        //    Bundle extras = data.getExtras();
        //    Log.i("BUNDLE EXTRAS:",extras.toString());
         //   Bitmap imageBitmap = (Bitmap) extras.get("data");
        //    ProfilePic.setImageBitmap(imageBitmap);
            ProfilePic.setImageURI(imageUri);
            DomainController.getUser().UpdatePicture(imageUri, new OnUploadDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //region test
    void setRadnomImageUri(){
        int num = TestFunctions.randBetween(1,36);
        File avatarFile = new File(Environment.getExternalStorageDirectory() + "/Avatar/" + num +".png");
        Log.i("beforeScan", avatarFile.getPath());
        MediaScannerConnection.scanFile(getContext(),
                new String[] { avatarFile.getAbsolutePath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("onScanCompleted", uri.getPath());
                        mImageUri = uri;
                        ProfilePic.setImageURI(mImageUri);
                        boolean fileSelected = DomainController.getUser().UpdatePicture(mImageUri, new OnUploadDataListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onSuccess() {
                                Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailed(Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (!fileSelected)
                            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //endregion
    class EditBtnListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            etxtEditName.setText(DomainController.getUser().getName());
            etxtEditSurname.setText(DomainController.getUser().getSurname());

            layout_info.setVisibility(View.GONE);
            layout_info.invalidate();
            layout_edit.setVisibility(View.VISIBLE);
            layout_edit.invalidate();


        }
    }

    class OkBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            DomainController.getUser().setName(etxtEditName.getText().toString());
            DomainController.getUser().setSurname(etxtEditSurname.getText().toString());
            //TODO: update userInfo
            String NameSurname= DomainController.getUser().getName() + " " + DomainController.getUser().getSurname();
            txtNameSurname.setText(NameSurname);

            layout_edit.setVisibility(View.GONE);
            layout_info.setVisibility(View.VISIBLE);
        }
    }

    class CancleBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //cuvaj izmene

            //osvezi prikaz
            layout_edit.setVisibility(View.GONE);
            layout_info.setVisibility(View.VISIBLE);
        }

    }
    class LtxtListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            dialogChangePass.show();
        }

    }
}
