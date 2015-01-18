package com.app.numconv;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SolutionBuilder {
    private StringBuilder _builder = new StringBuilder();

    public SolutionBuilder comment(int resId) {
        _builder.append(Application.getContext().getResources().getString(resId));
        return this;
    }

    public SolutionBuilder comment(int resId, Object... params) {
        _builder.append(Extensions.format(resId, params));
        return this;
    }

    public SolutionBuilder number(int number) {
        _builder.append(number);
        return this;
    }

    public SolutionBuilder number(float number) {
        _builder.append(number);
        return this;
    }

    public SolutionBuilder number(BigInteger number) {
        _builder.append(number);
        return this;
    }

    public SolutionBuilder number(BigDecimal number) {
        _builder.append(number);
        return this;
    }

    public SolutionBuilder number(String number) {
        _builder.append(number);
        return this;
    }

    public SolutionBuilder number(int number, String tag) {
        _builder.append('<').append(tag).append('>').append(number).append("</").append(tag).append('>');
        return this;
    }

    public SolutionBuilder number(float number, String tag) {
        _builder.append('<').append(tag).append('>').append(number).append("</").append(tag).append('>');
        return this;
    }

    public SolutionBuilder number(BigInteger number, String tag) {
        _builder.append('<').append(tag).append('>').append(number).append("</").append(tag).append('>');
        return this;
    }

    public SolutionBuilder number(BigDecimal number, String tag) {
        _builder.append('<').append(tag).append('>').append(number).append("</").append(tag).append('>');
        return this;
    }

    public SolutionBuilder number(String number, String tag) {
        _builder.append('<').append(tag).append('>').append(number).append("</").append(tag).append('>');
        return this;
    }

    public SolutionBuilder plus() {
        _builder.append(" + ");
        return this;
    }

    public SolutionBuilder minus() {
        _builder.append(" - ");
        return this;
    }

    public SolutionBuilder cross() {
        _builder.append("&middot;");
        return this;
    }

    public SolutionBuilder divide() {
        _builder.append("/");
        return this;
    }

    public SolutionBuilder equals() {
        _builder.append(" = ");
        return this;
    }

    public SolutionBuilder dot() {
        _builder.append(".");
        return this;
    }

    public SolutionBuilder index(int index) {
        _builder.append("<sub>").append(index).append("</sub>");
        return this;
    }

    public SolutionBuilder power(int power) {
        _builder.append("<sup>").append(power).append("</sup>");
        return this;
    }

    public SolutionBuilder endline() {
        _builder.append(";<br>");
        return this;
    }

    public SolutionBuilder newline() {
        _builder.append("<br>");
        return this;
    }

    public SolutionBuilder space() {
        _builder.append(' ');
        return this;
    }

    public void clear() {
        _builder.delete(0, _builder.length());
    }

    public String toString() {
        return _builder.toString();
    }
}
