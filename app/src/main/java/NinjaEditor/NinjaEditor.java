package NinjaEditor;

import android.os.Bundle;
import android.widget.EditText;
import android.content.Context;
import android.util.AttributeSet;
import android.text.TextWatcher;
import android.text.Editable;

import java.util.Set;

import CodeAnalyzer.JavaCodeAnalyzer;


public class NinjaEditor extends EditText {

    //----------------------------------------------------------------------------------------------------
    private JavaCodeAnalyzer m_javaCodeAnalyzer;

    //----------------------------------------------------------------------------------------------------
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

    //----------------------------------------------------------------------------------------------------
    private void initialize() {
        setHorizontallyScrolling(true);

        m_javaCodeAnalyzer = new JavaCodeAnalyzer();
        this.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {
                m_javaCodeAnalyzer.analyze(s);
            }
        });
    }

    //----------------------------------------------------------------------------------------------------
    public boolean setColors(Bundle colors) {
        m_javaCodeAnalyzer.setColors(colors);
        m_javaCodeAnalyzer.analyze();

        return true;
    }

    public Bundle getColors() {
        return m_javaCodeAnalyzer.getColors();
    }
}
