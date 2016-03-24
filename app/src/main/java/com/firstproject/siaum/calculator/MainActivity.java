package com.firstproject.siaum.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static long backPresClickTime;
    private int defaultOperatorSizeFlagValue = 1;
    private boolean isFisrtClick = true;
    private boolean isCalculatorFieldClear = true;
    private String[] operatorTable = {"+", "-", "*", "/", "%"};
    private BigDecimal answer = new BigDecimal("0.0");
    private EditText screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen = (EditText) findViewById(R.id.screen);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (backPresClickTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Wciśnij ponownie aby wyjść!", Toast.LENGTH_SHORT).show();
            backPresClickTime = System.currentTimeMillis();
        }
    }

    public void calculate() {
        Expression expression = new Expression(screen.getText().toString());
        try {
            this.answer = expression.eval();

            DecimalFormat df = new DecimalFormat("#.###############");
            df.setRoundingMode(RoundingMode.CEILING);
            String result = df.format(answer);
            screen.setText(result.replace(",", "."));
            this.isFisrtClick = true;
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Sprawdź poprawność składni", Toast.LENGTH_LONG).show();
            Log.e("Error on calculate ", e.toString());
        }
    }

    public void insert_text(String text) {
        if (this.isCalculatorFieldClear) {
            screen.setText("");
            this.isCalculatorFieldClear = false;
        }
        screen.append(text);
    }

    public void delete_text() {
        try {
            String text = screen.getText().toString();
            if (text.substring(text.length() - 2, text.length()).equals("(-")) {
                screen.setText((text.substring(0, text.length() - 2)));
            }
            if (screen.getText().toString().length() == 0) {
                screen.setText("0");
                isCalculatorFieldClear = true;
            }
        } catch (Exception e) {
            Log.e("", String.valueOf(e));
        }
    }

    public boolean isLastApperenceOperatorSame(String text, int operandSize) {
        String screenText = screen.getText().toString();
        try {
            if (screenText.substring(screenText.length() - operandSize).equals(text)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("screenText is 0", String.valueOf(e));
            return false;
        }
    }

    public boolean isLastApperenceOperatorSame(String text) {
        return isLastApperenceOperatorSame(text, defaultOperatorSizeFlagValue);
    }

    public boolean isLastApperenceOperatorExist() {
        try {
            String screenText = screen.getText().toString();
            for (int i = 0; i < operatorTable.length; i++) {
                if (screenText.substring(screenText.length() - defaultOperatorSizeFlagValue).equals(operatorTable[i])) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e("", String.valueOf(e));
            return false;
        }
    }

    public void checkIsNumberBeforeComaCharacter() {
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

    private void addMultiplyIfLastCharacterIsNotOperand() {
        if (screen.getText().toString().equals("0")){
            isCalculatorFieldClear = true;
        }
        if (!isCalculatorFieldClear) {
            String lastCharacter = "" + screen.getText().toString().charAt(screen.getText().toString().length() - 1);
            for (String elem : operatorTable) {
                if (elem.equals(lastCharacter)) {
                    return;
                }
                if (lastCharacter.equals("(")) {
                    return;
                }
            }
            insert_text("*");
        }
    }

    public void ButtonClickHandler(View v) {
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
                if (!isLastApperenceOperatorSame(".")) {
                    checkIsNumberBeforeComaCharacter();
                }
                break;
            case R.id.buttonAdd:
                if (!isLastApperenceOperatorSame("+") && !isLastApperenceOperatorExist()) {
                    if (!isCalculatorFieldClear) {
                        insert_text("+");
                    }
                }
                break;
            case R.id.buttonMinus:
                if (!isLastApperenceOperatorSame("-") && !isLastApperenceOperatorExist()) {
                    if (!isCalculatorFieldClear) {
                        insert_text("-");
                    }
                }
                break;
            case R.id.buttonMultiplication:
                if (!isLastApperenceOperatorSame("*") && !isLastApperenceOperatorExist()) {
                    if (!isCalculatorFieldClear) {
                        insert_text("*");
                    }
                }
                break;
            case R.id.buttonDivision:
                if (!isLastApperenceOperatorSame("/") && !isLastApperenceOperatorExist()) {
                    if (!isCalculatorFieldClear) {
                        insert_text("/");
                    }
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
                    this.isFisrtClick = true;
                    this.isCalculatorFieldClear = true;
                }
                break;
            case R.id.buttonC:
                this.answer = null;
                this.isCalculatorFieldClear = true;
                this.isFisrtClick = true;
                screen.setText("0");
                break;

            case R.id.buttonFactorial:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("SILNIA(");
                break;

            case R.id.buttonPercent:
                if (!isCalculatorFieldClear && !isLastApperenceOperatorSame("%") && !isLastApperenceOperatorExist()) {
                    insert_text("%");
                }
                break;

            case R.id.buttonOneByX:
                insert_text("1/");
                break;

            case R.id.buttonLeftBracket:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("(");
                break;

            case R.id.buttonBracketRight:
                if (!isCalculatorFieldClear) {
                    insert_text(")");
                }
                break;

            case R.id.buttonPower2:
                if (!isCalculatorFieldClear) {
                    insert_text("^2");
                }
                break;

            case R.id.buttonPower3:
                if (!isCalculatorFieldClear) {
                    insert_text("^3");
                }
                break;

            case R.id.buttonPowerToN:
                if (!isCalculatorFieldClear) {
                    insert_text("^");
                }
                break;

            case R.id.buttonNumberE:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("E");
                break;

            case R.id.buttonSqrt:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("SQRT(");
                break;

            case R.id.buttonSqrt3:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("SQRT3(");
                break;

            case R.id.buttonLog:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("LOG(");
                break;

            case R.id.buttonLog10:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("LOG10(");
                break;

            case R.id.buttonSin:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("SIN(");
                break;

            case R.id.buttonCos:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("COS(");
                break;

            case R.id.buttonTan:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("TAN(");
                break;

            case R.id.buttonPi:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("PI");
                break;

            case R.id.buttonSinh:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("SINH(");
                break;

            case R.id.buttonCosh:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("COSH(");
                break;

            case R.id.buttonTanh:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("TANH(");
                break;

            case R.id.buttonRand:
                addMultiplyIfLastCharacterIsNotOperand();
                insert_text("RANDOM()");
                break;

            case R.id.buttonPlusMinus:
                if (!isLastApperenceOperatorSame("(-", 2) && isFisrtClick) {
                    insert_text("(-");
                    isFisrtClick = false;
                } else {
                    delete_text();
                    isFisrtClick = true;
                }
                break;

            default:
                break;
        }
    }
}
