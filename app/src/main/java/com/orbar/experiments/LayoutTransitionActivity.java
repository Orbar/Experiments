package com.orbar.experiments;

import android.animation.LayoutTransition;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

public class LayoutTransitionActivity extends AppCompatActivity {

    @BindView(R.id.text1)
    TextView mTextView1;

    @BindView(R.id.text2)
    TextView mTextView2;

    @BindView(R.id.text3)
    TextView mTextView3;

    @BindView(R.id.inner_layout)
    LinearLayout mInnerLayout;

    @BindView(R.id.outer_layout)
    LinearLayout mOuterLayout;


    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_transition);

        ButterKnife.bind(this);

        mOuterLayout.setLayoutTransition(new OuterLayoutTranslation());
        mInnerLayout.setLayoutTransition(new InnerLayoutTranslation());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSubscriptions.add(
                RxView.clicks(mTextView1)
                        .subscribe(mTextView1 -> {
                            if (mTextView2.getVisibility() == View.GONE) {
                                mTextView2.setVisibility(View.VISIBLE);
                            } else {
                                mTextView2.setVisibility(View.GONE);
                            }
                        })
        );
    }

    @Override
    protected void onPause() {
        mSubscriptions.clear();
        super.onPause();
    }

    public class InnerLayoutTranslation extends LayoutTransition {
        public InnerLayoutTranslation() {
            super();
            setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
            setDuration(2000);
        }
    }

    public class OuterLayoutTranslation extends LayoutTransition {
        public OuterLayoutTranslation() {
            super();
            enableTransitionType(LayoutTransition.CHANGING);
            setDuration(2000);
        }
    }
}
