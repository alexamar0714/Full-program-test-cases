# Script to add a name/value index to property
CREATE index name_val_idx ON property (name, value(512));
