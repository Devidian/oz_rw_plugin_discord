package de.omegazirkel.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.risingworld.api.Plugin;

public class I18n {
    private Map<String, Properties> language = new HashMap<String, Properties>();
    private static final String defaultLanguage = "en";
    private Plugin plugin = null;

    public I18n(Plugin plugin) {
        this.plugin = plugin;
        this.loadLanguageData(plugin.getPath());
    }

    /**
     * 
     * @param pluginPath
     */
    private void loadLanguageData(String pluginPath) {
        File folder = new File(pluginPath + "/i18n");
        File[] listOfFiles = folder.listFiles();
        FileInputStream in;
        try {
            // System.out.println("Files found: "+listOfFiles.length);
            for (int i = 0; i < listOfFiles.length; i++) {
                // System.out.println("found: "+listOfFiles[i].getName());
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith("properties")) {
                    String lang = listOfFiles[i].getName().substring(0, 2);
                    // System.out.println("lang: "+lang);
                    Properties lngProperties = new Properties();
                    try {
                        in = new FileInputStream(listOfFiles[i]);
                        lngProperties.load(new InputStreamReader(in,"UTF8"));
                        in.close();
                        this.language.put(lang.toLowerCase(), lngProperties);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param key
     * @param lang
     * @return
     */
    public String get(String key, String lang) {
        try {
            Properties lngDefaultProperties = this.language.get(defaultLanguage);
            Properties lngProperties = null;
            if (!this.language.containsKey(lang.toLowerCase())) {
                lngProperties = lngDefaultProperties;
            } else {
                lngProperties = this.language.get(lang.toLowerCase());
            }
            return lngProperties.getProperty(key, lngDefaultProperties.getProperty(key, key));
        } catch (Exception e) {
            e.printStackTrace();
            return key;
        }

    }

    public String get(String key) {
        return this.get(key, defaultLanguage);
    }
}