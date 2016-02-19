package com.letv.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {

	/**
	 * 解压
	 * @param zipPath
	 * @param destDir
	 * @throws Exception
	 */
	public static void unZip(String zipPath, String destDir) throws Exception {

		ZipFile zf = null;
		try {
			zf = new ZipFile(zipPath);
			Enumeration<? extends ZipEntry> entries = zf.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				File f = new File(destDir + entry.getName());
				if (entry.isDirectory()) {
					continue;
				}

				File parent = f.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}

				InputStream inStream = null;
				FileOutputStream outStream = null;
				try {

					inStream = zf.getInputStream(entry);
					outStream = new FileOutputStream(destDir + entry.getName());
					int len = 0;
					int size = 1024 * 6;
					byte[] bs = new byte[size];
					while ((len = inStream.read(bs, 0, size)) > 0) {
						outStream.write(bs, 0, len);
					}

				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception(e);
				} finally {
					if (inStream != null) {
						inStream.close();
					}
					if (outStream != null) {
						outStream.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (zf != null) {
				try {
					zf.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
