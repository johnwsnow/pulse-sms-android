package xyz.klinker.messenger.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;

import xyz.klinker.messenger.R;
import xyz.klinker.messenger.shared.util.DensityUtil;

public class AccountPurchaseActivity extends AppCompatActivity {

    private boolean isInitial = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_purchase);
        setUpInitialLayout();

        new Handler().postDelayed(this::circularRevealIn, 100);
    }

    private void setUpInitialLayout() {
        findViewById(R.id.try_it).setOnClickListener(view -> tryIt());

        long startTime = 500;
        quickViewReveal(findViewById(R.id.icon_watch), startTime);
        quickViewReveal(findViewById(R.id.icon_tablet), startTime + 75);
        quickViewReveal(findViewById(R.id.icon_computer), startTime + 150);
        quickViewReveal(findViewById(R.id.icon_phone), startTime + 225);
        quickViewReveal(findViewById(R.id.icon_notify), startTime + 300);
    }

    protected void tryIt() {
        slidePurchaseOptionsIn();

        // set up purchasing views here
    }

    private void circularRevealIn() {
        View view = findViewById(R.id.initial_layout);
        view.setVisibility(View.VISIBLE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int cx = view.getWidth() / 2;
                int cy = view.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius).start();
            } else {
                view.setAlpha(0f);
                view.animate().alpha(1f).start();
            }
        } catch (Exception e) {
            finish();
        }
    }

    private void circularRevealOut() {
        final View view = findVisibleHolder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = view.getWidth() / 2;
            int cy = view.getHeight() / 2;
            float initialRadius = (float) Math.hypot(cx, cy);
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                    close();
                }
            });

            anim.start();
        } else {
            view.animate().alpha(0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    close();
                }
            }).start();
        }
    }

    private void quickViewReveal(View view, long delay) {
        view.setTranslationX(-1 * DensityUtil.toDp(this, 16));
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .translationX(0)
                .alpha(1f)
                .setStartDelay(delay)
                .start();
    }

    private void slidePurchaseOptionsIn() {
        slideIn(findViewById(R.id.purchase_layout));
    }

    private void slideIn(View view) {
        isInitial = false;
        final View initial = findViewById(R.id.initial_layout);

        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.setTranslationX(view.getWidth());
        view.animate()
                .alpha(1f)
                .translationX(0)
                .setListener(null)
                .start();

        initial.animate()
                .alpha(0f)
                .translationX(-1 * initial.getWidth())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        initial.setVisibility(View.INVISIBLE);
                        initial.setTranslationX(0);
                    }
                }).start();
    }

    private void slideOut() {
        isInitial = true;
        final View visible = findVisibleHolder();
        View initial = findViewById(R.id.initial_layout);

        visible.animate()
                .alpha(0f)
                .translationX(visible.getWidth())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        visible.setVisibility(View.INVISIBLE);
                        visible.setTranslationX(0);
                    }
                }).start();

        initial.setVisibility(View.VISIBLE);
        initial.setAlpha(0f);
        initial.setTranslationX(-1 * initial.getWidth());
        initial.animate()
                .alpha(1f)
                .translationX(0)
                .setListener(null)
                .start();
    }

    private View findVisibleHolder() {
        View initial = findViewById(R.id.initial_layout);
        View purchase = findViewById(R.id.purchase_layout);

        if (initial.getVisibility() != View.INVISIBLE) {
            return initial;
        } else {
            return purchase;
        }
    }

    @Override
    public void onBackPressed() {
        if (isInitial) {
            circularRevealOut();
        } else {
            slideOut();
        }
    }

    protected void close() {
        finish();
        overridePendingTransition(0, 0);
    }
}