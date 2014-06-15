package nl.fm.downline.common;

/**
 * @author Ruud de Jong
 */
public final class Retour <V, R> {
    private V value;
    private R reason;
    private final boolean success;

    private Retour(boolean success) {
        this.success = success;
    }

    private Retour(boolean success, V value, R reason) {
        this.success = success;
        this.value = value;
        this.reason = reason;
    }

    public static <Void, R> Retour<Void, R> createSuccessRetour() {
        return new Retour<Void, R>(true);
    }

    public static <V, R> Retour<V, R> createSuccessRetour(V value) {
        return new Retour<V, R>(true, value, null);
    }

    public static <V, R> Retour<V, R> createFaultRetour(R reason) {
        return new Retour<V, R>(false, null, reason);
    }

    public void setReason(R reason) {
        this.reason = reason;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public R getReasonForFault() {
        return this.reason;
    }

    public V getValue() {
        return this.value;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isFault() {
        return !this.success;
    }
}
