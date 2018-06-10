package cmdline;

enum Format {
    z7,
    zip,
    tar;

    protected String z7name() {
        return "7z";
    }
    
}
