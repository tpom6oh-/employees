package com.tpom6oh.employees;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpom6oh.employees.model.employee.EmployeeColumns;

public class EmployeesAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;

    public EmployeesAdapter(Context context, Cursor c, LayoutInflater layoutInflater) {
        super(context, c, 0);
        this.layoutInflater = layoutInflater;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v;
        if (getViewType(cursor) == 0) {
            v = layoutInflater.inflate(R.layout.headered_employee_list_item, viewGroup, false);
        } else {
            v = layoutInflater.inflate(R.layout.employee_list_item, viewGroup, false);
        }

        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder;
        if (view.getTag() != null) {
            viewHolder = (ViewHolder) view.getTag();
        } else {
            viewHolder = new ViewHolder();
            viewHolder.employeeName = (TextView) view.findViewById(R.id.employee_name);
            viewHolder.employeeDivision = (TextView) view.findViewById(R.id.employee_division);
            viewHolder.employeeTeam = (TextView) view.findViewById(R.id.employee_team);
            viewHolder.employeeSalary = (TextView) view.findViewById(R.id.employee_salary);
            viewHolder.employmentDate = (TextView) view.findViewById(R.id.employee_employment_date);
            viewHolder.employeeImage = (ImageView) view.findViewById(R.id.employee_image);
            view.setTag(viewHolder);
        }
        int nameIndex = cursor.getColumnIndex(EmployeeColumns.NAME);
        viewHolder.employeeName.setText(cursor.getString(nameIndex));
        int divisionIndex = cursor.getColumnIndex(EmployeeColumns.DIVISION);
        viewHolder.employeeDivision.setText(cursor.getString(divisionIndex));
        int teamIndex;
        int salaryIndex = cursor.getColumnIndex(EmployeeColumns.SALARY);
        viewHolder.employeeSalary.setText(cursor.getString(salaryIndex));
        int dateIndex = cursor.getColumnIndex(EmployeeColumns.EMPLOYMENT_DATE);
        viewHolder.employmentDate.setText(cursor.getString(dateIndex));
        int imageIndex;
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();

        if (cursor == null || !cursor.moveToPosition(position)) {
            return 0;
        }

        return getViewType(cursor);
    }

    private int getViewType(Cursor cursor) {
        String currentCompany = cursor.getString(cursor.getColumnIndex(EmployeeColumns.COMPANY));
        String currentEnterprise = cursor.getString(cursor.getColumnIndex(EmployeeColumns
                                                                                  .ENTERPRISE));
        if (cursor.moveToPrevious()) {
            String prevCompany = cursor.getString(cursor.getColumnIndex(EmployeeColumns.COMPANY));
            String prevEnterprise = cursor.getString(cursor.getColumnIndex(EmployeeColumns
                                                                                      .ENTERPRISE));
            int type = !currentCompany.equals(prevCompany) || !currentEnterprise.equals(prevEnterprise)
                       ? 0 : 1;
            cursor.moveToNext();
            return type;
        } else {
            cursor.moveToNext();
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {
        TextView employeeName;
        TextView employeeDivision;
        TextView employeeTeam;
        TextView employeeSalary;
        TextView employmentDate;
        ImageView employeeImage;
    }
}
