package eu.rageproject.assets.demo;

public interface IDataArchive {
	/**
	 * Archives the given file.
	 * 
	 * @param fileId The file identifier to archive.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	boolean archive(String fileId);
}