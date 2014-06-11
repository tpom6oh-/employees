package com.tpom6oh.employees;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.tpom6oh.employees.model.employee.EmployeeColumns;


public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

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
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this, EmployeeColumns.CONTENT_URI,
                                EmployeeColumns.FULL_PROJECTION, null, null,
                                EmployeeColumns.COMPANY);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader,
                               Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        cursorAdapter.swapCursor(null);
    }
}
