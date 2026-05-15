package com.restaurant.pos.activities;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.restaurant.pos.R;
import com.restaurant.pos.api.ERPNextService;
import com.restaurant.pos.models.PaymentEntry;
import com.restaurant.pos.utils.CartManager;
import com.restaurant.pos.utils.CurrencyUtils;
public class PaymentActivity extends AppCompatActivity {
    public static final String EXTRA_TOTAL="total_amount";
    public static final String EXTRA_INVOICE="invoice_name";
    private TextView tvTotal,tvChange,tvTendered;
    private EditText etTendered,etReference;
    private Button btnConfirm;
    private RadioGroup rgPaymentMethod;
    private ProgressBar progressBar;
    private ERPNextService service;
    private double totalAmount;
    private String invoiceName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        service=new ERPNextService(this);
        totalAmount=getIntent().getDoubleExtra(EXTRA_TOTAL,0);
        invoiceName=getIntent().getStringExtra(EXTRA_INVOICE);

        tvTotal=findViewById(R.id.tvTotal);
        tvChange=findViewById(R.id.tvChange);
        etTendered=findViewById(R.id.etTendered);
        etReference=findViewById(R.id.etReference);
        btnConfirm=findViewById(R.id.btnConfirm);
        rgPaymentMethod=findViewById(R.id.rgPaymentMethod);
        progressBar=findViewById(R.id.progressBar);

        tvTotal.setText(CurrencyUtils.format(totalAmount));
        etTendered.setText(CurrencyUtils.format(totalAmount));

        etTendered.addTextChangedListener(new android.text.TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){calculateChange();}
            public void afterTextChanged(android.text.Editable s){}
        });

        rgPaymentMethod.setOnCheckedChangeListener((group,id)->{
            boolean isCash=(id==R.id.rbCash);
            etTendered.setEnabled(isCash);
            etReference.setVisibility(isCash?View.GONE:View.VISIBLE);
        });

        btnConfirm.setOnClickListener(v->processPayment());
        findViewById(R.id.btnCancel).setOnClickListener(v->finish());
    }

    private void calculateChange(){
        try{
            double tendered=Double.parseDouble(etTendered.getText().toString().replaceAll("[^0-9.]",""));
            double change=tendered-totalAmount;
            tvChange.setText(change>=0?CurrencyUtils.format(change):"Need more: "+CurrencyUtils.format(-change));
            tvChange.setTextColor(getResources().getColor(change>=0?R.color.green:R.color.red,null));
        }catch(Exception e){ tvChange.setText("$0.00"); }
    }

    private void processPayment(){
        int selectedId=rgPaymentMethod.getCheckedRadioButtonId();
        PaymentEntry.Method method;
        if(selectedId==R.id.rbCard) method=PaymentEntry.Method.CARD;
        else if(selectedId==R.id.rbQR) method=PaymentEntry.Method.QR_CODE;
        else method=PaymentEntry.Method.CASH;

        String invName=invoiceName!=null?invoiceName:"DEMO-INV-001";
        PaymentEntry entry=new PaymentEntry(invName,method,totalAmount);

        String ref=etReference.getText().toString().trim();
        if(!ref.isEmpty()) entry.setReferenceNo(ref);

        progressBar.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);

        service.processPayment(entry,new com.restaurant.pos.api.ApiCallback<Boolean>(){
            @Override public void onSuccess(Boolean result){
                progressBar.setVisibility(View.GONE);
                CartManager.getInstance().clearCart();
                Toast.makeText(PaymentActivity.this,"Payment processed successfully!",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
            @Override public void onError(String msg){
                progressBar.setVisibility(View.GONE);
                btnConfirm.setEnabled(true);
                Toast.makeText(PaymentActivity.this,"Payment error: "+msg,Toast.LENGTH_LONG).show();
            }
        });
    }
}
