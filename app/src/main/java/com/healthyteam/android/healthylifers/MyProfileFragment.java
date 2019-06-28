package com.healthyteam.android.healthylifers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.User;

import org.w3c.dom.Text;

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
    Dialog dialogChangePass;
    Button dialogBtnOk;
    Button dialogBtnCancel;
    EditText etxtDialogOldPass;
    EditText etxtDialogNewPass;
    EditText etxtDialogConfirmPass;
    TextView dialogTxtError;
    private static MyProfileFragment instance;

    public static MyProfileFragment getInstance(){
        if(instance==null)
            instance=new MyProfileFragment();
        return instance;
    }


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragment_layout = inflater.inflate(R.layout.fragment_my_profile,container,false);
        txtNameSurname = (TextView)fragment_layout.findViewById(R.id.textView_NameSurnameMP);
        txtUsername = (TextView)fragment_layout.findViewById(R.id.textView_UsernameMP);
        txtPoints = (TextView)fragment_layout.findViewById(R.id.textView_PointsMP);
        //TODO: set Text View for pass change to be like hyperlink
        txtChangePass = (TextView)fragment_layout.findViewById(R.id.textView_changePassMP);
        btnEdit = (Button)fragment_layout.findViewById(R.id.button_EditMP);
        btnOk = (Button)fragment_layout.findViewById(R.id.button_OkMP);
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

        return fragment_layout;
    }

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
            //update userInfo
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
