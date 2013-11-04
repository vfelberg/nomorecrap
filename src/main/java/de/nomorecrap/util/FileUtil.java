package de.nomorecrap.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

//	public static final boolean FIRST_MATCH = true;

	public static void writeFile(String filename, String s) {
    File f = new File(filename);
    writeFile(f, s);
	}

  public static void writeFile(File file, String string) {
    ensureNewFile(file);
    BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(string);
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception ignore) {
			}
		}
  }

//	private static void ensureNewFile(String filename) {
//		ensureNewFile(new File(filename));
//	}

	private static void ensureNewFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
	
	public static String readFile(String filename) {
		StringBuilder s = new StringBuilder();
		BufferedReader in = null;
		try {
      in = new BufferedReader(new FileReader("infilename"));
			String str;
			while ((str = in.readLine()) != null) {
				s.append(str).append("\n"); // TODO RBE make this newline OS safe.
			}			
		} catch (IOException e) {
      e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		return s.toString();
	}

//	public static String getLatestPlugin(String prefix, final String packageName, boolean firstMatch) throws IOException {
//		List<File> files = getAllFilesInDirMatchingPattern(prefix, "^"+packageName+".*", firstMatch);
//		if (files == null)
//			return null;
//		Collections.sort(files, Collections.reverseOrder());		
//		return files.get(0).getCanonicalPath();
//	}

	public static List<File> getAllFilesInDirMatchingPattern(String directory,
                                                           final String regex, 
                                                           final boolean first_match2) {
    File directoryFile = new File(directory);
    checkDirectory(directoryFile);

    FilenameFilter fileFilter = new FilenameFilter() {
      private boolean matchedyet = false;

      public boolean accept(File dir, String name) {
        if (first_match2 && matchedyet)
          return false;
        else {
          boolean matched = name.matches(regex);
          if (matched) {
            matchedyet = matched;
          }
          return matched;
        }
      }
    };
    try {
      return listFiles(directoryFile, fileFilter);
    } catch (RuntimeException e) {
      throw new RuntimeException("problem adding files matching [" + regex + "] in dir " + directory, e);
    }
  }

	private static List<File> listFiles(File directory, FilenameFilter fileFilter) {
	  checkDirectory(directory);
    List<File> matchingFiles = new ArrayList<File>();
    for (File file : directory.listFiles()) {
      if (passesFilter(directory, fileFilter, file)) {
        matchingFiles.add(file);
      }
      if (file.isDirectory()) {
        matchingFiles.addAll(listFiles(file, fileFilter));
      }
    }
    return matchingFiles;
  }

  private static boolean passesFilter(File directory, FilenameFilter fileFilter, File file) {
    return fileFilter == null || fileFilter.accept(directory, file.getName());
  }

  private static void checkDirectory(File directory) {
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException(directory.getAbsolutePath()
          + " is not a directory");
    }
  }

	public static void eraseFile(String file) {
		File f = new File(file);
		ensureNewFile(f);
	}

	public static String getTmpFile(String string) {				
		return tmpDir()+File.separator+string;
	}

	public static String tmpDir() {
		String tmpDirName = System.getProperty("java.io.tmpDir", "/tmp");
		File f = new File(tmpDirName);
		if (!f.exists())
			f.mkdir();
		return tmpDirName;
	}
	
	public static String getSubTmpDir(String tmpDirName) throws IOException {
		File tmpDir = new File(tmpDir(), tmpDirName);
		tmpDir.mkdir();
		return tmpDir.getCanonicalPath();
	}

	public static List<String> makeRelativePaths(String classDir, List<File> files) {
		List<String> relativePaths = new ArrayList<String>();
		for (File file : files) {
			int prefixEnd = classDir.length() + 1;
			relativePaths .add(file.getPath().substring(prefixEnd));
		}
	  return relativePaths;
	}

  public static void ensureDirectory(String outputDir) {
    File outputDirFile = new File(outputDir);
    if (!outputDirFile.exists()) {
      outputDirFile.mkdirs();
    }
  }

  public static void ensureCleanDirectory(String outputDir) {
    File outputDirFile = new File(outputDir);
    if (outputDirFile.exists()) {
      deleteDirectory(outputDirFile);
    }
    outputDirFile.mkdirs();
  }

  public static void deleteDirectory(File outputDirFile) {
    for (String file : outputDirFile.list()) {
      File f = new File(outputDirFile, file);
      if (f.isDirectory()) {
        deleteDirectory(f);
      } else {
        f.delete();
      }
    }
    outputDirFile.delete();
  }

  
  public static String joinPath(String basePath, String path) {
    File absolutePath = new File(basePath, path);
    return absolutePath.getAbsolutePath();
  }

  public static boolean isAbsolute(String path) {
	  if (path == null)
		  return false;
    return path.startsWith(File.separator) || path.indexOf(':') != -1;
  }

  public static List<File> removeTestClassFiles(List<File> files) {
    List<File> withoutTestFiles = new ArrayList<File>();
  	for (File file : files) {
      if (!isTestClass(file)) {
  			withoutTestFiles.add(file);
      }
  	}
    return withoutTestFiles;
  }
  
  private static boolean isTestClass(File file) {
    return new TestClassChecker().isTestClass(file);
  }

  public static boolean hasTestClassFiles(List<File> files) {
    for (File file : files) {
      if (isTestClass(file)) {
        return true;
      }
    }
    return false;
  }

  private static String getFileNameWithoutExtension(File file) {
    String fileName = file.getName();
    int lastDot = fileName.lastIndexOf('.');
    if (lastDot != -1) {
      fileName = fileName.substring(0, lastDot);
    }
    return fileName;
  }

  private static boolean isTestFile(String fileName) {
    return (fileName.indexOf("AgitarTest") != -1) ||
        (fileName.endsWith("Test")) ||
        (fileName.startsWith("Test"));
  }

  public static List<File> removeNonTestClassFiles(List<File> classesInDirList) {
      List<File> withoutNonTestFiles = new ArrayList<File>();
      for (File file : classesInDirList) {
        if (isTestClass(file)) {
          withoutNonTestFiles.add(file);
        }
      }
      return withoutNonTestFiles;
    }

  public static List<String> directoriesOnly(List<String> classDirs) {
    List<String> directories = new ArrayList<String>();
    for (String pathElement : directories) {
      if (new File(pathElement).isDirectory()) {
        directories.add(pathElement);
      }
    }
    return directories;
  }

  public static List<String> directoriesAndJarsOnly(List<String> classDirs) {
    List<String> directories = new ArrayList<String>();
    for (String pathElement : classDirs) {
      if (pathElement.endsWith(".jar") || new File(pathElement).isDirectory()) {
        directories.add(pathElement);
      }
    }
    return directories;
  }
  
}
