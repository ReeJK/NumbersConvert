package com.app.numconv;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment KeyboardFragment.
     */
    public static KeyboardFragment newInstance() {
        KeyboardFragment fragment = new KeyboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private int _currentLayout = -1;

    private String _deleteKey = "Del";
    
    public KeyboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_keyboard, container, false);
        if(_currentLayout != -1) {
            LinearLayout table = (LinearLayout) root.findViewById(R.id.keyboard_table);
            new KeyboardLayout(getActivity()).Generate(table, _currentLayout);
        }

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //_deleteKey = getResources().getString(R.string.delete_text);
    }

    public void setLayout(int system) {
        if(_currentLayout != system) {
            View root = getView();
            if(root != null) {
                LinearLayout table = (LinearLayout)root.findViewById(R.id.keyboard_table);
                table.removeAllViews();
                new KeyboardLayout(getActivity()).Generate(table, system);
            }

            _currentLayout = system;
        }
    }

    void emulateInput(int keycode) {
        getActivity().dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keycode, 0));
        getActivity().dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, keycode, 0));
    }

    void emulateInput(char ch) {
        if(ch >= '0' && ch <= '9')
            emulateInput(KeyEvent.KEYCODE_0 + (ch - '0'));
        else if(ch >= 'a' && ch <= 'z')
            emulateInput(KeyEvent.KEYCODE_A + (ch - 'a'));
        else if(ch >= 'A' && ch <= 'Z')
            emulateInput(KeyEvent.KEYCODE_A + (ch - 'A'));
    }

    void emulateInput(CharSequence text) {
        if(text == _deleteKey)
            emulateInput(KeyEvent.KEYCODE_DEL);
        else if(text == ".")
            emulateInput(KeyEvent.KEYCODE_PERIOD);
        else if(text.length() == 1)
            emulateInput(text.charAt(0));
    }

    class KeyboardLayout {
        private View.OnClickListener _clickListener;

        public KeyboardLayout(Context context) {
            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            //boolean vibrationFeedback = preferences.getBoolean(getResources().getString(R.string.pref_id_enable_vibration_feedback), false);

            _clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v instanceof Button)
                        emulateInput(((Button) v).getText());
                    else
                        emulateInput(_deleteKey);

                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                }
            };
        }

        public void Generate(LinearLayout table, int system) {
            if(system == 2) {
                addRow(table, "0", "1", ".", _deleteKey);
            } else if(system == 3) {
                addRow(table, "0", "1", "2", ".", _deleteKey);
            } else if(system == 4) {
                addRow(table, "0", "1", _deleteKey);
                addRow(table, "2", "3", ".");
            } else if(system == 5) {
                addRow(table, "0", "1", "2", _deleteKey);
                addRow(table, "3", "4", ".");
            } else if(system == 6) {
                addRow(table, "0", "1", "2", _deleteKey);
                addRow(table, "3", "4", "5", ".");
            } else if(system == 7) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", ".");
            } else if(system == 8) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", "7", ".");
            } else if(system == 9) {
                addRow(table, "0", "1", "2", _deleteKey);
                addRow(table, "3", "4", "5", ".");
                addRow(table, "6", "7", "8");
            } else if(system == 10) {
                addRow(table, "0", "1", "2", _deleteKey);
                addRow(table, "3", "4", "5", ".");
                addRow(table, "6", "7", "8", "9");
            } else if(system == 11) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", ".");
                addRow(table, "7", "8", "9", "A");
            } else if(system == 12) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", "7", ".");
                addRow(table, "8", "9", "A", "B");
            } else if(system == 13) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", "7", ".");
                addRow(table, "8", "9", "A", "B", "C");
            } else if(system == 14) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", "7", "8", ".");
                addRow(table, "9", "A", "B", "C", "D");
            } else if(system == 15) {
                addRow(table, "0", "1", "2", "3", _deleteKey);
                addRow(table, "4", "5", "6", "7", "8", ".");
                addRow(table, "9", "A", "B", "C", "D", "E");
            } else if(system == 16) {
                addRow(table, "0", "1", "2", "3", "4", _deleteKey);
                addRow(table, "5", "6", "7", "8", "9", ".");
                addRow(table, "A", "B", "C", "D", "E", "F");
            } else if(system == 17) {
                addRow(table, "0", "1", "2", "3", "4", _deleteKey);
                addRow(table, "5", "6", "7", "8", "9", "A", ".");
                addRow(table, "B", "C", "D", "E", "F", "G");
            } else if(system == 18) {
                addRow(table, "0", "1", "2", "3", "4", _deleteKey);
                addRow(table, "5", "6", "7", "8", "9", "A", ".");
                addRow(table, "B", "C", "D", "E", "F", "G", "H");
            } else if(system == 19) {
                addRow(table, "0", "1", "2", "3", "4", "5", _deleteKey);
                addRow(table, "6", "7", "8", "9", "A", "B", ".");
                addRow(table, "C", "D", "E", "F", "G", "H", "I");
            } else if(system == 20) {
                addRow(table, "0", "1", "2", "3", "4", "5", _deleteKey);
                addRow(table, "6", "7", "8", "9", "A", "B", "C", ".");
                addRow(table, "D", "E", "F", "G", "H", "I", "J");
            } else if(system == 21) {
                addRow(table, "0", "1", "2", "3", "4", "5", _deleteKey);
                addRow(table, "6", "7", "8", "9", "A", "B", "C", ".");
                addRow(table, "D", "E", "F", "G", "H", "I", "J", "K");
            } else if(system == 22) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", _deleteKey);
                addRow(table, "7", "8", "9", "A", "B", "C", "D", ".");
                addRow(table, "E", "F", "G", "H", "I", "J", "K", "L");
            } else if(system == 23) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", _deleteKey);
                addRow(table, "7", "8", "9", "A", "B", "C", "D", "E", ".");
                addRow(table, "F", "G", "H", "I", "J", "K", "L", "M");
            } else if(system == 24) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", _deleteKey);
                addRow(table, "7", "8", "9", "A", "B", "C", "D", "E", ".");
                addRow(table, "F", "G", "H", "I", "J", "K", "L", "M", "N");
            } else if(system == 25) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", _deleteKey);
                addRow(table, "8", "9", "A", "B", "C", "D", "E", "F", ".");
                addRow(table, "G", "H", "I", "J", "K", "L", "M", "N", "O");
            } else if(system == 26) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", _deleteKey);
                addRow(table, "8", "9", "A", "B", "C", "D", "E", "F", "G", ".");
                addRow(table, "H", "I", "J", "K", "L", "M", "N", "O", "P");
            } else if(system == 27) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", _deleteKey);
                addRow(table, "8", "9", "A", "B", "C", "D", "E", "F", "G", ".");
                addRow(table, "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q");
            } else if(system == 28) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", _deleteKey);
                addRow(table, "9", "A", "B", "C", "D", "E", "F", "G", "H", ".");
                addRow(table, "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R");
            } else if(system == 29) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", _deleteKey);
                addRow(table, "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", ".");
                addRow(table, "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S");
            } else if(system == 30) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", _deleteKey);
                addRow(table, "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", ".");
                addRow(table, "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T");
            } else if(system == 31) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                addRow(table, "A", "B", "C", "D", "E", "F", "G", _deleteKey);
                addRow(table, "H", "I", "J", "K", "L", "M", "N", "O");
                addRow(table, "P", "Q", "R", "S", "T", "U", ".");
            } else if(system == 32) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                addRow(table, "A", "B", "C", "D", "E", "F", "G", "H", _deleteKey);
                addRow(table, "I", "J", "K", "L", "M", "N", "O", "P");
                addRow(table, "Q", "R", "S", "T", "U", "V", ".");
            } else if(system == 33) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                addRow(table, "A", "B", "C", "D", "E", "F", "G", "H", _deleteKey);
                addRow(table, "I", "J", "K", "L", "M", "N", "O", "P");
                addRow(table, "Q", "R", "S", "T", "U", "V", "W", ".");
            } else if(system == 34) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                addRow(table, "A", "B", "C", "D", "E", "F", "G", "H", _deleteKey);
                addRow(table, "I", "J", "K", "L", "M", "N", "O", "P", "Q");
                addRow(table, "R", "S", "T", "U", "V", "W", "X", ".");
            } else if(system == 35) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                addRow(table, "A", "B", "C", "D", "E", "F", "G", "H", _deleteKey);
                addRow(table, "I", "J", "K", "L", "M", "N", "O", "P", "Q");
                addRow(table, "R", "S", "T", "U", "V", "W", "X", "Y", ".");
            } else if(system == 36) {
                addRow(table, "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
                addRow(table, "A", "B", "C", "D", "E", "F", "G", "H", _deleteKey);
                addRow(table, "I", "J", "K", "L", "M", "N", "O", "P", "Q");
                addRow(table, "R", "S", "T", "U", "V", "W", "X", "Y", "Z", ".");
            }
        }

        private void addRow(LinearLayout table, String... keys) {
            LinearLayout row = new LinearLayout(table.getContext());
            for(int i = 0; i < keys.length; i++)
                addButton(row, keys[i], keys[i] == _deleteKey ? 2 : 1);

            table.addView(row, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        }

        private void addButton(LinearLayout row, String text, int weight) {
            View view;
            if(text == _deleteKey) {
                ImageButton b = new ImageButton(row.getContext());
                b.setImageResource(R.drawable.ic_delete_final);
                b.setBackgroundResource(R.drawable.button_background);
                setElevation(b, 2);
                b.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        View focus = getActivity().getCurrentFocus();
                        if(focus instanceof EditText) {
                            EditText et = (EditText) focus;
                            et.setText(et.getText().subSequence(et.getSelectionStart(), et.length()));
                        }
                        return true;
                    }
                });
                view = b;
            } else {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Button b = (Button) inflater.inflate(R.layout.keyboard_button, (ViewGroup) getView(), false);
                b.setText(text);
                view = b;
            }

            view.setOnClickListener(_clickListener);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
            row.addView(view, lp);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void setElevation(View view, float elevation) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setElevation(elevation);
            }
        }
    }
}
