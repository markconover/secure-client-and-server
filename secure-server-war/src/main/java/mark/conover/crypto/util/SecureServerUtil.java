package mark.conover.crypto.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of shared helper functions for the secure server.
 *
 * @author Mark Conover
 */
public class SecureServerUtil {

    private static final Logger LOG = LoggerFactory.getLogger(
        SecureServerUtil.class);

    public static void safeClose(BufferedReader buf) {
        if (buf != null) {
            try {
                buf.close();
            } catch (IOException e) {
                LOG.error("Error closing buffered reader." + e);
            }
        }
    }
    
    public static void safeClose(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                LOG.error("Error closing input stream." + e);
            }
        }
    }
    
    public static void safeClose(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                LOG.error("Error closing output stream." + e);
            }
        }
    }
    
    /**
     * Copies sourceFile to destination file. Source file must exist.
     * 
     * @param sourceFile
     *            - File that already exists that is to be copied.
     * @param destinationFile
     *            - File that does not exist, where the sourceFile should be
     *            copied to.
     * @throws IOException
     */
    public static void copy(File sourceFile, File destinationFile)
            throws IOException {
        
        // Source Stream
        FileInputStream fis = new FileInputStream(sourceFile);
        BufferedInputStream source = new BufferedInputStream(fis);
        
        // Destination Stream
        boolean exceptionThrown = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destinationFile);
        } catch (FileNotFoundException e) {
            LOG.error("Unable to copy files.", e);
            
            exceptionThrown = true;
            
            // Re-throw the exception for the caller to handle it.
            // NOTE: The finally block will still get executed even though this
            //       exception is re-thrown.
            throw(e);
        } finally {
            if (exceptionThrown) {
                safeClose(fis);
                safeClose(source);
            }
        }
        BufferedOutputStream destination = new BufferedOutputStream(fos);        

        // Attempt to copy byte by byte
        try {
            int readByte;
            while ((readByte = source.read()) != -1) {
                destination.write(readByte);
            }
        } catch (IOException e) {
            LOG.error("Unable to copy files.", e);
            
            // Re-throw the exception for the caller to handle it.
            // NOTE: The finally block will still get executed even though this
            //       exception is re-thrown.
            throw(e);
        } finally {
            // Close all open streams
            SecureServerUtil.safeClose(fis);
            SecureServerUtil.safeClose(source);
            
            SecureServerUtil.safeClose(fos);
            SecureServerUtil.safeClose(destination);
        }
    }
    
    public static byte[] retrieveResource(String resourcePath) 
        throws IOException {
        
        InputStream is = SecureServerUtil.class.getResourceAsStream(
                resourcePath);
        byte[] bytes = null;
        if (is != null) {
            try {
                bytes = IOUtils.toByteArray(is);
            } catch (IOException e) {
                LOG.error("Unable to get bytes for the resource path '{}'.", 
                    resourcePath, e);
                
                // Re-throw the exception for the caller to handle it.
                // NOTE: The finally block will still get executed even though
                //       this exception is re-thrown.
                throw (e);
            } finally {
                safeClose(is);
            }
        }

        return bytes;
    }

}
