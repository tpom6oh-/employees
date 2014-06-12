package com.tpom6oh.employees;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.tpom6oh.employees.model.EmployeesProvider;
import com.tpom6oh.employees.model.employee.EmployeeColumns;

public class SpinnerPopulateAsyncTask extends AsyncTask<Void, Void,
        SimpleCursorAdapter> {

    private final String targetColumn;
    private final Spinner spinner;
    private final Context context;

    public SpinnerPopulateAsyncTask(String targetColumn, Spinner spinner,
                                    Context context) {
        this.targetColumn = targetColumn;
        this.spinner = spinner;
        this.context = context;
    }

    @Override
    protected SimpleCursorAdapter doInBackground(Void... voids) {
        String[] projection = new String[]{EmployeeColumns._ID, targetColumn};
        Uri groupedUri = EmployeesProvider.groupBy(EmployeeColumns.CONTENT_URI,
                                                   targetColumn);
        Cursor cursor = context.getContentResolver().query(groupedUri,
                                                                 projection,
                                                                 null,
                                                                 null,
                                                                 targetColumn);
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(context,
                                        android.R.layout.simple_spinner_item,
                                        cursor,
                                        new String[] {targetColumn},
                                        new int[] {android.R.id.text1},
                                        0);
        adapter.setDropDownViewResource(android.R.layout
                                                .simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    protected void onPostExecute(SimpleCursorAdapter cursorAdapter) {
        spinner.setAdapter(cursorAdapter);
    }
}
