package com.app.numconv;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalculatorActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalculatorActionFragment extends Fragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalculatorActionFragment.
     */
    public static CalculatorActionFragment newInstance() {
        return new CalculatorActionFragment();
    }

    private ToggleButton _add;
    private ToggleButton _subtract;
    private ToggleButton _multiply;
    private ToggleButton _divide;
    private ToggleButton[] _buttons;

    private OnActionChangedListener _listener;

    public CalculatorActionFragment() {
        // Required empty public constructor
    }

    private ToggleButton getButtonByAction(MathAction action) {
        switch (action) {
            case Add:
                return _add;
            case Subtract:
                return _subtract;
            case Multiply:
                return _multiply;
            case Divide:
                return _divide;
        }

        throw new IllegalArgumentException("action");
    }

    public void setOnActionChangedListener(OnActionChangedListener listener) {
        _listener = listener;
    }

    public MathAction getAction() {
        if(_add.isChecked())
            return MathAction.Add;
        if(_subtract.isChecked())
            return MathAction.Subtract;
        if(_multiply.isChecked())
            return MathAction.Multiply;
        if(_divide.isChecked())
            return MathAction.Divide;

        throw new UnsupportedOperationException("unknown action");
    }

    public void setAction(MathAction action) {
        ToggleButton tb = getButtonByAction(action);
        tb.setChecked(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator_action, container, false);

        _add = (ToggleButton) view.findViewById(R.id.add);
        _subtract = (ToggleButton) view.findViewById(R.id.subtract);
        _multiply = (ToggleButton) view.findViewById(R.id.multiply);
        _divide = (ToggleButton) view.findViewById(R.id.divide);
        _buttons = new ToggleButton[] { _add, _subtract, _multiply, _divide };
        _add.setChecked(true);

        CompoundButton.OnCheckedChangeListener buttonCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            private boolean _updating = false;

            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                if(isChecked) {
                    _updating = true;
                    for (ToggleButton button : _buttons) {
                        if (button != view)
                            button.setChecked(false);
                    }
                    _updating = false;

                    if(_listener != null)
                        _listener.onActionChanged(CalculatorActionFragment.this);
                } else if(!_updating) {
                    view.setChecked(true);
                }
            }
        };

        for (ToggleButton button : _buttons)
            button.setOnCheckedChangeListener(buttonCheckedChangeListener);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public enum MathAction {
        Add,
        Subtract,
        Multiply,
        Divide
    }

    public interface OnActionChangedListener {
        void onActionChanged(CalculatorActionFragment fragment);
    }
}