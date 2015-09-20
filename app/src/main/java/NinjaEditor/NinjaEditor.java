package NinjaEditor;

import android.widget.EditText;
import android.content.Context;
import android.util.AttributeSet;
import android.text.TextWatcher;
import android.text.Editable;

import LexicalAnalyzer.JavaLexicalAnalyzer;

public class NinjaEditor extends EditText {
    private JavaLexicalAnalyzer m_javaLexicalAnalyzer;

    public NinjaEditor(Context context) {
        super(context);
        initialize();
    }
    public NinjaEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }
    public NinjaEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        setHorizontallyScrolling(true);

        m_javaLexicalAnalyzer = new JavaLexicalAnalyzer();
        this.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            public void afterTextChanged(Editable s) {
                m_javaLexicalAnalyzer.analyze(s);
            }
        });
    }
}
