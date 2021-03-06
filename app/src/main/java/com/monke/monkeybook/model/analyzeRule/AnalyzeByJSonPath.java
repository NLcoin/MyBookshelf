package com.monke.monkeybook.model.analyzeRule;

import android.text.TextUtils;

import com.jayway.jsonpath.JsonPath;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class AnalyzeByJSonPath {
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");

    public String read(Object object, String rule) {
        if (TextUtils.isEmpty(rule)) return null;
        String result;
        SourceRule sourceRule = splitSourceRule(rule);
        result = JsonPath.read(object, sourceRule.rule);
        if (!TextUtils.isEmpty(sourceRule.jsStr)) {
            try {
                String x = "var result = '" + result + "';" + sourceRule.jsStr;
                result = (String) engine.eval(x);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String read(String json, String rule) {
        if (TextUtils.isEmpty(rule)) return null;
        String result;
        SourceRule sourceRule = splitSourceRule(rule);
        result = JsonPath.read(json, sourceRule.rule);
        if (!TextUtils.isEmpty(sourceRule.jsStr)) {
            try {
                String x = "var result = " + result + ";";
                result = (String) engine.eval(x + sourceRule.jsStr);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private SourceRule splitSourceRule(String rule) {
        SourceRule sourceRule = new SourceRule();
        String str[] = rule.split("@js:");
        sourceRule.rule = str[0];
        if (str.length > 1) {
            sourceRule.jsStr = str[1];
        }
        return sourceRule;
    }

    class SourceRule {
        String rule;
        String jsStr;
    }
}
