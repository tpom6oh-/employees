package com.tpom6oh.employees.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.tpom6oh.employees.LruCacheArray;
import com.tpom6oh.employees.R;
import com.tpom6oh.employees.model.employee.EmployeeCursor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class EmployeesAdapter extends CursorAdapter {

    public static final String COLON = ": ";
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;
    private HashMap<String, Integer> columnIndexesCache = new HashMap<String, Integer>();
    private LruCacheArray lruCache = new LruCacheArray(100);

    public EmployeesAdapter(Context context, Cursor c, LayoutInflater layoutInflater) {
        super(context, c, 0);
        this.layoutInflater = layoutInflater;
        imageLoader = ImageLoader.getInstance();
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
        final BodyViewHolder bodyViewHolder = getBodyViewHolder(view);
        bindBody(context, cursor, bodyViewHolder);

        if (getViewType(cursor) == 0) {
            HeaderViewHolder headerViewHolder = getHeaderViewHolder(view);
            bindHeader(cursor, headerViewHolder);
        }
    }

    private void bindBody(Context context, Cursor cursor, BodyViewHolder bodyViewHolder) {
        imageLoader.cancelDisplayTask(bodyViewHolder.employeeImage);

        EmployeeCursor employeeCursor = new EmployeeCursor(cursor, columnIndexesCache);

        bodyViewHolder.employeeName.setText(employeeCursor.getName());
        bodyViewHolder.employeeDivision.setText(context.getString(R.string.division_label) + COLON + employeeCursor.getDivision());
        bodyViewHolder.employeeTeam.setText(context.getString(R.string.team_label) + COLON + employeeCursor.getTeam());
        bodyViewHolder.employeeSalary.setText(context.getString(R.string.salary_label) + COLON + employeeCursor.getSalary());
        bodyViewHolder.employmentDate.setText(context.getString(R.string.employment_date_label) +
                                              COLON + parseDate(employeeCursor.getEmploymentDate()));

        String imageUrl = employeeCursor.getImageUrl();
        imageLoader.displayImage(imageUrl, new ImageViewAware(bodyViewHolder.employeeImage));
    }

    private void bindHeader(Cursor cursor, HeaderViewHolder headerViewHolder) {
        EmployeeCursor employeeCursor = new EmployeeCursor(cursor, columnIndexesCache);
        String headerString = employeeCursor.getCompany() + " (" +
                              employeeCursor.getCountryName() + ", " + "" +
                              employeeCursor.getEnterprise() + ")";
        headerViewHolder.header.setText(headerString);
    }

    private String parseDate(Date date) {
        DateFormat df = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
        return df.format(date);
    }

    private HeaderViewHolder getHeaderViewHolder(View view) {
        HeaderViewHolder headerViewHolder;
        if (view.getTag(R.id.header) != null) {
            headerViewHolder = (HeaderViewHolder) view.getTag(R.id.header);
        } else {
            headerViewHolder = new HeaderViewHolder();
            headerViewHolder.header = (TextView) view.findViewById(R.id.header);
            view.setTag(R.id.header, headerViewHolder);
        }
        return headerViewHolder;
    }

    private BodyViewHolder getBodyViewHolder(View view) {
        BodyViewHolder viewHolder;
        if (view.getTag(R.id.employee_name) != null) {
            viewHolder = (BodyViewHolder) view.getTag(R.id.employee_name);
        } else {
            viewHolder = new BodyViewHolder();
            viewHolder.employeeName = (TextView) view.findViewById(R.id.employee_name);
            viewHolder.employeeDivision = (TextView) view.findViewById(R.id.employee_division);
            viewHolder.employeeTeam = (TextView) view.findViewById(R.id.employee_team);
            viewHolder.employeeSalary = (TextView) view.findViewById(R.id.employee_salary);
            viewHolder.employmentDate = (TextView) view.findViewById(R.id.employee_employment_date);
            viewHolder.employeeImage = (ImageView) view.findViewById(R.id.employee_image);
            view.setTag(R.id.employee_name, viewHolder);
        }
        return viewHolder;
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
        int position = cursor.getPosition();
        int cached = lruCache.get(position, -1);
        if (cached != -1) {
            return cached;
        }
        EmployeeCursor employeeCursor = new EmployeeCursor(cursor, columnIndexesCache);
        String currentCompany = employeeCursor.getCompany();
        String currentEnterprise = employeeCursor.getEnterprise();
        int type;
        if (cursor.moveToPrevious()) {
            String prevCompany = employeeCursor.getCompany();
            String prevEnterprise = employeeCursor.getEnterprise();
            type = !currentCompany.equals(prevCompany) || !currentEnterprise.equals(prevEnterprise)
                       ? 0 : 1;
        } else {
            type = 0;
        }
        cursor.moveToNext();
        lruCache.put(position, type);
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class BodyViewHolder {
        TextView employeeName;
        TextView employeeDivision;
        TextView employeeTeam;
        TextView employeeSalary;
        TextView employmentDate;
        ImageView employeeImage;
    }

    private class HeaderViewHolder {
        TextView header;
    }
}
