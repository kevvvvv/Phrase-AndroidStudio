package com.phrase.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by kolja on 21.10.15.
 */
public class PhraseConfiguration {
    private final Project project;
    private String currentConfig;


    public PhraseConfiguration(Project project){
        this.project = project;
        loadPhraseConfig();
    }

    public void setConfig(String s) {
        String projectPath = project.getBasePath();
        try {
            File configFile = new File(projectPath + "/.phrase.yml");
            FileUtils.writeStringToFile(configFile, s);
            LocalFileSystem.getInstance().refreshIoFiles(Collections.singletonList(configFile));
            currentConfig = s;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateConfig(Map config) {
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(config, writer);
        setConfig(writer.toString());
    }

    public void loadPhraseConfig() {
        String projectPath = project.getBasePath();
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(projectPath + "/.phrase.yml"), "UTF-8");
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } catch (FileNotFoundException e) {
            // Do nothing.
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        this.currentConfig = text.toString();
    }

    public String getProjectId() {
        String projectId = null;
        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phrase");
            if (root != null) {
                projectId = (String) root.get("project_id");
            }
        }

        return projectId;
    }

    public String getAccessToken() {
        String accessToken = null;

        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phrase");
            if (root != null) {
                accessToken = (String) root.get("access_token");
            }
        }

        return accessToken;
    }

    public String getLocaleId() {
        String localeId = null;

        if (configExists()) {
            Yaml yaml = new Yaml();
            Map configYml = (Map) yaml.load(currentConfig);
            Map root = (Map) configYml.get("phrase");
            if (root != null) {
                Map push = (Map) root.get("push");
                if (push != null) {
                    List<Map> sources = (List<Map>) push.get("sources");
                    Map source = sources.get(0);
                    Map params = (Map) source.get("params");
                    if (params != null){
                        localeId = (String) params.get("locale_id");
                    }
                }
            }
        }

        return localeId;
    }

    public boolean configExists() {
        return currentConfig.startsWith("phrase");
    }
}
