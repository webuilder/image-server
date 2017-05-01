package cool.lijian.imageserver.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import cool.lijian.imageserver.NamingStrategy;

/**
 * Use current date as the directory's name, and use uuid as file name.<br>
 * Example: 2017/02/01/
 * 
 * @author Li Jian
 *
 */
public class DateNamingStrategy implements NamingStrategy {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	@Override
	public String createFileId(byte[] fileData, String originalFileName) {
		Instant instant = Instant.now();
		ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
		String dir = zdt.format(FORMATTER);
		String fileName = UUID.randomUUID().toString();

		// File's extension name
		String suffix = "";
		int lastIndex = originalFileName.lastIndexOf('.');
		if (lastIndex > 0) {
			suffix = originalFileName.substring(lastIndex).toLowerCase();
		}
		String fileId = dir + "/" + fileName + suffix;
		return fileId;
	}

}
