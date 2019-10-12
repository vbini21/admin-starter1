package com.github.adminfaces.starter.bean.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	public static String CurrentTime() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }
}
