package pictorial.model;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.stage.FileChooser;

import java.io.*;

public class CodeGen {
    private static Template _pythonTemplate;
    private static Template _javaTemplate;
    private static Template _cppTemplate;
    private static Template _csTemplate;

    static {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(System.class, "/pictorial");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        try {
            _pythonTemplate = cfg.getTemplate("template.py");
            _csTemplate = cfg.getTemplate("template.cs");
            _cppTemplate = cfg.getTemplate("template.cpp");
            _javaTemplate = cfg.getTemplate("template.java");
        } catch(Exception e) {
            System.out.print("Failed to load a template: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String generateCode(String language, Settings.LanguageFormat langFormat, Settings settings) throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        if (language == "python")
            _pythonTemplate.process(settings.getHashTable(langFormat), sw);
        else if (language == "java")
            _javaTemplate.process(settings.getHashTable(langFormat), sw);
        else if (language == "c++")
            _cppTemplate.process(settings.getHashTable(langFormat), sw);
        else if (language == "csharp")
            _cppTemplate.process(settings.getHashTable(langFormat), sw);

            return sw.toString();
    }

    public static String generateCpp(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat cppFormat = s -> s;
        return CodeGen.generateCode("c++", cppFormat, settings);
    }

    public static String generateJava(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat javaFormat = s -> s.replace("::", ".");
        return CodeGen.generateCode("java", javaFormat, settings);
    }

    public static String generateCSharp(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat csFormat = s -> s.replace("::", ".");
        return generateCode("csharp", csFormat, settings);
    }

    public static String generatePython(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat pyFormat = s -> {
            if (s.equals("true")) {
                return "True";
            } else if (s.equals("false")) {
                return "False";
            }
            return s.replace("::", "_");
        };
        return generateCode("python", pyFormat, settings);
    }
}
