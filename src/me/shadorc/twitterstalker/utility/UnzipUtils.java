package me.shadorc.twitterstalker.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class UnzipUtils {

	public static void unzip(File jarFile, File destDir) throws IOException {

		JarFile jar = null;

		try {

			if(!destDir.exists()) {
				destDir.mkdir();
			}

			jar = new JarFile(jarFile);
			Enumeration <JarEntry> enumEntries = jar.entries();

			InputStream input = null;
			FileOutputStream output = null;

			while (enumEntries.hasMoreElements()) {
				try {
					JarEntry file = (JarEntry) enumEntries.nextElement();

					File f = new File(destDir + File.separator + file.getName());

					if (file.isDirectory()) { 
						f.mkdir();
						continue;
					}

					input = jar.getInputStream(file);
					output = new FileOutputStream(f);

					while(input.available() > 0) {
						output.write(input.read());
					}

				} finally {
					if(output != null) output.close();
					if(input != null) input.close();
				}
			}
		} finally {
			if(jar != null) jar.close();
		}
	}
}