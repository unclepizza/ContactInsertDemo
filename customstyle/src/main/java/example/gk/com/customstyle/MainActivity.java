package example.gk.com.customstyle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("智行火车票大卖");
        //前景色
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.parseColor("#009ad6"));
        ssb.setSpan(fcs, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //背景色
        BackgroundColorSpan bcs = new BackgroundColorSpan(Color.parseColor("#542365"));
        ssb.setSpan(bcs, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //删除线
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        ssb.setSpan(strikethroughSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_content.setText(ssb);
    }
}
