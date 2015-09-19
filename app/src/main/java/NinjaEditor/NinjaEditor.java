package NinjaEditor;

import android.widget.EditText;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Typeface;
import android.text.TextWatcher;
import android.text.Editable;
import android.text.style.StyleSpan;
import android.text.Spanned;

public class NinjaEditor extends EditText {

    public NinjaEditor(Context context) {
        super(context);
        Initialize();
    }
    public NinjaEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initialize();
    }
    public NinjaEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Initialize();
    }

    private void Initialize() {
        this.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });
    }
}
