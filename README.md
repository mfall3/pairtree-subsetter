pairtree-subsetter
==================

This tool was specifically designed to create a subset of a HathiTrust dataset in pairtree structure.

Usage: java -jar subsetter.jar volumeFilename sourceRoot destinationRoot

## arguments:

+ *volumeFilename* is the filename of the list of HathiTrust volume ids

+ *sourceRoot* is the directory containing the source dataset.  It is actually two levels above any actual pairtree_root.

+ *destinationRoot* is the intended directory for the resulting subset