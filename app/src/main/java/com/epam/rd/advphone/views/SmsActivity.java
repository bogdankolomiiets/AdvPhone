package com.epam.rd.advphone.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.epam.rd.advphone.R;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.adapters.ConcreteSmsRecyclerViewAdapter;
import com.epam.rd.advphone.databinding.ActivitySmsBinding;
import com.epam.rd.advphone.models.Sms;
import com.epam.rd.advphone.viewmodels.PickContactViewModel;
import com.epam.rd.advphone.viewmodels.SmsViewModel;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.epam.rd.advphone.Constants.CONTACT_NAME;
import static com.epam.rd.advphone.Constants.CONTACT_NUMBER;

public class SmsActivity extends AppCompatActivity {
    public static final int MAX_SMS_ASCII_CHARACTER_LENGTH = 918;
    public static final int MAX_SMS_UNICODE_CHARACTER_LENGTH = 402;
    public static final int MAX_SMS_COUNT = 6;
    public static final int MAX_UNICODE_SINGLE_SMS_LENGTH = 70;
    public static final int MAX_UNICODE_MULTIPLE_SMS_LENGTH = 67;
    public static final int MAX_ASCII_SINGLE_SMS_LENGTH = 160;
    public static final int MAX_ASCII_MULTIPLE_SMS_LENGTH = 153;

    private ActivitySmsBinding smsBinding;
    private TextView smsTextLength;
    private TextView smsCount;
    private EditText smsText;
    private ImageButton sendSmsBtn;
    private Map<String, String> recipients;
    private SmsManager smsManager;
    private ArrayList<String> smsArrayList;
    private int singleSmsLength;
    private final int MAX_RECIPIENT_COUNT = 5;
    private SmsViewModel smsViewModel;
    private LayoutInflater inflater;
    private FlexboxLayout recipientsContainer;
    private ConcreteSmsRecyclerViewAdapter concreteSmsRecyclerViewAdapter;
    private InputMethodManager imm;
    private YoYo.YoYoString yoYoString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smsBinding = DataBindingUtil.setContentView(this, R.layout.activity_sms);
        smsTextLength = smsBinding.smsTextLength;
        recipientsContainer = smsBinding.recipientsContainer;
        smsCount = smsBinding.smsCount;
        smsText = smsBinding.smsText;
        sendSmsBtn = smsBinding.sendSmsBtn;
        smsManager = SmsManager.getDefault();
        smsArrayList = new ArrayList<>();
        inflater = getLayoutInflater();
        concreteSmsRecyclerViewAdapter = new ConcreteSmsRecyclerViewAdapter();
        imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);

        smsViewModel = ViewModelProviders.of(this).get(SmsViewModel.class);
        smsViewModel.getSmsByRecipient().observe(this, concreteSms -> concreteSmsRecyclerViewAdapter.setSmsList(concreteSms));

        initRecipients();

        setupTextChangedListener();

        setupSendSmsBtnOnClickListener();

        initConcreteSmsRecyclerView();
    }

    private void initConcreteSmsRecyclerView() {
        RecyclerView concreteSmsRecyclerView = smsBinding.concreteSmsRecyclerView;
        concreteSmsRecyclerView.setHasFixedSize(true);
        concreteSmsRecyclerView.setAdapter(concreteSmsRecyclerViewAdapter);
        concreteSmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSendSmsBtnOnClickListener() {
        sendSmsBtn.setOnClickListener(view -> {
            if (recipients.size() > 0) {
                if (smsText.length() > 0) {
                    sendSms();
                    showToast(R.string.message_sent);
                    smsText.setText("");
                    smsArrayList.clear();

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(smsText.getWindowToken(), 0);
                    }
                    YoYo.with(Techniques.BounceInUp).playOn(sendSmsBtn);
                } else {
                    showToast(R.string.empty_message);
                    YoYo.with(Techniques.Shake).playOn(sendSmsBtn);
                }
            } else {
                startAddButtonAnimation();
            }
        });
    }

    private void setupTextChangedListener() {
        smsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //stub
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = charSequence.toString();

                smsArrayList = smsManager.divideMessage(text);

                if (smsArrayList.size() <= 1) {
                    singleSmsLength = isASCII(text) ? MAX_ASCII_SINGLE_SMS_LENGTH : MAX_UNICODE_SINGLE_SMS_LENGTH;
                } else {
                    singleSmsLength = isASCII(text) ? MAX_ASCII_MULTIPLE_SMS_LENGTH : MAX_UNICODE_MULTIPLE_SMS_LENGTH;
                }

                smsCount.setText(String.valueOf(smsArrayList.size()));

                int lastIndex = smsArrayList.size() - 1;
                smsTextLength.setText(String.valueOf(lastIndex != -1 ? singleSmsLength - smsArrayList.get(lastIndex).length() : 0));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //stub
            }
        });
    }

    private boolean isASCII(String text) {
        return text.matches("\\A\\p{ASCII}*\\z");
    }

    private void initRecipients() {
        recipients = new HashMap<>();

        String firstRecipientNumber = getIntent().getStringExtra(CONTACT_NUMBER);
        String firstRecipientName = getIntent().getStringExtra(CONTACT_NAME);
        if (firstRecipientNumber != null) {
            smsViewModel.setRecipientNumber(firstRecipientNumber);
            recipients.put(firstRecipientNumber, firstRecipientName);
            createTextViewForRecipients(firstRecipientNumber, firstRecipientName);
        } else {
            initAddContactButton();
        }
    }

    private void createTextViewForRecipients(String recipientNumber, String recipientName) {
        View recipientView = inflater.inflate(R.layout.single_recipient_item, smsBinding.recipientsContainer, false);
        TextView recipientId = recipientView.findViewById(R.id.recipientId);
        recipientId.setText(recipientName != null ? recipientName : recipientNumber);
        recipientView.setOnClickListener(view -> {
            recipients.remove(recipientNumber);
            recipientsContainer.removeView(recipientView);
            startAddButtonAnimation();
        });
        recipientsContainer.addView(recipientView);
        initAddContactButton();
    }

    private void initAddContactButton() {
        int recipientsSize = recipients.size();
        View pickNewRecipientBnt = inflater.inflate(R.layout.pick_new_recipient_button, smsBinding.recipientsContainer, false);
        pickNewRecipientBnt.setTag(R.string.pick_new_recipient_bnt_tag);
        pickNewRecipientBnt.setOnClickListener(v -> {
            //stopping button animation
            if (yoYoString != null && yoYoString.isRunning()) {
                yoYoString.stop();
            }

            if (recipientsSize >= MAX_RECIPIENT_COUNT) {
                showAlertDialog();
            } else {
                Intent intent = new Intent(SmsActivity.this, PickContactActivity.class);
                startActivityForResult(intent, RequestCodes.REQUEST_PICK_CONTACT);
            }
        });

        if (recipientsSize > 0) {
            recipientsContainer.removeView(recipientsContainer.findViewWithTag(R.string.pick_new_recipient_bnt_tag));
        }
        recipientsContainer.addView(pickNewRecipientBnt);

        startAddButtonAnimation();
    }

    private void startAddButtonAnimation() {
        if (recipients.size() == 0) {
            //reset animation if it's started
            if (yoYoString != null && yoYoString.isRunning()) {
                yoYoString.stop();
                yoYoString.stop(true);
            }

            TextView noRecipientTv = new TextView(this);
            noRecipientTv.setText(getString(R.string.no_recipient, "   "));
            noRecipientTv.setTextSize(18);
            yoYoString = YoYo.with(Techniques.Tada).pivot(10, 10).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    yoYoString = YoYo.with(Techniques.Flash).withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            noRecipientTv.setVisibility(View.GONE);
                            recipientsContainer.removeView(noRecipientTv);
                        }
                    }).playOn(recipientsContainer);
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    recipientsContainer.addView(noRecipientTv);
                }
            }).repeat(5).playOn(recipientsContainer);
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(R.string.max_count_title)
                .setMessage(R.string.max_count_text)
                .setPositiveButton(R.string.ok_btn, (dialogInterface, i) -> {
                    //stub
                }).create().show();
    }

    private void sendSms() {
        for (Map.Entry<String, String> entry : recipients.entrySet()) {
//            if (smsArrayList.size() > 1) {
//                smsManager.sendMultipartTextMessage(entry.getKey(), null, smsArrayList, null, null);
//            } else {
//                smsManager.sendTextMessage(entry.getKey(), null, smsArrayList.get(0), null, null);
//            }
            Sms newSms = new Sms(smsText.getText().toString(), entry.getKey(), entry.getValue(), System.currentTimeMillis());
            Sms newSms2 = new Sms("Hello", entry.getKey(), entry.getValue(), System.currentTimeMillis());
            newSms2.setRecipient(true);
            Sms newSms3 = new Sms("How are you?", entry.getKey(), entry.getValue(), System.currentTimeMillis());
            newSms3.setRecipient(true);
            Sms newSms4 = new Sms("Not bad", entry.getKey(), entry.getValue(), System.currentTimeMillis());
            newSms4.setRecipient(true);
            ViewModelProviders.of(this).get(PickContactViewModel.class).insertNewSms(newSms);
            ViewModelProviders.of(this).get(PickContactViewModel.class).insertNewSms(newSms2);
            ViewModelProviders.of(this).get(PickContactViewModel.class).insertNewSms(newSms3);
            ViewModelProviders.of(this).get(PickContactViewModel.class).insertNewSms(newSms4);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RequestCodes.REQUEST_PICK_CONTACT) {
            if (resultCode == RESULT_OK && data != null) {
                String recipientNumber = data.getStringExtra(CONTACT_NUMBER);
                String recipientName = data.getStringExtra(CONTACT_NAME);

                //if map of recipients contains value then we don't create new View
                if (recipients.put(recipientNumber, recipientName) == null) {
                    createTextViewForRecipients(recipientNumber, recipientName);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showToast(int resourceId) {
        Toast.makeText(SmsActivity.this, resourceId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
