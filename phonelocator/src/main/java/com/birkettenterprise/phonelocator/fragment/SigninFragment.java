package com.birkettenterprise.phonelocator.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.birkettenterprise.phonelocator.R;

/**
 * Created by alex on 24/03/14.
 */
public class SigninFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static SigninFragment newInstance(int num) {
        SigninFragment f = new SigninFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin, container, false);

        // Watch for button clicks.
      /*  Button button = (Button)v.findViewById(R.id.sig);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });*/

        return v;
    }
}
