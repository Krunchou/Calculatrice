package com.krunch.cdsm05.calculatrice;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    EditText mEtResultat;
    String mCalcul = "";
    Double mNb1 = null;
    Double mNb2 = null;
    Double mNb3 = null;
    Double mResult ;
    boolean mTotal = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtResultat = (EditText) findViewById(R.id.resultat);

    }

    public void onClick(View v) {
        Button btn = (Button) v;
        switch (v.getId()){
            case R.id.bt_virg:
                mCalcul += ".";
                mTotal=false;
                break;

            case R.id.bt_division:
                mCalcul += "/";
                mEtResultat.setText("");
                mEtResultat.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.calculationsoperator,0);
                mTotal=false;
                break;

            case R.id.bt_addition:

                mCalcul += "+";
                //add commentaire here
                mEtResultat.setText("");
                mEtResultat.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.calculationoperationplussign,0);
                mTotal=false;
                break;

            case R.id.bt_soustraction:
                mCalcul += "-";
                mEtResultat.setText("");
                mEtResultat.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.calculationoperationsminussign,0);
                mTotal=false;
                break;

            case R.id.bt_clear:
                mEtResultat.setText("");
                mCalcul = "";
                mNb1 = Double.NaN;
                mNb2 = Double.NaN;
                mNb3 = Double.NaN;
                mEtResultat.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                mTotal=false;
                break;

            case R.id.bt_multiplication:
                mCalcul += "*";
                mEtResultat.setText("");
                mEtResultat.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.multiplicationsign,0);
                mTotal=false;
                break;

            case R.id.bt_total:
                try {
                     mResult = eval(mCalcul);
                }catch (Exception e){

                }
                mEtResultat.setText(String.valueOf(mResult));
                mCalcul = String.valueOf(mResult);
                mEtResultat.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.equal,0);
                mTotal=true;
                break;

            default:

                //mSnb1 = btn.getText().toString();
                if (!mTotal) {
                    mEtResultat.append(btn.getText().toString());
                }else{
                    mCalcul="";
                    mEtResultat.setText(btn.getText().toString());
                    mTotal=false;
                }
                mCalcul += btn.getText().toString();
        }
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
