package com.firstproject.siaum.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    boolean clear_screen = true;
    boolean operator_state = false;
    boolean insert_state = false;
    boolean last_click = false;
    float Operand1 = 0f;
    float Operand2 = 0f;
    float Answer = 0f;
    String Operator = "";

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
            this.Operand2 = Float.parseFloat(screen.getText().toString());
        }
        if (this.Operator.equals("+")) {
            this.Answer = this.Operand1 + this.Operand2;
        } else if (this.Operator.equals("-")) {
            this.Answer = this.Operand1 - this.Operand2;
        } else if (this.Operator.equals("*")) {
            this.Answer = this.Operand1 * this.Operand2;
        } else if (this.Operator.equals("/")) {
            this.Answer = this.Operand1 / this.Operand2;
        } else if (this.Operator.equals("^")) {
            this.Answer = (float) Math.pow(this.Operand1, this.Operand2);
        } else if (this.Operator.equals("%")) {
            this.Answer = Operand1 % this.Operand2;
        } else {
            this.Answer = Float.parseFloat(screen.getText().toString());
        }

        screen.setText(this.Answer + "");
    }

    public void insert_text(String text) {
        EditText screen = (EditText) findViewById(R.id.screen);
        if (this.clear_screen) {
            screen.setText("");
            this.clear_screen = false;
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
            this.Operand1 = Float.parseFloat(screen.getText().toString());
        }
        this.operator_state = true;
        this.clear_screen = true;
        this.last_click = false;
        if (operator.equals("+")) this.Operator = "+";
        else if (operator.equals("-")) this.Operator = "-";
        else if (operator.equals("*")) this.Operator = "*";
        else if (operator.equals("/")) this.Operator = "/";
        else if (operator.equals("√")) {
            this.Answer = (float) Math.sqrt(Float.parseFloat(screen.getText().toString()));
            screen.setText(this.Answer + "");
            this.clear_screen = true;
            this.Operand1 = 0f;
            this.Operand2 = 0f;
            this.Operator = "";
            this.last_click = true;
            this.operator_state = false;
        } else if (operator.equals("d")) {
            this.Answer = 1 / Float.parseFloat(screen.getText().toString());
            screen.setText(this.Answer + "");
            this.clear_screen = true;
            this.Operand1 = 0f;
            this.Operand2 = 0f;
            this.Operator = "";
            this.last_click = true;
            this.operator_state = false;
        } else if (operator.equals("^")) this.Operator = "^";
        else if (operator.equals("%")) this.Operator = "%";
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
//                break;
//            case R.id.buttonSqr:	set_operator("√"); break;
//            case R.id.buttonPow:	set_operator("^"); break;
//            case R.id.buttonMod:	set_operator("%"); break;
//            case R.id.buttonOnediv:	set_operator("d"); break;
//            case R.id.buttonExe:
                if (screen.getText().toString().length() > 0 && !this.Operator.equals("")) {
                    calculator();
                    this.clear_screen = true;
                    this.Operand1 = 0f;
                    this.Operand2 = 0f;
                    this.Operator = "";
                    this.operator_state = false;
                }
                break;
            case R.id.buttonDel:
                if (screen.getText().toString().length() > 1) {
                    String screen_new = screen.getText().toString().substring(0, screen.getText().toString().length() - 1);
                    screen.setText(screen_new);
                    this.clear_screen = false;
                } else {
                    screen.setText("0");
                    this.clear_screen = true;
                }
                break;
            case R.id.buttonC:
                this.Operand1 = 0f;
                this.Operand2 = 0f;
                this.Answer = 0f;
                this.Operator = "";
                this.operator_state = false;
                this.insert_state = false;
                this.last_click = false;
                this.clear_screen = true;
                screen.setText("0");
                break;
        }
    }
}
