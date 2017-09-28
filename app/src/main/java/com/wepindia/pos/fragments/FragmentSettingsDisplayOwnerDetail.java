package com.wepindia.pos.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wep.common.app.Database.DatabaseHandler;

import com.wepindia.pos.R;

public class FragmentSettingsDisplayOwnerDetail extends Fragment {

    private TextView Name,Phone,Email,Address, POS, IsMainOffice;
    private EditText mId, appId, BillNoPrefix,RefernceNo,Gstin;
    private DatabaseHandler dbHelper;
    Spinner spinner1, spinner2;
    Context myContext;
    Button btnClose, btnApply;

    private final int CHECK_INTEGER_VALUE = 0;
    private final int CHECK_DOUBLE_VALUE = 1;
    private final int CHECK_STRING_VALUE = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_settings_display_owner_detail, container, false);
        myContext = getActivity();
        initialseViewVariablesAndDisplay(view);
        return view;
    }

    private void initialseViewVariablesAndDisplay(View view)
    {

        dbHelper = new DatabaseHandler(myContext);
        btnClose=(Button)view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close(v);
            }
        });
        btnApply=(Button)view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply(v);
            }
        });
        Name=(TextView)view.findViewById(R.id.ownerName);
        Gstin=(EditText) view.findViewById(R.id.ownerGstin);
        RefernceNo=(EditText) view.findViewById(R.id.ownerReferenceNo);
        Phone=(TextView)view.findViewById(R.id.ownerContact);
        Email=(TextView)view.findViewById(R.id.ownerEmail);
        POS=(TextView) view.findViewById(R.id.ownerPos);
        IsMainOffice=(TextView) view.findViewById(R.id.ownerOffice);
        /*spinner1=(Spinner)view.findViewById(R.id.ownerPos);
        spinner2=(Spinner)view.findViewById(R.id.ownerOffice);
        spinner2.setSelection(1);*/
        Address=(TextView)view.findViewById(R.id.ownerAddress);
        mId =(EditText) view.findViewById(R.id.OwnerDetail_mId);
        appId =(EditText) view.findViewById(R.id.OwnerDetail_appId);
        BillNoPrefix =(EditText) view.findViewById(R.id.ownerBillPrefix);

        loadOwnerDetail();
    }
    private void loadOwnerDetail()
    {
        dbHelper.CreateDatabase();
        dbHelper.OpenDatabase();
        Cursor cursor = dbHelper.getOwnerDetail();
        if(cursor!=null && cursor.moveToFirst())
        {
            String name = cursor.getString(cursor.getColumnIndex("OwnerName"));
            String gstin = cursor.getString(cursor.getColumnIndex("GSTIN"));
            String refernceNo = cursor.getString(cursor.getColumnIndex("refNo"));
            String phone = cursor.getString(cursor.getColumnIndex("Phone"));
            String email = cursor.getString(cursor.getColumnIndex("Email"));
            String address = cursor.getString(cursor.getColumnIndex("Address"));
            String pos = cursor.getString(cursor.getColumnIndex("POS"));
            String mainOffice = cursor.getString(cursor.getColumnIndex("IsMainOffice"));
            String BillNoPrefix_str = cursor.getString(cursor.getColumnIndex("BillNoPrefix"));
            if(BillNoPrefix_str == null)
                BillNoPrefix_str = "";

            Name.setText(name);
            Gstin.setText(gstin);
            RefernceNo.setText(refernceNo);
            Phone.setText(phone);
            Email.setText(email);
            Address.setText(address);
            BillNoPrefix.setText(BillNoPrefix_str);
            POS.setText(pos);
            IsMainOffice.setText(mainOffice);

        }
        // dbHelper.close();
    }
    private int getIndex_pos(String substring){

        int index = 0;
        for (int i = 0; index==0 && i < spinner1.getCount(); i++){

            if (spinner1.getItemAtPosition(i).toString().contains(substring)){
                index = i;
            }

        }

        return index;

    }
    private void close(View v)
    {
        dbHelper.CloseDatabase();
        getActivity().finish();
    }
    private void apply(View v)
    {
        String billPrefix = BillNoPrefix.getText().toString().trim();
        String gstin = Gstin.getText().toString().trim().toUpperCase();
        if (!Gstin.getText().toString().trim().toUpperCase().equals("") && Gstin.getText().toString().trim().toUpperCase().length()!=15)
        {
            Toast.makeText(myContext, "GSTIN can either be empty or of 15 characters", Toast.LENGTH_SHORT);
            return;
        }if(!checkGSTINValidation(gstin))
        {
            Toast.makeText(myContext, "Invalid GSTIN", Toast.LENGTH_SHORT);
            return;
        }
        String referenceNo = RefernceNo.getText().toString().trim();
        dbHelper.CreateDatabase();
        dbHelper.OpenDatabase();
        int ll = dbHelper.updateOwnerDetails(billPrefix,gstin,referenceNo);
        if(ll>0)
            Toast.makeText(myContext, "Details updated successfully", Toast.LENGTH_SHORT).show();
    }

    private boolean checkGSTINValidation(String str )
    {
        boolean mFlag = false;
        try {
            if(str.trim().length() == 0)
            {mFlag = true;}
            else if (str.trim().length() > 0 && str.length() == 15) {
                String[] part = str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                if (CHECK_INTEGER_VALUE == checkDataypeValue(part[0], "Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[1],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[2],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[3],"String")
                        && CHECK_INTEGER_VALUE == checkDataypeValue(part[4],"Int")
                        && CHECK_STRING_VALUE == checkDataypeValue(part[5],"String")
                        && (CHECK_INTEGER_VALUE == checkDataypeValue(str.substring(14),"Int") ||
                        CHECK_STRING_VALUE == checkDataypeValue(str.substring(14),"String"))) {

                    mFlag = true;
                } else {
                    mFlag = false;
                }
            } else {
                mFlag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFlag = false;
        }

        return mFlag;
    }

    public static int checkDataypeValue(String value, String type) {
        int flag =0;
        try {
            switch(type) {
                case "Int":
                    Integer.parseInt(value);
                    flag = 0;
                    break;
                case "Double" : Double.parseDouble(value);
                    flag = 1;
                    break;
                default : flag =2;
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            flag = -1;
        }
        return flag;
    }
}
