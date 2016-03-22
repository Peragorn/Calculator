package com.firstproject.siaum.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    boolean isCalculatorFieldClear = true;
    boolean insert_state = false;
    boolean last_click = false;
    String[] operatorTable = {"+", "-", "*", "/", "."};

    BigDecimal answer = new BigDecimal("0.0");
    Expression expression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
    }

    public void calculate() {
        EditText screen = (EditText) findViewById(R.id.screen);

        expression = new Expression(screen.getText().toString());
        this.answer = expression.eval();

        screen.setText(this.answer + "");
    }

    public void insert_text(String text) {
        EditText screen = (EditText) findViewById(R.id.screen);
        if (this.isCalculatorFieldClear) {
            screen.setText("");
            this.isCalculatorFieldClear = false;
        }
        this.insert_state = true;
        this.last_click = true;
        screen.append(text);
    }

    public boolean isLastApperenceOperatorSame(String text) {
        EditText screen = (EditText) findViewById(R.id.screen);
        String screenText = screen.getText().toString();
        if (screenText.substring(screenText.length() - 1).equals(text)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastApperenceOperatorExist(){
        EditText screen = (EditText) findViewById(R.id.screen);
        String screenText = screen.getText().toString();
        for(int i=0; i<operatorTable.length; i++){
            if(screenText.substring(screenText.length() - 1).equals(operatorTable[i])){
                return true;
            }
        }
        return false;
    }

    public void checkIsNumberBeforeComaCharacter() {
        EditText screen = (EditText) findViewById(R.id.screen);
        String screenText = screen.getText().toString();
        if (screenText.substring(screenText.length() - 1).equals("0") && screenText.length() == 1) {
            insert_text("0.");
            return;
        }
        if (screenText.substring(screenText.length() - 1).matches(".*\\d+.*")) {
            insert_text(".");
        } else {
            insert_text("0.");
        }
    }

    public void ButtonClickHandler(View v) {
        EditText screen = (EditText) findViewById(R.id.screen);
        switch (v.getId()) {
            case R.id.button0:
                insert_text("0");
                break;
            case R.id.button1:
                insert_text("1");
                break;
            case R.id.button2:
                insert_text("2");
                break;
            case R.id.button3:
                insert_text("3");
                break;
            case R.id.button4:
                insert_text("4");
                break;
            case R.id.button5:
                insert_text("5");
                break;
            case R.id.button6:
                insert_text("6");
                break;
            case R.id.button7:
                insert_text("7");
                break;
            case R.id.button8:
                insert_text("8");
                break;
            case R.id.button9:
                insert_text("9");
                break;
            case R.id.buttonPoint:
                if (!isLastApperenceOperatorSame(".") && !isLastApperenceOperatorExist()) {
                    checkIsNumberBeforeComaCharacter();
                }
                break;
            case R.id.buttonAdd:
                if (!isLastApperenceOperatorSame("+") && !isLastApperenceOperatorExist()) {
                    insert_text("+");
                }
                break;
            case R.id.buttonMinus:
                if (!isLastApperenceOperatorSame("-") && !isLastApperenceOperatorExist()) {
                    insert_text("-");
                }
                break;
            case R.id.buttonMultiplication:
                if (!isLastApperenceOperatorSame("*") && !isLastApperenceOperatorExist()) {
                    insert_text("*");
                }
                break;
            case R.id.buttonDivision:
                if (!isLastApperenceOperatorSame("/") && !isLastApperenceOperatorExist()) {
                    insert_text("/");
                }
                break;
            case R.id.buttonEquals:
                calculate();
                break;
            case R.id.buttonDel:
                if (screen.getText().toString().length() > 1) {
                    String screen_new = screen.getText().toString().substring(0, screen.getText().toString().length() - 1);
                    screen.setText(screen_new);
                    this.isCalculatorFieldClear = false;
                } else {
                    screen.setText("0");
                    this.isCalculatorFieldClear = true;
                }
                break;
            case R.id.buttonC:
                this.answer = null;
                this.isCalculatorFieldClear = true;
                screen.setText("0");
                break;

            case R.id.buttonFactorial:
                insert_text("SILNIA(");
                break;

            case R.id.buttonPercent:
                insert_text("%");
                break;

            case R.id.buttonOneByX:
                insert_text("1/");
                break;

            case R.id.buttonLeftBracket:
                insert_text("(");
                break;

            case R.id.buttonBracketRight:
                insert_text(")");
                break;

            case R.id.buttonPower2:
                insert_text("^2");
                break;

            case R.id.buttonPower3:
                insert_text("^3");
                break;

            case R.id.buttonPowerToN:
                insert_text("^");
                break;

            case R.id.buttonNumberE:
                insert_text("E");
                break;

            case R.id.buttonSqrt:
                insert_text("SQRT(");
                break;

            case R.id.buttonSqrt3:
                insert_text("SQRT3(");
                break;

            case R.id.buttonLog:
                insert_text("LOG(");
                break;

            case R.id.buttonLog10:
                insert_text("LOG10(");
                break;

            case R.id.buttonSin:
                insert_text("SIN(");
                break;

            case R.id.buttonCos:
                insert_text("COS(");
                break;

            case R.id.buttonTan:
                insert_text("TAN(");
                break;

            case R.id.buttonPi:
                insert_text("PI");
                break;

            case R.id.buttonSinh:
                insert_text("SINH(");
                break;

            case R.id.buttonCosh:
                insert_text("COSH(");
                break;

            case R.id.buttonTanh:
                insert_text("TANH(");
                break;

            case R.id.buttonRand:
                insert_text("RANDOM()");
                break;

            default:
                break;
        }
    }
}
