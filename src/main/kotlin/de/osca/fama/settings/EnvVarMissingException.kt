package de.osca.fama.settings

class EnvVarMissingException(key: String) :
    IllegalArgumentException(
        """
        Variable $key is missing. 
        Please read the documentation to ensure all required variables are set.
    """,
    )
