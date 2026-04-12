package com.omegaChess.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UperBitBuffer} — the bit-level read/write buffer
 * implementing ASN.1 UPER encoding rules.
 */
@DisplayName("UperBitBuffer Tests")
public class TestUperBitBuffer {

    // ===================== Boolean / single bit =====================

    @Test
    void testBooleanTrueRoundTrip() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBoolean(true);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertTrue(reader.readBoolean());
    }

    @Test
    void testBooleanFalseRoundTrip() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBoolean(false);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertFalse(reader.readBoolean());
    }

    @Test
    void testMultipleBooleans() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBoolean(true);
        writer.writeBoolean(false);
        writer.writeBoolean(true);
        writer.writeBoolean(true);
        writer.writeBoolean(false);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertTrue(reader.readBoolean());
        assertFalse(reader.readBoolean());
        assertTrue(reader.readBoolean());
        assertTrue(reader.readBoolean());
        assertFalse(reader.readBoolean());
    }

    // ===================== Constrained integers =====================

    @Test
    void testConstrainedIntMinValue() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeConstrainedInt(0, 0, 33);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(0, reader.readConstrainedInt(0, 33));
    }

    @Test
    void testConstrainedIntMaxValue() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeConstrainedInt(33, 0, 33);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(33, reader.readConstrainedInt(0, 33));
    }

    @Test
    void testConstrainedIntMiddleValue() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeConstrainedInt(17, 0, 33);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(17, reader.readConstrainedInt(0, 33));
    }

    @Test
    void testConstrainedIntWithOffset() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeConstrainedInt(105, 100, 200);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(105, reader.readConstrainedInt(100, 200));
    }

    @Test
    void testConstrainedIntSingleValue() {
        // When min == max, zero bits needed
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeConstrainedInt(5, 5, 5);
        byte[] bytes = writer.toByteArray();
        assertEquals(0, bytes.length);

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(5, reader.readConstrainedInt(5, 5));
    }

    @Test
    void testConstrainedIntOutOfRangeThrows() {
        final UperBitBuffer writer = new UperBitBuffer();
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { writer.writeConstrainedInt(50, 0, 33); }
        });
    }

    @Test
    void testConstrainedIntBelowMinThrows() {
        final UperBitBuffer writer = new UperBitBuffer();
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { writer.writeConstrainedInt(-1, 0, 33); }
        });
    }

    // ===================== Semi-constrained integers =====================

    @Test
    void testSemiConstrainedIntZero() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(0);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(0, reader.readSemiConstrainedInt());
    }

    @Test
    void testSemiConstrainedIntSmall() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(42);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(42, reader.readSemiConstrainedInt());
    }

    @Test
    void testSemiConstrainedIntLarge() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(100000);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(100000, reader.readSemiConstrainedInt());
    }

    @Test
    void testSemiConstrainedIntMaxByte() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(255);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(255, reader.readSemiConstrainedInt());
    }

    @Test
    void testSemiConstrainedIntTwoBytes() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(256);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(256, reader.readSemiConstrainedInt());
    }

    @Test
    void testSemiConstrainedIntMaxTwoBytes() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(65535);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(65535, reader.readSemiConstrainedInt());
    }

    @Test
    void testSemiConstrainedIntNegativeThrows() {
        final UperBitBuffer writer = new UperBitBuffer();
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { writer.writeSemiConstrainedInt(-1); }
        });
    }

    // ===================== Strings =====================

    @Test
    void testEmptyString() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeString("");
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals("", reader.readString());
    }

    @Test
    void testSimpleAsciiString() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeString("hello");
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals("hello", reader.readString());
    }

    @Test
    void testUnicodeString() {
        String unicode = "caf\u00e9 \u2603 \u00fc\u00f6\u00e4";
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeString(unicode);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(unicode, reader.readString());
    }

    @Test
    void testStringWithSpecialCharacters() {
        String special = "key=value,process=login,";
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeString(special);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(special, reader.readString());
    }

    @Test
    void testLongString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("abcdefghij");
        }
        String longStr = sb.toString();

        UperBitBuffer writer = new UperBitBuffer();
        writer.writeString(longStr);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(longStr, reader.readString());
    }

    // ===================== Optional strings =====================

    @Test
    void testOptionalStringPresent() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeOptionalString("present");
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals("present", reader.readOptionalString());
    }

    @Test
    void testOptionalStringNull() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeOptionalString(null);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertNull(reader.readOptionalString());
    }

    @Test
    void testOptionalStringEmpty() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeOptionalString("");
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals("", reader.readOptionalString());
    }

    // ===================== Mixed types =====================

    @Test
    void testMixedTypes() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBoolean(true);
        writer.writeConstrainedInt(15, 0, 33);
        writer.writeSemiConstrainedInt(999);
        writer.writeString("mixed");
        writer.writeOptionalString(null);
        writer.writeOptionalString("yes");
        writer.writeBoolean(false);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertTrue(reader.readBoolean());
        assertEquals(15, reader.readConstrainedInt(0, 33));
        assertEquals(999, reader.readSemiConstrainedInt());
        assertEquals("mixed", reader.readString());
        assertNull(reader.readOptionalString());
        assertEquals("yes", reader.readOptionalString());
        assertFalse(reader.readBoolean());
    }

    @Test
    void testMultipleStringsInSequence() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeString("first");
        writer.writeString("second");
        writer.writeString("third");
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals("first", reader.readString());
        assertEquals("second", reader.readString());
        assertEquals("third", reader.readString());
    }

    @Test
    void testMultipleSemiConstrainedInts() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeSemiConstrainedInt(0);
        writer.writeSemiConstrainedInt(1);
        writer.writeSemiConstrainedInt(127);
        writer.writeSemiConstrainedInt(128);
        writer.writeSemiConstrainedInt(1000000);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(0, reader.readSemiConstrainedInt());
        assertEquals(1, reader.readSemiConstrainedInt());
        assertEquals(127, reader.readSemiConstrainedInt());
        assertEquals(128, reader.readSemiConstrainedInt());
        assertEquals(1000000, reader.readSemiConstrainedInt());
    }

    // ===================== Raw bit operations =====================

    @Test
    void testWriteAndReadBits() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBits(0b10110, 5);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertEquals(0b10110, reader.readBits(5));
    }

    @Test
    void testWriteAndReadSingleBit() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBit(true);
        writer.writeBit(false);
        writer.writeBit(true);
        byte[] bytes = writer.toByteArray();

        UperBitBuffer reader = new UperBitBuffer(bytes);
        assertTrue(reader.readBit());
        assertFalse(reader.readBit());
        assertTrue(reader.readBit());
    }

    @Test
    void testReadPastEndThrows() {
        final UperBitBuffer reader = new UperBitBuffer(new byte[]{(byte) 0xFF});
        for (int i = 0; i < 8; i++) {
            reader.readBit(); // exhaust all 8 bits
        }
        assertThrows(RuntimeException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() { reader.readBit(); }
        });
    }

    // ===================== Byte array output =====================

    @Test
    void testEmptyBufferProducesEmptyArray() {
        UperBitBuffer writer = new UperBitBuffer();
        byte[] bytes = writer.toByteArray();
        assertEquals(0, bytes.length);
    }

    @Test
    void testPartialByteIsPadded() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBit(true); // 1 bit → should produce 1 byte with padding
        byte[] bytes = writer.toByteArray();
        assertEquals(1, bytes.length);
        assertEquals((byte) 0x80, bytes[0]); // 1000_0000
    }

    @Test
    void testExactByteNoPadding() {
        UperBitBuffer writer = new UperBitBuffer();
        writer.writeBits(0xAB, 8);
        byte[] bytes = writer.toByteArray();
        assertEquals(1, bytes.length);
        assertEquals((byte) 0xAB, bytes[0]);
    }
}
