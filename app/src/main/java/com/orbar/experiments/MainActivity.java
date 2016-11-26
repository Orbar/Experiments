package com.orbar.experiments;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewEditorActionEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.double_tap_edit)
    EditText mDoubleTapEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mDoubleTapEdit.setTag(mDoubleTapEdit.getKeyListener());
        mDoubleTapEdit.setKeyListener(null);

        Observable<Void> observable = RxView.clicks(mDoubleTapEdit).share();
        observable.buffer(observable.debounce(200, TimeUnit.MILLISECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .filter(list -> list.size() >= 2)
                .filter(list -> !mDoubleTapEdit.isSelected())
                .subscribe(voids -> {
                    Toast.makeText(this, "Double Click", Toast.LENGTH_SHORT).show();

                    enableEditing(!mDoubleTapEdit.isSelected());
                }, Actions.empty());

        RxTextView.editorActionEvents(mDoubleTapEdit)
                .filter(event -> event.actionId() == KeyEvent.ACTION_DOWN)
                .filter(event -> event.keyEvent().getKeyCode() == (KeyEvent.KEYCODE_ENTER))
                .subscribe(event -> {
                    enableEditing(false);
                });
    }

    private void enableEditing(boolean selected) {
        if (selected) {
            mDoubleTapEdit.setSelected(true);
            mDoubleTapEdit.setKeyListener((KeyListener) mDoubleTapEdit.getTag());

            //move cursor to end
            mDoubleTapEdit.setSelection(mDoubleTapEdit.getText().length());

            //enable edittext
            mDoubleTapEdit.setCursorVisible(true);
            mDoubleTapEdit.setFocusable(true);
            mDoubleTapEdit.setFocusableInTouchMode(true);

            // request focus
            mDoubleTapEdit.requestFocus();

            //show keyboard
        } else {
            mDoubleTapEdit.setSelected(false);

            mDoubleTapEdit.setKeyListener(null);

            mDoubleTapEdit.setCursorVisible(false);
            mDoubleTapEdit.setFocusable(false);
            mDoubleTapEdit.setFocusableInTouchMode(false);
        }
    }
}
