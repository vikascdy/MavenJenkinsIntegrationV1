// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.agent.launcher.classloader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to create URLs of jar-files and class directories located
 * in specified directory. It walks through directory tree recursively starting
 * from specified point and searches for *.class and *.jar files. When class
 * file is found, DirURLFactory parses class file in order to get full class
 * name, substructs it from the current file path and constructs URL of the
 * result folder. DirURLFactory ignores classes which are located out of their
 * packages or which have corrupted structure.
 */
public class DirTraverser {

	public static Pattern createExtendedPathPattern(final String patterns,
			final String delimiters) {
		if (patterns == null) {
			return null;
		}
		final StringBuilder rx = new StringBuilder();
		final StringTokenizer patternstok = new StringTokenizer(patterns,
				delimiters);
		if (!patternstok.hasMoreTokens()) {
			return Pattern.compile("<cannot match>");
		}
		while (patternstok.hasMoreTokens()) {
			String pattern = patternstok.nextToken().replace('\\', '/');
			if (!pattern.startsWith("/") && !pattern.startsWith("**")) {
				// Fix for bug #74485
				pattern = "**/" + pattern;
			}
			if (rx.length() > 0) {
				rx.append('|');
			}
			if (pattern.endsWith("/")) {
				pattern += "**";
			}
			if (pattern.equals("**")) {
				rx.append(".*");
				break;
			}
			final Matcher m = Pattern.compile(
					"/\\*\\*/|/\\*\\*|\\*\\*/|/\\*$|\\*|\\?|/|[^*/\\?]+")
					.matcher(pattern);
			while (m.find()) {
				final String t = m.group();
				if (t.equals("/**")) {
					rx.append("/.*");
				} else if (t.equals("**/")) {
					rx.append("(.*/|)");
				} else if (t.equals("/**/")) {
					rx.append("(/.*/|/)");
				} else if (t.equals("/*")) {
					rx.append("/[^/]+");
				} else if (t.equals("*")) {
					rx.append("[^/]*");
				} else if (t.equals("?")) {
					rx.append("[^/]{1}");
				} else {
					rx.append(Pattern.quote(t));
				}
			}
		}
		final String rxs = rx.toString();
		return Pattern.compile(rxs);
	}

	private static FileFilter smFileFilter = new FileFilter() {
		@Override
		public boolean accept(final File file) {
			return file.isDirectory()
					|| (file.isFile() && file.length() > 0 && (file.getName()
							.endsWith(".jar") || file.getName().endsWith(
							".class")));
		}
	};

	private Set<File> mURLs;

	public DirTraverser(final File classDir) {
		mURLs = new HashSet<File>();
		collectClassPath(classDir);
	}

	public File[] getFiles() {
		final File[] result = new File[mURLs.size()];
		return mURLs.toArray(result);
	}

	protected void collectClassPath(final File file) {
		if (file != null) {
			if (file.isDirectory()) {
				final File[] files = file.listFiles(smFileFilter);
				for (final File f : files) {
					collectClassPath(f);
				}
			} else if (file.isFile()) {
				if (file.getName().endsWith(".class")) {
					final String classPath = getClassPath(file);
					if (classPath != null) {
						mURLs.add(new File(classPath));
					}
				} else {
					// here can be only ".jar" due to directory traverser
					// filter
					mURLs.add(file);
				}
			}
		}
	}

	protected static String getClassPath(File file) {
		String result = null;
		// Read class name from class file
		final String sClassName = JavaClassParser.getClassName(file);
		if (null != sClassName) {
			try {
				// Get parent directory
				file = new File(file.getCanonicalPath());
				if (null != file.getParentFile()) {
					file = file.getParentFile();
					// Normalize file path
					String sPath;
					if (File.separatorChar == '\\') {
						sPath = file.getCanonicalPath().replace('\\', '/');
					} else {
						sPath = file.getCanonicalPath();
					}

					// If class is contained in some package, package name
					// should be substracted from file path.
					final int nDotIndex = sClassName.lastIndexOf('/');
					if (nDotIndex > 0) {
						final String sPackageName = sClassName.substring(0,
								nDotIndex);

						// Substact package name from file
						final int nPackageIndex = sPath
								.lastIndexOf(sPackageName) - 1;
						if (nPackageIndex >= 0) {
							result = sPath.substring(0, nPackageIndex);
						}
					} else {
						result = sPath;
					}
				}
			} catch (final IOException ex) {
				// TODO: Determine what to do here
				ex.printStackTrace();

			}
		}
		return result;
	}

	/**
	 * Java class parser is a short implementation of Java class file parser.
	 * The main goal of this class is providing full name of the class.
	 */
	static class JavaClassParser {
		public static final byte CONSTANT_UTF8 = 1;
		public static final byte CONSTANT_INTEGER = 3;
		public static final byte CONSTANT_FLOAT = 4;
		public static final byte CONSTANT_LONG = 5;
		public static final byte CONSTANT_DOUBLE = 6;
		public static final byte CONSTANT_CLASS = 7;
		public static final byte CONSTANT_FIELDREF = 9;
		public static final byte CONSTANT_STRING = 8;
		public static final byte CONSTANT_METHODREF = 10;
		public static final byte CONSTANT_INTERFACEMETHODREF = 11;
		public static final byte CONSTANT_NAMEANDTYPE = 12;

		public static String getClassName(final File file) {
			DataInputStream datastrm = null;
			String result = null;
			try {
				datastrm = new DataInputStream(new FileInputStream(file));
				result = parseClassName(datastrm);
			} catch (final Exception ex) {
				// TODO: Determine what to do here
				ex.printStackTrace();
			} finally {
				try {
					if (null != datastrm) {
						datastrm.close();
					}
				} catch (final Exception ex) {
					// TODO: Determine what to do here
					ex.printStackTrace();
				}
			}
			return result;
		}

		// This function is short piece of Java class parser that parse only
		// class name
		// and nothing else. It was written in conformance with Java class
		// specification.
		private static String parseClassName(final DataInputStream strm)
				throws Exception {
			// Read magic word
			final int nMagic = 0xCAFEBABE;
			if (nMagic != strm.readInt()) {
				return null;
			}
			// Read minor version
			strm.readUnsignedShort();
			// Read major version
			strm.readUnsignedShort();

			// Constant pool parsing
			final Map<Integer, Integer> mapClassConstants = new HashMap<Integer, Integer>();
			final Map<Integer, String> mapStringConstants = new HashMap<Integer, String>();
			parseConstantPool(strm, mapClassConstants, mapStringConstants);

			// Class access flags
			strm.readUnsignedShort();
			final int nClassIndex = strm.readUnsignedShort();

			// Parsing finished ... due to rest of class file is unnecessary.

			// Find class name
			final Integer classNameIndex = mapClassConstants.get(new Integer(
					nClassIndex));
			return mapStringConstants.get(classNameIndex);
		}

		private static void parseConstantPool(final DataInputStream strm,
				final Map<Integer, Integer> classConstants,
				final Map<Integer, String> stringConstants) throws Exception {
			// Note from the JVM specification:
			// "All eight byte constants take up two spots in the constant pool.
			// If this is the n'th byte in the constant pool, then the next item
			// will be numbered n+2".
			// (See additional index increment when Long and Double constants
			// appear.)

			final int nConstantPoolSize = strm.readUnsignedShort();
			for (int i = 1; i < nConstantPoolSize; ++i) {
				// Read pool entry tag
				final byte tag = strm.readByte();
				switch (tag) {
				case CONSTANT_CLASS:
					final int nClassNameIndex = strm.readUnsignedShort();
					classConstants.put(new Integer(i), new Integer(
							nClassNameIndex));
					break;
				case CONSTANT_NAMEANDTYPE:
				case CONSTANT_FIELDREF:
				case CONSTANT_METHODREF:
				case CONSTANT_INTERFACEMETHODREF:
					strm.readUnsignedShort();
					strm.readUnsignedShort();
					break;
				case CONSTANT_STRING:
					strm.readUnsignedShort();
					break;
				case CONSTANT_INTEGER:
					strm.readInt();
					break;
				case CONSTANT_FLOAT:
					strm.readFloat();
					break;
				case CONSTANT_LONG:
					strm.readLong();
					++i;
					break;
				case CONSTANT_DOUBLE:
					strm.readDouble();
					++i;
					break;
				case CONSTANT_UTF8:
					stringConstants.put(new Integer(i), strm.readUTF());
					break;
				default:
					break;
				}
			}
		}
	}
}
