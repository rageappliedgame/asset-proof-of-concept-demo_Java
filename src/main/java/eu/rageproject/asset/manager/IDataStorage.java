package eu.rageproject.asset.manager;

/**
 * Interface for data storage.
 * 
 * @author Ivan Martinez-Ortiz
 *
 */
public interface IDataStorage {
	
	/**
     * Deletes the given fileId.
     *
     * @param fileId The file identifier to delete.
     * 
     * @return {@code true} if it succeeds, {@code false} otherwise.
     */
    boolean delete(String fileId);

	/**
     * Checks if exits a file with <code>fileId</code> identifier.
     *
     * @param fileId The file identifier to check.
     * 
     * @returns {@code true} if exits, {@code false} otherwise.
     */
    boolean exists(String fileId);
    
    /**
     * 
     * Get file identifiers stored in this storage.
     * 
     * @return An array of fileIds.
     * */
    String[] files();

    /**
     * Loads content of the <code>fileId</code>.
     *  
     * @param fileId file identifier to load.
     * 
     * @return file contents or {@code null} if not exits.
     */
    String load(String fileId);

    /**
     * Saves the given file.
     * 
     * @param fileId file identifier to save.
     * 
     * @param fileData file content to save.
     */
    void save(String fileId, String fileData);
}
