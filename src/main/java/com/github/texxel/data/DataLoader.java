package com.github.texxel.data;

import com.github.texxel.data.exceptions.DataSerializationException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An Interface to define something that can load data. Various file formats exist that data
 * could be loaded from - for example Json or XML. This type of data loader only lets lists of
 * Data be loaded.
 */
public interface DataLoader {

    /**
     * Takes an input stream and reads out some primitive data. This method is
     * responsible for closing the stream
     * @param input where to read from
     * @return the PData in the file
     * @throws DataSerializationException if anything bad happens during loading
     */
    PData read(InputStream input);

    /**
     * Takes some primitive data and writes it to the output stream. This method
     * is responsible for closing the stream
     * @param output where the primitive data should be written
     * @throws DataSerializationException when the output can't be written
     */
    void write(OutputStream output, PData data);

}
