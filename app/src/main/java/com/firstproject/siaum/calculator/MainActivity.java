package com.firstproject.siaum.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    boolean isCalculatorFieldClear = true;
    boolean operator_state = false;
    boolean insert_state = false;
    boolean last_click = false;
    BigDecimal operand1 ;
    BigDecimal operand2 ;

    BigDecimal answer = new BigDecimal("0.0");
    String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.hide();
        }

    }
    public void calculator() {
        EditText screen = (EditText) findViewById(R.id.screen);
        if (screen.getText().toString().equals(".")) {
            screen.setText("0");
        }
        if (screen.getText().toString().length() > 0) {
//            this.operand2 = Float.parseFloat(screen.getText().toString());
            this.operand2 = new BigDecimal(screen.getText().toString());
        }
        if (this.operator.equals("+")) {
            this.answer = operand1.add(operand2);
        } else if (this.operator.equals("-")) {
            this.answer = operand1.subtract(operand2);
        } else if (this.operator.equals("*")) {
            this.answer = operand1.multiply(operand2);
        } else if (this.operator.equals("/")) {
            this.answer = new BigDecimal(operand1.divide(operand2, 15, RoundingMode.CEILING).stripTrailingZeros().toPlainString());

        } else if (this.operator.equals("^")) {
//            this.answer = (float) Math.pow(this.operand1, this.operand2);
        } else if (this.operator.equals("%")) {
//            this.answer = operand1 % this.operand2;
        } else {
//            this.answer = Float.parseFloat(screen.getText().toString());
        }

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

    public void set_operator(String operator) {
        EditText screen = (EditText) findViewById(R.id.screen);
        if (screen.getText().toString().equals(".")) screen.setText("0");
        if (this.insert_state && this.operator_state && this.last_click) {
            calculator();
        }
        if (screen.getText().toString().length() > 0) {
//            this.operand1 = Float.parseFloat(screen.getText().toString());
            this.operand1 = new BigDecimal(screen.getText().toString());
        }
        this.operator_state = true;
        this.isCalculatorFieldClear = true;
        this.last_click = false;
        if (operator.equals("+")) this.operator = "+";
        else if (operator.equals("-")) this.operator = "-";
        else if (operator.equals("*")) this.operator = "*";
        else if (operator.equals("/")) this.operator = "/";
        else if (operator.equals("√")) {
//            this.answer = (float) Math.sqrt(Float.parseFloat(screen.getText().toString()));
            screen.setText(this.answer + "");
            this.isCalculatorFieldClear = true;
            this.operand1 = null;
            this.operand2 = null;
            this.operator = "";
            this.last_click = true;
            this.operator_state = false;
        } else if (operator.equals("d")) {
//            this.answer = 1 / Float.parseFloat(screen.getText().toString());
            screen.setText(this.answer + "");
            this.isCalculatorFieldClear = true;
            this.operand1 = null;
            this.operand2 = null;
            this.operator = "";
            this.last_click = true;
            this.operator_state = false;
        } else if (operator.equals("^")) this.operator = "^";
        else if (operator.equals("%")) this.operator = "%";
        else if (operator.equals("=")){
            this.operator = "=";
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
                if (!screen.getText().toString().contains(".") || this.operator_state) {
                    insert_text(".");
                }
                break;
            case R.id.buttonAdd:
                set_operator("+");
                break;
            case R.id.buttonMinus:
                set_operator("-");
                break;
            case R.id.buttonMultiplication:
                set_operator("*");
                break;
            case R.id.buttonDivision:
                set_operator("/");
                break;
            case R.id.buttonEquals:
                set_operator("=");
                break;
//            case R.id.buttonSqr:	set_operator("√"); break;
//            case R.id.buttonPow:	set_operator("^"); break;
//            case R.id.buttonMod:	set_operator("%"); break;
//            case R.id.buttonOnediv:	set_operator("d"); break;
//            case R.id.buttonExe:
//                if (screen.getText().toString().length() > 0 && !this.operator.equals("")) {
//                    calculator();
//                    this.isCalculatorFieldClear = true;
//                    this.operand1 = 0f;
//                    this.operand2 = 0f;
//                    this.operator = "";
//                    this.operator_state = false;
//                }
//                break;
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
                this.operand1 = null;
                this.operand2 = null;
                this.answer = null;
                this.operator = "";
                this.operator_state = false;
                this.insert_state = false;
                this.last_click = false;
                this.isCalculatorFieldClear = true;
                screen.setText("0");
                break;
        }
    }
}
