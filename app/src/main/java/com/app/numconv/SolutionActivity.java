package com.app.numconv;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SolutionActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        Intent intent = getIntent();
        int firstBase = intent.getIntExtra("first_base", 16);
        int secondBase = intent.getIntExtra("second_base", 2);
        int resultBase = intent.getIntExtra("result_base", 10);

        String first = intent.getStringExtra("first_number");
        String second = intent.getStringExtra("second_number");

        boolean isCalc = intent.getBooleanExtra("calculator_mode", false);
        CalculatorActionFragment.MathAction action = CalculatorActionFragment.MathAction.values()[intent.getIntExtra("action", 0)];

        String solution;
        try {
            String firstDec;
            String secondDec;
            String resultDec;

            // Problem
            {
                SolutionBuilder sb = new SolutionBuilder();
                sb.comment(R.string.solution_problem).space();
                sb.number(first).index(firstBase);
                if (isCalc) {
                    writeAction(sb, action);
                    sb.number(second).index(secondBase);
                }
                sb.equals().number("?").index(resultBase);
                setText(R.id.solution_problem, sb.toString());
            }

            // First
            if (firstBase != 10) {
                SolutionBuilder sb = new SolutionBuilder();
                firstDec = writeSolution(sb, first, firstBase);
                setText(R.id.solution_first_to_dec_comment, isCalc ? R.string.convert_first_to_dec : R.string.convert_to_dec);
                setText(R.id.solution_first_to_dec, sb.toString());
            } else {
                firstDec = first;
                setText(R.id.solution_first_to_dec_comment, isCalc ? R.string.first_is_dec : R.string.number_is_dec);
                hide(R.id.solution_first_to_dec);
            }

            if (isCalc) {
                // Second
                if (secondBase != 10) {
                    SolutionBuilder sb = new SolutionBuilder();
                    secondDec = writeSolution(sb, second, secondBase);
                    setText(R.id.solution_second_to_dec, sb.toString());
                } else {
                    secondDec = second;
                    setText(R.id.solution_second_to_dec_comment, R.string.second_is_dec);
                    hide(R.id.solution_second_to_dec);
                }

                // Calculate
                resultDec = applyAction(firstDec, secondDec, action);

                SolutionBuilder sb = new SolutionBuilder();
                sb.comment(R.string.solution_calculating).space().number(firstDec);
                writeAction(sb, action);
                sb.number(second).equals().number(resultDec);
                setText(R.id.solution_calculating, sb.toString());
            } else {
                secondDec = "";
                resultDec = firstDec;

                hide(R.id.solution_second_to_dec_comment);
                hide(R.id.solution_second_to_dec);
                hide(R.id.solution_calculating);
            }

            // Convert result
            String result;
            if (resultBase != 10) {
                result = BaseConverter.convert(resultDec, 10, resultBase);

                setText(R.id.solution_result_to_comment, R.string.convert_result_to, resultBase);

                int dot = resultDec.indexOf('.');
                getSolutionIntegerFromDec((TableLayout) findViewById(R.id.solution_result_table_integer),
                        getLayoutInflater(), dot == -1 ? resultDec : resultDec.substring(0, dot), resultBase);

                if(dot != -1) {
                    TableLayout table = (TableLayout) findViewById(R.id.solution_result_table_fraction);
                    if(!getSolutionFractionFromDec(table, getLayoutInflater(), '0' + resultDec.substring(dot), resultBase))
                        hide(R.id.solution_too_many_fraction_steps);
                } else {
                    hide(R.id.solution_result_fraction_comment);
                    hide(R.id.solution_result_table_fraction);
                    hide(R.id.solution_too_many_fraction_steps);
                }

                SolutionBuilder sb = new SolutionBuilder();
                sb.comment(R.string.solution_result).space().number(result).index(resultBase);
                setText(R.id.solution_result, sb.toString());
            } else {
                result = resultDec;
                setText(R.id.solution_result_to_comment, R.string.result_is_dec);
                hide(R.id.solution_result_layout);
            }

            // Answer
            SolutionBuilder sb = new SolutionBuilder();
            sb.number(first).index(firstBase);
            if (isCalc) {
                writeAction(sb, action);
                sb.number(second).index(secondBase);
            }
            sb.equals();

            if (firstBase != 10 || (isCalc && secondBase != 10)) {
                sb.number(firstDec).index(10);
                writeAction(sb, action);
                sb.number(secondDec).index(10).equals();
            }

            sb.number(resultDec).index(10);

            if (resultBase != 10) {
                sb.equals().number(result).index(resultBase);
            }

            setText(R.id.solution_answer, sb.toString());
        } catch (NumberFormatException | ArithmeticException e) {
            setText(R.id.solution_answer, e.getLocalizedMessage());
            Log.w("solution", e);

            hide(R.id.solution_first_to_dec_comment);
            hide(R.id.solution_first_to_dec);
            hide(R.id.solution_second_to_dec_comment);
            hide(R.id.solution_second_to_dec);
            hide(R.id.solution_calculating);
            hide(R.id.solution_result_to_comment);
            hide(R.id.solution_result_layout);
        }
    }

    private void setText(int id, String str) {
        TextView et = (TextView) findViewById(id);
        et.setText(Html.fromHtml(str));
    }

    private void setText(int id, int resId) {
        TextView et = (TextView) findViewById(id);
        et.setText(resId);
    }

    private void setText(int id, int resId, Object... params) {
        TextView et = (TextView) findViewById(id);
        et.setText(String.format(getResources().getString(resId), params));
    }

    private void hide(int id) {
        findViewById(id).setVisibility(View.GONE);
    }

    private static void getSolutionIntegerFromDec(TableLayout table, LayoutInflater inflater, String decimal, int target) {
        BigInteger dec = new BigInteger(decimal);
        BigInteger bigSystem = BigInteger.valueOf(target);

        SolutionBuilder sb = new SolutionBuilder();

        while(dec.compareTo(BigInteger.ZERO) != 0) {
            sb.clear();

            TableRow row = (TableRow) inflater.inflate(R.layout.solution_table_row, table, false);
            TextView left = (TextView) row.findViewById(R.id.solution_row_left);
            TextView right = (TextView) row.findViewById(R.id.solution_row_right);

            BigInteger next = dec.divide(bigSystem);
            sb.number(target).cross().number(next, "b").plus().number(dec.mod(bigSystem), "u");
            left.setText(Html.fromHtml("<b>" + dec + "</b>"));
            right.setText(Html.fromHtml(sb.toString()));
            table.addView(row);

            dec = next;
        }
    }

    private static boolean getSolutionFractionFromDec(TableLayout table, LayoutInflater inflater, String decimal, int target) {
        BigDecimal fractionDec = new BigDecimal(decimal);
        BigDecimal fractionSystem = BigDecimal.valueOf(target);

        SolutionBuilder sb = new SolutionBuilder();

        int i = 0;
        while(fractionDec.compareTo(BigDecimal.ZERO) != 0 && i < BaseConverter.MaximumFractionSteps) {
            TableRow row = (TableRow) inflater.inflate(R.layout.solution_table_row, table, false);
            TextView left = (TextView) row.findViewById(R.id.solution_row_left);
            TextView right = (TextView) row.findViewById(R.id.solution_row_right);

            sb.number(fractionDec, "b").cross().number(target);
            left.setText(Html.fromHtml(sb.toString()));
            sb.clear();

            fractionDec = fractionDec.multiply(fractionSystem);
            sb.number(fractionDec.intValue(), "u").dot();
            fractionDec = fractionDec.subtract(BigDecimal.valueOf(fractionDec.intValue()));
            sb.number(fractionDec.toPlainString().substring(2), "b");
            right.setText(Html.fromHtml(sb.toString()));
            sb.clear();

            table.addView(row);

            i++;
        }

        return i == 16 && fractionDec.compareTo(BigDecimal.ZERO) != 0;
    }

    private static void writeAction(SolutionBuilder sb, CalculatorActionFragment.MathAction action) {
        switch (action) {
            case Add:
                sb.plus();
                return;
            case Subtract:
                sb.minus();
                return;
            case Multiply:
                sb.cross();
                return;
            case Divide:
                sb.divide();
                return;
        }

        throw new IllegalArgumentException("action");
    }

    private static String applyAction(String f, String s, CalculatorActionFragment.MathAction action) {
        BigDecimal fn = new BigDecimal(f);
        BigDecimal sn = new BigDecimal(s);

        switch (action) {
            case Add:
                return fn.add(sn).toString();
            case Subtract:
                return fn.subtract(sn).toString();
            case Multiply:
                return fn.multiply(sn).toString();
            case Divide:
                return fn.divide(sn).toString();
        }

        throw new IllegalArgumentException("action");
    }

    private String writeSolution(SolutionBuilder sb, String number, int source) {
        String res = BaseConverter.convert(number, source, 10, false);

        sb.number(number).index(source).equals();
        BaseConverter.getSolutionRepresentation(sb, number, source);
        sb.equals();
        BaseConverter.getSolutionMid(sb, number, source);
        sb.equals().number(res);
        sb.index(10);

        return res;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onHorizontalSwipe(boolean leftToRight) {
        if(leftToRight) {
            finish();
        }
    }
}
