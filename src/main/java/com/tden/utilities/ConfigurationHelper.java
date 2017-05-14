package com.tden.utilities;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */
@Slf4j
public class ConfigurationHelper {

    private static Properties properties = new Properties();

    private static String PROPERTIES_FILE_NAME = "bot-config.properties";

    static {
        initProperties();
    }

    public static synchronized void initProperties(){

        try {
            File f = new File(PROPERTIES_FILE_NAME);
            FileInputStream fi;

            if (f.canRead()) {
                fi = new FileInputStream(f);
                properties.load(fi);
                log.info(String.format("Property file %s is loaded", PROPERTIES_FILE_NAME));
            }

        }
        catch (IOException r){
            log.error(String.format("File not found: %s", PROPERTIES_FILE_NAME));
        }
    }


    public static String getProperty(String name) {

        Properties p = properties;
        String res = p.getProperty(name);

        if (res != null) {
            return res.trim();
        } else {
            return null;
        }

    }

    public static String getProperty(String name, String defaultValue) {

        String val = properties.getProperty(name, defaultValue);

        if (val != null) {
            return val.trim();
        } else {
            return null;
        }
    }

}
