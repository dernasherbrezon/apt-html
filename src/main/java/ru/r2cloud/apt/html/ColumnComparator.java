package ru.r2cloud.apt.html;

import java.util.Comparator;

import ru.r2cloud.apt.html.model.Column;

public class ColumnComparator implements Comparator<Column> {

	public static final ColumnComparator INSTANCE = new ColumnComparator();
	@Override
	public int compare(Column o1, Column o2) {
		int result = o1.getCodename().compareTo(o2.getCodename());
		if( result != 0 ) {
			return result;
		}
		return o1.getArch().compareTo(o2.getArch());
	}
}
