package io.digibyte.presenter.fragments;

import static io.digibyte.tools.animation.BRAnimator.animateBackgroundDim;
import static io.digibyte.tools.animation.BRAnimator.animateSignalSlide;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.digibyte.DigiByte;
import io.digibyte.R;
import io.digibyte.presenter.customviews.BRButton;
import io.digibyte.presenter.customviews.BRKeyboard;
import io.digibyte.presenter.customviews.BRLinearLayoutWithCaret;
import io.digibyte.tools.animation.BRAnimator;
import io.digibyte.tools.animation.SlideDetector;
import io.digibyte.tools.manager.BRClipboardManager;
import io.digibyte.tools.manager.BRSharedPrefs;
import io.digibyte.tools.qrcode.QRUtils;
import io.digibyte.tools.threads.BRExecutor;
import io.digibyte.tools.util.Utils;
import io.digibyte.wallet.BRWalletManager;

/**
 * BreadWallet
 * <p>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 6/29/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class FragmentReceive extends Fragment {
    private static final String TAG = FragmentReceive.class.getName();

    public TextView mTitle;
    public TextView mAddress;
    public ImageView mQrImage;
    public LinearLayout backgroundLayout;
    public LinearLayout signalLayout;
    private String receiveAddress;
    private View separator;
    private BRButton shareButton;
    private Button shareEmail;
    private Button shareTextMessage;
    private Button requestButton;
    private BRLinearLayoutWithCaret shareButtonsLayout;
    private BRLinearLayoutWithCaret copiedLayout;
    private boolean shareButtonsShown = false;
    private boolean isReceive;
    private ImageButton close;
    private Handler copyCloseHandler = new Handler();
    private BRKeyboard keyboard;
    private View separator2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive, container, false);
        mTitle = rootView.findViewById(R.id.title);
        mAddress = rootView.findViewById(R.id.address_text);
        mQrImage = rootView.findViewById(R.id.qr_image);
        backgroundLayout = rootView.findViewById(R.id.background_layout);
        signalLayout = rootView.findViewById(R.id.signal_layout);
        shareButton = rootView.findViewById(R.id.share_button);
        shareEmail = rootView.findViewById(R.id.share_email);
        shareTextMessage = rootView.findViewById(R.id.share_text);
        shareButtonsLayout = rootView.findViewById(R.id.share_buttons_layout);
        copiedLayout = rootView.findViewById(R.id.copied_layout);
        requestButton = rootView.findViewById(R.id.request_button);
        keyboard = rootView.findViewById(R.id.keyboard);
        keyboard.setBRButtonBackgroundResId(R.drawable.keyboard_white_button);
        keyboard.setBRKeyboardColor(R.color.white);
        separator = rootView.findViewById(R.id.separator);
        close = rootView.findViewById(R.id.close_button);
        separator2 = rootView.findViewById(R.id.separator2);
        separator2.setVisibility(View.GONE);
        setListeners();
        BRWalletManager.getInstance().addBalanceChangedListener(balance -> updateQr());
        signalLayout.removeView(shareButtonsLayout);
        signalLayout.removeView(copiedLayout);
        signalLayout.setLayoutTransition(BRAnimator.getDefaultTransition());
        signalLayout.setOnTouchListener(new SlideDetector(getContext(), signalLayout));
        return rootView;
    }


    private void setListeners() {
        shareEmail.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            String bitcoinUri = Utils.createBitcoinUrl(receiveAddress, 0, null, null, null);
            Uri qrImageUri = QRUtils.getQRImageUri(getContext(), bitcoinUri);
            QRUtils.share("mailto:", getActivity(), qrImageUri, null, null);

        });
        shareTextMessage.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            try {
                QRUtils.share("sms:", getActivity(), null, receiveAddress, "");
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        shareButton.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            shareButtonsShown = !shareButtonsShown;
            showShareButtons(shareButtonsShown);
        });
        mAddress.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            copyText();
        });
        requestButton.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            Activity app = getActivity();
            app.onBackPressed();
            BRAnimator.showRequestFragment(app, receiveAddress);

        });

        backgroundLayout.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            getActivity().onBackPressed();
        });
        mQrImage.setOnClickListener(v -> {
            if (!BRAnimator.isClickAllowed()) return;
            copyText();
        });

        close.setOnClickListener(v -> {
            Activity app = getActivity();
            if (app != null) {
                app.getFragmentManager().popBackStack();
            }
        });
    }

    private void showShareButtons(boolean b) {
        if (!b) {
            signalLayout.removeView(shareButtonsLayout);
            shareButton.setType(2);
        } else {
            signalLayout.addView(shareButtonsLayout,
                    isReceive ? signalLayout.getChildCount() - 2 : signalLayout.getChildCount());
            shareButton.setType(3);
            showCopiedLayout(false);
        }
    }

    private void showCopiedLayout(boolean b) {
        if (!b) {
            signalLayout.removeView(copiedLayout);
            copyCloseHandler.removeCallbacksAndMessages(null);
        } else {
            if (signalLayout.indexOfChild(copiedLayout) == -1) {
                signalLayout.addView(copiedLayout, signalLayout.indexOfChild(shareButton));
                showShareButtons(false);
                shareButtonsShown = false;
                copyCloseHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signalLayout.removeView(copiedLayout);
                    }
                }, 2000);
            } else {
                copyCloseHandler.removeCallbacksAndMessages(null);
                signalLayout.removeView(copiedLayout);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewTreeObserver observer = signalLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                observer.removeGlobalOnLayoutListener(this);
                animateBackgroundDim(backgroundLayout, false);
                animateSignalSlide(signalLayout, false, null);
            }
        });

        Bundle extras = getArguments();
        isReceive = extras.getBoolean("receive");
        if (!isReceive) {
            signalLayout.removeView(separator);
            signalLayout.removeView(requestButton);
            mTitle.setText(getString(R.string.UnlockScreen_myAddress));
        }

        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(() -> updateQr());

    }

    private void updateQr() {
        final Context ctx = getContext() == null ? DigiByte.getContext() : (Activity) getContext();
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(() -> {
            boolean success = BRWalletManager.refreshAddress(ctx);
            if (!success) {
                if (ctx instanceof Activity) {
                    BRExecutor.getInstance().forMainThreadTasks().execute(
                            () -> ((Activity) ctx).onBackPressed());
                }
                return;
            }
            BRExecutor.getInstance().forMainThreadTasks().execute(() -> {
                receiveAddress = BRSharedPrefs.getReceiveAddress(ctx);
                mAddress.setText(receiveAddress);
                boolean generated = QRUtils.generateQR(ctx, "digibyte:" + receiveAddress, mQrImage);
                if (!generated) {
                    throw new RuntimeException("failed to generate qr image for address");
                }
            });
        });
    }

    private void copyText() {
        BRClipboardManager.putClipboard(getContext(), mAddress.getText().toString());
        showCopiedLayout(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        animateBackgroundDim(backgroundLayout, true);
        animateSignalSlide(signalLayout, true, () -> {
            if (getActivity() != null) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}