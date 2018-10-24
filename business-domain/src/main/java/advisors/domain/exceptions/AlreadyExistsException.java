package advisors.domain.exceptions;

public class AlreadyExistsException extends Exception{
    public AlreadyExistsException(String s) {
        super(s + " existe déjà");
    }
}
