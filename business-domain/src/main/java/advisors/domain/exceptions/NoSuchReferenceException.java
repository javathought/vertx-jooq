package advisors.domain.exceptions;

public class NoSuchReferenceException extends Exception{
    public NoSuchReferenceException(String format) {
        super(format + "n'existe pas");
    }
}
