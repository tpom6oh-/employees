package com.tpom6oh.employees.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
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
import android.text.TextUtils;
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

import com.tpom6oh.employees.R;
import com.tpom6oh.employees.data.EmployeesDataLoaderService;
import com.tpom6oh.employees.model.EmployeesProvider;
import com.tpom6oh.employees.model.employee.EmployeeColumns;
import com.tpom6oh.employees.model.employee.EmployeeSelection;

public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SEARCH_DIALOG_FRAGMENT_TAG = "search dialog fragment";
    private static final String FILTER_HOLDER_TAG = "filter holder";
    public static final String SPINNER_DEFAULT_VALUE = "Any";
    public static final String SPINNER_DEFAULT_VALUE__ID = "-1";

    private static int MAX_SALARY;

    private EmployeesAdapter cursorAdapter;
    private FilterHolder filterHolder;

    private static String[] PROJECTION = new String[]{
            EmployeeColumns.NAME,
            EmployeeColumns.COMPANY,
            EmployeeColumns.SALARY,
            EmployeeColumns.COUNTRY_NAME,
            EmployeeColumns.TEAM,
            EmployeeColumns.DIVISION,
            EmployeeColumns.SALARY,
            EmployeeColumns.EMPLOYMENT_DATE,
            EmployeeColumns.IMAGE_URL,
            EmployeeColumns.ENTERPRISE,
            EmployeeColumns._ID};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MAX_SALARY = getResources().getInteger(R.integer.max_salary);
        initFilterHolder(savedInstanceState);

        setContentView(R.layout.main);
        setListView();

        getSupportLoaderManager().initLoader(0, null, this);

        updateData();
    }

    private void updateData() {
        Intent downloadData = new Intent(this, EmployeesDataLoaderService.class);
        startService(downloadData);
    }

    private void setListView() {
        ListView employeesList = (ListView) findViewById(R.id.employees_list_view);

        cursorAdapter = new EmployeesAdapter(this, null, getLayoutInflater());
        employeesList.setAdapter(cursorAdapter);
    }

    private void initFilterHolder(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            filterHolder = savedInstanceState.getParcelable(FILTER_HOLDER_TAG);
        } else {
            filterHolder = new FilterHolder();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(FILTER_HOLDER_TAG, filterHolder);
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
                showSearchDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SearchDialogFragment searchDialogFragment =
                new SearchDialogFragment();
        searchDialogFragment.show(fm, SEARCH_DIALOG_FRAGMENT_TAG);
    }

    /**
     * Restarts loader with current {@link #filterHolder}
     */
    public void onFilter() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Creates loader for {@link com.tpom6oh.employees.model.employee
     * .EmployeeColumns#CONTENT_URI} based on current {@link #filterHolder}
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        EmployeeSelection selection = new EmployeeSelection();

        if (!filterHolder.isEmpty()) {
            selection = generateSelection();
        }

        CursorLoader cursorLoader = new CursorLoader(this, EmployeeColumns.CONTENT_URI, PROJECTION,
                                selection.sel(), selection.args(), EmployeeColumns.COMPANY);
        return cursorLoader;
    }

    private EmployeeSelection generateSelection() {
        EmployeeSelection employeeSelection = new EmployeeSelection()
                .companyLike("%" + filterHolder.companyName + "%").and()
                .nameLike("%" + filterHolder.employeeName + "%").and()
                .salaryBetween(filterHolder.minSalary, filterHolder.maxSalary);

        if (!filterHolder.employmentYear.equals(SPINNER_DEFAULT_VALUE)) {
            employeeSelection.and().employmentYear(Integer.parseInt(filterHolder.employmentYear));
        }
        if (!filterHolder.division.equals(SPINNER_DEFAULT_VALUE)) {
            employeeSelection.and().division(filterHolder.division);
        }

        return employeeSelection;
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getDialog().setTitle(R.string.search_label);

            View filterView = getActivity().getLayoutInflater().inflate(R.layout.search_dialog,
                                                                        null);
            initViews(filterView);
            setSpinners();
            setButtons();
            setSeekBars();
            populateFields(mainActivity.filterHolder);

            return filterView;
        }

        private void populateFields(FilterHolder filterHolder) {
            minSalaryBar.setProgress(filterHolder.minSalary);
            maxSalaryBar.setProgress(filterHolder.maxSalary);
            companyNameEditText.setText(filterHolder.companyName);
            employeeNameEditText.setText(filterHolder.employeeName);
        }

        private void setSeekBars() {
            minSalaryText.setText(minSalaryText.getText().toString() + "0$");
            maxSalaryText.setText(maxSalaryText.getText().toString() + MAX_SALARY + "$");

            SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (seekBar.getId() == R.id.max_salary_bar) {
                        maxSalaryText.setText(getString(R.string.max_salary_text) + i + "$");
                    } else if (seekBar.getId() == R.id.min_salary_bar) {
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

                            mainActivity.filterHolder.employeeName =
                                    employeeNameEditText.getText().toString();
                            mainActivity.filterHolder.companyName =
                                    companyNameEditText.getText().toString();
                            mainActivity.filterHolder.maxSalary = maxSalary;
                            mainActivity.filterHolder.minSalary = minSalary;
                            mainActivity.onFilter();
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
            minSalaryBar = (SeekBar) filterView.findViewById(R.id.min_salary_bar);
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
            AdapterView.OnItemSelectedListener selectedListener = new OnSpinnerItemSelectedListener();

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

        private void resetFields() {
            divisionSpinner.setSelection(0);
            yearSpinner.setSelection(0);
            companyNameEditText.setText("");
            employeeNameEditText.setText("");
            minSalaryBar.setProgress(0);
            maxSalaryBar.setProgress(MAX_SALARY);
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
                    divisionAdapter.swapCursor(withDefaultField(cursor, EmployeeColumns.DIVISION));
                    divisionSpinner.setSelection(mainActivity.filterHolder.divisionSpinnerSelection);
                    break;
                case YEAR_LOADER_ID:
                    yearAdapter.swapCursor(withDefaultField(cursor, EmployeeColumns.EMPLOYMENT_YEAR));
                    yearSpinner.setSelection(mainActivity.filterHolder.yearSpinnerSelection);
                    break;
            }
        }

        private Cursor withDefaultField(Cursor cursor, String targetColumn) {
            MatrixCursor extras = new MatrixCursor(new String[] { EmployeeColumns._ID,
                                                                  targetColumn });
            extras.addRow(new String[] {SPINNER_DEFAULT_VALUE__ID, SPINNER_DEFAULT_VALUE });
            Cursor[] cursors = { extras, cursor };
            return new MergeCursor(cursors);
        }

        private class OnSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

            public OnSpinnerItemSelectedListener() {}

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerText = ((TextView)view).getText().toString();
                if (adapterView.getId() == R.id.division_spinner) {
                    mainActivity.filterHolder.division = spinnerText;
                    mainActivity.filterHolder.divisionSpinnerSelection = adapterView
                            .getSelectedItemPosition();
                } else if (adapterView.getId() == R.id.employment_year_spinner) {
                    mainActivity.filterHolder.employmentYear = spinnerText;
                    mainActivity.filterHolder.yearSpinnerSelection = adapterView
                            .getSelectedItemPosition();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        }
    }

    private static class FilterHolder implements Parcelable {
        private int minSalary;
        private int maxSalary = MAX_SALARY;
        private String division = SPINNER_DEFAULT_VALUE;
        private String employmentYear = SPINNER_DEFAULT_VALUE;
        private String companyName = "";
        private String employeeName = "";
        private int divisionSpinnerSelection;
        private int yearSpinnerSelection;

        public FilterHolder(Parcel in){
            minSalary = in.readInt();
            maxSalary = in.readInt();
            division = in.readString();
            employmentYear = in.readString();
            companyName = in.readString();
            employeeName = in.readString();
            divisionSpinnerSelection = in.readInt();
            yearSpinnerSelection = in.readInt();
        }

        private FilterHolder() {}

        private boolean isEmpty() {
            return TextUtils.isEmpty(companyName) && TextUtils.isEmpty(employeeName) && minSalary
            == 0 && maxSalary == MAX_SALARY && division.equals(SPINNER_DEFAULT_VALUE) &&
                   employmentYear.equals(SPINNER_DEFAULT_VALUE);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(minSalary);
            out.writeInt(maxSalary);
            out.writeString(division);
            out.writeString(employmentYear);
            out.writeString(companyName);
            out.writeString(employeeName);
            out.writeInt(divisionSpinnerSelection);
            out.writeInt(yearSpinnerSelection);
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
