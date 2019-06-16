package com.healthyteam.android.healthylifers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthyteam.android.healthylifers.Domain.UserTest;

import org.w3c.dom.Text;

public class MyProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile,container,false);
        TextView txtNameSurname = view.findViewById(R.id.textView_NameSurnameMP);
        TextView txtUsername = view.findViewById(R.id.textView_UsernameMP);
        TextView txtPoints = view.findViewById(R.id.textView_PointsMP);
        Button btnEdit = view.findViewById(R.id.button_EditMP);
        Button btnOk = view.findViewById(R.id.button_OkMP);
        Button btnCancel = view.findViewById(R.id.button_CancelMP);
        LinearLayout layout_Info = (LinearLayout) view.findViewById(R.id.LinearLayout_UserInfo);
        LinearLayout layout_edit = (LinearLayout) view.findViewById(R.id.LinearLayout_EditUserInfo);

        //layout_Info.setVisibility(View.VISIBLE);
        layout_edit.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new EditBtnListener());
        btnCancel.setOnClickListener(new CancleBtnListener());
        btnOk.setOnClickListener(new OkBtnListener());

        String NameSurname=UserTest.getInstance().getName() + " " + UserTest.getInstance().getSurname();
        txtNameSurname.setText(NameSurname);
        txtUsername.setText(UserTest.getInstance().getUsername());
        txtPoints.setText(UserTest.getInstance().getPoints().toString());

        return view;
    }

    class EditBtnListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_my_profile, null);
            LinearLayout layout_info = (LinearLayout) view.findViewById(R.id.LinearLayout_UserInfo);
            LinearLayout layout_edit = (LinearLayout) view.findViewById(R.id.LinearLayout_EditUserInfo);
            EditText edit_name = view.findViewById(R.id.EditText_NameMP);
            EditText edit_surname = view.findViewById(R.id.EditText_SurnameMP);
            TextView Ltxt_ChangePass = (TextView) view.findViewById(R.id.textView_changePassMP);

            edit_name.setText(UserTest.getInstance().getName());
            edit_surname.setText(UserTest.getInstance().getName());
            Ltxt_ChangePass.setOnClickListener(new LtxtListener());

            layout_info.setVisibility(View.GONE);
            layout_info.invalidate();
            layout_edit.setVisibility(View.VISIBLE);
            layout_edit.invalidate();


        }
    }

    class OkBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_my_profile, null);
            LinearLayout layout_Info = view.findViewById(R.id.LinearLayout_UserInfo);
            LinearLayout layout_edit = view.findViewById(R.id.LinearLayout_EditUserInfo);


            //cuvaj izmene

            //osvezi prikaz
            layout_edit.setVisibility(View.GONE);
            layout_Info.setVisibility(View.VISIBLE);
        }
    }

    class CancleBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_my_profile, null);
            LinearLayout layout_Info = view.findViewById(R.id.LinearLayout_UserInfo);
            LinearLayout layout_edit = view.findViewById(R.id.LinearLayout_EditUserInfo);

            layout_edit.setVisibility(View.GONE);
            layout_Info.setVisibility(View.VISIBLE);
        }

    }
    class LtxtListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.fragment_my_profile, null);
            //otvori dialog box
        }

    }
}
