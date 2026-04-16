# ---------------------------------------------------------
# Makefile for libasn1omega – Omega Chess ASN.1 native codec
#
# Prerequisites:
#   gcc, make
#   asn1c (only needed for the 'generate' target to regenerate
#          C sources; pre-generated sources are checked in)
#
# Targets:
#   all          – build shared library from checked-in generated sources
#   generate     – (re)run asn1c on omega-chess.asn (requires asn1c on PATH)
#   clean        – remove generated library and object files
# ---------------------------------------------------------

SCHEMA    := ../omega-chess.asn
GENDIR    := generated
LIBNAME   := libasn1omega

# Detect OS for shared library extension
ifeq ($(OS),Windows_NT)
    SOEXT := dll
    SOFLAGS := -shared
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Darwin)
        SOEXT := dylib
        SOFLAGS := -dynamiclib
    else
        SOEXT := so
        SOFLAGS := -shared
    endif
endif

LIBRARY := $(LIBNAME).$(SOEXT)

CFLAGS   := -fPIC -I$(GENDIR) -Wall -O2
LDFLAGS  := $(SOFLAGS)

.PHONY: all generate clean

# Build the shared library from pre-generated C sources + shim.
# Uses shell globbing so the wildcard resolves at recipe time.
all: $(LIBRARY)

$(LIBRARY):
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(GENDIR)/*.c shim.c

# Regenerate C sources from the ASN.1 schema (requires asn1c).
# The generated sources are checked in, so this target is only
# needed when omega-chess.asn changes.
generate:
	@echo "=== Running asn1c on $(SCHEMA) ==="
	mkdir -p $(GENDIR)
	cd $(GENDIR) && asn1c -fcompound-names -fincludes-quoted -gen-PER -pdu=all ../$(SCHEMA)
	@echo "=== asn1c generation complete ==="

clean:
	rm -f $(LIBRARY) *.o $(GENDIR)/*.o
