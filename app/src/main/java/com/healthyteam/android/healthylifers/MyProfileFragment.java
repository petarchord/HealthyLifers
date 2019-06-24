package com.healthyteam.android.healthylifers;

import android.content.Context;
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
        txtChangePass = (TextView)fragment_layout.findViewById(R.id.textView_changePassMP);
        btnEdit = (Button)fragment_layout.findViewById(R.id.button_EditMP);
        btnOk = (Button)fragment_layout.findViewById(R.id.button_OkMP);
        btnCancel = (Button)fragment_layout.findViewById(R.id.button_CancelMP);
        etxtEditName = (EditText)fragment_layout.findViewById(R.id.EditText_NameMP);
        etxtEditSurname= (EditText)fragment_layout.findViewById(R.id.EditText_SurnameMP);
        layout_info = (LinearLayout) fragment_layout.findViewById(R.id.LinearLayout_UserInfo);
        layout_edit = (LinearLayout) fragment_layout.findViewById(R.id.LinearLayout_EditUserInfo);



        //layout_Info.setVisibility(View.VISIBLE);
        layout_edit.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new EditBtnListener());
        btnCancel.setOnClickListener(new CancleBtnListener());
        btnOk.setOnClickListener(new OkBtnListener());
        txtChangePass.setOnClickListener(new LtxtListener());

        String NameSurname= DomainController.getUser().getName() + " " + DomainController.getUser().getSurname();
        txtNameSurname.setText(NameSurname);
        txtUsername.setText(DomainController.getUser().getUsername());
        txtPoints.setText(DomainController.getUser().getPointsStirng());

        return fragment_layout;
    }

    class EditBtnListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            Context context = getContext();
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

            //otvori dialog box
        }

    }
}
