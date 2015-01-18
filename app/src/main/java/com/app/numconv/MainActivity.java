package com.app.numconv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.numconv.anims.CalculatorActionCollapseAnimation;
import com.app.numconv.anims.CalculatorActionExpandAnimation;
import com.app.numconv.anims.CalculatorCollapseAnimation;
import com.app.numconv.anims.CalculatorExpandAnimation;

import java.math.BigDecimal;


public class MainActivity extends AppActivity {

    private boolean _isCalculatorMode;
    private boolean _appKeyboard;
    private boolean _preventCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NumberFragment.OnNumberChangedListener ncl = new NumberFragment.OnNumberChangedListener() {
            @Override
            public void onNumberChanged(NumberFragment fragment, String newNumber) {
                calculate();
            }

            @Override
            public void onBaseChanged(NumberFragment fragment, int newBase) {
                if(fragment.hasFocus())
                    changeKeyboard(fragment);

                calculate();
            }

            @Override
            public void onFocusChange(NumberFragment fragment) {
                changeKeyboard(fragment);

                if(_appKeyboard)
                    showKeyboard();
            }
        };

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        first.setHint(getString(R.string.first_number));
        first.setOnNumberChangedListener(ncl);
        first.setBase(16);
        first.setContextMenu(R.menu.cm_first);

        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        second.setHint(getString(R.string.second_number));
        second.setOnNumberChangedListener(ncl);
        second.setNextFocusIds(R.id.first, -1);
        second.setBase(2);
        second.setContextMenu(R.menu.cm_second);

        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);
        result.setHint(getString(R.string.result_number));
        result.setReadOnly(true);
        result.setContextMenu(R.menu.cm_result);
        result.setOnNumberChangedListener(new NumberFragment.OnNumberChangedListener() {
            @Override
            public void onNumberChanged(NumberFragment fragment, String newNumber) {
            }

            @Override
            public void onBaseChanged(NumberFragment fragment, int newBase) {
                calculate();
            }

            @Override
            public void onFocusChange(NumberFragment fragment) {
                hideKeyboard();
            }
        });
        result.setBase(10);

        CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);
        calculatorAction.setOnActionChangedListener(new CalculatorActionFragment.OnActionChangedListener() {
            @Override
            public void onActionChanged(CalculatorActionFragment fragment) {
                calculate();
            }
        });

        if(!_preferences.getBoolean(getResources().getString(R.string.pref_id_show_help_text), true)) {
            View hintText = findViewById(R.id.hint_text);
            if(hintText != null)
                hintText.setVisibility(View.GONE);
        }

        setCalculatorMode(false, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            this.overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
        } else if(requestCode == 1) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);
        CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);

        first.setBase(_preferences.getInt("first_base", 16));
        second.setBase(_preferences.getInt("second_base", 2));
        result.setBase(_preferences.getInt("result_base", 10));

        first.setNumber(_preferences.getString("first_number", ""));
        second.setNumber(_preferences.getString("second_number", ""));

        setCalculatorMode(_preferences.getBoolean("calculator_mode", false), true);
        calculatorAction.setAction(CalculatorActionFragment.MathAction.values()[_preferences.getInt("action", 0)]);
    }

    @Override
    protected void onStop() {
        super.onStop();

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);
        CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);

        SharedPreferences.Editor editor = _preferences.edit();
        editor.putInt("first_base", first.getBase());
        editor.putInt("second_base", second.getBase());
        editor.putInt("result_base", result.getBase());

        editor.putString("first_number", first.getNumber());
        editor.putString("second_number", second.getNumber());

        editor.putBoolean("calculator_mode", _isCalculatorMode);
        editor.putInt("action", calculatorAction.getAction().ordinal());
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);
        CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);

        outState.putInt("first_base", first.getBase());
        outState.putInt("second_base", second.getBase());
        outState.putInt("result_base", result.getBase());

        outState.putString("first_number", first.getNumber());
        outState.putString("second_number", second.getNumber());

        outState.putBoolean("calculator_mode", _isCalculatorMode);
        outState.putInt("action", calculatorAction.getAction().ordinal());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);
        CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);

        first.setBase(savedInstanceState.getInt("first_base", 16));
        second.setBase(savedInstanceState.getInt("second_base", 2));
        result.setBase(savedInstanceState.getInt("result_base", 10));

        first.setNumber(savedInstanceState.getString("first_number"));
        second.setNumber(savedInstanceState.getString("second_number"));

        setCalculatorMode(savedInstanceState.getBoolean("calculator_mode"), true);
        calculatorAction.setAction(CalculatorActionFragment.MathAction.values()[savedInstanceState.getInt("action")]);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _appKeyboard = _preferences.getBoolean(getResources().getString(R.string.pref_id_use_app_keyboard), true);
        if(_appKeyboard) {
            showKeyboard();
        } else {
            hideKeyboard();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_calculator) {
            setCalculatorMode(!_isCalculatorMode);
            return true;
        } else if (id == R.id.action_solution) {
            showSolution();
            return true;
        } else if(id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);

        MenuItem sfs = menu.findItem(R.id.swap_first_with_second);
        MenuItem sfr = menu.findItem(R.id.swap_first_with_result);
        MenuItem ssr = menu.findItem(R.id.swap_second_with_result);
        if(sfs != null) {
            sfs.setEnabled(first.isValidNumber() && second.isValidNumber());
            sfs.setVisible(_isCalculatorMode);
        }
        if(sfr != null) {
            sfr.setEnabled(first.isValidNumber() && result.isValidNumber());
        }
        if(ssr != null) {
            ssr.setEnabled(second.isValidNumber() && result.isValidNumber());
            ssr.setVisible(_isCalculatorMode);
        }
        if(!menu.hasVisibleItems()) {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);

        switch (item.getItemId()) {
            case R.id.clear_first:
                first.setNumber("");
                return true;
            case R.id.clear_second:
                second.setNumber("");
                return true;
            case R.id.clear_result:
                result.setNumber("");
                return true;
            case R.id.swap_first_with_second:
                swapNumbers(first, second);
                return true;
            case R.id.swap_first_with_result:
                swapNumbers(first, result);
                return true;
            case R.id.swap_second_with_result:
                swapNumbers(second, result);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void swapNumbers(NumberFragment a, NumberFragment b) {
        String num = a.getNumber();
        int base = a.getBase();

        _preventCalculate = true;

        a.setBase(b.getBase());
        a.setNumber(b.getNumber());

        b.setBase(base);
        b.setNumber(num);

        _preventCalculate = false;
        calculate();
    }

    @Override
    protected void onVerticalSwipe(boolean upToDown) {
        setCalculatorMode(upToDown);
    }

    @Override
    protected void onHorizontalSwipe(boolean leftToRight) {
        if(!leftToRight)
            showSolution();
    }

    private void showSolution() {
        Intent intent = new Intent(this, SolutionActivity.class);

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
        NumberFragment result = (NumberFragment) getFragmentManager().findFragmentById(R.id.result);
        CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);

        intent.putExtra("first_base", first.getBase());
        intent.putExtra("second_base", second.getBase());
        intent.putExtra("result_base", result.getBase());

        intent.putExtra("first_number", first.getNumber());
        intent.putExtra("second_number", second.getNumber());

        intent.putExtra("calculator_mode", _isCalculatorMode);
        intent.putExtra("action", calculatorAction.getAction().ordinal());

        startActivityForResult(intent, 0);
    }

    private void setCalculatorMode(boolean inCalc) {
        setCalculatorMode(inCalc, false);
    }

    private void setCalculatorMode(boolean inCalc, boolean force) {
        View calcPanel = findViewById(R.id.calc_panel);
        View calcPanelH = findViewById(R.id.calc_panel_h);

        NumberFragment first = (NumberFragment) getFragmentManager().findFragmentById(R.id.first);
        NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);

        if(_isCalculatorMode == inCalc && !force)
            return;

        if(inCalc) {
            calcPanel.startAnimation(new CalculatorExpandAnimation(calcPanel));
            if(calcPanelH != null)
                calcPanelH.startAnimation(new CalculatorActionExpandAnimation(calcPanelH, calcPanel));

            first.setNextFocusIds(-1, R.id.second);
            if(first.hasFocus())
                first.requestFocus();
        } else {
            calcPanel.startAnimation(new CalculatorCollapseAnimation(calcPanel));
            if(calcPanelH != null)
                calcPanelH.startAnimation(new CalculatorActionCollapseAnimation(calcPanelH, calcPanel));

            first.setNextFocusIds(-1, -1);
            if(second.hasFocus() || first.hasFocus())
                first.requestFocus();
        }

        if(force) {
            calcPanel.getAnimation().restrictDuration(0);
            if(calcPanelH != null)
                calcPanelH.getAnimation().restrictDuration(0);
        }

        _isCalculatorMode = inCalc;
        setEnabledTree(calcPanel, _isCalculatorMode);

        calculate();
    }

    private void changeKeyboard(NumberFragment focus) {
        int system = focus.getBase();

        KeyboardFragment keyboard  = (KeyboardFragment)getFragmentManager().findFragmentById(R.id.keyboard);
        keyboard.setLayout(system);
    }

    private void showKeyboard() {
        View kb = findViewById(R.id.keyboard);
        if(kb != null)
            kb.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        View kb = findViewById(R.id.keyboard);
        if(kb != null)
            kb.setVisibility(View.GONE);
    }

    private void calculate() {
        if(_preventCalculate)
            return;

        NumberFragment first  = (NumberFragment)getFragmentManager().findFragmentById(R.id.first);
        NumberFragment result = (NumberFragment)getFragmentManager().findFragmentById(R.id.result);
        String calculated;

        try {
            if (!_isCalculatorMode) {
                calculated = BaseConverter.convert(first.getNumber(), first.getBase(), result.getBase());
            } else {
                NumberFragment second = (NumberFragment) getFragmentManager().findFragmentById(R.id.second);
                CalculatorActionFragment calculatorAction = (CalculatorActionFragment) getFragmentManager().findFragmentById(R.id.action);

                BigDecimal fn = new BigDecimal(BaseConverter.convert(first.getNumber(), first.getBase(), 10, false));
                BigDecimal sn = new BigDecimal(BaseConverter.convert(second.getNumber(), second.getBase(), 10, false));
                BigDecimal rn = applyAction(fn, sn, calculatorAction.getAction());

                calculated = BaseConverter.convert(rn.toString(), 10, result.getBase());
            }
        } catch (NumberFormatException e) {
            calculated = e.getLocalizedMessage();
        }  catch (ArithmeticException e) {
            calculated = e.getLocalizedMessage();
        }

        result.setNumber(calculated);
    }

    private static BigDecimal applyAction(BigDecimal fn, BigDecimal sn, CalculatorActionFragment.MathAction action) {
        switch (action) {
            case Add:
                return fn.add(sn);
            case Subtract:
                return fn.subtract(sn);
            case Multiply:
                return fn.multiply(sn);
            case Divide:
                return fn.divide(sn);
        }

        throw new IllegalArgumentException("action");
    }
}
