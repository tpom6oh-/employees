package com.tpom6oh.employees;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tpom6oh.employees.model.EmployeesProvider;
import com.tpom6oh.employees.model.employee.EmployeeColumns;

public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SEARCH_DIALOG_FRAGMENT_TAG = "search dialog fragment";
    private SimpleCursorAdapter cursorAdapter;
    private ListView listView;
    private static String[] PROJECTION = new String[]{EmployeeColumns.NAME,
            EmployeeColumns.COMPANY,
            EmployeeColumns.SALARY};;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listView = (ListView) findViewById(R.id.employees_list_view);
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.headered_employee_list_item, null,
                                                PROJECTION, new int[] {R.id.employee_name,
                                                                       R.id.header,
                                                                       R.id.employee_salary}, 0);
        listView.setAdapter(cursorAdapter);

        getSupportLoaderManager().initLoader(0, null, this);

        Intent downloadData = new Intent(this, EmployeesDataLoaderService.class);
        startService(downloadData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                FragmentManager fm = getSupportFragmentManager();
                SearchDialogFragment searchDialogFragment =
                        new SearchDialogFragment();
                searchDialogFragment.show(fm, SEARCH_DIALOG_FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFilter(FilterHolder filterHolder) {
        getSupportLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this, EmployeeColumns.CONTENT_URI,
                                EmployeeColumns.FULL_PROJECTION, null, null,
                                EmployeeColumns.COMPANY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursorAdapter.swapCursor(null);
    }

    public static class SearchDialogFragment extends DialogFragment implements LoaderManager
            .LoaderCallbacks<Cursor> {

        public static final int DIVISION_LOADER_ID = 0;
        public static final int YEAR_LOADER_ID = 1;
        private static String FILTER_HOLDER_TAG = "filter holder";
        private FilterHolder filterHolder;
        private Spinner divisionSpinner;
        private Spinner yearSpinner;
        private EditText companyNameEditText;
        private EditText employeeNameEditText;
        private SeekBar minSalaryBar;
        private TextView minSalaryText;
        private SeekBar maxSalaryBar;
        private TextView maxSalaryText;
        private Button searchButton;
        private Button resetButton;
        private Button cancelButton;

        private MainActivity mainActivity;
        private SimpleCursorAdapter divisionAdapter;
        private SimpleCursorAdapter yearAdapter;

        public SearchDialogFragment() {}

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mainActivity = (MainActivity) activity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState == null) {
                filterHolder = new FilterHolder();
            } else {
                filterHolder = savedInstanceState.getParcelable(FILTER_HOLDER_TAG);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getDialog().setTitle(R.string.search_label);

            View filterView = getActivity().getLayoutInflater().inflate(R.layout.search_dialog,
                                                                        null);
            initViews(filterView);
            setSpinners();
            setButtons();
            setSeekBars();

            return filterView;
        }

        private void setSeekBars() {
            SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (seekBar.getId() == R.id.max_salary_bar) {
                        maxSalaryText.setText(getString(R.string.max_salary_text) + i + "$");
                    } else if (seekBar.getId() == R.id.min_salary_seek_bar) {
                        minSalaryText.setText(getString(R.string.min_salary_text) + i + "$");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };
            maxSalaryBar.setOnSeekBarChangeListener(listener);
            minSalaryBar.setOnSeekBarChangeListener(listener);
        }

        private void setButtons() {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.search_button:
                            int minSalary = minSalaryBar.getProgress();
                            int maxSalary = maxSalaryBar.getProgress();

                            if (minSalary >= maxSalary) {
                                Toast.makeText(getActivity(),
                                               getActivity().getString(R.string.salary_error),
                                               Toast.LENGTH_SHORT).show();
                                return;
                            }

                            filterHolder.employeeName = employeeNameEditText.getText().toString();
                            filterHolder.companyName = companyNameEditText.getText().toString();
                            filterHolder.maxSalary = maxSalary;
                            filterHolder.minSalary = minSalary;
                            mainActivity.onFilter(filterHolder);
                            dismiss();
                            break;
                        case R.id.cancel_button:
                            dismiss();
                            break;
                        case R.id.reset_button:
                            resetFields();
                            break;
                    }
                }
            };
            cancelButton.setOnClickListener(onClickListener);
            resetButton.setOnClickListener(onClickListener);
            searchButton.setOnClickListener(onClickListener);
        }

        private void initViews(View filterView) {
            divisionSpinner = (Spinner) filterView.findViewById(R.id.division_spinner);
            yearSpinner = (Spinner) filterView.findViewById(R.id.employment_year_spinner);
            minSalaryBar = (SeekBar) filterView.findViewById(R.id.min_salary_seek_bar);
            minSalaryText = (TextView) filterView.findViewById(R.id.min_salary_text);
            maxSalaryBar = (SeekBar) filterView.findViewById(R.id.max_salary_bar);
            maxSalaryText = (TextView) filterView.findViewById(R.id.max_salary_text);
            companyNameEditText = (EditText) filterView.findViewById(R.id.company_name_in);
            employeeNameEditText = (EditText) filterView.findViewById(R.id.employee_name_in);
            cancelButton = (Button) filterView.findViewById(R.id.cancel_button);
            searchButton = (Button) filterView.findViewById(R.id.search_button);
            resetButton = (Button) filterView.findViewById(R.id.reset_button);
        }

        private void setSpinners() {
            AdapterView.OnItemSelectedListener selectedListener = new MyOnItemSelectedListener();

            divisionAdapter = getSpinnerCursorAdapter(EmployeeColumns.DIVISION);
            divisionSpinner.setAdapter(divisionAdapter);
            getLoaderManager().initLoader(DIVISION_LOADER_ID, null, this);
            divisionSpinner.setOnItemSelectedListener(selectedListener);

            yearAdapter = getSpinnerCursorAdapter(EmployeeColumns.EMPLOYMENT_YEAR);
            yearSpinner.setAdapter(yearAdapter);
            getLoaderManager().initLoader(YEAR_LOADER_ID, null, this);
            yearSpinner.setOnItemSelectedListener(selectedListener);
        }

        private SimpleCursorAdapter getSpinnerCursorAdapter(String targetColumn) {
            SimpleCursorAdapter simpleCursorAdapter =
                    new SimpleCursorAdapter(getActivity(),
                                            android.R.layout.simple_spinner_item,
                                            null,
                                            new String[]{targetColumn},
                                            new int[]{android.R.id.text1},
                                            0);
            simpleCursorAdapter.setDropDownViewResource(android.R.layout
                                                            .simple_spinner_dropdown_item);
            return simpleCursorAdapter;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable(FILTER_HOLDER_TAG, filterHolder);
        }

        private void resetFields() {
            divisionSpinner.setSelection(0);
            yearSpinner.setSelection(0);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String column;
            switch (i) {
                case DIVISION_LOADER_ID:
                    column = EmployeeColumns.DIVISION;
                    break;
                case YEAR_LOADER_ID:
                    column = EmployeeColumns.EMPLOYMENT_YEAR;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            String[] projection = new String[]{EmployeeColumns._ID, column};
            Uri groupedUri = EmployeesProvider.groupBy(EmployeeColumns.CONTENT_URI, column);
            return new CursorLoader(getActivity(), groupedUri, projection, null, null, column);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> objectLoader, Cursor cursor) {
            swapSpinnerCursor(objectLoader, cursor);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> objectLoader) {
            swapSpinnerCursor(objectLoader, null);
        }

        private void swapSpinnerCursor(Loader<Cursor> objectLoader, Cursor cursor) {
            switch (objectLoader.getId()) {
                case DIVISION_LOADER_ID:
                    divisionAdapter.swapCursor(cursor);
                    break;
                case YEAR_LOADER_ID:
                    yearAdapter.swapCursor(cursor);
            }
        }

        private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

            public MyOnItemSelectedListener() {}

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerText = ((TextView)view).getText().toString();
                if (adapterView.getId() == R.id.division_spinner) {
                    filterHolder.divsion = spinnerText;
                } else if (adapterView.getId() == R.id.employment_year_spinner) {
                    filterHolder.employmentYear = Integer.parseInt(spinnerText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        }
    }

    private static class FilterHolder implements Parcelable {
        private int minSalary;
        private int maxSalary;
        private String divsion;
        private int employmentYear;
        private String companyName;
        private String employeeName;

        public FilterHolder(int minSalary, int maxSalary, String divsion, int employmentYear,
                             String companyName, String employeeName) {
            this.minSalary = minSalary;
            this.maxSalary = maxSalary;
            this.divsion = divsion;
            this.employmentYear = employmentYear;
            this.companyName = companyName;
            this.employeeName = employeeName;
        }

        public FilterHolder(Parcel in){
            minSalary = in.readInt();
            maxSalary = in.readInt();
            divsion = in.readString();
            employmentYear = in.readInt();
            companyName = in.readString();
            employeeName = in.readString();
        }

        private FilterHolder() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(minSalary);
            out.writeInt(maxSalary);
            out.writeString(divsion);
            out.writeInt(employmentYear);
            out.writeString(companyName);
            out.writeString(employeeName);
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public FilterHolder createFromParcel(Parcel in) {
                return new FilterHolder(in);
            }

            public FilterHolder[] newArray(int size) {
                return new FilterHolder[size];
            }
        };
    }
}
