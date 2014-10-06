package pictorial.model;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javafx.stage.FileChooser;

import java.io.*;

public class CodeGen {
    private static String generateCode(String extension, Settings.LanguageFormat langFormat, Settings settings) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(System.class, "/pictorial");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        Template temp = cfg.getTemplate("template" + extension);
        StringWriter sw = new StringWriter();
        temp.process(settings.getHashTable(langFormat), sw);
        return sw.toString();
    }

    public static String generateCpp(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat cppFormat = s -> s;
        return CodeGen.generateCode(".cpp", cppFormat, settings);
    }

    public static String generateJava(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat javaFormat = s -> s.replace("::", ".");
        return CodeGen.generateCode(".java", javaFormat, settings);
    }

    public static String generateCSharp(Settings settings) throws IOException, TemplateException {
        Settings.LanguageFormat csFormat = s -> s.replace("::", ".");
        return generateCode(".cs", csFormat, settings);
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
        return generateCode(".py", pyFormat, settings);
    }
}
