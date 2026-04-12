# ---------------------------------------------------------
# Makefile for libasn1omega – Omega Chess ASN.1 codec
#
# Prerequisites:
#   asn1c (mouse07410/asn1c fork) installed and on PATH
#   gcc, make
#
# Targets:
#   all          – generate C sources and build shared lib
#   generate     – run asn1c on omega-chess.asn
#   clean        – remove generated sources and library
# ---------------------------------------------------------

SCHEMA    := ../omega-chess.asn
GENDIR    := generated
LIBNAME   := libasn1omega

# Detect OS for shared library extension
UNAME_S := $(shell uname -s)
ifeq ($(UNAME_S),Darwin)
    SOEXT := dylib
    SOFLAGS := -dynamiclib
else ifeq ($(OS),Windows_NT)
    SOEXT := dll
    SOFLAGS := -shared
else
    SOEXT := so
    SOFLAGS := -shared
endif

LIBRARY := $(LIBNAME).$(SOEXT)

# All generated .c files plus the shim
GEN_SRCS := $(wildcard $(GENDIR)/*.c)
SHIM_SRC := shim.c
SRCS     := $(GEN_SRCS) $(SHIM_SRC)
OBJS     := $(SRCS:.c=.o)

CFLAGS   := -fPIC -I$(GENDIR) -Wall -O2
LDFLAGS  := $(SOFLAGS)

.PHONY: all generate clean

all: generate $(LIBRARY)

generate:
	@echo "=== Running asn1c on $(SCHEMA) ==="
	mkdir -p $(GENDIR)
	cd $(GENDIR) && asn1c -fcompound-names -fincludes-quoted -pdu=all ../$(SCHEMA)
	@echo "=== asn1c generation complete ==="

$(LIBRARY): $(SRCS)
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $(SRCS)

$(GENDIR)/%.o: $(GENDIR)/%.c
	$(CC) $(CFLAGS) -c -o $@ $<

shim.o: shim.c
	$(CC) $(CFLAGS) -c -o $@ $<

clean:
	rm -rf $(GENDIR)/*
	rm -f $(LIBRARY) *.o
