package com.oschain.fastchaindb.client.constants;

public enum ChainType {

    /** The create. */
    BIGCHAINDB("bigchaindb"),
    /** The create. */
    ETH("eth"),
    /** The create. */
    FABRIC("fabric");



    /** The value. */
    private final String value;

    /**
     * Instantiates a new operations.
     *
     * @param value the value
     */
    ChainType(final String value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.value;
    }
}
