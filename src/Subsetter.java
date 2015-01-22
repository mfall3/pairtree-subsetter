import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.io.FileUtils;

import java.io.*;

import gov.loc.repository.pairtree.Pairtree;

public class Subsetter {

	public static void main(String[] args) throws IOException {

		//check arguments

		if (args.length != 3) exitWithMessage("Proper Usage is: java -jar subsetter.jar volumeFile sourceRoot destinationRoot");

		String volumeFilename = args[0];  // List of HT volume IDs
		String sourceRoot = args[1];  /// Root of source pairtree, must exist
		String destRoot = args[2];    // Root of destination pairtree, does not need to exist

		File volumeFile = new File(volumeFilename);
		if (!volumeFile.exists())
			exitWithMessage("Cannot open " + volumeFilename + ". Proper Usage is: java -jar subsetter.jar volumeFilename sourceRoot destinationRoot.");

		File sourceRootDir = new File(sourceRoot);
		if (!sourceRootDir.exists() || !sourceRootDir.isDirectory())
			exitWithMessage(sourceRoot + " not found or not a directory. Proper Usage is: java -jar subsetter.jar volumeFilename sourceRoot destinationRoot.");

		File destRootDir = new File(destRoot);
		if (!destRootDir.exists()) {
			System.out.println("creating directory: " + destRoot);
			destRootDir.mkdir();
			if (!destRootDir.exists())
				exitWithMessage("Unable to find or create directory " + destRoot + ". Proper Usage is: java -jar subsetter.jar volumeFilename sourceRoot destinationRoot.");
		}

		String exceptionFilename = destRoot + File.separator + "exceptions.txt";
		File exceptionFile = new File(exceptionFilename);
		FileWriter fw = null;

		// Read the volume list
		List<String> volumeList = FileUtils.readLines(volumeFile);

		try {

			fw = new FileWriter(exceptionFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			for (String volumeId : volumeList) {
				volumeId = URLDecoder.decode(volumeId, "UTF-8");
				Pairtree pt = new Pairtree();

				// Parse the volume ID
				String sourcePart = volumeId.substring(0, volumeId.indexOf("."));
				String volumePart = volumeId.substring(volumeId.indexOf(".") + 1, volumeId.length());
				String uncleanId = pt.uncleanId(volumePart);
				String path = pt.mapToPPath(uncleanId);
				String cleanId = pt.cleanId(volumePart);

				// Path to the volume directory in the pairtree (must exist)
				String sourceVolume = sourceRoot + File.separator + sourcePart
						+ File.separator + "pairtree_root"
						+ File.separator + path
						+ File.separator + cleanId;

				// Path to the destimation volume directory (doesn't need to exist)
				String destVolume = destRoot + File.separator + sourcePart
						+ File.separator + "pairtree_root"
						+ File.separator + path
						+ File.separator + cleanId;

				// Copy the contents
				System.out.println("Copying " + sourceVolume + " to " + destVolume);
				try {
					FileUtils.copyDirectory(new File(sourceVolume), new File(destVolume));
				} catch (IOException e) {
					// e.printStackTrace();
					bw.write(sourceVolume + " not found.\n");
				}
			}
			bw.close();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void exitWithMessage(String m) {
		System.out.println(m);
		System.exit(0);
	}
}
