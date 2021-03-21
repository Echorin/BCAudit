package com.oschain.fastchaindb.blockchain.constants;

public enum TransactionType {

        /** The create. */
        ARCHIVES("archives"),
        /** The create. */
        OTHER("other");



        /** The value. */
        private final String value;

        /**
         * Instantiates a new operations.
         *
         * @param value the value
         */
        TransactionType(final String value) {
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
