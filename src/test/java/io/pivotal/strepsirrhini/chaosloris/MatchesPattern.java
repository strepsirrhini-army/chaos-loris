package io.pivotal.strepsirrhini.chaosloris;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Pattern;

public final class MatchesPattern extends TypeSafeMatcher<String> {

    private final Pattern pattern;

    private MatchesPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public static Matcher<String> matchesPattern(Pattern pattern) {
        return new MatchesPattern(pattern);
    }

    public static Matcher<String> matchesPattern(String regex) {
        return new MatchesPattern(Pattern.compile(regex));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("a string matching the pattern '%s'", this.pattern));
    }

    @Override
    protected boolean matchesSafely(String item) {
        return this.pattern.matcher(item).matches();
    }

}
