package me.hanqin.demos.email;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MailClientConfigDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Credentials for mail service");
        View inflate = inflater.inflate(R.layout.config_dialog, container);

        inflate.findViewById(R.id.send_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = getTextFrom(view.getRootView(), R.id.username);
                final String password = getTextFrom(view.getRootView(), R.id.password);
                MailUtility.sendMailWithJavaApi(getActivity(), username, password, "Hello Android", "Hello Email");
            }
        });
        return inflate;
    }

    private String getTextFrom(View inflate, int textViewId) {
        EditText editText = (EditText) inflate.findViewById(textViewId);
        return editText.getText().toString();
    }
}
