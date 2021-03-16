package reghzy.offlinemsg.commands;

public class ParsedValue<T> {
    private T value;
    private boolean failed;

    public ParsedValue(T value, boolean failed) {
        this.value = value;
        this.failed = failed;
    }

    public T getValue() {
        return this.value;
    }

    public boolean failed() {
        return this.failed;
    }
}
