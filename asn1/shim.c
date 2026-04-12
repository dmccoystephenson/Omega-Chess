/*
 * shim.c – Thin C shim providing stable JNA-callable symbols
 *          for encoding and decoding Omega Chess protocol messages.
 *
 * This file wraps the asn1c-generated encoder/decoder functions
 * with a simple byte-array interface that the Java OCCodec JNA
 * wrapper can call.
 *
 * Build: compiled as part of libasn1omega (see Makefile).
 *
 * NOTE: The function bodies below are stubs.  After running
 *       asn1c -fcompound-names -fincludes-quoted -pdu=all omega-chess.asn
 *       inspect the generated converter-example.c and fill in the
 *       actual encode/decode calls using the asn1c runtime API.
 */

#include <stdlib.h>
#include <string.h>

/*
 * Encode an XER XML byte buffer into the BER/PER binary wire format
 * for the given PDU type.
 *
 * pduType  – ASN.1 type name, e.g. "LoginRequest"
 * xmlBytes – XER XML payload (UTF-8)
 * xmlLen   – length of xmlBytes
 *
 * Returns a malloc'd buffer containing the encoded bytes.
 * The caller must free the buffer with ocmsg_free().
 * Returns NULL on failure.
 */
void *ocmsg_encode(const char *pduType, const char *xmlBytes, int xmlLen) {
    /* TODO: implement using asn1c xer_decode + der_encode */
    (void)pduType;
    (void)xmlBytes;
    (void)xmlLen;
    return NULL;
}

/*
 * Decode a BER/PER binary buffer into XER XML for the given PDU type.
 *
 * pduType  – ASN.1 type name
 * perBytes – encoded payload
 * perLen   – length of perBytes
 * outLen   – (out) length of the returned XML buffer
 *
 * Returns a malloc'd buffer containing XER XML (UTF-8).
 * The caller must free the buffer with ocmsg_free().
 * Returns NULL on failure.
 */
char *ocmsg_decode(const char *pduType, const char *perBytes, int perLen, int *outLen) {
    /* TODO: implement using asn1c ber_decode + xer_encode */
    (void)pduType;
    (void)perBytes;
    (void)perLen;
    if (outLen) *outLen = 0;
    return NULL;
}

/*
 * Free a buffer returned by ocmsg_encode or ocmsg_decode.
 */
void ocmsg_free(void *ptr) {
    free(ptr);
}
