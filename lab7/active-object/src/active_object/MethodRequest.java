package active_object;

interface MethodRequest {
    /**
     * This method indicates whether the servant is guarded
     * @return true if the resource is guarded or false if it is not guarded
     */
    boolean isGuarded();

    /**
     * This method call the method request. It is used to put the
     * method request in the servant method requests queue
     */
    void call();
}
