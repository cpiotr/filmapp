package pl.ciruk.film.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import pl.ciruk.film.utils.core.PreconditionsHelper;

import com.google.common.base.Preconditions;

public class ContentExtractor {
	private static final Log LOG = LogFactory.getLog(ContentExtractor.class);
	
	
	/**
	 * Zwraca tresc pliku wejsciowego.
	 */
	public String getContent(File file) {
		Preconditions.checkArgument(file != null, PreconditionsHelper.CANT_BE_NULL, "File");
		String content = null;
		
		if (file.exists() && file.isFile()) {
			try {
				content = parse(file);
			} catch (IOException e) {
				LOG.error("getContent - Error occurred while reading file: " + file.getAbsolutePath(), e);
			} catch (SAXException e) {
				LOG.error("getContent - Error occurred while parsing file: " + file.getAbsolutePath(), e);
			} catch (TikaException e) {
				LOG.error("getContent - Error occurred while parsing file with Tika: " + file.getAbsolutePath());
			}
		} else {
			LOG.error("FileParser() - Cannot load given file: " + file.getAbsolutePath());
		}
		
		return content;
	}
	
	/** Parsuje plik wejsciowy i zwraca jego tresc w postaci tekstowej. */
	private String parse(File file) throws IOException, SAXException, TikaException {
		InputStream input = null;  
		ContentHandler textHandler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		OfficeParser parser = new OfficeParser();
		
		try {
			input = new FileInputStream(file);
			parser.parse(input, textHandler, metadata);
		} finally {
			if (input != null) {
				input.close();
			}
		}
		
		return textHandler.toString();
	}
}
