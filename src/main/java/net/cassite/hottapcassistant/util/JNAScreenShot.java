/*
 * copied and modified from https://stackoverflow.com/questions/24668407/how-to-take-a-screenshot-of-desktop-fast-with-java-in-windows-ffmpeg-etc
 */

package net.cassite.hottapcassistant.util;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

import java.awt.*;
import java.awt.image.*;

import static com.sun.jna.platform.win32.WinDef.*;
import static com.sun.jna.platform.win32.WinGDI.*;
import static com.sun.jna.platform.win32.WinNT.HANDLE;

public class JNAScreenShot {
    private static HDC windowDC;

    public static BufferedImage getScreenshot(Rectangle bounds) {
        if (windowDC == null) {
            windowDC = GDI.GetDC(USER.GetDesktopWindow());
        }
        HBITMAP outputBitmap = GDI.CreateCompatibleBitmap(windowDC, bounds.width, bounds.height);
        try {
            HDC blitDC = GDI.CreateCompatibleDC(windowDC);
            try {
                HANDLE oldBitmap = GDI.SelectObject(blitDC, outputBitmap);
                try {
                    GDI.BitBlt(blitDC, 0, 0, bounds.width, bounds.height, windowDC, bounds.x, bounds.y, GDI32.SRCCOPY);
                } finally {
                    GDI.SelectObject(blitDC, oldBitmap);
                }
                BITMAPINFO bi = new BITMAPINFO(40);
                bi.bmiHeader.biSize = 40;
                boolean ok = GDI.GetDIBits(blitDC, outputBitmap, 0, bounds.height, (byte[]) null, bi, DIB_RGB_COLORS);
                if (ok) {
                    BITMAPINFOHEADER bih = bi.bmiHeader;
                    bih.biHeight = -Math.abs(bih.biHeight);
                    bi.bmiHeader.biCompression = 0;
                    return bufferedImageFromBitmap(blitDC, outputBitmap, bi);
                } else {
                    return null;
                }
            } finally {
                GDI.DeleteObject(blitDC);
            }
        } finally {
            GDI.DeleteObject(outputBitmap);
        }
    }

    private static BufferedImage bufferedImageFromBitmap(HDC blitDC, HBITMAP outputBitmap, BITMAPINFO bi) {
        BITMAPINFOHEADER bih = bi.bmiHeader;
        int height = Math.abs(bih.biHeight);
        final DirectColorModel cm;
        final DataBuffer buffer;
        final WritableRaster raster;
        int strideBits = (bih.biWidth * bih.biBitCount);
        int strideBytesAligned = (((strideBits - 1) | 0x1F) + 1) >> 3;
        final int strideElementsAligned;
        switch (bih.biBitCount) {
            case 16 -> {
                strideElementsAligned = strideBytesAligned / 2;
                cm = new DirectColorModel(16, 0x7C00, 0x3E0, 0x1F);
                buffer = new DataBufferUShort(strideElementsAligned * height);
                raster = Raster.createPackedRaster(buffer, bih.biWidth, height, strideElementsAligned, cm.getMasks(), null);
            }
            case 32 -> {
                strideElementsAligned = strideBytesAligned / 4;
                cm = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
                buffer = new DataBufferInt(strideElementsAligned * height);
                raster = Raster.createPackedRaster(buffer, bih.biWidth, height, strideElementsAligned, cm.getMasks(), null);
            }
            default -> throw new IllegalArgumentException("Unsupported bit count: " + bih.biBitCount);
        }
        final boolean ok;
        switch (buffer.getDataType()) {
            case DataBuffer.TYPE_INT -> {
                assert buffer instanceof DataBufferInt;
                int[] pixels = ((DataBufferInt) buffer).getData();
                ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0);
            }
            case DataBuffer.TYPE_USHORT -> {
                assert buffer instanceof DataBufferUShort;
                short[] pixels = ((DataBufferUShort) buffer).getData();
                ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), pixels, bi, 0);
            }
            default -> throw new AssertionError("Unexpected buffer element type: " + buffer.getDataType());
        }
        if (ok) {
            return new BufferedImage(cm, raster, false, null);
        } else {
            return null;
        }
    }

    private static final User32 USER = User32.INSTANCE;
    private static final GDI32 GDI = GDI32.INSTANCE;
}

interface GDI32 extends com.sun.jna.platform.win32.GDI32 {
    GDI32 INSTANCE = Native.load(GDI32.class);

    boolean BitBlt(HDC hdcDest, int nXDest, int nYDest, int nWidth, int nHeight, HDC hdcSrc, int nXSrc, int nYSrc, int dwRop);

    HDC GetDC(HWND hWnd);

    boolean GetDIBits(HDC dc, HBITMAP bmp, int startScan, int scanLines, byte[] pixels, BITMAPINFO bi, int usage);

    boolean GetDIBits(HDC dc, HBITMAP bmp, int startScan, int scanLines, short[] pixels, BITMAPINFO bi, int usage);

    boolean GetDIBits(HDC dc, HBITMAP bmp, int startScan, int scanLines, int[] pixels, BITMAPINFO bi, int usage);

    int SRCCOPY = 0xCC0020;
}

interface User32 extends com.sun.jna.platform.win32.User32 {
    User32 INSTANCE = Native.load(User32.class, W32APIOptions.UNICODE_OPTIONS);

    HWND GetDesktopWindow();
}
