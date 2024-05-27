package model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateRange {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd['T'HH:mm[:ss]]").toFormatter();

	public DateRange() {

	}

	public DateRange(LocalDateTime startDate, LocalDateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static long formatterTimeToEpochSecond(LocalDateTime time) {
		String timer = time.format(formatter);
		return LocalDateTime.parse(timer, formatter).toEpochSecond(ZoneOffset.UTC);
	}

	public static LocalDateTime formatterEpochSecondTotime(long time) {
		return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

}
