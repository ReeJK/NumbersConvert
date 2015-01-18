package com.app.numconv;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NumberFragment extends Fragment {
    private static Integer[] _Bases = new Integer[35];

    static {
        for(int i = 0; i < _Bases.length; i++)
            _Bases[i] = i + 2;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment NumberFragment.
     */
    public static NumberFragment newInstance() {
        return new NumberFragment();
    }

    private EditText _number;
    private Button _base;
    private boolean _readonly;
    private boolean _appKeyboard;
    private int _contextMenuRes;

    private OnNumberChangedListener _listener;

    public NumberFragment() {
        // Required empty public constructor
    }

    public void setOnNumberChangedListener(OnNumberChangedListener listener) {
        _listener = listener;
    }

    public void setContextMenu(int resId) {
        _contextMenuRes = resId;
        _number.setOnCreateContextMenuListener(this);
    }

    public void setHint(String hintText) {
        _number.setHint(hintText);
    }

    public void setReadOnly(boolean readonly) {
        _readonly = readonly;

        if(readonly)
            _number.setKeyListener(null);
        else
            setBase(getBase());
    }

    public boolean hasFocus() {
        return _number.isFocused();
    }

    public void requestFocus() {
        _number.requestFocus();
    }

    public void setNextFocusIds(int prevId, int nextId) {
        if(nextId != -1)
            _number.setImeOptions(EditorInfo.IME_ACTION_NEXT | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        else if(prevId != -1)
            _number.setImeOptions(EditorInfo.IME_ACTION_PREVIOUS | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        else
            _number.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        _number.setNextFocusDownId(nextId);
        _number.setNextFocusRightId(nextId);
        _number.setNextFocusForwardId(nextId);

        _number.setNextFocusUpId(prevId);
        _number.setNextFocusUpId(prevId);

        if(hasFocus() && !_appKeyboard) { // hack to update action button
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(_number.getWindowToken(), 0);
            imm.restartInput(_number);
            imm.showSoftInput(_number, 0);
        }
    }

    public void setBase(int base) {
        if(base < 2)
            throw new IllegalArgumentException("base must be >= 2");

        _base.setText(String.valueOf(base));

        if(!_readonly && !_appKeyboard) {
            if (base <= 10)
                _number.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            else
                _number.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        if(_listener != null)
            _listener.onBaseChanged(this, base);
    }

    public int getBase() {
        return Integer.parseInt(_base.getText().toString());
    }

    public void setNumber(String number) {
        _number.setText(number);

        if(hasFocus())
            _number.setSelection(number.length());

        if(_listener != null)
            _listener.onNumberChanged(this, number);
    }

    public String getNumber() {
        String text = _number.getText().toString();
        if(text.length() == 0 || text.equals("-") || text.equals(".")) {
            return "0";
        }

        return text;
    }

    public boolean isValidNumber() {
        return BaseConverter.isValid(getNumber(), getBase());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number, container, false);

        _base = (Button) view.findViewById(R.id.base);
        _base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBaseSelectionDialog();
            }
        });
        _base.setText("2");

        _number = (EditText) view.findViewById(R.id.number);
        _number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(_listener != null)
                    _listener.onNumberChanged(NumberFragment.this, getNumber());
            }
        });

        _number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(_listener != null)
                    _listener.onFocusChange(NumberFragment.this);
            }
        });

        if(_appKeyboard) {
            _number.setInputType(InputType.TYPE_NULL);
        }

        return view;
    }

    private void showBaseSelectionDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.base_dropdown, (ViewGroup) getView(), false);
        GridView grid = (GridView) view.findViewById(R.id.grid);
        grid.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.base_dropdown_item, _Bases));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setView(view).setTitle(R.string.base);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        final AlertDialog dialog = builder.create();

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setBase(position + 2);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v == _number && _contextMenuRes != 0) {
            getActivity().getMenuInflater().inflate(_contextMenuRes, menu);
            getActivity().onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return getActivity().onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        _appKeyboard = preferences.getBoolean(getResources().getString(R.string.pref_id_use_app_keyboard), true);

        if(_number != null) {
            if(_appKeyboard) {
                _number.setInputType(InputType.TYPE_NULL);
            } else
                setBase(getBase());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnNumberChangedListener {
        void onNumberChanged(NumberFragment fragment, String newNumber);
        void onBaseChanged(NumberFragment fragment, int newBase);
        void onFocusChange(NumberFragment fragment);
    }
}
