package com.example.mydeal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InputFragment extends DialogFragment {
    private static final String PROMPT_KEY = "prompt_key";

    private InputFragmentListener mListener;
    private EditText etLink;

    public static InputFragment newInstance(String prompt) {

        Bundle args = new Bundle();
        args.putString(PROMPT_KEY , prompt);

        InputFragment fragment = new InputFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof InputFragmentListener){
            mListener = (InputFragmentListener) context;
        }else{throw new ClassCastException("Caller must implement Listener");}
    }
    public interface InputFragmentListener {
        void onInputFragmentListenerOK(String link, String tag);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.input_fragment_dialog, container, false);

        String prompt = getArguments().getString(PROMPT_KEY);

        TextView tvPrompt = (TextView) view.findViewById(R.id.tvPrompt);
        tvPrompt.setText(prompt);

        etLink = (EditText) view.findViewById(R.id.etLink);

        Button btnOk = (Button) view.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = etLink.getText().toString();
                //if (validate(classname,classcode, classcode02)){
                 mListener.onInputFragmentListenerOK(linkText , getTag());
                //    }
                dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;

        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
