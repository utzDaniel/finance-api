#!/bin/sh

set -e

CERT_FILE="/certs/dev.crt"
TRUSTSTORE="$JAVA_HOME/lib/security/cacerts"
ALIAS="keycloak-dev"
PASSWORD="changeit"

if [ -f "$CERT_FILE" ]; then
    echo "Importando certificado do Keycloak..."

    keytool -delete \
        -alias "$ALIAS" \
        -keystore "$TRUSTSTORE" \
        -storepass "$PASSWORD" \
        2>/dev/null || true

    keytool -importcert \
        -noprompt \
        -trustcacerts \
        -alias "$ALIAS" \
        -file "$CERT_FILE" \
        -keystore "$TRUSTSTORE" \
        -storepass "$PASSWORD"

    echo "Certificado do Keycloak importado com sucesso."
else
    echo "AVISO: certificado $CERT_FILE não encontrado."
fi

exec java -jar /app/app.jar
